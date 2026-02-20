package nl.novi.endassignment.pocbackend.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistUpdateDto extends UserUpdateDto {

    private String firstName;
    private String lastName;
    private String city;
    private String typeOfArt;

    @Size(min = 20, max = 250, message = "Biografie moet tussen 20 en 250 tekens zijn")
    private String biography;
}
