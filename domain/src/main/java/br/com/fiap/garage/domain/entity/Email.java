package br.com.fiap.garage.domain.entity;

import br.com.fiap.commons.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "id")
@Entity
@Table(schema = "garage")
public class Email extends AuditableEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(comment = "Email id. Owner: db")
    private UUID id;

    @Column(nullable = false, comment = "Email recipient. Owner: self")
    private String recipient;

    @Setter
    @Column(nullable = false, comment = "Email bcc. Owner: self")
    private String bcc;

    @Column(nullable = false, comment = "Email subject. Owner: self")
    private String subject;

    @Column(nullable = false, columnDefinition = "text", comment = "Email message. Owner: self")
    private String message;
}
