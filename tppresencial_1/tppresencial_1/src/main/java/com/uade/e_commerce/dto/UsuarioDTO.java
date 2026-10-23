package com.uade.e_commerce.dto;

import com.uade.e_commerce.model.RolUsuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UsuarioDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    private RolUsuario rol;

    private String direccion;
    private String telefono;

    public UsuarioDTO() {
        this.rol = RolUsuario.USUARIO;
    }

    public UsuarioDTO(Long id, String nombre, String email, String password, RolUsuario rol, String direccion, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol != null ? rol : RolUsuario.USUARIO;
        this.direccion = direccion;
        this.telefono = telefono;
    }
}
