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
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
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
        String artistName = (auth.getFirstName() + " " + auth.getLastName());
        Artist artist = artistRepository.findByName(artistName)
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

    public List<ArtworkResponseDto> getArtworkByTitle(String title) {
        return artworkMapper.toDtoList(artworkRepository.findByTitle(title));
    }

    public List<ArtworkResponseDto> getArtworksByArtist(Artist artist) {
        return artworkMapper.toDtoList(artworkRepository.findByArtist(artist.getFirstName(), artist.getLastName()));

    }

    public List<ArtworkResponseDto> getArtworksByPriceRange(String range) {
        List <Artwork> artworks = switch (range) {
            case "Minder dan €100" -> artworkRepository.findByPriceLessThan(new BigDecimal("100"));
            case "Tussen €100 en €300" ->
                    artworkRepository.findByPriceBetween(new BigDecimal("100.01"), new BigDecimal("500"));
            case "Tussen €300 en €800" ->
                    artworkRepository.findByPriceBetween(new BigDecimal("300.01"), new BigDecimal("800"));
            case "Tussen €800 en €1500" ->
                    artworkRepository.findByPriceBetween(new BigDecimal("800.01"), new BigDecimal("1500"));
            case "Meer dan €1500" -> artworkRepository.findByPriceGreaterThan(new BigDecimal("1500.01"));
            default -> artworkRepository.findAll();
        };

        return artworkMapper.toDtoList(artworks);
    }

    public List<ArtworkResponseDto> getArtworksByGenre(String genreName) {
        Genre genre = genreRepository.findByName(genreName.toUpperCase())
                .orElseThrow(() -> new RecordNotFoundException("Genre met naam " + genreName + " niet gevonden!"));
        return artworkMapper.toDtoList(genre.getArtworks());
    }

    public List<ArtworkResponseDto> getArtworksByAvailability(AvailabilityType availabilityType) {
        return artworkMapper.toDtoList(artworkRepository.findByAvailability(availabilityType));
    }

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

    @Transactional
    public String deleteArtwork(long id) {
        Artwork existingArtwork = artworkRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstwerk met id " + id + " niet gevonden!"));
        artworkRepository.delete(existingArtwork);
        return ("Kunstwerk met id " + id + " is verwijderd.");
    }

}
