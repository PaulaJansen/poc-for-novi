package nl.novi.endassignment.pocbackend.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(
            name = "user_seq",
            sequenceName = "user_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(unique = true, updatable = false)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate dateOfRegistration;

    private String profilePicture;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    // Domain constructor
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.dateOfRegistration = LocalDate.now();
    }
}
