package com.lazxnet.urlcraft.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlRequest {

    @NotBlank(message = "La URL es obligatoria")
    private String url;
}
