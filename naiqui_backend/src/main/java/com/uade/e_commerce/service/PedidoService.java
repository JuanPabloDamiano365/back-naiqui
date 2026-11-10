package com.uade.e_commerce.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
import com.uade.e_commerce.model.ProductoVariante;
import com.uade.e_commerce.repository.CarritoRepository;
import com.uade.e_commerce.repository.PedidoRepository;
import com.uade.e_commerce.repository.ProductoVarianteRepository;

@Service
@Transactional
public class PedidoService {

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_CANCELADO = "CANCELADO";
    private static final List<String> ESTADOS_PERMITIDOS = List.of(
            ESTADO_PENDIENTE, "CONFIRMADO", "EN_PREPARACION", "ENVIADO", "ENTREGADO", ESTADO_CANCELADO);

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final ProductoVarianteRepository varianteRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                          CarritoRepository carritoRepository,
                          ProductoVarianteRepository varianteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.carritoRepository = carritoRepository;
        this.varianteRepository = varianteRepository;
    }
    private ItemPedidoDTO toItemDTO(ItemPedido item) {
        Double subtotal = item.getPrecioUnitario() * item.getCantidad();
        return new ItemPedidoDTO(
                item.getId(),
                item.getProducto().getId(),
                item.getProducto().getNombre(),
                item.getTalle(),
                item.getColor() == null ? item.getProducto().getColor() : item.getColor(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                subtotal
        );
    }
    private PedidoDTO toDTO(Pedido pedido) {
        return toDTO(pedido, null);
    }

    private PedidoDTO toDTO(Pedido pedido, Authentication authentication) {
        boolean vendedor = authentication != null
                && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_VENDEDOR"));
        List<ItemPedidoDTO> items = pedido.getItems().stream()
                .filter(item -> !vendedor || perteneceAlVendedor(item, authentication))
                .map(this::toItemDTO)
                .toList();
        double total = items.stream().mapToDouble(ItemPedidoDTO::getSubtotal).sum();
        return new PedidoDTO(
                pedido.getId(),
                pedido.getUsuario().getId(),
                pedido.getUsuario().getNombre(),
                pedido.getUsuario().getEmail(),
                pedido.getFecha(),
                pedido.getEstado(),
                vendedor ? total : pedido.getTotal(),
                items,
                pedido.getMetodoPago(),
                pedido.getNombreEntrega(),
                pedido.getTelefonoEntrega(),
                pedido.getDireccionEntrega(),
                pedido.getCodigoPostalEntrega(),
                pedido.getCiudadEntrega(),
                pedido.getProvinciaEntrega()
        );
    }
    public List<PedidoDTO> listarPedidos() {
        return listarPedidos(null);
    }

    public List<PedidoDTO> listarPedidos(Authentication authentication) {
        return pedidoRepository.findAll().stream()
                .map(pedido -> toDTO(pedido, authentication))
                .filter(pedido -> !pedido.getItems().isEmpty())
                .toList();
    }

    public PedidoDTO buscarPedidoPorId(Long id) {
        return buscarPedidoPorId(id, null);
    }

    public PedidoDTO buscarPedidoPorId(Long id, Authentication authentication) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el pedido con id " + id));
        return toDTO(pedido, authentication);
    }

    public List<PedidoDTO> listarPedidosPorUsuario(Long usuarioId) {
        return listarPedidosPorUsuario(usuarioId, null);
    }

    public List<PedidoDTO> listarPedidosPorUsuario(Long usuarioId, Authentication authentication) {
        return pedidoRepository.findByUsuarioId(usuarioId).stream()
                .map(pedido -> toDTO(pedido, authentication))
                .filter(pedido -> !pedido.getItems().isEmpty())
                .toList();
    }

    private boolean perteneceAlVendedor(ItemPedido item, Authentication authentication) {
        return item.getProducto().getCreador() != null
                && authentication.getName().equalsIgnoreCase(item.getProducto().getCreador().getEmail());
    }

    private boolean esVendedor(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_VENDEDOR"));
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
        List<ItemCarrito> items = carrito.getItems().stream()
                .sorted(Comparator.comparing((ItemCarrito item) -> item.getProducto().getId())
                        .thenComparing(item -> colorDe(item), Comparator.nullsFirst(String::compareTo))
                        .thenComparing(ItemCarrito::getTalle))
                .toList();
        Map<Long, ProductoVariante> variantesBloqueadas = new LinkedHashMap<>();
        Map<Long, Integer> cantidadesPorVariante = new LinkedHashMap<>();
        for (ItemCarrito itemCarrito : items) {
            Producto producto = itemCarrito.getProducto();
            String color = colorDe(itemCarrito);
            ProductoVariante variante = varianteRepository
                    .findByProductoIdAndColorAndTalleForUpdate(
                            producto.getId(), color, itemCarrito.getTalle())
                    .orElseThrow(() -> new BusinessException("No hay stock disponible para '"
                            + producto.getNombre() + "' en color " + color
                            + " y talle " + itemCarrito.getTalle()));
            variantesBloqueadas.put(variante.getId(), variante);
            cantidadesPorVariante.merge(variante.getId(), itemCarrito.getCantidad(), Integer::sum);
        }
        for (Map.Entry<Long, Integer> entrada : cantidadesPorVariante.entrySet()) {
            ProductoVariante variante = variantesBloqueadas.get(entrada.getKey());
            if (variante.getStock() == null || variante.getStock() < entrada.getValue()) {
                throw new BusinessException("Stock insuficiente para '" + variante.getProducto().getNombre()
                        + "' en color " + variante.getColor() + " y talle " + variante.getTalle());
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
        for (ItemCarrito itemCarrito : items) {
            Producto producto = itemCarrito.getProducto();
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProducto(producto);
            itemPedido.setTalle(itemCarrito.getTalle());
            itemPedido.setColor(colorDe(itemCarrito));
            itemPedido.setCantidad(itemCarrito.getCantidad());
            itemPedido.setPrecioUnitario(producto.getPrecio());
            pedido.getItems().add(itemPedido);

            total += producto.getPrecio() * itemCarrito.getCantidad();
        }
        for (Map.Entry<Long, Integer> entrada : cantidadesPorVariante.entrySet()) {
            ProductoVariante variante = variantesBloqueadas.get(entrada.getKey());
            variante.setStock(variante.getStock() - entrada.getValue());
            varianteRepository.save(variante);
        }
        pedido.setTotal(total);

        Pedido guardado = pedidoRepository.save(pedido);
        carrito.getItems().clear();
        carritoRepository.save(carrito);

        return toDTO(guardado);
    }
    public PedidoDTO actualizarEstado(Long id, String nuevoEstado) {
        return actualizarEstado(id, nuevoEstado, null);
    }

    public PedidoDTO actualizarEstado(Long id, String nuevoEstado, Authentication authentication) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el pedido con id " + id));
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new BusinessException("Debe indicarse el nuevo estado");
        }
        String estadoNormalizado = nuevoEstado.trim().toUpperCase();
        if (!ESTADOS_PERMITIDOS.contains(estadoNormalizado)) {
            throw new BusinessException("Estado inválido. Los estados permitidos son: "
                    + String.join(", ", ESTADOS_PERMITIDOS));
        }
        if (ESTADO_CANCELADO.equals(estadoNormalizado)) {
            if (esVendedor(authentication)) {
                throw new BusinessException("Solo un administrador puede cancelar el pedido completo");
            }
            if (!ESTADO_PENDIENTE.equalsIgnoreCase(pedido.getEstado())) {
                throw new BusinessException("Solo se pueden cancelar pedidos en estado PENDIENTE");
            }
            return cancelarPedido(id, authentication);
        }
        pedido.setEstado(estadoNormalizado);
        Pedido actualizado = pedidoRepository.save(pedido);
        return toDTO(actualizado, authentication);
    }

    public PedidoDTO cancelarPedido(Long id) {
        return cancelarPedido(id, null);
    }

    public PedidoDTO cancelarPedido(Long id, Authentication authentication) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el pedido con id " + id));

        if (!ESTADO_PENDIENTE.equalsIgnoreCase(pedido.getEstado())) {
            throw new BusinessException("Solo se pueden cancelar pedidos en estado PENDIENTE");
        }
        for (ItemPedido item : pedido.getItems()) {
            Producto producto = item.getProducto();
            String color = item.getColor() == null ? producto.getColor() : item.getColor();
            ProductoVariante variante = varianteRepository
                    .findByProductoIdAndColorAndTalleForUpdate(producto.getId(), color, item.getTalle())
                    .orElseThrow(() -> new BusinessException("No se encontró la variante de '"
                            + producto.getNombre() + "' para reponer el stock"));
            variante.setStock(variante.getStock() + item.getCantidad());
            varianteRepository.save(variante);
        }

        pedido.setEstado(ESTADO_CANCELADO);
        Pedido actualizado = pedidoRepository.save(pedido);
        return toDTO(actualizado, authentication);
    }

    private String colorDe(ItemCarrito item) {
        return item.getColor() == null ? item.getProducto().getColor() : item.getColor();
    }
}
