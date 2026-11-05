package com.uade.e_commerce.service;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.dto.AgregarItemCarritoDTO;
import com.uade.e_commerce.dto.ActualizarCantidadCarritoDTO;
import com.uade.e_commerce.dto.CarritoDTO;
import com.uade.e_commerce.dto.ItemCarritoDTO;
import com.uade.e_commerce.exception.BusinessException;
import com.uade.e_commerce.exception.ResourceNotFoundException;
import com.uade.e_commerce.model.Carrito;
import com.uade.e_commerce.model.ItemCarrito;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.CarritoRepository;
import com.uade.e_commerce.repository.ItemCarritoRepository;
import com.uade.e_commerce.repository.ProductoRepository;
import com.uade.e_commerce.repository.UsuarioRepository;

@Service
@Transactional
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarritoService(CarritoRepository carritoRepository,
                           ItemCarritoRepository itemCarritoRepository,
                           ProductoRepository productoRepository,
                           UsuarioRepository usuarioRepository) {
        this.carritoRepository = carritoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }
    private ItemCarritoDTO toItemDTO(ItemCarrito item) {
        Double subtotal = item.getProducto().getPrecio() * item.getCantidad();
        return new ItemCarritoDTO(
                item.getId(),
                item.getProducto().getId(),
                item.getProducto().getNombre(),
                item.getProducto().getPrecio(),
                item.getTalle(),
                item.getCantidad(),
                subtotal
        );
    }
    private CarritoDTO toDTO(Carrito carrito) {
        List<ItemCarritoDTO> items = carrito.getItems().stream()
                .map(this::toItemDTO)
                .toList();
        Double total = items.stream().mapToDouble(ItemCarritoDTO::getSubtotal).sum();
        return new CarritoDTO(carrito.getId(), carrito.getUsuario().getId(), carrito.getFechaCreacion(), items, total);
    }
    private Carrito buscarCarritoPorUsuario1(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepository.findById(usuarioId)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "No se encontró el usuario con id " + usuarioId));
                    Carrito carrito = new Carrito();
                    carrito.setUsuario(usuario);
                    carrito.setFechaCreacion(LocalDateTime.now());
                    return carritoRepository.save(carrito);
                });
    }
    public CarritoDTO buscarCarritoPorUsuario(Long usuarioId) {
        return toDTO(buscarCarritoPorUsuario1(usuarioId));
    }

    public CarritoDTO agregarItem(Long usuarioId, AgregarItemCarritoDTO itemRequest) {
        Carrito carrito = buscarCarritoPorUsuario1(usuarioId);

        Producto producto = productoRepository.findById(itemRequest.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con id " + itemRequest.getProductoId()));

        ItemCarrito item = itemCarritoRepository
                .findByCarritoIdAndProductoIdAndTalle(carrito.getId(), producto.getId(), itemRequest.getTalle())
                .orElse(null);

        // Si ya existe la misma variante, acumulamos la cantidad.
        int cantidadFinal = itemRequest.getCantidad() + (item == null ? 0 : item.getCantidad());
        if (producto.getStock() == null || producto.getStock() < cantidadFinal) {
            throw new BusinessException("Stock insuficiente para el producto '" + producto.getNombre() + "'");
        }
        if (item != null) {
            item.setCantidad(cantidadFinal);
        } else {
            item = new ItemCarrito();
            item.setCarrito(carrito);
            item.setProducto(producto);
            item.setTalle(itemRequest.getTalle());
            item.setCantidad(itemRequest.getCantidad());
            carrito.getItems().add(item);
        }

        itemCarritoRepository.save(item);
        return toDTO(carrito);
    }

    public CarritoDTO actualizarCantidad(Long usuarioId, Long itemId, ActualizarCantidadCarritoDTO request) {
        Carrito carrito = buscarCarritoPorUsuario1(usuarioId);
        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el item de carrito con id " + itemId));
        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new BusinessException("El item indicado no pertenece al carrito de este usuario");
        }
        Producto producto = item.getProducto();
        if (producto.getStock() == null || producto.getStock() < request.getCantidad()) {
            throw new BusinessException("Stock insuficiente para el producto '" + producto.getNombre() + "'");
        }
        item.setCantidad(request.getCantidad());
        itemCarritoRepository.save(item);
        return toDTO(carrito);
    }
    public CarritoDTO eliminarItem(Long usuarioId, Long itemId) {
        Carrito carrito = buscarCarritoPorUsuario1(usuarioId);

        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el item de carrito con id " + itemId));
        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new BusinessException("El item indicado no pertenece al carrito de este usuario");
        }

        carrito.getItems().remove(item);
        return toDTO(carrito);
    }
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = buscarCarritoPorUsuario1(usuarioId);
        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }
}
