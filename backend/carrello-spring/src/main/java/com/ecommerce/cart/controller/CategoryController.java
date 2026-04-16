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
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.cart.dto.CategoryDTO;
import com.ecommerce.cart.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/categories")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Category Controller", description = "API per la gestione delle categorie merceologiche")
public class CategoryController {

	private final CategoryService service;

	@Operation(summary = "Ottieni tutte le categorie", description = "Restituisce l'elenco completo delle categorie disponibili nel sistema")
	@GetMapping
	public ResponseEntity<List<CategoryDTO>> getAllCategories() {
		log.info("return all categories");

		return ResponseEntity.ok(service.findAll());
	}

	@Operation(summary = "Recupera categoria per ID", description = "Restituisce i dettagli di una specifica categoria tramite il suo ID")
	@ApiResponse(responseCode = "200", description = "Categoria trovata con successo")
	@ApiResponse(responseCode = "404", description = "Categoria non trovata")
	@GetMapping("/{id}")
	public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
		log.info("get category by id");

		return ResponseEntity.ok(service.findById(id));
	}

	@Operation(summary = "Crea una nuova categoria", description = "Permette di aggiungere una nuova categoria al sistema")
	@PostMapping
	public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO dto) {
		log.info("create category");

		return ResponseEntity.ok(service.create(dto));
	}

	@Operation(summary = "Aggiorna una categoria", description = "Modifica i dati di una categoria esistente identificata dall'ID")
	@PutMapping("/{id}")
	public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO dto, @PathVariable Long id) {
		log.info("update category");

		return ResponseEntity.ok(service.update(dto, id));
	}

	@Operation(summary = "Elimina una categoria", description = "Rimuove una categoria dal sistema tramite il suo ID")
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		log.info("delete category");

		service.delete(id);
	}
}
