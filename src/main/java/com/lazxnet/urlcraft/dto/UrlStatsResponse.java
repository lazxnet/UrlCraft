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

    public UrlStatsResponse(String originalUrl, String shortUrl, Long clickCount, LocalDateTime createdAt) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.clickCount = clickCount;
        this.createdAt = createdAt;
    }
}
