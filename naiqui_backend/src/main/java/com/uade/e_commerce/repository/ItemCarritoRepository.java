package com.uade.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.e_commerce.model.ItemCarrito;

public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    Optional<ItemCarrito> findByCarritoIdAndProductoIdAndTalleAndColor(
            Long carritoId, Long productoId, Integer talle, String color);
}
