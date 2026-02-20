package nl.novi.endassignment.pocbackend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.novi.endassignment.pocbackend.models.Role;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInputDto {

    @NotBlank(message = "Vul een geldige username in")
    private String username;

    @NotBlank(message = "Vul een geldig e-mailadres in")
    private String email;

    @NotBlank(message = "Wachtwoord mag niet leeg zijn")
    @Size(min = 8, max = 64, message = "Wachtwoord moet tussen 8 en 64 tekens zijn")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Wachtwoord moet minstens één letter, één cijfer en één speciaal teken bevatten"
    )
    private String password;
    private String profilePicture;
    private MultipartFile profilePictureFile;
}
