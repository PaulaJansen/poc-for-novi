package nl.novi.endassignment.pocbackend.controllers;

import jakarta.validation.Valid;
import nl.novi.endassignment.pocbackend.dtos.ArtistInputDto;
import nl.novi.endassignment.pocbackend.dtos.ArtistResponseDto;
import nl.novi.endassignment.pocbackend.dtos.ArtworkResponseDto;
import nl.novi.endassignment.pocbackend.services.ArtistService;
import nl.novi.endassignment.pocbackend.services.ArtworkService;
import org.hibernate.sql.ast.tree.from.TableAliasResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/artists")
public class ArtistController {

    private final ArtistService artistService;
    private final ArtworkService artworkService;

    public ArtistController(ArtistService artistService, ArtworkService artworkService) {
        this.artistService = artistService;
        this.artworkService = artworkService;
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

    @GetMapping("/{id}/artworks")
    public ResponseEntity<List<ArtworkResponseDto>> getArtworksByArtist(@PathVariable long id) {
        List<ArtworkResponseDto> artworks = artworkService.getArtworksByArtistId(id);
        return ResponseEntity.ok(artworks);
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistResponseDto> createArtist(@Valid @ModelAttribute ArtistInputDto artistInputDto) throws IOException {
        ArtistResponseDto newArtist = artistService.createArtist(artistInputDto);
        return new ResponseEntity<>(newArtist, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArtistResponseDto> updateArtistInfo(@PathVariable long id, @RequestBody ArtistInputDto artistInputDto) throws IOException {
        ArtistResponseDto updatedArtist = artistService.updateArtistInfo(id, artistInputDto);
        return ResponseEntity.ok(updatedArtist);
    }

    @PatchMapping(value = "/{id}/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistResponseDto> updateArtistProfilePicture(@PathVariable long id, @RequestParam MultipartFile profilePicture) throws IOException {
        ArtistResponseDto updatedArtist = artistService.updateArtistProfilePicture(id, profilePicture);
        return ResponseEntity.ok(updatedArtist);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArtist(@PathVariable long id) {
        String message = artistService.deleteArtist(id);
        return ResponseEntity.ok(message);
    }
}

