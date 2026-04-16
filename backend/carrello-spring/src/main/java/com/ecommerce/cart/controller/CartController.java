package com.ecommerce.cart.controller;

import com.ecommerce.cart.dto.request.AddItemCartRequest;
import com.ecommerce.cart.dto.request.UpdateQtyRequest;
import com.ecommerce.cart.dto.response.ApiResponse;
import com.ecommerce.cart.dto.response.CartResponse;
import com.ecommerce.cart.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ApiResponse<CartResponse> getCart(@PathVariable Long userId) {
    	log.info("GET cart - userId={}", userId);
    	
        return ApiResponse.success("Cart retrieved successfully", cartService.getCart(userId));
    }

	@PostMapping("/{userId}/items")
	public ApiResponse<CartResponse> addItem(@PathVariable Long userId, @Valid @RequestBody AddItemCartRequest request) {
	    log.info("ADD item - userId={}, productId={}, quantity={}", userId, request.productId(), request.quantity());
		
		return ApiResponse.success("Item added to cart", cartService.addItem(userId, request));
	}

	@PutMapping("/{userId}/items")
	public ApiResponse<CartResponse> updateItemQuantity(@PathVariable Long userId, @Valid @RequestBody UpdateQtyRequest request) {		
	    log.info("UPDATE item qty - userId={}, productId={}, quantity={}", userId, request.productId(), request.quantity());
		
		return ApiResponse.success("Cart item updated", cartService.updateItemQuantity(userId, request));
	}

	@DeleteMapping("/{userId}/items/{productId}")
	public ApiResponse<CartResponse> removeItem(@PathVariable Long userId, @PathVariable Long productId) {	
		log.info("REMOVE item - userId={}, productId={}", userId, productId);
		
		return ApiResponse.success("Item removed from cart", cartService.removeItem(userId, productId));
	}

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> clearCart(@PathVariable Long userId) {
    	log.warn("CLEAR cart - userId={}", userId);
    	
        cartService.clearCart(userId);
        return ApiResponse.success("Cart cleared", null);
    }
}
