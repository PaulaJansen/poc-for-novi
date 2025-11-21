package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.ArtistResponseDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorInputDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorResponseDto;
import nl.novi.endassignment.pocbackend.models.Artist;
import nl.novi.endassignment.pocbackend.models.Visitor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VisitorMapper {

    private final UserMapperInherited userMapperInherited;
    private final ArtworkMapper artworkMapper;
    private final VisitorMapper visitorMapper;

    public VisitorMapper(UserMapperInherited userMapperInherited, ArtworkMapper artworkMapper, VisitorMapper visitorMapper) {
        this.userMapperInherited = userMapperInherited;
        this.artworkMapper = artworkMapper;
        this.visitorMapper = visitorMapper;
    }

    public VisitorResponseDto toDto(Visitor visitor) {

        VisitorResponseDto visitorResponseDto = new VisitorResponseDto();
        visitorResponseDto = (VisitorResponseDto) userMapperInherited.mapUserFieldsToDto(visitor, visitorResponseDto);

        visitorResponseDto.setName(visitor.getName());

        if (visitor.getFavorites() != null) {
            visitorResponseDto.setFavoritesTitles(
                    visitor.getFavorites()
                            .stream()
                            .map(artworkMapper::toTitle)
                            .toList()
            );
        }

        return visitorResponseDto;
    }

    public Visitor toEntity(VisitorInputDto visitorInputDto) {

        Visitor visitor = new Visitor();

        userMapperInherited.mapUserFieldsToEntity(visitor, visitorInputDto);

        visitor.setName(visitorInputDto.getName());

        if (visitorInputDto.getFavoritesTitles() != null) {
            visitor.setFavorites(
                    visitorInputDto.getFavoritesTitles()
                            .stream()
                            .map(artworkMapper::fromTitle)
                            .toList()
            );
        }

        return visitor;
    }

    public List<VisitorResponseDto> toDtoList(List<Visitor> visitors) {
        return visitors.stream().map(visitorMapper::toDto).toList();
    }
}
