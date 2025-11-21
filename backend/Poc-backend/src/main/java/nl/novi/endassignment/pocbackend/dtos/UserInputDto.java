package nl.novi.endassignment.pocbackend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.novi.endassignment.pocbackend.models.Role;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInputDto {

    @NotBlank(message = "Vul een geldige username in")
    private String username;

    @NotBlank(message = "Vul een geldig e-mailadres in")
    private String email;

    private String profilePicture;
}
