package nl.novi.endassignment.pocbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDto {

    private long id;
    private String roleName;

    public RoleResponseDto(String roleName) {
        this.roleName = roleName;
    }
}
