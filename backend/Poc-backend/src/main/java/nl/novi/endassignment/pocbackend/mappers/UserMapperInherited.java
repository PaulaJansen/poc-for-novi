package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.UserInputDto;
import nl.novi.endassignment.pocbackend.dtos.UserResponseDto;
import nl.novi.endassignment.pocbackend.models.Role;
import nl.novi.endassignment.pocbackend.models.RoleType;
import nl.novi.endassignment.pocbackend.models.User;
import nl.novi.endassignment.pocbackend.repositories.RoleRepository;

import java.util.List;

public class UserMapperInherited {

    public static void mapUserFieldsToDto(User user, UserResponseDto userResponseDto) {
        userResponseDto.setId(user.getId());
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setDateOfRegistration(user.getDateOfRegistration());
        userResponseDto.setProfilePicture(user.getProfilePicture());

        List<String> roleTitles = user.getRoles()
                .stream()
                .map(role -> role.getRoleType().name())
                .toList();
        userResponseDto.setRoleNames(roleTitles);
    }

    public static void mapUserFieldsToEntity(User user, UserInputDto userInputDto, RoleRepository roleRepository) {
        user.setUsername(userInputDto.getUsername());
        user.setEmail(userInputDto.getEmail());
        user.setProfilePicture(userInputDto.getProfilePicture());

        List<Role> roles = userInputDto.getRoles()
                .stream()
                .map(String::toUpperCase)
                .map(RoleType::valueOf)
                .map(roleType -> roleRepository.findByRoleType(roleType)
                        .orElseThrow(() -> new RuntimeException("Rol niet gevonden: " + roleType)))
                .toList();
        user.setRoles(roles);
    }
}
