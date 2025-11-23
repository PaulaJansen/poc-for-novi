package nl.novi.endassignment.pocbackend.services;

import jakarta.transaction.Transactional;
import nl.novi.endassignment.pocbackend.dtos.ArtworkResponseDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorInputDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorResponseDto;
import nl.novi.endassignment.pocbackend.exceptions.RecordNotFoundException;
import nl.novi.endassignment.pocbackend.mappers.ArtworkMapper;
import nl.novi.endassignment.pocbackend.mappers.VisitorMapper;
import nl.novi.endassignment.pocbackend.models.Artwork;
import nl.novi.endassignment.pocbackend.models.Visitor;
import nl.novi.endassignment.pocbackend.repositories.ArtworkRepository;
import nl.novi.endassignment.pocbackend.repositories.RoleRepository;
import nl.novi.endassignment.pocbackend.repositories.VisitorRepository;
import nl.novi.endassignment.pocbackend.models.RoleType;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final VisitorMapper visitorMapper;
    private final RoleRepository roleRepository;
    private final ArtworkRepository artworkRepository;
    private final ArtworkMapper artworkMapper;

    public VisitorService(VisitorRepository visitorRepository, VisitorMapper visitorMapper, RoleRepository roleRepository, ArtworkRepository artworkRepository, ArtworkMapper artworkMapper) {
        this.visitorRepository = visitorRepository;
        this.visitorMapper = visitorMapper;
        this.roleRepository = roleRepository;
        this.artworkRepository = artworkRepository;
        this.artworkMapper = artworkMapper;
    }

    public List<VisitorResponseDto> getAllVisitors() {
        return visitorMapper.toDtoList(visitorRepository.findAll());
    }

    public VisitorResponseDto getVisitorById(long id) {
        return visitorMapper.toDto(visitorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Bezoeker met id " + id + " niet gevonden!")));
    }

    public List<VisitorResponseDto> getVisitorByName(String name) {
        return visitorMapper.toDtoList(visitorRepository.findByName(name));
    }

    @Transactional
    public VisitorResponseDto createVisitor(VisitorInputDto visitorInputDto) {
        Visitor visitor = visitorMapper.toEntity(visitorInputDto);

        if (visitor.getRoles() == null || visitor.getRoles().isEmpty()) {
            roleRepository.findByName(RoleType.VISITOR)
                    .ifPresent(role -> visitor.getRoles().add(role));
        }

        return visitorMapper.toDto(visitorRepository.save(visitor));
    }

    @Transactional
    public VisitorResponseDto updateVisitor(long id, VisitorInputDto visitorInputDto) {
        Visitor existingVisitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Bezoeker met id " + id + " niet gevonden!"));

        existingVisitor.setName(visitorInputDto.getName());
        existingVisitor.setEmail(visitorInputDto.getEmail());
        existingVisitor.setUsername(visitorInputDto.getUsername());
        existingVisitor.setProfilePicture(visitorInputDto.getProfilePicture());

        return visitorMapper.toDto(visitorRepository.save(existingVisitor));
    }

    @Transactional
    public VisitorResponseDto patchVisitor(long id, VisitorInputDto visitorInputDto) {
        Visitor existingVisitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Bezoeker met id " + id + " niet gevonden!"));

        if (visitorInputDto.getName() != null) existingVisitor.setName(visitorInputDto.getName());
        if (visitorInputDto.getEmail() != null) existingVisitor.setEmail(visitorInputDto.getEmail());
        if (visitorInputDto.getUsername() != null) existingVisitor.setUsername(visitorInputDto.getUsername());
        if (visitorInputDto.getProfilePicture() != null)
            existingVisitor.setProfilePicture(visitorInputDto.getProfilePicture());

        return visitorMapper.toDto(visitorRepository.save(existingVisitor));
    }

    public List<ArtworkResponseDto> getFavorites(long id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Bezoeker met id " + id + " niet gevonden!"));

        return artworkMapper.toDtoList(visitor.getFavorites());

    }

    @Transactional
    public VisitorResponseDto addFavorites(long id, long artworkId) {
        Visitor existingVisitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Bezoeker met id " + id + " niet gevonden!"));

        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RecordNotFoundException("Kunstwerk met id " + artworkId + " niet gevonden!"));

        if (!existingVisitor.getFavorites().contains(artwork)) {
            existingVisitor.getFavorites().add(artwork);
        }

        return visitorMapper.toDto(visitorRepository.save(existingVisitor));
    }

    @Transactional
    public VisitorResponseDto removeFavorites(long id, long artworkId) {
        Visitor existingVisitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Bezoeker met id " + id + " niet gevonden!"));

        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RecordNotFoundException("Kunstwerk met id " + artworkId + " niet gevonden!"));

        existingVisitor.getFavorites().remove(artwork);

        return visitorMapper.toDto(visitorRepository.save(existingVisitor));
    }

    @Transactional
    public String deleteVisitor(long id, String currentUsername) throws AccessDeniedException {
        Visitor existingVisitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Bezoeker met id " + id + " niet gevonden!"));

        if (!existingVisitor.getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("Je mag alleen je eigen account verwijderen");
        }

        visitorRepository.delete(existingVisitor);
        return ("Bezoeker met id " + id + " is verwijderd.");
    }
}

