package nl.novi.endassignment.pocbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistResponseDto extends UserResponseDto {

    private String firstName;
    private String lastName;
    private String city;
    private String typeOfArt;
    private String biography;
    private List<String> portfolioTitles;
}
