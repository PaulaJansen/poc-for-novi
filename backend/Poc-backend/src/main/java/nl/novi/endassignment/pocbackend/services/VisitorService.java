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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final VisitorMapper visitorMapper;
    private final RoleRepository roleRepository;
    private final ArtworkRepository artworkRepository;
    private final ArtworkMapper artworkMapper;
    private final PasswordEncoder passwordEncoder;
    private final Path uploadDirectory;

    public VisitorService(VisitorRepository visitorRepository, VisitorMapper visitorMapper, RoleRepository roleRepository, ArtworkRepository artworkRepository, ArtworkMapper artworkMapper, PasswordEncoder passwordEncoder, Path uploadDirectory) {
        this.visitorRepository = visitorRepository;
        this.visitorMapper = visitorMapper;
        this.roleRepository = roleRepository;
        this.artworkRepository = artworkRepository;
        this.artworkMapper = artworkMapper;
        this.passwordEncoder = passwordEncoder;
        this.uploadDirectory = uploadDirectory;
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
    public VisitorResponseDto createVisitor(VisitorInputDto visitorInputDto) throws IOException {

        if (visitorInputDto.getPassword() == null || visitorInputDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Wachtwoord mag niet leeg zijn!");
        }

        Visitor visitor = visitorMapper.toEntity(visitorInputDto);

        visitor.setPassword(passwordEncoder.encode(visitorInputDto.getPassword()));

        if (visitor.getRoles() == null || visitor.getRoles().isEmpty()) {
            roleRepository.findByRoleName(RoleType.VISITOR)
                    .ifPresent(role -> {
                        if (visitor.getRoles() == null) {
                            visitor.setRoles(new ArrayList<>());
                        }
                        visitor.getRoles().add(role);
                    });
        }

        if (visitorInputDto.getProfilePictureFile() != null && !visitorInputDto.getProfilePictureFile().isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + visitorInputDto.getProfilePictureFile().getOriginalFilename();

            Path path = uploadDirectory.resolve(fileName);

            visitorInputDto.getProfilePictureFile().transferTo(path.toFile());

            visitor.setProfilePicture(fileName);
        }

        Visitor savedVisitor = visitorRepository.save(visitor);
        VisitorResponseDto visitorResponseDto = visitorMapper.toDto(savedVisitor);
        if (savedVisitor.getProfilePicture() != null) {
            visitorResponseDto.setProfilePicture("/uploads/" + savedVisitor.getProfilePicture());
        }

        return visitorResponseDto;
    }

    @PreAuthorize("@visitorSecurity.isOwner(#id)")
    @Transactional
    public VisitorResponseDto updateVisitor(long id, VisitorInputDto visitorInputDto) {
        Visitor existingVisitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Bezoeker met id " + id + " niet gevonden!"));

        if (visitorInputDto.getName() != null) existingVisitor.setName(visitorInputDto.getName());
        if (visitorInputDto.getEmail() != null) existingVisitor.setEmail(visitorInputDto.getEmail());
        if (visitorInputDto.getUsername() != null) existingVisitor.setUsername(visitorInputDto.getUsername());

        if (visitorInputDto.getProfilePicture() != null) {
            String oldPicture = existingVisitor.getProfilePicture();
            if (oldPicture != null) {
                Path oldFilePath = uploadDirectory.resolve(oldPicture);
                try {
                    Files.deleteIfExists(oldFilePath);
                } catch (IOException e) {
                        System.out.println("Kon oude profielfoto niet verwijderen.");
                        e.printStackTrace();
                }
            }
        }
        existingVisitor.setProfilePicture(visitorInputDto.getProfilePicture());

        return visitorMapper.toDto(visitorRepository.save(existingVisitor));
    }

    public List<ArtworkResponseDto> getFavorites(long id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Bezoeker met id " + id + " niet gevonden!"));

        return artworkMapper.toDtoList(visitor.getFavorites());

    }

    @PreAuthorize("@visitorSecurity.isOwner(#id)")
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

    @PreAuthorize("@visitorSecurity.isOwner(#id)")
    @Transactional
    public VisitorResponseDto removeFavorites(long id, long artworkId) {
        Visitor existingVisitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Bezoeker met id " + id + " niet gevonden!"));

        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RecordNotFoundException("Kunstwerk met id " + artworkId + " niet gevonden!"));

        existingVisitor.getFavorites().remove(artwork);

        return visitorMapper.toDto(visitorRepository.save(existingVisitor));
    }

    @PreAuthorize("@visitorSecurity.isOwner(#id)")
    @Transactional
    public String deleteVisitor(long id) {
        Visitor existingVisitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Bezoeker met id " + id + " niet gevonden!"));

        visitorRepository.delete(existingVisitor);
        return ("Bezoeker met id " + id + " is verwijderd.");
    }
}

