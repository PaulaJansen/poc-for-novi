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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;
    private final RoleRepository roleRepository;

    public ArtistService(ArtistRepository artistRepository, ArtistMapper artistMapper, RoleRepository roleRepository) {
        this.artistRepository = artistRepository;
        this.artistMapper = artistMapper;
        this.roleRepository = roleRepository;
    }

    public List<ArtistResponseDto> getAllArtists() {
        return artistMapper.toDtoList(artistRepository.findAll());
    }

    public ArtistResponseDto getArtistById(long id) {
        return artistMapper.toDto(artistRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstenaar met id " + id + " niet gevonden!")));
    }

    public List<ArtistResponseDto> getArtistByName(String name) {
        return artistMapper.toDtoList(artistRepository.findByName(name));
    }

    @Transactional
    public ArtistResponseDto createArtist(ArtistInputDto artistInputDto) {
        Artist artist = artistMapper.toEntity(artistInputDto);

        if (artist.getRoles() == null || artist.getRoles().isEmpty()) {
            roleRepository.findByName(RoleType.ARTIST)
                    .ifPresent(role -> artist.getRoles().add(role));
        }

        return artistMapper.toDto(artistRepository.save(artist));
    }

    @PreAuthorize("@artistSecurity.isOwner(#id)")
    @Transactional
    public ArtistResponseDto updateArtist(long id, ArtistInputDto artistInputDto) {
        Artist existingArtist = artistRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstenaar met id " + id + " niet gevonden!"));

        existingArtist.setFirstName(artistInputDto.getFirstName());
        existingArtist.setLastName(artistInputDto.getLastName());
        existingArtist.setEmail(artistInputDto.getEmail());
        existingArtist.setUsername(artistInputDto.getUsername());
        existingArtist.setProfilePicture(artistInputDto.getProfilePicture());
        existingArtist.setCity(artistInputDto.getCity());
        existingArtist.setTypeOfArt(artistInputDto.getTypeOfArt());
        existingArtist.setBiography(artistInputDto.getBiography());

        return artistMapper.toDto(artistRepository.save(existingArtist));
    }

    @PreAuthorize("@artistSecurity.isOwner(#id)")
    @Transactional
    public ArtistResponseDto patchArtist(long id, ArtistInputDto artistInputDto) {
        Artist existingArtist = artistRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstenaar met id " + id + " niet gevonden!"));

        if (artistInputDto.getFirstName() != null) existingArtist.setFirstName(artistInputDto.getFirstName());
        if (artistInputDto.getLastName() != null) existingArtist.setLastName(artistInputDto.getLastName());
        if (artistInputDto.getEmail() != null) existingArtist.setEmail(artistInputDto.getEmail());
        if (artistInputDto.getUsername() != null) existingArtist.setUsername(artistInputDto.getUsername());
        if (artistInputDto.getProfilePicture() != null) existingArtist.setProfilePicture(artistInputDto.getProfilePicture());
        if (artistInputDto.getCity() != null) existingArtist.setCity(artistInputDto.getCity());
        if (artistInputDto.getTypeOfArt() != null) existingArtist.setTypeOfArt(artistInputDto.getTypeOfArt());
        if (artistInputDto.getBiography() != null) existingArtist.setBiography(artistInputDto.getBiography());

        return artistMapper.toDto(artistRepository.save(existingArtist));
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
