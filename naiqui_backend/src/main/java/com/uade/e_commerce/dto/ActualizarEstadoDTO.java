package com.uade.e_commerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoDTO {

    @NotBlank(message = "Debe indicarse el nuevo estado")
    @Pattern(
            regexp = "PENDIENTE|CONFIRMADO|EN_PREPARACION|ENVIADO|ENTREGADO|CANCELADO",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "El estado del pedido no es válido")
    private String estado;
}
