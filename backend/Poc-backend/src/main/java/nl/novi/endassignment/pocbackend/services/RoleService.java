package nl.novi.endassignment.pocbackend.services;

import nl.novi.endassignment.pocbackend.dtos.RoleResponseDto;
import nl.novi.endassignment.pocbackend.exceptions.RecordNotFoundException;
import nl.novi.endassignment.pocbackend.mappers.RoleMapper;
import nl.novi.endassignment.pocbackend.models.RoleType;
import nl.novi.endassignment.pocbackend.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public List<RoleResponseDto> getAllRoles() {
        return roleMapper.toDtoList(roleRepository.findAll());
    }

    public RoleResponseDto getRoleByName(RoleType roleName) {
        return roleMapper.toDto(roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RecordNotFoundException("Rol met naam " + roleName + " niet gevonden!")));
    }
}
