package nl.novi.endassignment.pocbackend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
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

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Artwork> portfolio;

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
