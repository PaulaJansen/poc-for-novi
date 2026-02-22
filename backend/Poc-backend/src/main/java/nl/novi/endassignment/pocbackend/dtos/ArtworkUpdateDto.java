package nl.novi.endassignment.pocbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkUpdateDto {

    private String title;
    private List<String> removeImages;
    private List<String> genreNames;
    private BigDecimal price;
    private String availability;
    private Integer widthInCm;
    private Integer lengthInCm;
    private Integer heightInCm;
}
