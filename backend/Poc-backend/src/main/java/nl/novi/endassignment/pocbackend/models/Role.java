package nl.novi.endassignment.pocbackend.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name="roles")

public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    @Enumerated(EnumType.STRING)
    private RoleType roleName;

    @ManyToMany(mappedBy = "roles")
    private List<User> users;

    // Domain constructor
    public Role(RoleType roleName) {
        this.roleName = roleName;
    }
}
