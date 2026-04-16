package com.ecommerce.cart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.cart.dto.ProductDTO;
import com.ecommerce.cart.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/products")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Prodotti Carrello Abstract", description = "Project work Abstract 2026")
public class ProductController {

	private final ProductService service;
<<<<<<< HEAD
	
	@Operation(summary = "Recupera tutti i prodotti")
=======

>>>>>>> branch 'main' of https://github.com/DarioZancuo/demo-cart.git
	@GetMapping
	public ResponseEntity<List<ProductDTO>> getAllProducts() {
		log.info("return all products");

		return ResponseEntity.ok(service.findAll());
	}
<<<<<<< HEAD
	
	@Operation(summary = "Recupera un prodotto per ID")
=======

>>>>>>> branch 'main' of https://github.com/DarioZancuo/demo-cart.git
	@GetMapping("/{id}")
	public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
		log.info("get product by id");

		return ResponseEntity.ok(service.findById(id));
	}
<<<<<<< HEAD
	
	@Operation(summary = "Crea un prodotto")
=======

>>>>>>> branch 'main' of https://github.com/DarioZancuo/demo-cart.git
	@PostMapping
	public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
		log.info("create product");

		return ResponseEntity.ok(service.create(dto));
	}
<<<<<<< HEAD
	
	@Operation(summary = "Aggiorna un prodotto per ID")
=======

>>>>>>> branch 'main' of https://github.com/DarioZancuo/demo-cart.git
	@PutMapping("/{id}")
	public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO dto, @PathVariable Long id) {
		log.info("update product");
		return ResponseEntity.ok(service.update(dto, id));

	}
<<<<<<< HEAD
	
	@Operation(summary = "Elimina un prodotto per ID")
=======

>>>>>>> branch 'main' of https://github.com/DarioZancuo/demo-cart.git
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		log.info("delete product");

		service.delete(id);
	}

	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<ProductDTO>> getByCategory(@PathVariable Long categoryId) {
		log.info("get products by category id: {}", categoryId);
		return ResponseEntity.ok(service.findByCategory(categoryId));
	}

	@GetMapping("/search")
	public ResponseEntity<List<ProductDTO>> searchByName(@RequestParam String name) {
		log.info("search products by name: {}", name);
		return ResponseEntity.ok(service.searchByName(name));
	}

}
