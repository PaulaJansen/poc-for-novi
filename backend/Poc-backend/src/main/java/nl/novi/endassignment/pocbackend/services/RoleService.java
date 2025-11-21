package nl.novi.endassignment.pocbackend.services;

import nl.novi.endassignment.pocbackend.exceptions.RecordNotFoundException;
import nl.novi.endassignment.pocbackend.models.Role;
import nl.novi.endassignment.pocbackend.models.RoleType;
import nl.novi.endassignment.pocbackend.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleByName(RoleType roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RecordNotFoundException("Rol met naam " + roleName + " niet gevonden!"));
    }
}
