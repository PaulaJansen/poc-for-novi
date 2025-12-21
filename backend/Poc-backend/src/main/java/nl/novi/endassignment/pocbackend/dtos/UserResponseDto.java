package nl.novi.endassignment.pocbackend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private long id;
    private String username;
    private String email;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfRegistration;

    private String profilePicture;
    private List<String> roleNames = new ArrayList<>();

    public UserResponseDto(String username, String email) {
        this.username = username;
        this.email = email;
        this.roleNames = new ArrayList<>();
    }

    // Voor frontend
    public String getProfilePictureUrl() {
        if (profilePicture == null) return null;
        return "/uploads/" + profilePicture;
    }
}
