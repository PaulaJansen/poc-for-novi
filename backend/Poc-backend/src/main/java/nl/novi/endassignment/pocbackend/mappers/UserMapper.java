package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.UserResponseDto;
import nl.novi.endassignment.pocbackend.models.Artist;
import nl.novi.endassignment.pocbackend.models.User;
import nl.novi.endassignment.pocbackend.models.Visitor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    private final VisitorMapper visitorMapper;
    private final ArtistMapper artistMapper;

    public UserMapper(VisitorMapper visitorMapper, ArtistMapper artistMapper) {
        this.visitorMapper = visitorMapper;
        this.artistMapper = artistMapper;
    }

    public UserResponseDto toDto (User user) {
        if (user instanceof Artist artist) {
            return artistMapper.toDto(artist);
        } else if (user instanceof Visitor visitor) {
            return visitorMapper.toDto(visitor);
        } else {

            // fallback
            UserResponseDto userResponseDto = new UserResponseDto();
            userResponseDto.setId(user.getId());
            userResponseDto.setUsername(user.getUsername());
            userResponseDto.setEmail(user.getEmail());
            userResponseDto.setProfilePicture(user.getProfilePicture());
            userResponseDto.setDateOfRegistration(user.getDateOfRegistration());

            List<String> roleNames = user.getRoles()
                    .stream()
                    .map(role -> role.getRoleName().name())
                    .toList();
            userResponseDto.setRoleNames(roleNames);

            return userResponseDto;
        }
    }
}
