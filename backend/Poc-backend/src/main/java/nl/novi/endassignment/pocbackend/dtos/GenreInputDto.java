package nl.novi.endassignment.pocbackend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreInputDto {

    @NotBlank(message = "Vul tenminste 1 genre in")
    private String name;
}
