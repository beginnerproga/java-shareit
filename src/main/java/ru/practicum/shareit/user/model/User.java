package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Entity
@Table(name = "users", schema = "public")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;

    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
