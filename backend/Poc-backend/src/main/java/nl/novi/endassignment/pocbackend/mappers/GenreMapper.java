package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.GenreInputDto;
import nl.novi.endassignment.pocbackend.dtos.GenreResponseDto;
import nl.novi.endassignment.pocbackend.models.Artwork;
import nl.novi.endassignment.pocbackend.models.Genre;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenreMapper {

    public GenreResponseDto toDto(Genre genre) {
        GenreResponseDto genreResponseDto = new GenreResponseDto();
        genreResponseDto.setName(genre.getName());

        if (genre.getArtworks() != null) {
            genreResponseDto.setArtworkTitles(genre.getArtworks()
                    .stream()
                    .map(Artwork::getTitle)
                    .toList()
            );
        }

        return genreResponseDto;
    }

    public Genre toEntity(GenreInputDto genreInputDto) {
        Genre genre = new Genre();
        genre.setName(genreInputDto.getName().trim().toUpperCase());

        return genre;
    }

    public List<GenreResponseDto> toDtoList(List<Genre> genres) {
        return genres.stream()
                .map(this::toDto)
                .toList();
    }
}
