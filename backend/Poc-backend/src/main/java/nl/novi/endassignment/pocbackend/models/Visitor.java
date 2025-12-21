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
@Table(name="visitors")

public class Visitor extends User {

    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "visitor_favorites",
            joinColumns = @JoinColumn(name = "visitor_id"),
            inverseJoinColumns = @JoinColumn(name = "artwork_id")
    )
    private List<Artwork> favorites;

    // Domain constructor
    public Visitor(String username, String email, String password, String name) {
        super(username, email, password);
        this.setName(name);
    }
}
