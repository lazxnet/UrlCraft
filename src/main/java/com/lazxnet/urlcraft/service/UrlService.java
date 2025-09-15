package com.lazxnet.urlcraft.service;

import com.lazxnet.urlcraft.exception.InvalidUrlException;
import com.lazxnet.urlcraft.exception.UrlExpiredException;
import com.lazxnet.urlcraft.model.Url;
import com.lazxnet.urlcraft.repository.UrlRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UrlService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private static final int DEFAULT_EXPIRATION_DAYS = 30;

    @Value("${app.base-url}")
    @Getter
    private String baseUrl;

    @Autowired
    private UrlRepository urlRepository;

    public String createShortUrl(String originalUrl) {
        return createShortUrl(originalUrl, DEFAULT_EXPIRATION_DAYS);
    }

    public String createShortUrl(String originalUrl, int expirationDays) {
        // Validar la URL
        if (!isValidUrl(originalUrl)) {
            throw new InvalidUrlException("URL no válida");
        }

        // Validar días de expiración
        if (expirationDays < 1) {
            throw new IllegalArgumentException("Los días de expiración deben ser al menos 1");
        }

        // Generar código corto único
        String shortCode;
        do {
            shortCode = generateShortCode();
        } while (urlRepository.existsByShortCode(shortCode));

        // Calcular fecha de expiración
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(expirationDays);

        // Crear y guardar la URL
        Url url = new Url(originalUrl, shortCode, expiresAt);
        urlRepository.save(url);

        return baseUrl + "/" + shortCode;
    }

    public Optional<Url> getOriginalUrl(String shortCode) {
        Optional<Url> urlOptional = urlRepository.findByShortCode(shortCode);

        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();

            // Verificar si la URL ha expirado
            if (url.isExpired()) {
                throw new UrlExpiredException("La URL ha expirado");
            }
        }

        return urlOptional;
    }

    private String generateShortCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    private boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}