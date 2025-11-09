package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.UserResponseDto;
import nl.novi.endassignment.pocbackend.models.Artist;
import nl.novi.endassignment.pocbackend.models.User;
import nl.novi.endassignment.pocbackend.models.Visitor;

import java.util.List;

public class UserMapper {

    public static UserResponseDto toDto (User user) {
        if (user instanceof Artist artist) {
            return ArtistMapper.toDto(artist);
        } else if (user instanceof Visitor visitor) {
            return VisitorMapper.toDto(visitor);
        }

        // fallback
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setProfilePicture(user.getProfilePicture());
        userResponseDto.setDateOfRegistration(user.getDateOfRegistration());

        List<String> roleNames = user.getRoles()
                .stream()
                .map(role -> role.getRoleType().name())
                .toList();
        userResponseDto.setRoleName(roleNames);

        return userResponseDto;
    }
}
