package nl.novi.endassignment.pocbackend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    private String username;

    @Email(message = "Ongeldig e-mailadres")
    private String email;

    @Size(min = 8, max = 64, message = "Wachtwoord moet tussen 8 en 64 tekens zijn")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Wachtwoord moet minstens één letter, één cijfer en één speciaal teken bevatten"
    )
    private String password;
}
