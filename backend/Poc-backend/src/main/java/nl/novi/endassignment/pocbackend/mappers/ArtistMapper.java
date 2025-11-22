package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.ArtistInputDto;
import nl.novi.endassignment.pocbackend.dtos.ArtistResponseDto;
import nl.novi.endassignment.pocbackend.dtos.ArtworkResponseDto;
import nl.novi.endassignment.pocbackend.models.Artist;
import nl.novi.endassignment.pocbackend.models.Artwork;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArtistMapper {

    private final UserMapperInherited userMapperInherited;
    private final ArtworkMapper artworkMapper;

    public ArtistMapper(UserMapperInherited userMapperInherited, ArtworkMapper artworkMapper) {
        this.userMapperInherited = userMapperInherited;
        this.artworkMapper = artworkMapper;
    }

    public ArtistResponseDto toDto(Artist artist) {

        ArtistResponseDto artistResponseDto = new ArtistResponseDto();
        artistResponseDto = (ArtistResponseDto) userMapperInherited.mapUserFieldsToDto(artist, artistResponseDto);

        artistResponseDto.setFirstName(artist.getFirstName());
        artistResponseDto.setLastName(artist.getLastName());
        artistResponseDto.setCity(artist.getCity());
        artistResponseDto.setTypeOfArt(artist.getTypeOfArt());
        artistResponseDto.setBiography(artist.getBiography());
        artistResponseDto.setPortfolioTitles(
                artist.getPortfolio()
                        .stream()
                        .map(artworkMapper::toTitle)
                        .toList()
        );

        return artistResponseDto;
    }

    public Artist toEntity(ArtistInputDto artistInputDto) {

        Artist artist = new Artist();

        userMapperInherited.mapUserFieldsToEntity(artist, artistInputDto);

        artist.setFirstName(artistInputDto.getFirstName());
        artist.setLastName(artistInputDto.getLastName());
        artist.setCity(artistInputDto.getCity());
        artist.setTypeOfArt(artistInputDto.getTypeOfArt());

        return artist;
    }

    public List<ArtistResponseDto> toDtoList(List<Artist> artists) {
        return artists.stream().map(this::toDto).toList();
    }
}
