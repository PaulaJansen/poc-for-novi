package nl.novi.endassignment.pocbackend.repositories;

import nl.novi.endassignment.pocbackend.models.Artwork;
import nl.novi.endassignment.pocbackend.models.AvailabilityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.List;

public interface ArtworkRepository extends JpaRepository<Artwork, Long>, JpaSpecificationExecutor<Artwork> {
    List<Artwork> findByArtistId(long artistId);
}
