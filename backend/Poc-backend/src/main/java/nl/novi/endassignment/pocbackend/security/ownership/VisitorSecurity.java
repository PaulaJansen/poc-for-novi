package nl.novi.endassignment.pocbackend.security.ownership;

import nl.novi.endassignment.pocbackend.repositories.VisitorRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class VisitorSecurity {

    private final VisitorRepository visitorRepository;

    public VisitorSecurity(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    public boolean isOwner(long visitorId) {
        String loggedInUsername = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return visitorRepository.findById(visitorId)
                .map(visitor -> visitor.getUsername().equals(loggedInUsername))
                .orElse(false);
    }
}
