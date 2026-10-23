package com.uade.e_commerce.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.dto.ProductoDTO;
import com.uade.e_commerce.exception.ResourceNotFoundException;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.ProductoRepository;
import com.uade.e_commerce.repository.UsuarioRepository;

@Service
@Transactional
public class FavoritoService {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public FavoritoService(UsuarioRepository usuarioRepository, ProductoRepository productoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    private ProductoDTO toDTO(Producto producto) {
        return new ProductoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getGenero(),
                producto.getMarca(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getColor(),
                producto.getStock(),
                producto.getImagenUrl(),
                producto.getCategoria() != null ? producto.getCategoria().getId() : null,
                producto.getCategoria() != null ? producto.getCategoria().getNombre() : null
        );
    }

    public List<ProductoDTO> obtenerFavoritos(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el usuario con id " + usuarioId));
        Set<Producto> favoritos = usuario.getFavoritos();
        return favoritos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ProductoDTO> agregarFavorito(Long usuarioId, Long productoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el usuario con id " + usuarioId));
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el producto con id " + productoId));
        usuario.getFavoritos().add(producto);
        usuarioRepository.save(usuario);
        return obtenerFavoritos(usuarioId);
    }

    public List<ProductoDTO> eliminarFavorito(Long usuarioId, Long productoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el usuario con id " + usuarioId));
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el producto con id " + productoId));
        usuario.getFavoritos().remove(producto);
        usuarioRepository.save(usuario);
        return obtenerFavoritos(usuarioId);
    }
}
