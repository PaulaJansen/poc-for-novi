package nl.novi.endassignment.pocbackend.services;

import jakarta.transaction.Transactional;
import nl.novi.endassignment.pocbackend.dtos.ArtistInputDto;
import nl.novi.endassignment.pocbackend.dtos.ArtistResponseDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorInputDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorResponseDto;
import nl.novi.endassignment.pocbackend.exceptions.RecordNotFoundException;
import nl.novi.endassignment.pocbackend.mappers.ArtistMapper;
import nl.novi.endassignment.pocbackend.models.Artist;
import nl.novi.endassignment.pocbackend.models.RoleType;
import nl.novi.endassignment.pocbackend.models.Visitor;
import nl.novi.endassignment.pocbackend.repositories.ArtistRepository;
import nl.novi.endassignment.pocbackend.repositories.ArtworkRepository;
import nl.novi.endassignment.pocbackend.repositories.RoleRepository;

import java.util.List;

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

    public ArtistResponseDto getArtistByName(String name) {
        return artistMapper.toDto(artistRepository.findByName(name)
                .orElseThrow(() -> new RecordNotFoundException("Kunstenaar " + name + " niet gevonden!")));
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

    @Transactional
    public String deleteArtist(long id) {
        Artist existingArtist = artistRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Kunstenaar met id " + id + " niet gevonden!"));
        artistRepository.delete(existingArtist);
        return ("Kunstenaar met id " + id + " is verwijderd.");
    }
}
