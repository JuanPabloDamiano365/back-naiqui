package com.uade.e_commerce.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.uade.e_commerce.dto.UsuarioDTO;

class UsuarioRoleTest {

    @Test
    void usuarioDebeTenerRolPorDefectoDeCliente() {
        Usuario usuario = new Usuario();

        assertEquals(RolUsuario.USUARIO, usuario.getRol());
    }

    @Test
    void usuarioDtoDebePermitirDefinirRol() {
        UsuarioDTO dto = new UsuarioDTO();

        assertEquals(RolUsuario.USUARIO, dto.getRol());
    }
}
