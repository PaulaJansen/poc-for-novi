package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.ArtworkInputDto;
import nl.novi.endassignment.pocbackend.dtos.ArtworkResponseDto;
import nl.novi.endassignment.pocbackend.models.Artwork;
import nl.novi.endassignment.pocbackend.models.AvailabilityType;
import nl.novi.endassignment.pocbackend.models.Genre;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArtworkMapper {

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
        artworkResponseDto.setArtistName(artwork.getArtist().getFirstName() + " " + artwork.getArtist().getLastName());

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

    public List<ArtworkResponseDto> toDtoList(List<Artwork> artworks) {
        return artworks.stream().map(this::toDto).toList();
    }
}
