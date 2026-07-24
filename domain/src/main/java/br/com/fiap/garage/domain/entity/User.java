package br.com.fiap.garage.domain.entity;

import br.com.fiap.commons.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.InheritanceType.JOINED;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "id")
@Entity
@Table(schema = "garage", name = "users")
@Inheritance(strategy = JOINED)
public class User extends AuditableEntity implements UserDetails {

    @Id
    @GeneratedValue
    @Column(comment = "User id. Owner: db")
    private UUID id;

    @Getter(onMethod_ = @Override)
    @Column(nullable = false, unique = true, comment = "Username. Owner: self")
    private String username;

    @Getter(onMethod_ = @Override)
    @Column(nullable = false, length = 60, comment = "User password. Owner: self")
    private String password;

    @Column(nullable = false, comment = "User name. Owner: self")
    private String name;

    @Column(nullable = false, unique = true, comment = "User e-mail. Owner: self")
    private String email;

    // Aggregate
    @Singular(value = "authority", ignoreNullCollections = true)
    @ManyToMany(cascade = {MERGE, PERSIST}, fetch = EAGER)
    @JoinTable(schema = "garage", name = "users_auth",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "auth_id"))
    @OrderBy("createdAt desc")
    private Set<Auth> authorities;

    public void update(User user) {
        if (user == null)
            return;

        if (user.name != null)
            this.name = user.name;

        if (user.email != null) {
            this.email = user.email;
            this.username = user.email;
        }
    }

    public void updatePassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }
}
