package com.ecommerce.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.cart.entity.jpa.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

}
