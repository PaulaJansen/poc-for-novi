package nl.novi.endassignment.pocbackend.services;

import jakarta.transaction.Transactional;
import nl.novi.endassignment.pocbackend.dtos.ArtistInputDto;
import nl.novi.endassignment.pocbackend.dtos.ArtistResponseDto;
import nl.novi.endassignment.pocbackend.exceptions.RecordNotFoundException;
import nl.novi.endassignment.pocbackend.mappers.ArtistMapper;
import nl.novi.endassignment.pocbackend.models.Artist;
import nl.novi.endassignment.pocbackend.models.RoleType;
import nl.novi.endassignment.pocbackend.repositories.ArtistRepository;
import nl.novi.endassignment.pocbackend.repositories.RoleRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Path uploadDirectory;
    private final FileStorageService fileStorageService;

    public ArtistService(ArtistRepository artistRepository, ArtistMapper artistMapper, RoleRepository roleRepository, PasswordEncoder passwordEncoder, Path uploadDirectory, FileStorageService fileStorageService) {
        this.artistRepository = artistRepository;
        this.artistMapper = artistMapper;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.uploadDirectory = uploadDirectory;
        this.fileStorageService = fileStorageService;
    }

    public List<ArtistResponseDto> getAllArtists() {
        return artistMapper.toDtoList(artistRepository.findAll());
    }

    public ArtistResponseDto getArtistById(long id) {
        return artistMapper.toDto(artistRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstenaar met id " + id + " niet gevonden!")));
    }

    public List<ArtistResponseDto> getArtistByName(String firstName, String lastName) {
        return artistMapper.toDtoList(artistRepository.findByFirstNameAndLastName(firstName, lastName));
    }

    @Transactional
    public ArtistResponseDto createArtist(ArtistInputDto artistInputDto) throws IOException {

        if (artistInputDto.getPassword() == null || artistInputDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Wachtwoord mag niet leeg zijn!");
        }

        Artist artist = artistMapper.toEntity(artistInputDto);

        artist.setPassword(passwordEncoder.encode(artistInputDto.getPassword()));


        if (artist.getRoles() == null || artist.getRoles().isEmpty()) {
            roleRepository.findByRoleName(RoleType.ARTIST)
                    .ifPresent(role -> {
                        if (artist.getRoles() == null) {
                            artist.setRoles(new ArrayList<>());
                        }
                        artist.getRoles().add(role);
                    });
        }

        if (artistInputDto.getProfilePictureFile() != null && !artistInputDto.getProfilePictureFile().isEmpty()) {
            String relativePath = fileStorageService.saveFile(artistInputDto.getProfilePictureFile(), "profile");
            artist.setProfilePicture(relativePath);
        }

        Artist savedArtist = artistRepository.save(artist);
        ArtistResponseDto artistResponseDto = artistMapper.toDto(savedArtist);
        if (savedArtist.getProfilePicture() != null) {
            artistResponseDto.setProfilePicture("/uploads/" + savedArtist.getProfilePicture());
        }

        return artistResponseDto;
    }

    @PreAuthorize("@artistSecurity.isOwner(#id)")
    @Transactional
    public ArtistResponseDto updateArtistInfo(long id, ArtistInputDto artistInputDto) throws IOException {
        Artist existingArtist = artistRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstenaar met id " + id + " niet gevonden!"));

        if (artistInputDto.getFirstName() != null) existingArtist.setFirstName(artistInputDto.getFirstName());
        if (artistInputDto.getLastName() != null) existingArtist.setLastName(artistInputDto.getLastName());
        if (artistInputDto.getEmail() != null) existingArtist.setEmail(artistInputDto.getEmail());
        if (artistInputDto.getUsername() != null) existingArtist.setUsername(artistInputDto.getUsername());
        if (artistInputDto.getCity() != null) existingArtist.setCity(artistInputDto.getCity());
        if (artistInputDto.getTypeOfArt() != null) existingArtist.setTypeOfArt(artistInputDto.getTypeOfArt());
        if (artistInputDto.getBiography() != null) existingArtist.setBiography(artistInputDto.getBiography());

        Artist savedArtist = artistRepository.save(existingArtist);

        return artistMapper.toDto(savedArtist);
    }

    @PreAuthorize("@artistSecurity.isOwner(#id)")
    @Transactional
    public ArtistResponseDto updateArtistProfilePicture(long id, MultipartFile profilePicture) throws IOException {
       Artist existingArtist = artistRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstenaar met id " + id + " niet gevonden!"));

        if (existingArtist.getProfilePicture() != null) {
            fileStorageService.deleteFile(existingArtist.getProfilePicture());
        }

        String relativePath = fileStorageService.saveFile(profilePicture, "profile");
        existingArtist.setProfilePicture(relativePath);

        Artist savedArtist = artistRepository.save(existingArtist);
        ArtistResponseDto artistResponseDto = artistMapper.toDto(savedArtist);
        if (savedArtist.getProfilePicture() != null) {
            artistResponseDto.setProfilePicture("/uploads/" + savedArtist.getProfilePicture());
        }

        return artistResponseDto;
    }

    @PreAuthorize("@artistSecurity.isOwner(#id)")
    @Transactional
    public String deleteArtist(long id) {
        Artist existingArtist = artistRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstenaar met id " + id + " niet gevonden!"));
        artistRepository.delete(existingArtist);
        return ("Kunstenaar met id " + id + " is verwijderd.");
    }
}
