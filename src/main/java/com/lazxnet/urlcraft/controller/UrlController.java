package com.lazxnet.urlcraft.controller;

import com.lazxnet.urlcraft.dto.UrlRequest;
import com.lazxnet.urlcraft.dto.UrlResponse;
import com.lazxnet.urlcraft.exception.ResourceNotFoundException;
import com.lazxnet.urlcraft.model.Url;
import com.lazxnet.urlcraft.service.UrlService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@Slf4j
@RestController
public class UrlController {

    @Autowired
    private UrlService urlService;

    @PostMapping("/api/v1/urls")
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody UrlRequest request) {
        String shortUrl = urlService.createShortUrl(request.getUrl());
        log.info("Creando URL acortada: {} -> {}",request.getUrl() , shortUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UrlResponse(shortUrl));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        Optional<Url> urlOptional = urlService.getOriginalUrl(shortCode);
        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();
            log.info("Redireccionando código corto: {} a URL: {}", shortCode, url.getOriginalUrl());
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(url.getOriginalUrl()))
                    .build();
        } else {
            log.warn("Intento de redirección con código corto no encontrado: {}", shortCode);
            throw new ResourceNotFoundException("URL no encontrada para el código: " + shortCode);
        }
    }

}