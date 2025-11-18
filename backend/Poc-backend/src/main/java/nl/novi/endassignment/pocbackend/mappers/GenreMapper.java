package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.GenreInputDto;
import nl.novi.endassignment.pocbackend.dtos.GenreResponseDto;
import nl.novi.endassignment.pocbackend.models.Genre;
import nl.novi.endassignment.pocbackend.repositories.ArtworkRepository;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    private final ArtworkMapper artworkMapper;

    public GenreMapper(ArtworkMapper artworkMapper) {
        this.artworkMapper = artworkMapper;
    }

    public GenreResponseDto toDto(Genre genre) {
        GenreResponseDto genreResponseDto = new GenreResponseDto();
        genreResponseDto.setName(genre.getName());
        genreResponseDto.setArtworks(genre.getArtworks()
                .stream()
                .map(artworkMapper::toDto)
                .toList()
        );

        return genreResponseDto;
    }

    public Genre toEntity(GenreInputDto genreInputDto) {
        Genre genre = new Genre();
        genre.setName(genreInputDto.getName());

        return genre;
    }
}
