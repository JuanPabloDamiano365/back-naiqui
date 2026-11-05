package com.uade.e_commerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarCantidadCarritoDTO {

    @NotNull(message = "Debe indicarse la cantidad")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
}
