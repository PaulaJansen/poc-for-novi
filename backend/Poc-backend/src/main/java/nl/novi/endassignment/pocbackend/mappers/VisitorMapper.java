package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.VisitorInputDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorResponseDto;
import nl.novi.endassignment.pocbackend.models.Visitor;
import org.springframework.stereotype.Component;

@Component
public class VisitorMapper {

    private final UserMapperInherited userMapperInherited;
    private final ArtworkMapper artworkMapper;

    public VisitorMapper(UserMapperInherited userMapperInherited, ArtworkMapper artworkMapper) {
        this.userMapperInherited = userMapperInherited;
        this.artworkMapper = artworkMapper;
    }

    public VisitorResponseDto toDto(Visitor visitor) {

        VisitorResponseDto visitorResponseDto = new VisitorResponseDto();

        userMapperInherited.mapUserFieldsToDto(visitor, visitorResponseDto);

        visitorResponseDto.setName(visitor.getName());
        visitorResponseDto.setFavoritesTitles(
                visitor.getFavorites()
                        .stream()
                        .map(artworkMapper::toTitle)
                        .toList()
        );

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
}
