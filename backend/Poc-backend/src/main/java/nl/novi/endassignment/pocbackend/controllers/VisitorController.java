package nl.novi.endassignment.pocbackend.controllers;

import jakarta.validation.Valid;
import nl.novi.endassignment.pocbackend.dtos.ArtworkResponseDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorInputDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorResponseDto;
import nl.novi.endassignment.pocbackend.services.VisitorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
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

    @GetMapping
    public ResponseEntity<List<VisitorResponseDto>> getVisitorByName(@RequestParam String name) {
        List<VisitorResponseDto> visitors = visitorService.getVisitorByName(name);
        return ResponseEntity.ok(visitors);
    }

    @PostMapping
    public ResponseEntity<VisitorResponseDto> createVisitor(@Valid @RequestBody VisitorInputDto visitorInputDto) {
        VisitorResponseDto newVisitor = visitorService.createVisitor(visitorInputDto);
        return new ResponseEntity<>(newVisitor, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VisitorResponseDto> updateVisitor(@Valid @PathVariable long id, @RequestBody VisitorInputDto visitorInputDto) {
        VisitorResponseDto updatedVisitor = visitorService.updateVisitor(id, visitorInputDto);
        return ResponseEntity.ok(updatedVisitor);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VisitorResponseDto> patchVisitor(@PathVariable long id, @RequestBody VisitorInputDto visitorInputDto) {
        VisitorResponseDto updatedVisitor = visitorService.patchVisitor(id, visitorInputDto);
        return ResponseEntity.ok(updatedVisitor);
    }

    @GetMapping("/{id}/favorites")
    public ResponseEntity<List<ArtworkResponseDto>> getFavorites(@PathVariable long id) {
        List<ArtworkResponseDto> favorites = visitorService.getFavorites(id);
        return ResponseEntity.ok(favorites);
    }

    @PatchMapping("/{id}/favorites/{artworkId}")
    public ResponseEntity<VisitorResponseDto> addFavorites(@PathVariable long id, @PathVariable long artworkId) {
        VisitorResponseDto updatedVisitor = visitorService.addFavorites(id, artworkId);
        return ResponseEntity.ok(updatedVisitor);
    }

    @PatchMapping("/{id}/favorites/{artworkId}")
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




