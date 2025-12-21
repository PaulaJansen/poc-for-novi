package nl.novi.endassignment.pocbackend.controllers;

import jakarta.validation.Valid;
import nl.novi.endassignment.pocbackend.dtos.ArtworkInputDto;
import nl.novi.endassignment.pocbackend.dtos.ArtworkResponseDto;
import nl.novi.endassignment.pocbackend.services.ArtworkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/artworks")
public class ArtworkController {

    private final ArtworkService artworkService;

    public ArtworkController(ArtworkService artworkService) {
        this.artworkService = artworkService;
    }

    @GetMapping
    public ResponseEntity<List<ArtworkResponseDto>> getAllArtworks() {
        List<ArtworkResponseDto> artworks = artworkService.getAllArtworks();
        return ResponseEntity.ok(artworks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtworkResponseDto> getArtworkById(@PathVariable long id) {
        ArtworkResponseDto artwork = artworkService.getArtworkById(id);
        return ResponseEntity.ok(artwork);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ArtworkResponseDto>> filterArtworks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String artistFirstName,
            @RequestParam(required = false) String artistLastName,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) List<String> genres,
            @RequestParam(required = false) List<String> availabilities) {

        List<ArtworkResponseDto> artworks = artworkService.filterArtworks(title, artistFirstName, artistLastName, minPrice, maxPrice, genres, availabilities);
        return ResponseEntity.ok(artworks);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtworkResponseDto> createArtwork(@Valid @ModelAttribute ArtworkInputDto artworkInputDto) {
        ArtworkResponseDto newArtwork = artworkService.createArtwork(artworkInputDto);
        return new ResponseEntity<>(newArtwork, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtworkResponseDto> updateArtwork(@PathVariable long id, @ModelAttribute ArtworkInputDto artworkInputDto) {
        ArtworkResponseDto updatedArtwork = artworkService.updateArtwork(id, artworkInputDto);
        return ResponseEntity.ok(updatedArtwork);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArtwork(@PathVariable long id) {
        String message = artworkService.deleteArtwork(id);
        return ResponseEntity.ok(message);
    }
}
