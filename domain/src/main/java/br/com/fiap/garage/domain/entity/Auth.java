package br.com.fiap.garage.domain.entity;

import br.com.fiap.commons.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "id")
@Entity
@Table(schema = "garage")
public class Auth extends AuditableEntity implements GrantedAuthority {

    @Id
    @GeneratedValue
    @Column(comment = "Authorization id. Owner: db")
    private UUID id;

    @Getter(onMethod_ = @Override)
    @Column(nullable = false, length = 20, comment = "Authorization name. Owner: self")
    private String authority;
}
