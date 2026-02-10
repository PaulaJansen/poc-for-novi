package nl.novi.endassignment.pocbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "artists")

public class Artist extends User {

    private String firstName;
    private String lastName;
    private String city;
    private String typeOfArt;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Artwork> portfolio = new ArrayList<>();

    // Domain constructor
    public Artist(String username, String email, String password, String firstName, String lastName, String city, String typeOfArt, String biography) {
        super(username, email, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.typeOfArt = typeOfArt;
        this.biography = biography;
    }
}
