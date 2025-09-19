package com.lazxnet.urlcraft.controller;

import com.lazxnet.urlcraft.dto.UrlListResponse;
import com.lazxnet.urlcraft.dto.UrlRequest;
import com.lazxnet.urlcraft.dto.UrlResponse;
import com.lazxnet.urlcraft.dto.UrlUpdateRequest;
import com.lazxnet.urlcraft.exception.ResourceNotFoundException;
import com.lazxnet.urlcraft.model.Url;
import com.lazxnet.urlcraft.service.UrlServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Slf4j
@CrossOrigin(value = "http://localhost:5173")
@RestController
public class UrlController {

    @Autowired
    private UrlServiceImpl urlService;

    @PostMapping("/api/v1/urls")
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody UrlRequest request) {

        String shortUrl;

        if (request.getCustomCode() != null && !request.getCustomCode().trim().isEmpty()) {
            //usar el código personalizado si se proporciona
            shortUrl = urlService.createShortUrl(request.getUrl(), request.getCustomCode());
            log.info("Creando URL acortada personalizada: {} -> {} (código: {})",
                    request.getUrl(), shortUrl, request.getCustomCode());
        } else {
            //generar código automáticamente si no se proporciona código personalizado
            shortUrl = urlService.createShortUrl(request.getUrl());
            log.info("Creando URL acortada: {} -> {}", request.getUrl(), shortUrl);
        }

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

    @GetMapping("/api/v1/urls")
    public ResponseEntity<List<UrlListResponse>> getAllUrls() {
        List<UrlListResponse> urls = urlService.getAllUrls();
        log.info("Retornando todos las URLs: {}", urls);
        return ResponseEntity.ok(urls);
    }

    @PutMapping("/api/v1/urls/{shortCode}")
    public ResponseEntity<UrlResponse> updateUrl(@PathVariable String shortCode, @Valid @RequestBody UrlUpdateRequest request) {
        String updatedShortUrl = urlService.updateUrl(shortCode, request.getOriginalUrl());
        log.info("URL actualizada: {} -> {}", shortCode, updatedShortUrl);
        return ResponseEntity.ok(new UrlResponse(updatedShortUrl));
    }


    @DeleteMapping("/api/v1/urls/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        urlService.deleteUrl(shortCode);
        log.info("URL eliminada exitosamente: {}", shortCode);
        return ResponseEntity.noContent().build();
    }

}