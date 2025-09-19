package com.lazxnet.urlcraft.service;

import com.lazxnet.urlcraft.dto.UrlListResponse;
import com.lazxnet.urlcraft.exception.InvalidUrlException;
import com.lazxnet.urlcraft.exception.ResourceNotFoundException;
import com.lazxnet.urlcraft.model.Url;
import com.lazxnet.urlcraft.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UrlServiceImpl implements UrlService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;

    @Autowired
    private UrlRepository urlRepository;

    public String createShortUrl(String originalUrl) {
        return createShortUrl(originalUrl, null);
    }

    public String createShortUrl(String originalUrl, String customCode) {
        // Validar la URL
        if (!isValidUrl(originalUrl)) {
            throw new InvalidUrlException("URL no válida");
        }

        String shortCode;

        // Si se proporciona un código personalizado, validarlo y usarlo
        if (customCode != null && !customCode.trim().isEmpty()) {
            // Validar formato del código personalizado
            if (!isValidCustomCode(customCode)) {
                throw new IllegalArgumentException("El código personalizado no es válido. Solo puede contener letras, números, guiones y guiones bajos, y debe tener entre 4 y 20 caracteres");
            }

            // Verificar si el código personalizado ya existe
            if (urlRepository.existsByShortCode(customCode)) {
                throw new IllegalArgumentException("El código personalizado ya está en uso");
            }

            shortCode = customCode;
        } else {
            // Generar código corto único
            do {
                shortCode = generateShortCode();
            } while (urlRepository.existsByShortCode(shortCode));
        }

        // Crear y guardar la URL
        Url url = new Url(originalUrl, shortCode);
        urlRepository.save(url);

        return shortCode;
    }

    public Optional<Url> getOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
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

    private boolean isValidCustomCode(String customCode) {
        // Validar longitud
        if (customCode.length() < 4 || customCode.length() > 20) {
            return false;
        }

        // Validar caracteres permitidos (solo letras, números, guiones y guiones bajos)
        return customCode.matches("^[a-zA-Z0-9_-]*$");
    }

    public List<UrlListResponse> getAllUrls() {
        List<Url> urls = urlRepository.findAll();
        return urls.stream()
                .map(url -> new UrlListResponse(
                        url.getOriginalUrl(),
                        url.getShortCode()
                ))
                .collect(Collectors.toList());
    }

    public void deleteUrl(String shortCode) {
        if (!urlRepository.existsByShortCode(shortCode)) {
            throw new ResourceNotFoundException("URL no encontrada para el codigo: " + shortCode);
        }

        log.info("Eliminando url: " + shortCode);
        urlRepository.deleteByShortCode(shortCode);
    }

    public String updateUrl(String currentShortCode, String newOriginalUrl) {
        // Buscar la URL existente
        Optional<Url> existingUrlOpt = urlRepository.findByShortCode(currentShortCode);
        if (existingUrlOpt.isEmpty()) {
            throw new ResourceNotFoundException("URL no encontrada para el código: " + currentShortCode);
        }

        Url existingUrl = existingUrlOpt.get();

        // Validar la nueva URL
        if (!isValidUrl(newOriginalUrl)) {
            throw new InvalidUrlException("URL no válida");
        }

        // Actualizar solo la URL original
        existingUrl.setOriginalUrl(newOriginalUrl);

        // Guardar los cambios
        urlRepository.save(existingUrl);
        log.info("Actualizando url: " + existingUrl);
        return existingUrl.getShortCode();
    }
}