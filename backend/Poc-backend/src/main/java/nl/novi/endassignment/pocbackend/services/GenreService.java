package nl.novi.endassignment.pocbackend.services;

import jakarta.transaction.Transactional;
import nl.novi.endassignment.pocbackend.dtos.GenreInputDto;
import nl.novi.endassignment.pocbackend.exceptions.RecordNotFoundException;
import nl.novi.endassignment.pocbackend.models.Artwork;
import nl.novi.endassignment.pocbackend.models.Genre;
import nl.novi.endassignment.pocbackend.repositories.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Genre getGenre(String name) {
        return genreRepository.findByName(name.toUpperCase())
                .orElseThrow(() -> new RecordNotFoundException("Genre met naam " + name + " niet gevonden!"));
    }

    public List<Artwork> getArtworksWithGenre(String name) {
        Genre genre = genreRepository.findByName(name.toUpperCase())
                .orElseThrow(() -> new RecordNotFoundException("Genre met naam " + name + " niet gevonden!"));
        return genre.getArtworks();
    }

    @Transactional
    public Genre findOrCreate(GenreInputDto genreInputDto) {
        String normalized = genreInputDto.getName().trim().toUpperCase();

        return genreRepository.findByName(normalized)
                .orElseGet(() -> {
                    Genre newGenre = new Genre();
                    newGenre.setName(normalized);
                    return genreRepository.save(newGenre);
                });
    }
}
