package com.uade.e_commerce.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.dto.UsuarioDTO;
import com.uade.e_commerce.exception.BusinessException;
import com.uade.e_commerce.exception.ResourceNotFoundException;
import com.uade.e_commerce.model.Carrito;
import com.uade.e_commerce.model.RolUsuario;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.CarritoRepository;
import com.uade.e_commerce.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CarritoRepository carritoRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository, CarritoRepository carritoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.carritoRepository = carritoRepository;
    }
    private UsuarioDTO toDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setPassword(null);
        dto.setRol(usuario.getRol() != null ? usuario.getRol() : RolUsuario.USUARIO);
        dto.setDireccion(usuario.getDireccion());
        dto.setTelefono(usuario.getTelefono());
        return dto;
    }
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }
    public UsuarioDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con id " + id));
        return toDTO(usuario);
    }

    public UsuarioDTO cambiarRol(Long id, RolUsuario rol) {
        if (rol == null) {
            throw new BusinessException("Debe indicarse un rol válido");
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con id " + id));
        usuario.setRol(rol);
        return toDTO(usuarioRepository.save(usuario));
    }

    public UsuarioDTO registrarUsuario(UsuarioDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Ya existe un usuario registrado con el email " + dto.getEmail());
        }
        if (dto.getPassword().length() < 8) {
            throw new BusinessException("La contraseña debe tener al menos 8 caracteres");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(RolUsuario.USUARIO);
        usuario.setDireccion(dto.getDireccion());
        usuario.setTelefono(dto.getTelefono());

        Usuario guardado = usuarioRepository.save(usuario);
        Carrito carrito = new Carrito();
        carrito.setUsuario(guardado);
        carrito.setFechaCreacion(LocalDateTime.now());
        carritoRepository.save(carrito);

        return toDTO(guardado);
    }
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con id " + id));

        usuario.setNombre(dto.getNombre());
        usuario.setDireccion(dto.getDireccion());
        usuario.setTelefono(dto.getTelefono());

        Usuario actualizado = usuarioRepository.save(usuario);
        return toDTO(actualizado);
    }
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró el usuario con id " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
