package com.uade.e_commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uade.e_commerce.model.ProductoVariante;

import jakarta.persistence.LockModeType;

public interface ProductoVarianteRepository extends JpaRepository<ProductoVariante, Long> {

    List<ProductoVariante> findByProductoIdOrderByColorAscTalleAsc(Long productoId);

    Optional<ProductoVariante> findByProductoIdAndColorAndTalle(Long productoId, String color, Integer talle);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select variante from ProductoVariante variante where variante.producto.id = :productoId "
            + "and variante.color = :color and variante.talle = :talle")
    Optional<ProductoVariante> findByProductoIdAndColorAndTalleForUpdate(
            @Param("productoId") Long productoId,
            @Param("color") String color,
            @Param("talle") Integer talle);
}
