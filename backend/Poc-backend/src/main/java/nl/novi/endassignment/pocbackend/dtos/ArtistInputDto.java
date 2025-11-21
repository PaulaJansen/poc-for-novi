package nl.novi.endassignment.pocbackend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.novi.endassignment.pocbackend.models.Artwork;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistInputDto extends UserInputDto{

    @NotBlank(message = "Voornaam is verplicht")
    private String firstName;

    @NotBlank(message = "Achternaam is verplicht")
    private String lastName;

    @NotBlank(message = "Laat weten waar je kunst te vinden is!")
    private String city;

    @NotBlank(message = "Laat weten wat je maakt!")
    private String typeOfArt;

    @NotBlank(message = "Laat weten wie je bent!")
    @Size(min=20, max=250)
    private String biography;
}
