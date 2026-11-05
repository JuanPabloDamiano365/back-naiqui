package com.uade.e_commerce.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.dto.ItemPedidoDTO;
import com.uade.e_commerce.dto.CrearPedidoDTO;
import com.uade.e_commerce.dto.PedidoDTO;
import com.uade.e_commerce.exception.BusinessException;
import com.uade.e_commerce.exception.ResourceNotFoundException;
import com.uade.e_commerce.model.Carrito;
import com.uade.e_commerce.model.ItemCarrito;
import com.uade.e_commerce.model.ItemPedido;
import com.uade.e_commerce.model.Pedido;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.repository.CarritoRepository;
import com.uade.e_commerce.repository.PedidoRepository;
import com.uade.e_commerce.repository.ProductoRepository;

@Service
@Transactional
public class PedidoService {

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_CANCELADO = "CANCELADO";

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                          CarritoRepository carritoRepository,
                          ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
    }
    private ItemPedidoDTO toItemDTO(ItemPedido item) {
        Double subtotal = item.getPrecioUnitario() * item.getCantidad();
        return new ItemPedidoDTO(
                item.getId(),
                item.getProducto().getId(),
                item.getProducto().getNombre(),
                item.getTalle(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                subtotal
        );
    }
    private PedidoDTO toDTO(Pedido pedido) {
        List<ItemPedidoDTO> items = pedido.getItems().stream()
                .map(this::toItemDTO)
                .toList();
        return new PedidoDTO(
                pedido.getId(),
                pedido.getUsuario().getId(),
                pedido.getFecha(),
                pedido.getEstado(),
                pedido.getTotal(),
                items,
                pedido.getMetodoPago()
        );
    }
    public List<PedidoDTO> listarPedidos() {
        return pedidoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }
    public PedidoDTO buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el pedido con id " + id));
        return toDTO(pedido);
    }
    public List<PedidoDTO> listarPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toDTO)
                .toList();
    }

    public PedidoDTO crearPedidoDesdeCarrito(Long usuarioId) {
        return crearPedidoDesdeCarrito(usuarioId, null);
    }

    public PedidoDTO crearPedidoDesdeCarrito(Long usuarioId, CrearPedidoDTO datosEntrega) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario con id " + usuarioId + " no tiene un carrito asociado"));

        if (carrito.getItems().isEmpty()) {
            throw new BusinessException("No se puede confirmar la compra: el carrito está vacío");
        }
        // Primero validamos todo el stock para no dejar una compra a medias.
        for (ItemCarrito itemCarrito : carrito.getItems()) {
            Producto producto = itemCarrito.getProducto();
            if (producto.getStock() == null || producto.getStock() < itemCarrito.getCantidad()) {
                throw new BusinessException("Stock insuficiente para el producto '" + producto.getNombre() + "'");
            }
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(carrito.getUsuario());
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(ESTADO_PENDIENTE);
        if (datosEntrega == null) {
            pedido.setNombreEntrega(carrito.getUsuario().getNombre());
            pedido.setTelefonoEntrega(carrito.getUsuario().getTelefono());
            pedido.setDireccionEntrega(carrito.getUsuario().getDireccion());
        } else {
            pedido.setNombreEntrega(datosEntrega.getNombre());
            pedido.setTelefonoEntrega(datosEntrega.getTelefono());
            pedido.setDireccionEntrega(datosEntrega.getDireccion());
            pedido.setCodigoPostalEntrega(datosEntrega.getCodigoPostal());
            pedido.setCiudadEntrega(datosEntrega.getCiudad());
            pedido.setProvinciaEntrega(datosEntrega.getProvincia());
            pedido.setMetodoPago(datosEntrega.getMetodoPago());
        }

        double total = 0.0;
        for (ItemCarrito itemCarrito : carrito.getItems()) {
            Producto producto = itemCarrito.getProducto();
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProducto(producto);
            itemPedido.setTalle(itemCarrito.getTalle());
            itemPedido.setCantidad(itemCarrito.getCantidad());
            itemPedido.setPrecioUnitario(producto.getPrecio());
            pedido.getItems().add(itemPedido);

            total += producto.getPrecio() * itemCarrito.getCantidad();
            producto.setStock(producto.getStock() - itemCarrito.getCantidad());
            productoRepository.save(producto);
        }
        pedido.setTotal(total);

        Pedido guardado = pedidoRepository.save(pedido);
        carrito.getItems().clear();
        carritoRepository.save(carrito);

        return toDTO(guardado);
    }
    public PedidoDTO actualizarEstado(Long id, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el pedido con id " + id));
        pedido.setEstado(nuevoEstado.toUpperCase());
        Pedido actualizado = pedidoRepository.save(pedido);
        return toDTO(actualizado);
    }

    public PedidoDTO cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el pedido con id " + id));

        if (!ESTADO_PENDIENTE.equalsIgnoreCase(pedido.getEstado())) {
            throw new BusinessException("Solo se pueden cancelar pedidos en estado PENDIENTE");
        }
        for (ItemPedido item : pedido.getItems()) {
            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() + item.getCantidad());
            productoRepository.save(producto);
        }

        pedido.setEstado(ESTADO_CANCELADO);
        Pedido actualizado = pedidoRepository.save(pedido);
        return toDTO(actualizado);
    }
}
