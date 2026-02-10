package nl.novi.endassignment.pocbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class VisitorResponseDto extends UserResponseDto {

    private String name;
    private List<Long> favoritesIds = new ArrayList<>();

    public VisitorResponseDto(String username, String email, String name) {
        super(username, email);
        this.name = name;
    }
}
