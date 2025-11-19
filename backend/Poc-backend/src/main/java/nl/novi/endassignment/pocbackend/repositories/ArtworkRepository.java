package nl.novi.endassignment.pocbackend.repositories;

import nl.novi.endassignment.pocbackend.models.Artwork;
import nl.novi.endassignment.pocbackend.models.AvailabilityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    List<Artwork> findByTitle(String title);

    List<Artwork> findByArtist(String firstName, String lastName);

    List<Artwork> findByAvailability(AvailabilityType availabilityType);

    List<Artwork> findByPriceLessThan(BigDecimal bigDecimal);

    List<Artwork> findByPriceBetween(BigDecimal bigDecimal, BigDecimal bigDecimal1);

    List<Artwork> findByPriceGreaterThan(BigDecimal bigDecimal);
}
