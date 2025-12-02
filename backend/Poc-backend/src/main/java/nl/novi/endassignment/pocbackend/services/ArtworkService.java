package nl.novi.endassignment.pocbackend.services;

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

import java.math.BigDecimal;
import java.util.List;

@Service
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final ArtistRepository artistRepository;
    private final GenreService genreService;
    private final ArtworkMapper artworkMapper;
    private final GenreRepository genreRepository;

    public ArtworkService(ArtworkRepository artworkRepository, ArtistRepository artistRepository, GenreService genreService, ArtworkMapper artworkMapper, GenreRepository genreRepository) {
        this.artworkRepository = artworkRepository;
        this.artistRepository = artistRepository;
        this.genreService = genreService;
        this.artworkMapper = artworkMapper;
        this.genreRepository = genreRepository;
    }

    @Transactional
    public ArtworkResponseDto createArtwork(ArtworkInputDto artworkInputDto) {
        Artwork artwork = artworkMapper.toEntity(artworkInputDto);

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

        return artworkMapper.toDto(artworkRepository.save(artwork));
    }

    public List<ArtworkResponseDto> getAllArtworks() {
        return artworkMapper.toDtoList(artworkRepository.findAll());
    }

    public ArtworkResponseDto getArtworkById(long id) {
        return artworkMapper.toDto(artworkRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstwerk met id " + id + " niet gevonden!")));
    }

    public List<ArtworkResponseDto> filterArtworks(
            String title,
            String artistFirstName,
            String artistLastName,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String genreName,
            AvailabilityType availability) {

        Specification<Artwork> specification = (root, query, cb) -> cb.conjunction();

        if (title != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("title"), title));
        }

        if (artistFirstName != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("firstName"), artistFirstName));
        }
        if (artistLastName != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("lastName"), artistLastName));
        }

        if (minPrice != null && maxPrice != null) {
            specification = specification.and((root, query, cb) -> cb.between(root.get("price"), minPrice, maxPrice));
        } else if (minPrice != null) {
            specification = specification.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        } else if (maxPrice != null) {
            specification = specification.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        if (genreName != null) {
            Genre genre = genreRepository.findByName(genreName.toUpperCase())
                    .orElseThrow(() -> new RecordNotFoundException("Genre met naam " + genreName + " niet gevonden!"));
            specification = specification.and((root, query, cb) -> cb.isMember(genre, root.get("genres")));
        }

        if (availability != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("availability"), availability));
        }

        List<Artwork> artworks = artworkRepository.findAll(specification);
        return artworkMapper.toDtoList(artworks);
    }

    @PreAuthorize("@artworkSecurity.isOwner(#id)")
    @Transactional
    public ArtworkResponseDto updateArtwork(long id, ArtworkInputDto artworkInputDto) {
        Artwork existingArtwork = artworkRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstwerk met id " + id + " niet gevonden!"));

        existingArtwork.setTitle(artworkInputDto.getTitle());
        existingArtwork.setImages(artworkInputDto.getImages());
        existingArtwork.setPrice(artworkInputDto.getPrice());
        existingArtwork.setWidthInCm(artworkInputDto.getWidthInCm());
        existingArtwork.setLengthInCm(artworkInputDto.getLengthInCm());
        existingArtwork.setHeightInCm(artworkInputDto.getHeightInCm());
        existingArtwork.setAvailability(AvailabilityType.valueOf(artworkInputDto.getAvailability().toUpperCase()));


        List<Genre> genres = artworkInputDto.getGenreNames()
                .stream()
                .map(GenreInputDto::new)
                .map(genreService::findOrCreate)
                .toList();
        existingArtwork.setGenres(genres);

        return artworkMapper.toDto(artworkRepository.save(existingArtwork));
    }

    @PreAuthorize("@artworkSecurity.isOwner(#id)")
    @Transactional
    public ArtworkResponseDto patchArtwork(long id, ArtworkInputDto artworkInputDto) {
        Artwork existingArtwork = artworkRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstwerk niet gevonden"));

        if (artworkInputDto.getTitle() != null) existingArtwork.setTitle(artworkInputDto.getTitle());
        if (artworkInputDto.getImages() != null) existingArtwork.setImages(artworkInputDto.getImages());
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
            existingArtwork.setGenres(genres);
        }

        return artworkMapper.toDto(artworkRepository.save(existingArtwork));
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
