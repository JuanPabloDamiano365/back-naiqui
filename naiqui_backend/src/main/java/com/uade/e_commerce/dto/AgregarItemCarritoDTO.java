package com.uade.e_commerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgregarItemCarritoDTO {

    @NotNull(message = "Debe indicarse el producto a agregar")
    private Long productoId;

    @NotNull(message = "Debe indicarse el talle")
    private Integer talle;

    @NotBlank(message = "Debe indicarse el color")
    private String color;

    @NotNull(message = "Debe indicarse la cantidad")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
}
