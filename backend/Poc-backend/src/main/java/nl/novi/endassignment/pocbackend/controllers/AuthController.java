package nl.novi.endassignment.pocbackend.controllers;

import nl.novi.endassignment.pocbackend.dtos.AuthDto;
import nl.novi.endassignment.pocbackend.models.User;
import nl.novi.endassignment.pocbackend.repositories.UserRepository;
import nl.novi.endassignment.pocbackend.security.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager man, JwtService service, UserRepository userRepository) {
        this.authManager = man;
        this.jwtService = service;
        this.userRepository = userRepository;
    }

    @PostMapping("/auth")
    public ResponseEntity<Object> signIn(@RequestBody AuthDto authDto) {
        UsernamePasswordAuthenticationToken up =
                new UsernamePasswordAuthenticationToken(authDto.getUsername(), authDto.getPassword());

        try {
            Authentication auth = authManager.authenticate(up);

            UserDetails ud = (UserDetails) auth.getPrincipal();
            User user = userRepository.findByUsername(authDto.getUsername()).orElseThrow();
            String token = jwtService.generateToken(ud, user.getId());

            return ResponseEntity.ok(
                    Map.of("token", token)
                    );
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                    .body("Token generated");
        }
        catch (AuthenticationException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}