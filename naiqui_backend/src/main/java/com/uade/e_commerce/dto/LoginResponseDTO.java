package com.uade.e_commerce.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uade.e_commerce.model.RolUsuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private Long id;
    private String nombre;
    private String email;
    private RolUsuario rol;
    @JsonIgnore
    private String token;
    private String mensaje;
}
