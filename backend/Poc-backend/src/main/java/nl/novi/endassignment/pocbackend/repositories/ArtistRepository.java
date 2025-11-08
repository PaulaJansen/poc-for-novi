package nl.novi.endassignment.pocbackend.repositories;

import nl.novi.endassignment.pocbackend.models.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
