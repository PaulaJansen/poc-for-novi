package nl.novi.endassignment.pocbackend.models;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "notification_settings")
public class NotificationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean emailNotifications;
    private boolean marketingEmails;
    private boolean newFollowerAlert;
    private boolean newFavoriteAlert;

    @OneToOne(mappedBy = "notificationSettings")
    private User user;
}
