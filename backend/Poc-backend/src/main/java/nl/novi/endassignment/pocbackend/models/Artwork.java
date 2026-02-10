package nl.novi.endassignment.pocbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "artworks")

public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    private String title;

    @ElementCollection
    @CollectionTable(
            name = "artwork_images",
            joinColumns = @JoinColumn(name = "artwork_id")
    )
    @Column(name = "image")
    private List<String> images = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "artworks_genres",
            joinColumns = @JoinColumn(name = "artwork_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private AvailabilityType availability;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    @JsonIgnore
    @ToString.Exclude
    private Artist artist;

    private int widthInCm;
    private int lengthInCm;
    private int heightInCm;

    @ManyToMany(mappedBy = "favorites")
    private List<Visitor> favoriteOf = new ArrayList<>();

    // Domain constructor
    public Artwork(String title, BigDecimal price, AvailabilityType availability, Artist artist, int widthInCm, int lengthInCm, int heightInCm) {
        this.title = title;
        this.price = price;
        this.availability = availability;
        this.artist = artist;
        this.widthInCm = widthInCm;
        this.lengthInCm = lengthInCm;
        this.heightInCm = heightInCm;
        this.images = new ArrayList<>();
        this.genres = new HashSet<>();
        this.favoriteOf = new ArrayList<>();
    }
}