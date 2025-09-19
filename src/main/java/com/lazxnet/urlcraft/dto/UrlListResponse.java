package com.lazxnet.urlcraft.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlListResponse {
    private String originalUrl;
    private String shortCode;

    public UrlListResponse(String originalUrl, String shortCode) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
    }
}