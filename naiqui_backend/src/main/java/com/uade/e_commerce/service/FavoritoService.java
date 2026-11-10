package com.uade.e_commerce.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.dto.ProductoDTO;
import com.uade.e_commerce.dto.ProductoVarianteDTO;
import com.uade.e_commerce.exception.ResourceNotFoundException;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.model.ProductoImagen;
import com.uade.e_commerce.model.RolUsuario;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.ProductoRepository;
import com.uade.e_commerce.repository.ProductoVarianteRepository;
import com.uade.e_commerce.repository.UsuarioRepository;

@Service
@Transactional
public class FavoritoService {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final ProductoVarianteRepository varianteRepository;

    public FavoritoService(UsuarioRepository usuarioRepository, ProductoRepository productoRepository,
                           ProductoVarianteRepository varianteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.varianteRepository = varianteRepository;
    }

    private ProductoDTO toDTO(Producto producto) {
        List<ProductoVarianteDTO> variantes = varianteRepository
                .findByProductoIdOrderByColorAscTalleAsc(producto.getId()).stream()
                .map(variante -> new ProductoVarianteDTO(
                        variante.getColor(), variante.getTalle(), variante.getStock()))
                .toList();
        int stock = variantes.isEmpty()
                ? producto.getStock()
                : variantes.stream().mapToInt(ProductoVarianteDTO::getStock).sum();
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setGenero(producto.getGenero());
        dto.setMarca(producto.getMarca());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setPrecioOriginal(producto.getPrecioOriginal());
        dto.setColor(producto.getColor());
        dto.setStock(stock);
        List<String> imageUrls = producto.getImagenes().stream().map(ProductoImagen::getUrl).toList();
        if (imageUrls.isEmpty() && producto.getImagenUrl() != null) imageUrls = List.of(producto.getImagenUrl());
        dto.setImagenes(imageUrls);
        dto.setImagenUrl(imageUrls.isEmpty() ? null : imageUrls.get(0));
        if (producto.getCreador() != null) {
            dto.setCreadorId(producto.getCreador().getId());
            dto.setCreadorNombre((producto.getCreador().getNombre() + " "
                    + (producto.getCreador().getApellido() == null ? "" : producto.getCreador().getApellido())).trim());
            dto.setCreadorEmail(producto.getCreador().getEmail());
            dto.setCreadorRol(producto.getCreador().getRol() == null
                    ? RolUsuario.USUARIO.name()
                    : producto.getCreador().getRol().name());
        }
        dto.setCategoriaId(producto.getCategoria() != null ? producto.getCategoria().getId() : null);
        dto.setCategoriaNombre(producto.getCategoria() != null ? producto.getCategoria().getNombre() : null);
        dto.setVariantes(variantes);
        return dto;
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
