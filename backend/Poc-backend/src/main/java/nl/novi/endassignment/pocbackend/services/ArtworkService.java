package nl.novi.endassignment.pocbackend.services;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import nl.novi.endassignment.pocbackend.dtos.ArtworkInputDto;
import nl.novi.endassignment.pocbackend.dtos.ArtworkResponseDto;
import nl.novi.endassignment.pocbackend.dtos.ArtworkUpdateDto;
import nl.novi.endassignment.pocbackend.dtos.GenreInputDto;
import nl.novi.endassignment.pocbackend.exceptions.RecordNotFoundException;
import nl.novi.endassignment.pocbackend.mappers.ArtworkMapper;
import nl.novi.endassignment.pocbackend.models.*;
import nl.novi.endassignment.pocbackend.repositories.ArtistRepository;
import nl.novi.endassignment.pocbackend.repositories.ArtworkRepository;
import nl.novi.endassignment.pocbackend.repositories.VisitorRepository;
import nl.novi.endassignment.pocbackend.security.ownership.ArtworkSecurity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final ArtistRepository artistRepository;
    private final GenreService genreService;
    private final ArtworkMapper artworkMapper;
    private final Path uploadDirectory;
    private final FileStorageService fileStorageService;
    private final ArtworkSecurity artworkSecurity;
    private final VisitorRepository visitorRepository;

    public ArtworkService(ArtworkRepository artworkRepository, ArtistRepository artistRepository, GenreService genreService, ArtworkMapper artworkMapper, Path uploadDirectory, FileStorageService fileStorageService, ArtworkSecurity artworkSecurity, VisitorRepository visitorRepository) {
        this.artworkRepository = artworkRepository;
        this.artistRepository = artistRepository;
        this.genreService = genreService;
        this.artworkMapper = artworkMapper;
        this.uploadDirectory = uploadDirectory;
        this.fileStorageService = fileStorageService;
        this.artworkSecurity = artworkSecurity;
        this.visitorRepository = visitorRepository;
    }

    @Transactional
    public ArtworkResponseDto createArtwork(ArtworkInputDto artworkInputDto) throws IOException {
        List<String> fileNames = new ArrayList<>();

        try {
            for (MultipartFile file : artworkInputDto.getImages()) {
                if (!file.isEmpty()) {
                    fileNames.add(fileStorageService.saveFile(file, "artworks"));
                }
            }


            Artwork artwork = artworkMapper.toEntity(artworkInputDto, fileNames);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth.getName());

            Artist artist = artistRepository.findByUsername(username)
                    .orElseThrow(() -> new RecordNotFoundException("Kunstenaar niet gevonden!"));

            artwork.setArtist(artist);

            Set<Genre> genres = artworkInputDto.getGenreNames()
                    .stream()
                    .map(String::trim)
                    .map(genreService::findOrCreateByName)
                    .collect(Collectors.toSet());
            artwork.setGenres(genres);

            artworkRepository.save(artwork);

            List<String> imageUrls = fileNames.stream()
                    .map(name -> "/uploads/" + name)
                    .toList();

            ArtworkResponseDto artworkResponseDto = artworkMapper.toDto(artwork);
            artworkResponseDto.setImages(imageUrls);

            return artworkResponseDto;
        } catch (IOException e) {
            for (String fileName : fileNames) {
                fileStorageService.deleteFile(fileName);
            }

            throw new RuntimeException("Kan bestand niet opslaan: " + (artworkInputDto.getImages().getFirst().getOriginalFilename()), e);
        }
    }

    public List<ArtworkResponseDto> getAllArtworks() {
        return artworkMapper.toDtoList(artworkRepository.findAll());
    }

    public ArtworkResponseDto getArtworkById(long id) {
        return artworkMapper.toDto(artworkRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstwerk met id " + id + " niet gevonden!")));
    }

    public List<ArtworkResponseDto> getArtworksByArtistId(long artistId) {
        return artworkMapper.toDtoList(artworkRepository.findByArtistId(artistId));
    }

    public Specification<Artwork> buildArtistSpecification(String artistFirstName, String artistLastName) {
        return (root, query, cb) -> {

            Join<Artwork, Artist> artistJoin = root.join("artist");
            Predicate p = cb.conjunction();

            if (artistFirstName != null && !artistFirstName.isBlank()) {
                p = cb.and(p, cb.like(cb.lower(artistJoin.get("firstName")),
                        "%" + artistFirstName.toLowerCase() + "%"));
            }
            if (artistLastName != null && !artistLastName.isBlank()) {
                p = cb.and(p, cb.like(cb.lower(artistJoin.get("lastName")),
                        "%" + artistLastName.toLowerCase() + "%"));
            }
            return p;
        };
    }

    public Specification<Artwork> buildGenreSpecification(String genreSearch) {
        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);
            Join<Artwork, Genre> genreJoin = root.join("genres");
            return cb.like(
                    cb.lower(genreJoin.get("name")),
                    "%" + genreSearch.toLowerCase() + "%"
            );
        };
    }

    public Specification<Artwork> buildAvailabilitySpecification(List<AvailabilityType> availabilities) {
        return (root, query, cb) -> root.get("availability").in(availabilities);
    }

    public List<ArtworkResponseDto> filterArtworks(
            String title,
            String artistFirstName,
            String artistLastName,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> genreNames,
            List<AvailabilityType> availabilityNames) {

        Specification<Artwork> specification = (root, query, cb) -> cb.conjunction();

        if (title != null && !title.isBlank()) {
            specification = specification.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }

        if (artistFirstName != null || artistLastName != null) {
            specification = specification.and(
                    buildArtistSpecification(artistFirstName, artistLastName)
            );
        }

        if (minPrice != null && maxPrice != null) {
            specification = specification.and((root, query, cb) -> cb.between(root.get("price"), minPrice, maxPrice));
        } else if (minPrice != null) {
            specification = specification.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        } else if (maxPrice != null) {
            specification = specification.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        if (genreNames != null && !genreNames.isEmpty()) {
            String genreSearch = genreNames.getFirst();

            if (!genreSearch.isBlank()) {
                specification = specification.and(
                        buildGenreSpecification(genreSearch)
                );
            }
        }

        if (availabilityNames != null && !availabilityNames.isEmpty()) {
            specification = specification.and(
                    buildAvailabilitySpecification(availabilityNames)
            );
        }

        List<Artwork> artworks = artworkRepository.findAll(specification);
        return artworkMapper.toDtoList(artworks);
    }

    @Transactional
    public ArtworkResponseDto updateArtwork(long id, ArtworkUpdateDto artworkUpdateDto, List<MultipartFile> images) throws IOException {
        Artwork existingArtwork = artworkRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstwerk niet gevonden"));

        if (!artworkSecurity.isOwner(existingArtwork)) {
            throw new AccessDeniedException("Je bent geen eigenaar van dit kunstwerk");
        }

        if (artworkUpdateDto.getTitle() != null) existingArtwork.setTitle(artworkUpdateDto.getTitle());
        if (artworkUpdateDto.getPrice() != null) existingArtwork.setPrice(artworkUpdateDto.getPrice());
        if (artworkUpdateDto.getWidthInCm() != null && artworkUpdateDto.getWidthInCm() != 0)
            existingArtwork.setWidthInCm(artworkUpdateDto.getWidthInCm());
        if (artworkUpdateDto.getLengthInCm() != null && artworkUpdateDto.getLengthInCm() != 0)
            existingArtwork.setLengthInCm(artworkUpdateDto.getLengthInCm());
        if (artworkUpdateDto.getHeightInCm() != null && artworkUpdateDto.getHeightInCm() != 0)
            existingArtwork.setHeightInCm(artworkUpdateDto.getHeightInCm());
        if (artworkUpdateDto.getAvailability() != null)
            existingArtwork.setAvailability(AvailabilityType.valueOf(artworkUpdateDto.getAvailability().toUpperCase()));

        if (artworkUpdateDto.getRemoveImages() != null && !artworkUpdateDto.getRemoveImages().isEmpty()) {
            Iterator<String> iterator = existingArtwork.getImages().iterator();
            while (iterator.hasNext()) {
                String imgPath = iterator.next();
                if (artworkUpdateDto.getRemoveImages().contains(imgPath)) {
                    iterator.remove();
                    try {
                        fileStorageService.deleteFile(imgPath);
                    } catch (IOException e) {
                        System.err.println("Kon bestand niet verwijderen: " + imgPath);
                    }

                }
            }
        }

        List<String> newFiles = new ArrayList<>();
        if (images != null) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    try {
                        String path = fileStorageService.saveFile(file, "artworks");
                        existingArtwork.getImages().add(path);
                        newFiles.add(path);
                    } catch (IOException e) {
                        for (String path : newFiles) {
                            fileStorageService.deleteFile(path);
                        }

                        throw new RuntimeException("Kan bestand niet opslaan: " + file.getOriginalFilename(), e);
                    }
                }
            }
        }

        if (artworkUpdateDto.getGenreNames() != null && !artworkUpdateDto.getGenreNames().isEmpty()) {
            Set<Genre> genres = artworkUpdateDto.getGenreNames()
                    .stream()
                    .map(GenreInputDto::new)
                    .map(genreService::findOrCreate)
                    .collect(Collectors.toSet());
            existingArtwork.getGenres().clear();
            existingArtwork.getGenres().addAll(genres);
        }

        Artwork updatedArtwork = artworkRepository.save(existingArtwork);

        return artworkMapper.toDtoForEdit(existingArtwork);
    }

    @PreAuthorize("@artworkSecurity.isOwner(#id)")
    @Transactional
    public String deleteArtwork(long id) {
        Artwork existingArtwork = artworkRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstwerk met id " + id + " niet gevonden!"));

        visitorRepository.findAll().stream()
                .filter(visitor -> visitor.getFavorites().contains(existingArtwork))
                .forEach(visitor -> visitor.getFavorites().remove(existingArtwork));

        artworkRepository.delete(existingArtwork);
        return ("Kunstwerk met id " + id + " is verwijderd.");
    }
}
