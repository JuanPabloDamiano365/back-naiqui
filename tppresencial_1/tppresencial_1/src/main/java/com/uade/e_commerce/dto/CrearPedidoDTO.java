package com.uade.e_commerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearPedidoDTO {

    @NotBlank(message = "El nombre y apellido son obligatorios")
    @Size(max = 120)
    private String nombre;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 40)
    private String telefono;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200)
    private String direccion;

    @NotBlank(message = "El código postal es obligatorio")
    @Size(max = 20)
    private String codigoPostal;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100)
    private String ciudad;

    @NotBlank(message = "La provincia es obligatoria")
    @Size(max = 100)
    private String provincia;

    @NotBlank(message = "Debe seleccionarse un método de pago")
    @Pattern(regexp = "TARJETA|TRANSFERENCIA", message = "El método de pago no es válido")
    private String metodoPago;
}
