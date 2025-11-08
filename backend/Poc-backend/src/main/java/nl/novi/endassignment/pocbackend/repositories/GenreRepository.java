package nl.novi.endassignment.pocbackend.repositories;

import nl.novi.endassignment.pocbackend.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
