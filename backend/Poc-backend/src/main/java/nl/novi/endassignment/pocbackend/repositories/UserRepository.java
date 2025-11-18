package nl.novi.endassignment.pocbackend.repositories;

import nl.novi.endassignment.pocbackend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameAndEmail(String username, String email);
    List<User> findByDateOfRegistration(LocalDate dateOfRegistration);
    List<User> findByUsernameContainingIgnoreCase(String username);
}
