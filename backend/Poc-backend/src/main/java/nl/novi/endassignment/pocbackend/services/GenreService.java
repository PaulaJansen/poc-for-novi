package nl.novi.endassignment.pocbackend.services;

import jakarta.transaction.Transactional;
import nl.novi.endassignment.pocbackend.dtos.GenreInputDto;
import nl.novi.endassignment.pocbackend.dtos.GenreResponseDto;
import nl.novi.endassignment.pocbackend.exceptions.RecordNotFoundException;
import nl.novi.endassignment.pocbackend.mappers.GenreMapper;
import nl.novi.endassignment.pocbackend.models.Genre;
import nl.novi.endassignment.pocbackend.repositories.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public GenreService(GenreRepository genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    public List<GenreResponseDto> getAllGenres() {
        return genreMapper.toDtoList(genreRepository.findAll());
    }

    public GenreResponseDto getGenre(String name) {
        return genreMapper.toDto(genreRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RecordNotFoundException("Genre met naam " + name + " niet gevonden!")));
    }

    @Transactional
    public Genre findOrCreate(GenreInputDto genreInputDto) {
        String normalized = genreInputDto.getName().trim().toUpperCase();

        return genreRepository.findByNameIgnoreCase(normalized)
                .orElseGet(() -> {
                    Genre newGenre = new Genre();
                    newGenre.setName(normalized);
                    return genreRepository.save(newGenre);
                });
    }

    @Transactional
    public Genre findOrCreateByName(String name) {
        return genreRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Genre genre = new Genre();
                    genre.setName(name);
                    return genreRepository.save(genre);
                });
    }
}
