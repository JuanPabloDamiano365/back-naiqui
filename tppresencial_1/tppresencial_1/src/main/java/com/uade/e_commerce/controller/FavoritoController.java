package com.uade.e_commerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.e_commerce.dto.ProductoDTO;
import com.uade.e_commerce.service.FavoritoService;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerFavoritos(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(favoritoService.obtenerFavoritos(usuarioId));
    }

    @PostMapping("/{productoId}")
    public ResponseEntity<List<ProductoDTO>> agregarFavorito(@PathVariable Long usuarioId,
                                                             @PathVariable Long productoId) {
        return ResponseEntity.ok(favoritoService.agregarFavorito(usuarioId, productoId));
    }

    @DeleteMapping("/{productoId}")
    public ResponseEntity<List<ProductoDTO>> eliminarFavorito(@PathVariable Long usuarioId,
                                                               @PathVariable Long productoId) {
        return ResponseEntity.ok(favoritoService.eliminarFavorito(usuarioId, productoId));
    }
}