package com.uade.e_commerce;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.dto.ProductoDTO;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.model.ProductoVariante;
import com.uade.e_commerce.repository.ProductoRepository;
import com.uade.e_commerce.repository.ProductoVarianteRepository;
import com.uade.e_commerce.service.ProductoService;

@SpringBootTest
class ECommerceApplicationTests {

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private ProductoVarianteRepository varianteRepository;

	@Autowired
	private ProductoService productoService;

	@Test
	void contextLoads() {
	}

	@Test
	void catalogoCargaStockDiferenciadoPorColorYTalle() {
		List<Producto> productos = productoRepository.findAll();
		assertFalse(productos.isEmpty());

		List<ProductoVariante> variantes = varianteRepository
				.findByProductoIdOrderByColorAscTalleAsc(productos.get(0).getId());
		assertTrue(variantes.stream().map(ProductoVariante::getColor).distinct().count() > 1);
		assertTrue(variantes.stream().map(ProductoVariante::getTalle).distinct().count() > 1);
		assertTrue(variantes.stream().map(ProductoVariante::getStock).distinct().count() > 1);
	}

	@Test
	@Transactional
	void actualizarPrecioConservaVariantesExistentes() {
		Producto producto = productoRepository.findAll().get(0);
		List<Long> idsVariantes = varianteRepository
				.findByProductoIdOrderByColorAscTalleAsc(producto.getId()).stream()
				.map(ProductoVariante::getId)
				.toList();
		ProductoDTO actualizacion = productoService.buscarProductoPorId(producto.getId());
		actualizacion.setPrecio(actualizacion.getPrecio() + 100);
		actualizacion.setPrecioOriginal(null);

		ProductoDTO actualizado = productoService.actualizarProducto(producto.getId(), actualizacion);

		assertEquals(actualizacion.getPrecio(), actualizado.getPrecio());
		assertEquals(idsVariantes, varianteRepository
				.findByProductoIdOrderByColorAscTalleAsc(producto.getId()).stream()
				.map(ProductoVariante::getId)
				.toList());
	}

}
