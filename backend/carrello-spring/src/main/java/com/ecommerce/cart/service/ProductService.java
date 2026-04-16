package com.ecommerce.cart.service;

import java.util.List;
import com.ecommerce.cart.entity.jpa.Category;

import org.springframework.stereotype.Service;

import com.ecommerce.cart.dto.ProductDTO;
import com.ecommerce.cart.entity.jpa.Product;
import com.ecommerce.cart.exception.custom.NotFoundException;
import com.ecommerce.cart.repository.CategoryRepository;
import com.ecommerce.cart.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

	private final ProductRepository repository;
	private final CategoryRepository categoryRepository;

	public List<ProductDTO> findAll() {
		List<Product> prodotti = repository.findAll();

		return prodotti.stream().map(this::convertToDTO).toList();
	};

	public ProductDTO findById(Long id) {
		Product product = repository.findById(id)
				.orElseThrow(() -> new NotFoundException("Prodotto non trovato con id: " + id));
		return convertToDTO(product);
	}

	public ProductDTO create(ProductDTO dto) {
		Category category = categoryRepository.findById(dto.id_cat())
				.orElseThrow(() -> new NotFoundException("Categoria non trovata con id: " + dto.id_cat()));

		Product product = new Product();
		product.setName(dto.name());
		product.setDescription(dto.description());
		product.setPrice(dto.price());
		product.setCategory(category);
		product.setImgUrl(dto.imgUrl());

		Product saved = repository.save(product);
		log.info("Prodotto creato con id: {}", saved.getId());
		return convertToDTO(saved);
	}

	public ProductDTO update(ProductDTO dto, Long id) {
		Product product = repository.findById(id)
				.orElseThrow(() -> new NotFoundException("Prodotto non trovato con id: " + id));

		Category category = categoryRepository.findById(dto.id_cat())
				.orElseThrow(() -> new NotFoundException("Categoria non trovata con id: " + dto.id_cat()));

		product.setName(dto.name());
		product.setDescription(dto.description());
		product.setPrice(dto.price());
		product.setCategory(category);
		product.setImgUrl(dto.imgUrl());

		Product saved = repository.save(product);
		log.info("Prodotto aggiornato con id: {}", saved.getId());
		return convertToDTO(saved);
	}

	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new NotFoundException("Prodotto non trovato con id: " + id);
		}
		repository.deleteById(id);
		log.info("Prodotto eliminato con id: {}", id);
	}

	// Metodo helper per la conversione (o usa MapStruct in futuro)
	private ProductDTO convertToDTO(Product p) {
		Category cat = p.getCategory();
		return new ProductDTO(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getImgUrl(), cat.getId(), cat.getName());
	}

	public List<ProductDTO> findByCategory(Long categoryId) {
		if (!categoryRepository.existsById(categoryId)) {
			throw new NotFoundException("Categoria non trovata con id: " + categoryId);
		}
		return repository.findByCategoryId(categoryId).stream().map(this::convertToDTO).toList();
	}

	public List<ProductDTO> searchByName(String name) {
		if (name == null || name.isBlank()) {
			return findAll();
		}
		return repository.findByNameContainingIgnoreCase(name).stream().map(this::convertToDTO).toList();
	}

}
