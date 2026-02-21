package nl.novi.endassignment.pocbackend.security.ownership;

import nl.novi.endassignment.pocbackend.exceptions.RecordNotFoundException;
import nl.novi.endassignment.pocbackend.models.Artwork;
import nl.novi.endassignment.pocbackend.repositories.ArtworkRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("artworkSecurity")
public class ArtworkSecurity {

    private final ArtworkRepository artworkRepository;

    public ArtworkSecurity(ArtworkRepository artworkRepository) {
        this.artworkRepository = artworkRepository;
    }

    public boolean isOwner(Artwork artwork) {
        String loggedInUsername = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return artwork.getArtist().getUsername().equals(loggedInUsername);
    }

    public boolean isOwner(Long artworkId) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RecordNotFoundException("Kunstwerk niet gevonden"));
        return isOwner(artwork);
    }
}
