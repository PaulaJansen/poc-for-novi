package nl.novi.endassignment.pocbackend.services;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import nl.novi.endassignment.pocbackend.dtos.ArtworkInputDto;
import nl.novi.endassignment.pocbackend.dtos.ArtworkResponseDto;
import nl.novi.endassignment.pocbackend.dtos.GenreInputDto;
import nl.novi.endassignment.pocbackend.exceptions.RecordNotFoundException;
import nl.novi.endassignment.pocbackend.mappers.ArtworkMapper;
import nl.novi.endassignment.pocbackend.models.Artist;
import nl.novi.endassignment.pocbackend.models.Artwork;
import nl.novi.endassignment.pocbackend.models.AvailabilityType;
import nl.novi.endassignment.pocbackend.models.Genre;
import nl.novi.endassignment.pocbackend.repositories.ArtistRepository;
import nl.novi.endassignment.pocbackend.repositories.ArtworkRepository;
import nl.novi.endassignment.pocbackend.repositories.GenreRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final ArtistRepository artistRepository;
    private final GenreService genreService;
    private final ArtworkMapper artworkMapper;
    private final GenreRepository genreRepository;
    private final Path uploadDirectory;

    public ArtworkService(ArtworkRepository artworkRepository, ArtistRepository artistRepository, GenreService genreService, ArtworkMapper artworkMapper, GenreRepository genreRepository, Path uploadDirectory) {
        this.artworkRepository = artworkRepository;
        this.artistRepository = artistRepository;
        this.genreService = genreService;
        this.artworkMapper = artworkMapper;
        this.genreRepository = genreRepository;
        this.uploadDirectory = uploadDirectory;
    }

    @Transactional
    public ArtworkResponseDto createArtwork(ArtworkInputDto artworkInputDto) {
        List<String> fileNames = new ArrayList<>();

        for (MultipartFile file : artworkInputDto.getImages()) {
            try {
                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = uploadDirectory.resolve(filename);
                Files.copy(file.getInputStream(), filePath);
                fileNames.add(filename);
            } catch (IOException e) {
                throw new RuntimeException("Kan bestand niet opslaan: " + file.getOriginalFilename(), e);
            }
        }

        Artwork artwork = artworkMapper.toEntity(artworkInputDto, fileNames);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth.getName());
        Artist artist = artistRepository.findByUsername(username)
                .orElseThrow(() -> new RecordNotFoundException("Kunstenaar niet gevonden!"));
        artwork.setArtist(artist);

        List<Genre> genres = artworkInputDto.getGenreNames()
                .stream()
                .map(GenreInputDto::new)
                .map(genreService::findOrCreate)
                .toList();
        artwork.setGenres(genres);

        artworkRepository.save(artwork);

        List<String> imageUrls = fileNames.stream()
                .map(name -> "/uploads/" + name)
                .toList();

        ArtworkResponseDto artworkResponseDto = artworkMapper.toDto(artwork);
        artworkResponseDto.setImages(imageUrls);

        return artworkResponseDto;
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
            List<String> availabilityNames) {

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
            List<AvailabilityType> availabilities = availabilityNames.stream()
                    .map(s -> AvailabilityType.valueOf(s.toUpperCase()))
                    .toList();

            specification = specification.and(
                    buildAvailabilitySpecification(availabilities)
            );
        }

        List<Artwork> artworks = artworkRepository.findAll(specification);
        return artworkMapper.toDtoList(artworks);
    }

    void deleteImageFile(String oldImage) {
        try {
            deleteFile(oldImage);
        } catch (IOException e) {
            System.err.println("Kon afbeelding niet verwijderen: " + oldImage);
        }
    }

    void deleteFile(String oldImage) throws IOException {
        Files.deleteIfExists(uploadDirectory.resolve(oldImage));
    }

    @PreAuthorize("@artworkSecurity.isOwner(#id)")
    @Transactional
    public ArtworkResponseDto updateArtwork(long id, ArtworkInputDto artworkInputDto) {
        Artwork existingArtwork = artworkRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstwerk niet gevonden"));

        if (artworkInputDto.getTitle() != null) existingArtwork.setTitle(artworkInputDto.getTitle());

        if (artworkInputDto.getRemoveImages() != null) {
            for (String oldImage : artworkInputDto.getRemoveImages()) {
                existingArtwork.getImages().remove(oldImage);
                deleteImageFile(oldImage);
            }
        }

        if (artworkInputDto.getImages() != null && !artworkInputDto.getImages().isEmpty()) {
            for (MultipartFile file : artworkInputDto.getImages()) {
                try {
                    String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    Path filePath = uploadDirectory.resolve(filename);
                    Files.copy(file.getInputStream(), filePath);
                    existingArtwork.getImages().add(filename);
                } catch (IOException e) {
                    throw new RuntimeException("Kan bestand niet opslaan: " + file.getOriginalFilename(), e);
                }
            }
        }

        if (artworkInputDto.getPrice() != null) existingArtwork.setPrice(artworkInputDto.getPrice());
        if (artworkInputDto.getWidthInCm() != 0) existingArtwork.setWidthInCm(artworkInputDto.getWidthInCm());
        if (artworkInputDto.getLengthInCm() != 0) existingArtwork.setLengthInCm(artworkInputDto.getLengthInCm());
        if (artworkInputDto.getHeightInCm() != 0) existingArtwork.setHeightInCm(artworkInputDto.getHeightInCm());
        if (artworkInputDto.getAvailability() != null)
            existingArtwork.setAvailability(AvailabilityType.valueOf(artworkInputDto.getAvailability().toUpperCase()));

        if (artworkInputDto.getGenreNames() != null && !artworkInputDto.getGenreNames().isEmpty()) {
            List<Genre> genres = artworkInputDto.getGenreNames()
                    .stream()
                    .map(GenreInputDto::new)
                    .map(genreService::findOrCreate)
                    .toList();
            existingArtwork.getGenres().clear();
            existingArtwork.getGenres().addAll(genres);
        }

        artworkRepository.save(existingArtwork);

        List<String> imageUrls = existingArtwork.getImages().stream()
                .map(name -> "/uploads/" + name)
                .toList();

        ArtworkResponseDto artworkResponseDto = artworkMapper.toDto(existingArtwork);
        artworkResponseDto.setImages(imageUrls);

        return artworkResponseDto;
    }

    @PreAuthorize("@artworkSecurity.isOwner(#id)")
    @Transactional
    public String deleteArtwork(long id) {
        Artwork existingArtwork = artworkRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstwerk met id " + id + " niet gevonden!"));
        artworkRepository.delete(existingArtwork);
        return ("Kunstwerk met id " + id + " is verwijderd.");
    }
}
