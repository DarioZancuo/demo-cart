package com.ecommerce.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.cart.entity.jpa.Product;

public interface ProductRepository extends JpaRepository<Product,Long> {

}
