package nl.novi.endassignment.pocbackend.dtos;

import jakarta.validation.constraints.NotBlank;
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
public class VisitorInputDto extends UserInputDto {

    @NotBlank(message = "Vul je naam in")
    private String name;
}
