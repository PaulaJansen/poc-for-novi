package nl.novi.endassignment.pocbackend.controllers;

import nl.novi.endassignment.pocbackend.dtos.UserResponseDto;
import nl.novi.endassignment.pocbackend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDto>> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email
    ) {
        if (username != null) {
            return ResponseEntity.ok(
                    List.of(userService.getUserByUsername(username))
            );
        }

        if (email != null) {
            return ResponseEntity.ok(
                    List.of(userService.getUserByEmail(email))
            );
        }

        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        String message = userService.deleteUser(id);
        return ResponseEntity.ok(message);
    }
}
