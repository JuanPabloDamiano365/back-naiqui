package com.uade.e_commerce.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    private String genero;
    private String marca;
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    @Positive(message = "El precio original debe ser mayor a 0")
    private Double precioOriginal;

    private String color;

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    private String imagenUrl;
    private List<String> imagenes;
    private Long creadorId;
    private String creadorNombre;
    private String creadorEmail;
    private String creadorRol;

    @NotNull(message = "Debe indicarse la categoría del producto")
    private Long categoriaId;

    private String categoriaNombre;

    private List<@Valid ProductoVarianteDTO> variantes;
}
