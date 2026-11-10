package com.uade.e_commerce.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.dto.CategoriaDTO;
import com.uade.e_commerce.exception.BusinessException;
import com.uade.e_commerce.exception.ResourceNotFoundException;
import com.uade.e_commerce.model.Categoria;
import com.uade.e_commerce.repository.CategoriaRepository;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }
    private CategoriaDTO toDTO(Categoria categoria) {
        return new CategoriaDTO(categoria.getId(), categoria.getNombre());
    }
    public List<CategoriaDTO> listarCategorias() {
        return categoriaRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }
    public CategoriaDTO buscarCategoriaPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con id " + id));
        return toDTO(categoria);
    }
    public CategoriaDTO crearCategoria(CategoriaDTO dto) {
        if (categoriaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new BusinessException("Ya existe una categoría con el nombre '" + dto.getNombre() + "'");
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        Categoria guardada = categoriaRepository.save(categoria);
        return toDTO(guardada);
    }
    public CategoriaDTO actualizarCategoria(Long id, CategoriaDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con id " + id));
        categoria.setNombre(dto.getNombre());
        Categoria actualizada = categoriaRepository.save(categoria);
        return toDTO(actualizada);
    }
    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró la categoría con id " + id);
        }
        categoriaRepository.deleteById(id);
    }
}
