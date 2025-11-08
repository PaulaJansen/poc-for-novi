package nl.novi.endassignment.pocbackend.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="visitors")

public class Visitor extends User {

    private String name;

    @ManyToMany
    @JoinTable(
            name = "visitor_favorites",
            joinColumns = @JoinColumn(name = "visitor_id"),
            inverseJoinColumns = @JoinColumn(name = "artwork_id")
    )
    private List<Artwork> favorites;

    // Getters & setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Artwork> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Artwork> favorites) {
        this.favorites = favorites;
    }
}
