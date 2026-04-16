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
@Tag(name = "Product Controller", description = "API per la gestione del catalogo prodotti")
public class ProductController {

	private final ProductService service;

	@Operation(summary = "Ottieni tutti i prodotti", description = "Ritorna una lista completa di tutti i prodotti a catalogo")
	@GetMapping
	public ResponseEntity<List<ProductDTO>> getAllProducts() {
		log.info("return all products");

		return ResponseEntity.ok(service.findAll());
	}

	
	@Operation(summary = "Recupera un prodotto per ID", description = "Fornisce i dettagli di un singolo prodotto cercato tramite il suo identificativo univoco")
	@GetMapping("/{id}")
	public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
		log.info("get product by id");

		return ResponseEntity.ok(service.findById(id));
	}
	
	@Operation(summary = "Crea un nuovo prodotto", description = "Aggiunge un nuovo prodotto al database. L'ID viene generato automaticamente.")
	@PostMapping
	public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
		log.info("create product");

		return ResponseEntity.ok(service.create(dto));
	}
	
	@Operation(summary = "Aggiorna un prodotto esistente", description = "Modifica i dati di un prodotto esistente identificato dall'ID")
	@PutMapping("/{id}")
	public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO dto, @PathVariable Long id) {
		log.info("update product");
		return ResponseEntity.ok(service.update(dto, id));

	}
	
	@Operation(summary = "Elimina un prodotto per ID", description = "Rimuove definitivamente il prodotto dal sistema")	
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		log.info("delete product");

		service.delete(id);
	}

	@Operation(summary = "Filtra prodotti per categoria", description = "Restituisce una lista di prodotti appartenenti a una specifica categoria")
	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<ProductDTO>> getByCategory(@PathVariable Long categoryId) {
		log.info("get products by category id: {}", categoryId);
		return ResponseEntity.ok(service.findByCategory(categoryId));
	}

	@Operation(summary = "Cerca prodotti per nome", description = "Esegue una ricerca testuale nel catalogo prodotti")
	@GetMapping("/search")
	public ResponseEntity<List<ProductDTO>> searchByName(@RequestParam String name) {
		log.info("search products by name: {}", name);
		return ResponseEntity.ok(service.searchByName(name));
	}

}
