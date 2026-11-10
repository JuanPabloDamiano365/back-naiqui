package com.uade.e_commerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.uade.e_commerce.exception.BusinessException;
import com.uade.e_commerce.dto.CrearPedidoDTO;
import com.uade.e_commerce.model.Carrito;
import com.uade.e_commerce.model.ItemCarrito;
import com.uade.e_commerce.model.ItemPedido;
import com.uade.e_commerce.model.Pedido;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.model.ProductoVariante;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.CarritoRepository;
import com.uade.e_commerce.repository.PedidoRepository;
import com.uade.e_commerce.repository.ProductoVarianteRepository;

class PedidoServiceTest {

    @Test
    void vendedorSoloRecibeSusItemsYElSubtotalCorrespondiente() {
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        ProductoVarianteRepository varianteRepository = mock(ProductoVarianteRepository.class);
        Usuario vendedor = new Usuario();
        vendedor.setEmail("seller@example.com");
        Usuario otroVendedor = new Usuario();
        otroVendedor.setEmail("other@example.com");
        Producto propio = producto(10);
        propio.setCreador(vendedor);
        Producto ajeno = producto(10);
        ajeno.setId(5L);
        ajeno.setNombre("Otro producto");
        ajeno.setPrecio(50.0);
        ajeno.setCreador(otroVendedor);
        Pedido pedido = pedidoExistente();
        pedido.setTotal(150.0);
        pedido.setItems(List.of(itemPedido(pedido, propio, 1), itemPedido(pedido, ajeno, 1)));
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));
        PedidoService service = new PedidoService(pedidoRepository, carritoRepository, varianteRepository);
        var authentication = new UsernamePasswordAuthenticationToken(
                "seller@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_VENDEDOR")));

        var result = service.listarPedidos(authentication);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getItems().size());
        assertEquals(4L, result.get(0).getItems().get(0).getProductoId());
        assertEquals(100.0, result.get(0).getTotal());
    }

    @Test
    void vendedorNoPuedeCancelarPedidoCompleto() {
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        ProductoVarianteRepository varianteRepository = mock(ProductoVarianteRepository.class);
        Pedido pedido = pedidoExistente();
        when(pedidoRepository.findById(12L)).thenReturn(Optional.of(pedido));
        PedidoService service = new PedidoService(pedidoRepository, carritoRepository, varianteRepository);
        var authentication = new UsernamePasswordAuthenticationToken(
                "seller@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_VENDEDOR")));

        BusinessException error = assertThrows(
                BusinessException.class, () -> service.actualizarEstado(12L, "CANCELADO", authentication));

        assertEquals("Solo un administrador puede cancelar el pedido completo", error.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void crearPedidoDescuentaElStockDeLaVarianteComprada() {
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        ProductoVarianteRepository varianteRepository = mock(ProductoVarianteRepository.class);
        Producto producto = producto(10);
        ProductoVariante variante = variante(producto, 3);
        Carrito carrito = carrito(producto, 1);

        when(carritoRepository.findByUsuarioId(7L)).thenReturn(Optional.of(carrito));
        when(varianteRepository.findByProductoIdAndColorAndTalleForUpdate(4L, "Azul", 40))
                .thenReturn(Optional.of(variante));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(12L);
            return pedido;
        });

        PedidoService service = new PedidoService(
                pedidoRepository, carritoRepository, varianteRepository);

        var pedido = service.crearPedidoDesdeCarrito(7L);

        assertEquals(2, variante.getStock());
        assertEquals("Azul", pedido.getItems().get(0).getColor());
        assertEquals(0, carrito.getItems().size());
        verify(varianteRepository).save(variante);
    }

    @Test
    void crearPedidoGuardaYDevuelveTodosLosDatosDeEntrega() {
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        ProductoVarianteRepository varianteRepository = mock(ProductoVarianteRepository.class);
        Producto producto = producto(10);
        ProductoVariante variante = variante(producto, 3);
        Carrito carrito = carrito(producto, 1);
        CrearPedidoDTO datosEntrega = new CrearPedidoDTO(
                "Ana Compradora", "1122334455", "Calle 123", "1000",
                "Buenos Aires", "CABA", "TARJETA");

        when(carritoRepository.findByUsuarioId(7L)).thenReturn(Optional.of(carrito));
        when(varianteRepository.findByProductoIdAndColorAndTalleForUpdate(4L, "Azul", 40))
                .thenReturn(Optional.of(variante));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(12L);
            return pedido;
        });

        PedidoService service = new PedidoService(
                pedidoRepository, carritoRepository, varianteRepository);

        var pedido = service.crearPedidoDesdeCarrito(7L, datosEntrega);

        assertEquals("Ana Compradora", pedido.getNombreEntrega());
        assertEquals("1122334455", pedido.getTelefonoEntrega());
        assertEquals("Calle 123", pedido.getDireccionEntrega());
        assertEquals("1000", pedido.getCodigoPostalEntrega());
        assertEquals("Buenos Aires", pedido.getCiudadEntrega());
        assertEquals("CABA", pedido.getProvinciaEntrega());
    }

    @Test
    void crearPedidoRechazaUnaVarianteSinStock() {
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        ProductoVarianteRepository varianteRepository = mock(ProductoVarianteRepository.class);
        Producto producto = producto(10);
        ProductoVariante variante = variante(producto, 0);
        Carrito carrito = carrito(producto, 1);

        when(carritoRepository.findByUsuarioId(7L)).thenReturn(Optional.of(carrito));
        when(varianteRepository.findByProductoIdAndColorAndTalleForUpdate(4L, "Azul", 40))
                .thenReturn(Optional.of(variante));

        PedidoService service = new PedidoService(
                pedidoRepository, carritoRepository, varianteRepository);

        BusinessException error = assertThrows(
                BusinessException.class, () -> service.crearPedidoDesdeCarrito(7L));

        assertEquals("Stock insuficiente para 'Zapatilla de prueba' en color Azul y talle 40", error.getMessage());
        assertEquals(0, variante.getStock());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void actualizarEstadoPermiteUnEstadoDeSeguimientoValido() {
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        ProductoVarianteRepository varianteRepository = mock(ProductoVarianteRepository.class);
        Pedido pedido = pedidoExistente();

        when(pedidoRepository.findById(12L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        PedidoService service = new PedidoService(
                pedidoRepository, carritoRepository, varianteRepository);

        var actualizado = service.actualizarEstado(12L, "enviado");

        assertEquals("ENVIADO", actualizado.getEstado());
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void actualizarEstadoRechazaEstadosFueraDelFlujoPermitido() {
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        ProductoVarianteRepository varianteRepository = mock(ProductoVarianteRepository.class);
        Pedido pedido = pedidoExistente();

        when(pedidoRepository.findById(12L)).thenReturn(Optional.of(pedido));

        PedidoService service = new PedidoService(
                pedidoRepository, carritoRepository, varianteRepository);

        assertThrows(BusinessException.class, () -> service.actualizarEstado(12L, "DESPACHADO"));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    private Pedido pedidoExistente() {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setNombre("Usuario");
        usuario.setEmail("usuario@example.com");

        Pedido pedido = new Pedido();
        pedido.setId(12L);
        pedido.setUsuario(usuario);
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(100.0);
        pedido.setItems(new ArrayList<>());
        return pedido;
    }

    private Producto producto(int stock) {
        Producto producto = new Producto();
        producto.setId(4L);
        producto.setNombre("Zapatilla de prueba");
        producto.setColor("Azul");
        producto.setPrecio(100.0);
        producto.setStock(stock);
        return producto;
    }

    private ProductoVariante variante(Producto producto, int stock) {
        ProductoVariante variante = new ProductoVariante();
        variante.setId(9L);
        variante.setProducto(producto);
        variante.setColor("Azul");
        variante.setTalle(40);
        variante.setStock(stock);
        return variante;
    }

    private ItemPedido itemPedido(Pedido pedido, Producto producto, int cantidad) {
        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setProducto(producto);
        item.setCantidad(cantidad);
        item.setPrecioUnitario(producto.getPrecio());
        item.setTalle(40);
        item.setColor(producto.getColor());
        return item;
    }

    private Carrito carrito(Producto producto, int cantidad) {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setNombre("Usuario");

        Carrito carrito = new Carrito();
        carrito.setId(3L);
        carrito.setUsuario(usuario);

        ItemCarrito item = new ItemCarrito();
        item.setCarrito(carrito);
        item.setProducto(producto);
        item.setColor("Azul");
        item.setTalle(40);
        item.setCantidad(cantidad);
        carrito.setItems(new ArrayList<>(List.of(item)));
        return carrito;
    }
}
