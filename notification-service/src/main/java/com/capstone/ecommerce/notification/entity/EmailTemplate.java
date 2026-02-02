package com.capstone.ecommerce.notification.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "email_template")
public class EmailTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "template_name", nullable = false, length = 100)
    private EmailTemplateType templateName;

    @NotNull
    @Column(name = "version", nullable = false)
    private Integer version;

    @Size(max = 255)
    @NotNull
    @Column(name = "subject", nullable = false)
    private String subject;

    @NotNull
    @Lob
    @Column(name = "body", nullable = false)
    private String body;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by")
    private Long createdBy;

}