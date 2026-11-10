package com.uade.e_commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarritoDTO {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private String imagenUrl;
    private Double precioUnitario;
    private Integer talle;
    private String color;
    private Integer cantidad;
    private Double subtotal;
}
