package com.uade.e_commerce;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.model.ProductoVariante;
import com.uade.e_commerce.repository.ProductoRepository;
import com.uade.e_commerce.repository.ProductoVarianteRepository;

@SpringBootTest
class ECommerceApplicationTests {

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private ProductoVarianteRepository varianteRepository;

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

}
