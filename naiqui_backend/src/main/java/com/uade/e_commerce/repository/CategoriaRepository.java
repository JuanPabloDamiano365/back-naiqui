package com.uade.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.e_commerce.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
