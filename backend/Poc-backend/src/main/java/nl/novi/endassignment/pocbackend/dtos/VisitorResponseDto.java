package nl.novi.endassignment.pocbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitorResponseDto extends UserResponseDto {

    private String name;
    private List<String> favoritesTitles;
}
