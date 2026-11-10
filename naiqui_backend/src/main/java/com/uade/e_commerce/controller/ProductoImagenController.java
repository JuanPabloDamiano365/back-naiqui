package com.uade.e_commerce.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uade.e_commerce.service.ProductoImagenStorageService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/productos/imagenes")
public class ProductoImagenController {

    private final ProductoImagenStorageService storageService;

    public ProductoImagenController(ProductoImagenStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public Map<String, String> subirImagen(@RequestPart("imagen") @NotNull MultipartFile imagen) {
        return Map.of("url", storageService.guardar(imagen));
    }
}
