package nl.novi.endassignment.pocbackend.models;

import jakarta.persistence.*;

import java.util.List;

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

    // Getters & setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTypeOfArt() {
        return typeOfArt;
    }

    public void setTypeOfArt(String typeOfArt) {
        this.typeOfArt = typeOfArt;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public List<Artwork> getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(List<Artwork> portfolio) {
        this.portfolio = portfolio;
    }
}
