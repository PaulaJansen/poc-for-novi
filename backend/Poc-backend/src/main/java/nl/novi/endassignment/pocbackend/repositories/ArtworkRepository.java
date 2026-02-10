package nl.novi.endassignment.pocbackend.repositories;

import nl.novi.endassignment.pocbackend.models.Artwork;
import nl.novi.endassignment.pocbackend.models.AvailabilityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ArtworkRepository extends JpaRepository<Artwork, Long>, JpaSpecificationExecutor<Artwork> {
    @Query("""
    SELECT a
    FROM Artwork a
    WHERE a.artist.id = :artistId
""")
    List<Artwork> findByArtistId(long artistId);

//    @Query("""
//    SELECT a FROM Artwork a
//    LEFT JOIN FETCH a.images
//    LEFT JOIN FETCH a.genres
//    LEFT JOIN FETCH a.artist ar
//    WHERE a.id = :id
//""")
//    Artwork findByIdForEdit(long id);
}
