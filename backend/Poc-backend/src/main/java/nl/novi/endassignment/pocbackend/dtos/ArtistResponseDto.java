package nl.novi.endassignment.pocbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ArtistResponseDto extends UserResponseDto {

    private String firstName;
    private String lastName;
    private String city;
    private String typeOfArt;
    private String biography;
    private List<String> portfolioTitles;

    public ArtistResponseDto(String username, String email, String firstName, String lastName, String city, String typeOfArt, String biography) {
        super(username, email);
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.typeOfArt = typeOfArt;
        this.biography = biography;
    }
}
