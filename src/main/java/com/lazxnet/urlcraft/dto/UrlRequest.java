package com.lazxnet.urlcraft.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlRequest {

    @NotBlank(message = "La URL es obligatoria")
    private String url;

    @Size(min = 4, max = 20, message = "El código personalizado debe tener entre 4 y 20 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = "El código personalizado solo puede contener letras, números, guiones y guiones bajos")
    private String customCode;
}
