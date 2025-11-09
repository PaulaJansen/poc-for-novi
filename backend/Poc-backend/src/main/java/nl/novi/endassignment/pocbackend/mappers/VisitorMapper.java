package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.VisitorInputDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorResponseDto;
import nl.novi.endassignment.pocbackend.models.Artwork;
import nl.novi.endassignment.pocbackend.models.Visitor;

import java.util.List;

public class VisitorMapper {

    public static VisitorResponseDto toDto(Visitor visitor) {

        VisitorResponseDto visitorResponseDto = new VisitorResponseDto();

        UserMapperInherited.mapUserFieldsToDto(visitor, visitorResponseDto);

        List<String> favoritesTitles = visitor.getFavorites()
                .stream()
                .map(Artwork::getTitle)
                .toList();
        visitorResponseDto.setFavoritesTitles(favoritesTitles);

        visitorResponseDto.setName(visitor.getName());

        return visitorResponseDto;
    }

    public static Visitor toEntity(VisitorInputDto visitorInputDto) {

        Visitor visitor = new Visitor();

        UserMapperInherited.mapUserFieldsToEntity(visitor, visitorInputDto);
        visitor.setName(visitorInputDto.getName());

        //hier moet denk iets met de mapper van de artwork, de joy
        if (visitorInputDto.getFavoritesTitles() != null) {
            visitor.setFavorites();
        }

        return visitor;
    }
}
