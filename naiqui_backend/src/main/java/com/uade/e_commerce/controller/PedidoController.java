package com.uade.e_commerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.e_commerce.dto.ActualizarEstadoDTO;
import com.uade.e_commerce.dto.CrearPedidoDTO;
import com.uade.e_commerce.dto.PedidoDTO;
import com.uade.e_commerce.service.PedidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/pedidos")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<PedidoDTO>> listarPedidos(Authentication authentication) {
        return ResponseEntity.ok(pedidoService.listarPedidos(authentication));
    }

    @GetMapping("/pedidos/{id}")
    @PreAuthorize("@pedidoAuthorizationService.puedeVerPedido(#p0, authentication)")
    public ResponseEntity<PedidoDTO> buscarPedidoPorId(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(pedidoService.buscarPedidoPorId(id, authentication));
    }

    @GetMapping("/usuarios/{usuarioId}/pedidos")
    @PreAuthorize("@usuarioAuthorizationService.puedeAcceder(#p0, authentication)")
    public ResponseEntity<List<PedidoDTO>> listarPedidosPorUsuario(
            @PathVariable Long usuarioId, Authentication authentication) {
        return ResponseEntity.ok(pedidoService.listarPedidosPorUsuario(usuarioId, authentication));
    }

    @PostMapping("/usuarios/{usuarioId}/pedidos")
    @PreAuthorize("hasRole('USUARIO') and @usuarioAuthorizationService.puedeAcceder(#p0, authentication)")
    public ResponseEntity<PedidoDTO> crearPedido(@PathVariable Long usuarioId,
                                                  @Valid @RequestBody(required = false) CrearPedidoDTO request) {
        PedidoDTO creado = pedidoService.crearPedidoDesdeCarrito(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/pedidos/{id}/estado")
    @PreAuthorize("@pedidoAuthorizationService.puedeVerPedido(#id, authentication)")
    public ResponseEntity<PedidoDTO> actualizarEstado(
            @PathVariable Long id, @Valid @RequestBody ActualizarEstadoDTO dto, Authentication authentication) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, dto.getEstado(), authentication));
    }

    @PutMapping("/pedidos/{id}/cancelar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoDTO> cancelarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id));
    }
}
