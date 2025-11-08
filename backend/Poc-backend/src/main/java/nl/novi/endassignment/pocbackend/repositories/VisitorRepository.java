package nl.novi.endassignment.pocbackend.repositories;

import nl.novi.endassignment.pocbackend.models.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {
}
