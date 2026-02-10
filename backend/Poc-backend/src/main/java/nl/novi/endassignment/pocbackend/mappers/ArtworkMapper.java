package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.ArtworkInputDto;
import nl.novi.endassignment.pocbackend.dtos.ArtworkResponseDto;
import nl.novi.endassignment.pocbackend.dtos.ArtworkUpdateDto;
import nl.novi.endassignment.pocbackend.models.Artist;
import nl.novi.endassignment.pocbackend.models.Artwork;
import nl.novi.endassignment.pocbackend.models.AvailabilityType;
import nl.novi.endassignment.pocbackend.models.Genre;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ArtworkMapper {

    public ArtworkResponseDto toDto(Artwork artwork) {
        ArtworkResponseDto artworkResponseDto = new ArtworkResponseDto();
        artworkResponseDto.setId(artwork.getId());
        artworkResponseDto.setTitle(artwork.getTitle());
        artworkResponseDto.setImages(artwork.getImages());
        artworkResponseDto.setPrice(artwork.getPrice());
        if (artwork.getAvailability() != null) {
            artworkResponseDto.setAvailability(artwork.getAvailability().name());
            artworkResponseDto.setAvailabilityLabel(artwork.getAvailability().getLabel());
        }
        artworkResponseDto.setWidthInCm(artwork.getWidthInCm());
        artworkResponseDto.setLengthInCm(artwork.getLengthInCm());
        artworkResponseDto.setHeightInCm(artwork.getHeightInCm());
        artworkResponseDto.setArtistId(artwork.getArtist() != null ? artwork.getArtist().getId() : null);
        artworkResponseDto.setArtistName(artwork.getArtist() != null
                ? artwork.getArtist().getFirstName() + " " + artwork.getArtist().getLastName()
                : null);

        artworkResponseDto.setGenreNames(
                artwork.getGenres()
                        .stream()
                        .map(Genre::getName)
                        .toList()
        );

        return artworkResponseDto;
    }

    public ArtworkResponseDto toDtoForEdit(Artwork artwork) {
        ArtworkResponseDto artworkResponseDto = new ArtworkResponseDto();
        artworkResponseDto.setId(artwork.getId());
        artworkResponseDto.setTitle(artwork.getTitle());
        artworkResponseDto.setImages(
                artwork.getImages().stream()
                        .map(name -> "/uploads/" + name)
                        .toList()
        );
        artworkResponseDto.setPrice(artwork.getPrice());
        if (artwork.getAvailability() != null) {
            artworkResponseDto.setAvailability(artwork.getAvailability().name());
            artworkResponseDto.setAvailabilityLabel(artwork.getAvailability().getLabel());
        }
        artworkResponseDto.setWidthInCm(artwork.getWidthInCm());
        artworkResponseDto.setLengthInCm(artwork.getLengthInCm());
        artworkResponseDto.setHeightInCm(artwork.getHeightInCm());
        artworkResponseDto.setGenreNames(
                artwork.getGenres().stream().map(Genre::getName).toList()
        );

        return artworkResponseDto;
    }

    public Artwork toEntity(ArtworkInputDto artworkInputDto, List<String> fileNames) {
        Artwork artwork = new Artwork();
        artwork.setTitle(artworkInputDto.getTitle());
        artwork.setImages(fileNames);
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
