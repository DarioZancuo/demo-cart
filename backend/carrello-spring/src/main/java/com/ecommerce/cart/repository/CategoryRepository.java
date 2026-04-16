package com.ecommerce.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.cart.entity.jpa.Category;

public interface CategoryRepository extends JpaRepository<Category,Long> {

}
