package com.lazxnet.urlcraft.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlListResponse {
    private String originalUrl;
    private String shortUrl;
    private String shortCode;

    public UrlListResponse(String originalUrl, String shortUrl, String shortCode) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.shortCode = shortCode;
    }
}