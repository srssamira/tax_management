package com.zup.br.taxes_management.controllers.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaxTypeRegisterDTO {

    @NotNull(message = "name can't be null")
    @NotBlank(message = "name can't be blank")
    private String name;

    @NotNull(message = "description can't be null")
    @NotBlank(message = "description can't be blank")
    private String description;

    @NotNull(message = "aliquot can't be null")
    private Double aliquot;
}
