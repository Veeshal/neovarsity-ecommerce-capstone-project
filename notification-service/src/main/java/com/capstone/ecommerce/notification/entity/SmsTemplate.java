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
@Table(name = "sms_template")
public class SmsTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Size(max = 100)
    @NotNull
    @Column(name = "template_name", nullable = false, length = 100)
    private SmsTemplateType templateName;

    @NotNull
    @Column(name = "version", nullable = false)
    private Integer version;

    @Size(max = 500)
    @NotNull
    @Column(name = "body", nullable = false, length = 500)
    private String body;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by")
    private Long createdBy;

}