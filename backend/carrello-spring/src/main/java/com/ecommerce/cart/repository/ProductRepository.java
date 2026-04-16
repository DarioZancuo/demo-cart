package com.ecommerce.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.cart.entity.jpa.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	//Risoluzione problema query n+1
	@EntityGraph(attributePaths = { "category" })
	List<Product> findAll();
	List<Product> findByCategoryId(Long categoryId);
    List<Product> findByNameContainingIgnoreCase(String name);
	
}
