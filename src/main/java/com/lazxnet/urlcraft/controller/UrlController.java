package com.lazxnet.urlcraft.controller;

import com.lazxnet.urlcraft.dto.UrlRequest;
import com.lazxnet.urlcraft.dto.UrlResponse;
import com.lazxnet.urlcraft.dto.UrlStatsResponse;
import com.lazxnet.urlcraft.exception.ResourceNotFoundException;
import com.lazxnet.urlcraft.exception.UrlExpiredException;
import com.lazxnet.urlcraft.model.Url;
import com.lazxnet.urlcraft.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@Tag(name = "URL Shortener", description = "API para acortar URLs y gestionar estadísticas")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @PostMapping("/api/v1/urls")
    @Operation(summary = "Crear URL acortada", description = "Convierte una URL larga en una versión acortada")
    @ApiResponse(responseCode = "201", description = "URL acortada creada exitosamente")
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody UrlRequest request) {
        String shortUrl = urlService.createShortUrl(request.getUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(new UrlResponse(shortUrl));
    }

    @GetMapping("/{shortCode}")
    @Operation(summary = "Redireccionar", description = "Redirige a la URL original usando el código corto")
    @ApiResponse(responseCode = "302", description = "Redirección exitosa")
    @ApiResponse(responseCode = "404", description = "URL no encontrada")
    @ApiResponse(responseCode = "410", description = "URL expirada")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        Optional<Url> urlOptional = urlService.getOriginalUrl(shortCode);
        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();
            urlService.incrementClickCount(url);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(url.getOriginalUrl()))
                    .build();
        } else {
            throw new ResourceNotFoundException("URL no encontrada para el código: " + shortCode);
        }
    }

    @GetMapping("/api/v1/urls/{shortCode}/stats")
    @Operation(summary = "Obtener estadísticas", description = "Devuelve estadísticas de uso para una URL acortada")
    @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    @ApiResponse(responseCode = "404", description = "URL no encontrada")
    @ApiResponse(responseCode = "410", description = "URL expirada")
    public ResponseEntity<UrlStatsResponse> getUrlStats(@PathVariable String shortCode) {
        Optional<Url> urlOptional = urlService.getOriginalUrl(shortCode);
        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();
            UrlStatsResponse response = new UrlStatsResponse(
                    url.getOriginalUrl(),
                    urlService.getBaseUrl() + "/" + url.getShortCode(),
                    url.getClickCount(),
                    url.getCreatedAt(),
                    url.getExpiresAt()
            );
            return ResponseEntity.ok(response);
        } else {
            throw new ResourceNotFoundException("URL no encontrada para el código: " + shortCode);
        }
    }
}