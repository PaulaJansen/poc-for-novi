package nl.novi.endassignment.pocbackend.security.ownership;

import nl.novi.endassignment.pocbackend.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    private final UserRepository userRepository;

    public UserSecurity(UserRepository userRepository) {this.userRepository = userRepository;}

    public boolean isOwner(long userId) {
        String loggedInUsername = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findById(userId)
                .map(user -> user.getUsername().equals(loggedInUsername))
                .orElse(false);
    }
}
