package nl.novi.endassignment.pocbackend.repositories;

import nl.novi.endassignment.pocbackend.models.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.function.Function;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    Optional<Artwork> findByTitle(Artwork artwork);
}
