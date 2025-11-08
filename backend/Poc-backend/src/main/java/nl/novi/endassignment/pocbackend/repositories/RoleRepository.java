package nl.novi.endassignment.pocbackend.repositories;

import nl.novi.endassignment.pocbackend.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
