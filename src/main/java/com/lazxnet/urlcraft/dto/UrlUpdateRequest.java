package com.lazxnet.urlcraft.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlUpdateRequest {

    @NotBlank(message = "La URL es obligatoria")
    private String originalUrl;
}
