package nl.novi.endassignment.pocbackend.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "artworks")

public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @ElementCollection
    @CollectionTable(
            name = "artwork_images",
            joinColumns = @JoinColumn(name = "artwork_id")
    )
    @Column(name = "image")
    private List<String> images;

    @ManyToMany
    @JoinTable(
            name = "artworks_genres",
            joinColumns = @JoinColumn(name = "artwork_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    List<Genre> genres;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private AvailabilityType availability;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    private int widthInCm;
    private int lengthInCm;
    private int heightInCm;

    @ManyToMany(mappedBy = "favorites")
    private List<Visitor> favoriteOf;

    // Getters & setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public AvailabilityType getAvailability() {
        return availability;
    }

    public void setAvailability(AvailabilityType availability) {
        this.availability = availability;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public int getWidthInCm() {
        return widthInCm;
    }

    public void setWidthInCm(int widthInCm) {
        this.widthInCm = widthInCm;
    }

    public int getLengthInCm() {
        return lengthInCm;
    }

    public void setLengthInCm(int lengthInCm) {
        this.lengthInCm = lengthInCm;
    }

    public int getHeightInCm() {
        return heightInCm;
    }

    public void setHeightInCm(int heightInCm) {
        this.heightInCm = heightInCm;
    }

    public List<Visitor> getFavoriteOf() {
        return favoriteOf;
    }

    public void setFavoriteOf(List<Visitor> favoriteOf) {
        this.favoriteOf = favoriteOf;
    }
}
