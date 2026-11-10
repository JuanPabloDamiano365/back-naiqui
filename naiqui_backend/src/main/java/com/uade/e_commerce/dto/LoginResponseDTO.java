package com.uade.e_commerce.dto;

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
    private String token;
    private String mensaje;
}
