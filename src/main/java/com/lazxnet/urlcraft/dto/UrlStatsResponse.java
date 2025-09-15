package com.lazxnet.urlcraft.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UrlStatsResponse {
    private String originalUrl;
    private String shortUrl;
    private Long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt; // Nuevo campo
    private boolean expired; // Nuevo campo

    public UrlStatsResponse(String originalUrl, String shortUrl, Long clickCount,
                            LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.clickCount = clickCount;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.expired = expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
