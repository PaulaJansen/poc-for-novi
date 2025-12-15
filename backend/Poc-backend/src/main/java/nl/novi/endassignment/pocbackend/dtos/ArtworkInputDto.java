package nl.novi.endassignment.pocbackend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkInputDto {

    @NotBlank(message = "Vul een titel in")
    private String title;

    @NotEmpty(message = "Voeg tenminste 1 afbeelding toe")
    private List<MultipartFile> images;

    private List<String> removeImages;

    @NotEmpty(message = "Voeg tenminste 1 genre toe")
    private List<String> genreNames;

    @NotNull(message = "Vul een prijs in")
    private BigDecimal price;

    private String availability;

    private int widthInCm;
    private int lengthInCm;
    private int heightInCm;
}
