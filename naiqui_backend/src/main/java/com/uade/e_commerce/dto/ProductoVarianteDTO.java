package com.uade.e_commerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoVarianteDTO {

    @NotBlank(message = "El color de la variante es obligatorio")
    private String color;

    @NotNull(message = "El talle de la variante es obligatorio")
    @Positive(message = "El talle debe ser mayor a 0")
    private Integer talle;

    @NotNull(message = "El stock de la variante es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;
}
