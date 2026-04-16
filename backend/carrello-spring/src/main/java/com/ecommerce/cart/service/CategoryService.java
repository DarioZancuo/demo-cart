package com.ecommerce.cart.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.cart.dto.CategoryDTO;
import com.ecommerce.cart.entity.jpa.Category;
import com.ecommerce.cart.entity.jpa.Product;
import com.ecommerce.cart.exception.custom.BadRequestException;
import com.ecommerce.cart.exception.custom.NotFoundException;
import com.ecommerce.cart.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
	private final CategoryRepository repository;

	public List<CategoryDTO> findAll() {
		List<Category> categories = repository.findAll();

		return categories.stream().map(this::convertToDTO).toList();
	};

	public CategoryDTO findById(Long id) {
		Category category = repository.findById(id)
				.orElseThrow(() -> new NotFoundException("Categoria non trovata con id: " + id));
		return convertToDTO(category);
	}

	public CategoryDTO create(CategoryDTO dto) {
		Category category = new Category();
		category.setName(dto.name());
		category.setDescription(dto.description());

		Category saved = repository.save(category);
		log.info("Categoria creata con id: {}", saved.getId());

		return convertToDTO(saved);
	}

	public CategoryDTO update(CategoryDTO dto, Long id) {
		Category category = repository.findById(id)
				.orElseThrow(() -> new NotFoundException("Categoria non trovata con id: " + id));

		category.setName(dto.name());
		category.setDescription(dto.description());

		Category saved = repository.save(category);
		log.info("Cateogria aggiornata con id: {}", saved.getId());

		return convertToDTO(saved);
	}

	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new NotFoundException("Categoria non trovata con id: " + id);
		}
		
		Category cat = repository.findById(id).get();
		List<Product> categoryProducts = cat.getProducts();
		if(categoryProducts.size() > 0) {
			throw new BadRequestException("Impossibile eliminare categoria associata a prodotti.");
		}
		
		repository.deleteById(id);
		log.info("Categoria eliminata con id: {}", id);
	}

	// Metodo helper per la conversione in DTO
	private CategoryDTO convertToDTO(Category c) {

		return new CategoryDTO(c.getId(), c.getName(), c.getDescription());
	}
}
