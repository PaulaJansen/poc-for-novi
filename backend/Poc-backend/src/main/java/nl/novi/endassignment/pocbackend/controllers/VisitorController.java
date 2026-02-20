package nl.novi.endassignment.pocbackend.controllers;

import jakarta.validation.Valid;
import nl.novi.endassignment.pocbackend.dtos.ArtworkResponseDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorInputDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorResponseDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorUpdateDto;
import nl.novi.endassignment.pocbackend.services.VisitorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/visitors")
public class VisitorController {

    private final VisitorService visitorService;

    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @GetMapping
    public ResponseEntity<List<VisitorResponseDto>> getAllVisitors() {
        List<VisitorResponseDto> visitors = visitorService.getAllVisitors();
        return ResponseEntity.ok(visitors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitorResponseDto> getVisitorById(@PathVariable long id) {
        VisitorResponseDto visitor = visitorService.getVisitorById(id);
        return ResponseEntity.ok(visitor);
    }

    @GetMapping("/search")
    public ResponseEntity<List<VisitorResponseDto>> getVisitorByName(@RequestParam String name) {
        List<VisitorResponseDto> visitors = visitorService.getVisitorByName(name);
        return ResponseEntity.ok(visitors);
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VisitorResponseDto> createVisitor(@Valid @ModelAttribute VisitorInputDto visitorInputDto) throws IOException {
        VisitorResponseDto newVisitor = visitorService.createVisitor(visitorInputDto);
        return new ResponseEntity<>(newVisitor, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VisitorResponseDto> updateVisitor(@PathVariable long id, @RequestBody VisitorUpdateDto visitorUpdateDto) throws IOException {
        VisitorResponseDto updatedVisitor = visitorService.updateVisitor(id, visitorUpdateDto);
        return ResponseEntity.ok(updatedVisitor);
    }

    @PatchMapping(value = "/{id}/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VisitorResponseDto> updateVisitorProfilePicture(@PathVariable long id, @RequestParam MultipartFile profilePicture) throws IOException {
        VisitorResponseDto updatedVisitor = visitorService.updateVisitorProfilePicture(id, profilePicture);
        return ResponseEntity.ok(updatedVisitor);
    }

    @GetMapping("/{id}/favorites")
    public ResponseEntity<List<ArtworkResponseDto>> getFavorites(@PathVariable long id) {
        List<ArtworkResponseDto> favorites = visitorService.getFavorites(id);
        return ResponseEntity.ok(favorites);
    }

    @PatchMapping("/{id}/favorites/{artworkId}/add")
    public ResponseEntity<VisitorResponseDto> addFavorites(@PathVariable long id, @PathVariable long artworkId) {
        VisitorResponseDto updatedVisitor = visitorService.addFavorites(id, artworkId);
        return ResponseEntity.ok(updatedVisitor);
    }

    @PatchMapping("/{id}/favorites/{artworkId}/remove")
    public ResponseEntity<VisitorResponseDto> removeFavorites(@PathVariable long id, @PathVariable long artworkId) {
        VisitorResponseDto updatedVisitor = visitorService.removeFavorites(id, artworkId);
        return ResponseEntity.ok(updatedVisitor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVisitor(@PathVariable long id) {
        String message = visitorService.deleteVisitor(id);
        return ResponseEntity.ok(message);
    }
}




