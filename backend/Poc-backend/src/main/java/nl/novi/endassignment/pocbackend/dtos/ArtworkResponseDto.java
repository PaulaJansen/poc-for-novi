package nl.novi.endassignment.pocbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkResponseDto {

    private Long id;
    private String title;
    private List<String> images = new ArrayList<>();
    private List<String> genreNames = new ArrayList<>();
    private BigDecimal price;
    private String availability;
    private String availabilityLabel;
    private Long artistId;
    private String artistName;
    private int widthInCm;
    private int lengthInCm;
    private int heightInCm;
}
