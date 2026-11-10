package com.uade.e_commerce.dto;

import com.uade.e_commerce.model.RolUsuario;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarRolUsuarioDTO {

    @NotNull(message = "Debe indicarse un rol válido")
    private RolUsuario rol;
}
