package com.uade.e_commerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.uade.e_commerce.model.Carrito;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.CarritoRepository;
import com.uade.e_commerce.repository.ItemCarritoRepository;
import com.uade.e_commerce.repository.ProductoRepository;
import com.uade.e_commerce.repository.UsuarioRepository;

class CarritoServiceTest {

    @Test
    void buscarCarritoDebeCrearUnoCuandoElUsuarioNoTiene() {
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        ItemCarritoRepository itemCarritoRepository = mock(ItemCarritoRepository.class);
        ProductoRepository productoRepository = mock(ProductoRepository.class);
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
                usuarioRepository);

        var carrito = service.buscarCarritoPorUsuario(3L);

        assertEquals(3L, carrito.getUsuarioId());
        assertNotNull(carrito.getFechaCreacion());
        assertEquals(0, carrito.getItems().size());
        verify(carritoRepository).save(any(Carrito.class));
    }
}
