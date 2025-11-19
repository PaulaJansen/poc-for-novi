package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.UserInputDto;
import nl.novi.endassignment.pocbackend.dtos.UserResponseDto;
import nl.novi.endassignment.pocbackend.models.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapperInherited {

    public UserResponseDto mapUserFieldsToDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setId(user.getId());
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setDateOfRegistration(user.getDateOfRegistration());
        userResponseDto.setProfilePicture(user.getProfilePicture());

        List<String> roleTitles = user.getRoles()
                .stream()
                .map(role -> role.getRoleName().name())
                .toList();
        userResponseDto.setRoleNames(roleTitles);

        return userResponseDto;
    }

    public void mapUserFieldsToEntity(User user, UserInputDto userInputDto) {
        user.setUsername(userInputDto.getUsername());
        user.setEmail(userInputDto.getEmail());
        user.setProfilePicture(userInputDto.getProfilePicture());
    }

    public List<UserResponseDto> toDtoList(List<User> users) {
        return users.stream()
                .map(this::mapUserFieldsToDto)
                .toList();
    }
}
