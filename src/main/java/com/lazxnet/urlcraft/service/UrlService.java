package com.lazxnet.urlcraft.service;

import com.lazxnet.urlcraft.model.Url;
import com.lazxnet.urlcraft.repository.UrlRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Random;

@Service
public class UrlService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;

    @Value("${app.base-url}")
    @Getter
    private String baseUrl;

    @Autowired
    private UrlRepository urlRepository;

    public String createShortUrl(String originalUrl) {
        // Validar la URL
        if (!isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("URL no válida");
        }

        // Generar código corto único
        String shortCode;
        do {
            shortCode = generateShortCode();
        } while (urlRepository.existsByShortCode(shortCode));

        // Crear y guardar la URL
        Url url = new Url(originalUrl, shortCode);
        urlRepository.save(url);

        return baseUrl + "/" + shortCode;
    }

    public Optional<Url> getOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }

    public void incrementClickCount(Url url) {
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
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
