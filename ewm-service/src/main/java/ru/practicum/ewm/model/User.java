package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "e_mail", nullable = false)
    private String email;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "private_profile", nullable = false)
    private Boolean privateProfile;
    @ManyToMany
    @JoinTable(name = "subscribers",
            joinColumns = @JoinColumn(name = "subscribed_on", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    List<User> subscribedOn;

}
