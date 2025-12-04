package nl.novi.endassignment.pocbackend.controllers;

import jakarta.validation.Valid;
import nl.novi.endassignment.pocbackend.dtos.ArtistInputDto;
import nl.novi.endassignment.pocbackend.dtos.ArtistResponseDto;
import nl.novi.endassignment.pocbackend.services.ArtistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public ResponseEntity<List<ArtistResponseDto>> getAllArtists() {
        List<ArtistResponseDto> artists = artistService.getAllArtists();
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponseDto> getArtistById(@PathVariable long id) {
        ArtistResponseDto artist = artistService.getArtistById(id);
        return ResponseEntity.ok(artist);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArtistResponseDto>> getArtistByName(@RequestParam String firstName, String lastName) {
        List<ArtistResponseDto> artist = artistService.getArtistByName(firstName, lastName);
        return ResponseEntity.ok(artist);
    }

    @PostMapping
    public ResponseEntity<ArtistResponseDto> createArtist(@Valid @RequestBody ArtistInputDto artistInputDto) {
        ArtistResponseDto newArtist = artistService.createArtist(artistInputDto);
        return new ResponseEntity<>(newArtist, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistResponseDto> updateArtist(@Valid @PathVariable long id, @RequestBody ArtistInputDto artistInputDto) {
        ArtistResponseDto updatedArtist = artistService.updateArtist(id, artistInputDto);
        return ResponseEntity.ok(updatedArtist);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ArtistResponseDto> patchArtist(@PathVariable long id, @RequestBody ArtistInputDto artistInputDto) {
        ArtistResponseDto updatedArtist = artistService.patchArtist(id, artistInputDto);
        return ResponseEntity.ok(updatedArtist);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArtist(@PathVariable long id) {
        String message = artistService.deleteArtist(id);
        return ResponseEntity.ok(message);
    }
}

