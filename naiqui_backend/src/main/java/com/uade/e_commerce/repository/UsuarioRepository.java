package com.uade.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.model.RolUsuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    java.util.Optional<Usuario> findFirstByRolOrderByIdAsc(RolUsuario rol);
}
