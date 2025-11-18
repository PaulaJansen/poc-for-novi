package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.ArtworkInputDto;
import nl.novi.endassignment.pocbackend.dtos.ArtworkResponseDto;
import nl.novi.endassignment.pocbackend.models.Artwork;
import nl.novi.endassignment.pocbackend.models.AvailabilityType;
import nl.novi.endassignment.pocbackend.models.Genre;
import nl.novi.endassignment.pocbackend.repositories.GenreRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ArtworkMapper {

    private final GenreRepository genreRepository;

    public ArtworkMapper(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public ArtworkResponseDto toDto(Artwork artwork) {
        ArtworkResponseDto artworkResponseDto = new ArtworkResponseDto();
        artworkResponseDto.setId(artwork.getId());
        artworkResponseDto.setTitle(artwork.getTitle());
        artworkResponseDto.setImages(artwork.getImages());
        artworkResponseDto.setPrice(artwork.getPrice());
        artworkResponseDto.setAvailability(artwork.getAvailability() != null ? artwork.getAvailability().name() : null);
        artworkResponseDto.setWidthInCm(artwork.getWidthInCm());
        artworkResponseDto.setLengthInCm(artwork.getLengthInCm());
        artworkResponseDto.setHeightInCm(artwork.getHeightInCm());

        String artistName = artwork.getArtist().getFirstName() + " " + artwork.getArtist().getLastName();
        artworkResponseDto.setArtistName(artistName);

        artworkResponseDto.setGenreNames(
                artwork.getGenres()
                        .stream()
                        .map(Genre::getName)
                        .toList()
        );

        return artworkResponseDto;
    }

    public Artwork toEntity(ArtworkInputDto artworkInputDto) {
        Artwork artwork = new Artwork();
        artwork.setTitle(artworkInputDto.getTitle());
        artwork.setImages(artworkInputDto.getImages());
        artwork.setPrice(artworkInputDto.getPrice());
        artwork.setWidthInCm(artworkInputDto.getWidthInCm());
        artwork.setLengthInCm(artworkInputDto.getLengthInCm());
        artwork.setHeightInCm(artworkInputDto.getHeightInCm());

        if (artworkInputDto.getAvailability() != null) {
            artwork.setAvailability(AvailabilityType.valueOf(artworkInputDto.getAvailability().toUpperCase()));
        }

        artwork.setGenres(artworkInputDto.getGenreNames()
                .stream()
                .map(String::toUpperCase)
                .map(name -> genreRepository.findByName(name)
                        .orElseThrow(() -> new RuntimeException("Genre not found: " + name)))
                .collect(Collectors.toList())
        );

        return artwork;
    }

//    Voor Visitor- en ArtistMapper

    public String toTitle(Artwork artwork) {
        return artwork.getTitle();
    }

    public Artwork fromTitle(String title) {
        Artwork artwork = new Artwork();
        artwork.setTitle(title);
        return artwork;
    }
}
