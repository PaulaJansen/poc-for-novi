package nl.novi.endassignment.pocbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreResponseDto {

    private long id;
    private String name;
    List<String> artworkTitles = new ArrayList<>();

    public GenreResponseDto(String name) {
        this.name = name;
    }
}
