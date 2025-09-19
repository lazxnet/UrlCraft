package com.lazxnet.urlcraft.service;

import com.lazxnet.urlcraft.dto.UrlListResponse;
import com.lazxnet.urlcraft.model.Url;

import java.util.List;
import java.util.Optional;

public interface UrlService {
    String createShortUrl(String originalUrl);
    String createShortUrl(String originalUrl, String customCode);
    String createShortUrl(String originalUrl, String customCode, int expirationDays);
    Optional<Url> getOriginalUrl(String shortCode);
    List<UrlListResponse> getAllUrls();
    void deleteUrl(String shortCode);
    String updateUrl(String currentShortCode, String newOriginalUrl);
}
