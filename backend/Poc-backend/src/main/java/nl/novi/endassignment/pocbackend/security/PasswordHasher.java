package nl.novi.endassignment.pocbackend.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


// Klasse voor het hashen van de wachtwoorden in de data.sql

public class PasswordHasher {
    public static void main(String[] args) {
        String rawPassword = "Wachtwoord123";
        String hashed = new BCryptPasswordEncoder().encode(rawPassword);
        System.out.println(hashed);
    }
}
