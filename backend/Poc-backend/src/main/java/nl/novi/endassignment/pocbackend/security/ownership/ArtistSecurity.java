package nl.novi.endassignment.pocbackend.security.ownership;

import nl.novi.endassignment.pocbackend.repositories.ArtistRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ArtistSecurity {

    private final ArtistRepository artistRepository;

    public ArtistSecurity(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public boolean isOwner(long artistId) {
        String loggedInUsername = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return artistRepository.findById(artistId)
                .map(artist -> artist.getUsername().equals(loggedInUsername))
                .orElse(false);
    }
}
