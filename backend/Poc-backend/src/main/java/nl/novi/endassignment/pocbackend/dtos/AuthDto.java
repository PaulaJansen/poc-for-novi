package nl.novi.endassignment.pocbackend.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor

public class AuthDto {

    private String username;
    private String password;

    public AuthDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
