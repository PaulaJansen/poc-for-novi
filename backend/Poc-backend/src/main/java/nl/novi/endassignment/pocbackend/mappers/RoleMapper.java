package nl.novi.endassignment.pocbackend.mappers;

import nl.novi.endassignment.pocbackend.dtos.RoleResponseDto;
import nl.novi.endassignment.pocbackend.models.Role;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleMapper {

    public RoleResponseDto toDto(Role role) {
        RoleResponseDto roleResponseDto = new RoleResponseDto();
        roleResponseDto.setRoleName(role.getRoleName().name());

        return roleResponseDto;
    }

    public List<RoleResponseDto> toDtoList(List<Role> roles) {
        return roles.stream()
                .map(this::toDto)
                .toList();
    }
}
