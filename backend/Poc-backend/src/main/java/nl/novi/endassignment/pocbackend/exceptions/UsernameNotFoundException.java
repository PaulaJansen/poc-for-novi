package nl.novi.endassignment.pocbackend.exceptions;

import java.io.Serial;

public class UsernameNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UsernameNotFoundException(String username) {
        super("Gebruiker met username "+ username + "niet gevonden!");
    }
}
