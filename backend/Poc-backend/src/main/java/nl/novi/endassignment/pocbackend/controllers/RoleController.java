package nl.novi.endassignment.pocbackend.controllers;

import nl.novi.endassignment.pocbackend.dtos.RoleResponseDto;
import nl.novi.endassignment.pocbackend.models.RoleType;
import nl.novi.endassignment.pocbackend.services.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        List<RoleResponseDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{roleName}")
    public ResponseEntity<RoleResponseDto> getRoleByName(@PathVariable RoleType roleName) {
        RoleResponseDto role = roleService.getRoleByName(roleName);
        return ResponseEntity.ok(role);
    }
}
