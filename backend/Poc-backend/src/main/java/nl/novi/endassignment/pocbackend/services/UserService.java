package nl.novi.endassignment.pocbackend.services;

import nl.novi.endassignment.pocbackend.dtos.UserResponseDto;
import nl.novi.endassignment.pocbackend.exceptions.RecordNotFoundException;
import nl.novi.endassignment.pocbackend.mappers.UserMapper;
import nl.novi.endassignment.pocbackend.models.User;
import nl.novi.endassignment.pocbackend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponseDto> getAllUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    public UserResponseDto getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Gebruiker met id " + id + " niet gevonden!"));
        return userMapper.toDto(user);
    }

    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RecordNotFoundException("Gebruiker met gebruikersnaam " + username + " niey gevonden!"));
        return userMapper.toDto(user);
    }

    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RecordNotFoundException("Gebruiker met e-mailadres " + email + " niet gevonden!"));
        return userMapper.toDto(user);
    }


}
