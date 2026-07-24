package br.com.fiap.garage.domain.entity;

import br.com.fiap.commons.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "id")
@Entity
@Table(schema = "garage")
public class Notification extends AuditableEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(comment = "Notification id. Owner: db")
    private UUID id;

    @Column(comment = "Api Client id. Owner: client")
    private UUID externalId;

    // Value Object
    @OneToOne(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "id")
    @MapsId
    private Email email;

    public void initEmailBcc(String bcc) {
        if (email != null)
            email.setBcc(bcc);
    }
}
