package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.ArtistInputDto;
import nl.novi.endassignment.pocbackend.dtos.ArtistResponseDto;
import nl.novi.endassignment.pocbackend.models.Artist;
import nl.novi.endassignment.pocbackend.models.Artwork;

import java.util.List;

public class ArtistMapper {

    public static ArtistResponseDto toDto(Artist artist) {

        ArtistResponseDto artistResponseDto = new ArtistResponseDto();

        UserMapperInherited.mapUserFieldsToDto(artist, artistResponseDto);

        List<String> portfolioTitles = artist.getPortfolio()
                .stream()
                .map(Artwork::getTitle)
                .toList();
        artistResponseDto.setPortfolioTitles(portfolioTitles);

        artistResponseDto.setFirstName(artist.getFirstName());
        artistResponseDto.setLastName(artist.getLastName());
        artistResponseDto.setCity(artist.getCity());
        artistResponseDto.setTypeOfArt(artist.getTypeOfArt());
        artistResponseDto.setBiography(artist.getBiography());

        return artistResponseDto;
    }

    public static Artist toEntity(ArtistInputDto artistInputDto) {

        Artist artist = new Artist();

        UserMapperInherited.mapUserFieldsToEntity(artist, artistInputDto);

        artist.setFirstName(artistInputDto.getFirstName());
        artist.setLastName(artistInputDto.getLastName());
        artist.setCity(artistInputDto.getCity());
        artist.setTypeOfArt(artistInputDto.getTypeOfArt());

        //hier moet denk iets met de mapper van de artwork, de joy
        if (artistInputDto.getPortfolioTitles() != null) {
            artist.setPortfolio();
        }

        return artist;
    }
}
