package com.uade.e_commerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.uade.e_commerce.model.Carrito;
import com.uade.e_commerce.model.ItemCarrito;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.CarritoRepository;
import com.uade.e_commerce.repository.ItemCarritoRepository;
import com.uade.e_commerce.repository.ProductoRepository;
import com.uade.e_commerce.repository.ProductoVarianteRepository;
import com.uade.e_commerce.repository.UsuarioRepository;

class CarritoServiceTest {

    @Test
    void carritoDevuelveLaImagenDeCadaProducto() {
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        ItemCarritoRepository itemCarritoRepository = mock(ItemCarritoRepository.class);
        ProductoRepository productoRepository = mock(ProductoRepository.class);
        ProductoVarianteRepository varianteRepository = mock(ProductoVarianteRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);

        Usuario usuario = new Usuario();
        usuario.setId(3L);
        Producto producto = new Producto();
        producto.setId(9L);
        producto.setNombre("Zapatilla");
        producto.setImagenUrl("https://example.com/zapatilla.jpg");
        producto.setPrecio(100.0);

        Carrito carrito = new Carrito();
        carrito.setId(7L);
        carrito.setUsuario(usuario);
        carrito.setItems(new ArrayList<>());
        ItemCarrito item = new ItemCarrito();
        item.setId(11L);
        item.setCarrito(carrito);
        item.setProducto(producto);
        item.setTalle(40);
        item.setColor("Azul");
        item.setCantidad(1);
        carrito.getItems().add(item);

        when(carritoRepository.findByUsuarioId(3L)).thenReturn(Optional.of(carrito));
        CarritoService service = new CarritoService(
                carritoRepository,
                itemCarritoRepository,
                productoRepository,
                varianteRepository,
                usuarioRepository);

        var resultado = service.buscarCarritoPorUsuario(3L);

        assertEquals("https://example.com/zapatilla.jpg", resultado.getItems().get(0).getImagenUrl());
    }

    @Test
    void buscarCarritoDebeCrearUnoCuandoElUsuarioNoTiene() {
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        ItemCarritoRepository itemCarritoRepository = mock(ItemCarritoRepository.class);
        ProductoRepository productoRepository = mock(ProductoRepository.class);
        ProductoVarianteRepository varianteRepository = mock(ProductoVarianteRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        Usuario usuario = new Usuario();
        usuario.setId(3L);

        when(carritoRepository.findByUsuarioId(3L)).thenReturn(Optional.empty());
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(usuario));
        when(carritoRepository.save(any(Carrito.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CarritoService service = new CarritoService(
                carritoRepository,
                itemCarritoRepository,
                productoRepository,
                varianteRepository,
                usuarioRepository);

        var carrito = service.buscarCarritoPorUsuario(3L);

        assertEquals(3L, carrito.getUsuarioId());
        assertNotNull(carrito.getFechaCreacion());
        assertEquals(0, carrito.getItems().size());
        verify(carritoRepository).save(any(Carrito.class));
    }
}
