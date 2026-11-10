package com.uade.e_commerce.service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.dto.ProductoDTO;
import com.uade.e_commerce.dto.ProductoVarianteDTO;
import com.uade.e_commerce.exception.ResourceNotFoundException;
import com.uade.e_commerce.model.Categoria;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.model.ProductoVariante;
import com.uade.e_commerce.repository.CategoriaRepository;
import com.uade.e_commerce.repository.ProductoRepository;
import com.uade.e_commerce.repository.ProductoVarianteRepository;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoVarianteRepository varianteRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository,
                           ProductoVarianteRepository varianteRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.varianteRepository = varianteRepository;
    }
    private ProductoDTO toDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setGenero(producto.getGenero());
        dto.setMarca(producto.getMarca());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setPrecioOriginal(producto.getPrecioOriginal());
        dto.setColor(producto.getColor());
        List<ProductoVariante> variantes = varianteRepository.findByProductoIdOrderByColorAscTalleAsc(producto.getId());
        dto.setStock(variantes.isEmpty()
                ? producto.getStock()
                : variantes.stream().mapToInt(ProductoVariante::getStock).sum());
        dto.setVariantes(variantes.stream()
                .map(variante -> new ProductoVarianteDTO(variante.getColor(), variante.getTalle(), variante.getStock()))
                .toList());
        dto.setImagenUrl(producto.getImagenUrl());
        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getId());
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
        }
        return dto;
    }

    public List<ProductoDTO> listarProductos() {
        return productoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }
    public ProductoDTO buscarProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con id " + id));
        return toDTO(producto);
    }
    public List<ProductoDTO> listarProductosPorCategoria(Long categoriaId) {
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResourceNotFoundException("No se encontró la categoría con id " + categoriaId);
        }
        return productoRepository.findByCategoriaId(categoriaId).stream()
                .map(this::toDTO)
                .toList();
    }
    public List<ProductoDTO> listarProductosPorGenero(String genero) {
        return productoRepository.findByGeneroIgnoreCase(genero).stream()
                .map(this::toDTO)
                .toList();
    }
    public ProductoDTO crearProducto(ProductoDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con id " + dto.getCategoriaId()));

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setGenero(dto.getGenero());
        producto.setMarca(dto.getMarca());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setPrecioOriginal(dto.getPrecioOriginal());
        producto.setColor(dto.getColor());
        producto.setStock(dto.getStock());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setCategoria(categoria);

        Producto guardado = productoRepository.save(producto);
        guardarVariantes(guardado, dto);
        return toDTO(guardado);
    }
    public ProductoDTO actualizarProducto(Long id, ProductoDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con id " + id));

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con id " + dto.getCategoriaId()));

        producto.setNombre(dto.getNombre());
        producto.setGenero(dto.getGenero());
        producto.setMarca(dto.getMarca());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setPrecioOriginal(dto.getPrecioOriginal());
        producto.setColor(dto.getColor());
        producto.setStock(dto.getStock());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setCategoria(categoria);

        Producto actualizado = productoRepository.save(producto);
        if (dto.getVariantes() != null) {
            varianteRepository.deleteAll(varianteRepository.findByProductoIdOrderByColorAscTalleAsc(id));
            guardarVariantes(actualizado, dto);
        } else {
            actualizarStockTotal(actualizado);
        }
        return toDTO(actualizado);
    }
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró el producto con id " + id);
        }
        productoRepository.deleteById(id);
    }

    private void guardarVariantes(Producto producto, ProductoDTO dto) {
        List<ProductoVarianteDTO> variantes = dto.getVariantes();
        if (variantes == null || variantes.isEmpty()) {
            variantes = new ArrayList<>();
            String color = producto.getColor() == null || producto.getColor().isBlank()
                    ? "Negro"
                    : producto.getColor();
            int stockPorTalle = dto.getStock() / 6;
            int stockRestante = dto.getStock() % 6;
            for (int talle = 38; talle <= 43; talle++) {
                int stock = stockPorTalle + (stockRestante-- > 0 ? 1 : 0);
                variantes.add(new ProductoVarianteDTO(color, talle, stock));
            }
        }

        List<ProductoVariante> entidades = variantes.stream()
                .map(variante -> new ProductoVariante(
                        null, producto, variante.getColor(), variante.getTalle(), variante.getStock()))
                .toList();
        varianteRepository.saveAll(entidades);
        actualizarStockTotal(producto);
    }

    private void actualizarStockTotal(Producto producto) {
        int stock = varianteRepository.findByProductoIdOrderByColorAscTalleAsc(producto.getId()).stream()
                .mapToInt(ProductoVariante::getStock)
                .sum();
        producto.setStock(stock);
        productoRepository.save(producto);
    }
}
