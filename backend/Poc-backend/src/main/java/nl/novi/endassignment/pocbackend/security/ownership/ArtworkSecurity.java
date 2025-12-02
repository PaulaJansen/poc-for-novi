package nl.novi.endassignment.pocbackend.security.ownership;

import nl.novi.endassignment.pocbackend.repositories.ArtworkRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ArtworkSecurity {

    private final ArtworkRepository artworkRepository;

    public ArtworkSecurity(ArtworkRepository artworkRepository) {
        this.artworkRepository = artworkRepository;
    }

    public boolean isOwner(long artworkId) {
        String loggedInUsername = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return artworkRepository.findById(artworkId)
                .map(artwork -> artwork.getArtist().getUsername().equals(loggedInUsername))
                .orElse(false);
    }
}
