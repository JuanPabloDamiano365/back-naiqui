package com.uade.e_commerce.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioEmail;
    private LocalDateTime fecha;
    private String estado;
    private Double total;
    private List<ItemPedidoDTO> items;
    private String metodoPago;
    private String nombreEntrega;
    private String telefonoEntrega;
    private String direccionEntrega;
    private String codigoPostalEntrega;
    private String ciudadEntrega;
    private String provinciaEntrega;
}
