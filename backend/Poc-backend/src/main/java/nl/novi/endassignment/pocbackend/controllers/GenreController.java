package nl.novi.endassignment.pocbackend.controllers;

import nl.novi.endassignment.pocbackend.dtos.GenreResponseDto;
import nl.novi.endassignment.pocbackend.services.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<List<GenreResponseDto>> getAllGenres() {
        List<GenreResponseDto> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{genreName}")
    public ResponseEntity<GenreResponseDto> getGenreByName(@PathVariable String genreName) {
        GenreResponseDto genre = genreService.getGenre(genreName);
        return ResponseEntity.ok(genre);
    }
}
