package com.uade.e_commerce.dto;

import com.uade.e_commerce.model.RolUsuario;

public record UsuarioSesionDTO(Long id, String nombre, String email, RolUsuario rol) {
}
