package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.VisitorInputDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorResponseDto;
import nl.novi.endassignment.pocbackend.models.Artwork;
import nl.novi.endassignment.pocbackend.models.Visitor;
import org.springframework.stereotype.Component;

import java.util.List;

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
        visitorResponseDto = (VisitorResponseDto) userMapperInherited.mapUserFieldsToDto(visitor, visitorResponseDto);

        visitorResponseDto.setName(visitor.getName());

        if (visitor.getFavorites() != null) {
            visitorResponseDto.setFavoritesIds(
                    visitor.getFavorites()
                            .stream()
                            .map(Artwork::getId)
                            .toList()
            );
        }

        return visitorResponseDto;
    }

    public Visitor toEntity(VisitorInputDto visitorInputDto) {

        Visitor visitor = new Visitor();

        userMapperInherited.mapUserFieldsToEntity(visitor, visitorInputDto);
        visitor.setName(visitorInputDto.getName());

        return visitor;
    }

    public List<VisitorResponseDto> toDtoList(List<Visitor> visitors) {
        return visitors.stream().map(this::toDto).toList();
    }
}
