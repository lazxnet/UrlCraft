package com.lazxnet.urlcraft.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "urls")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @NotNull
    @Column(nullable = false, unique = true)
    private String shortCode;

    private LocalDateTime createdAt;

    private  LocalDateTime expiresAt;

    // Constructores
    public Url() {
    }

    public Url(String originalUrl, String shortCode, LocalDateTime expiresAt) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now();
    }

    // Metodo para verificar si la URL ha expirado
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
