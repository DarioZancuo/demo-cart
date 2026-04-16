package com.ecommerce.cart.service;

import com.ecommerce.cart.dto.request.AddItemCartRequest;
import com.ecommerce.cart.dto.request.UpdateQtyRequest;
import com.ecommerce.cart.dto.response.CartItemResponse;
import com.ecommerce.cart.dto.response.CartResponse;
import com.ecommerce.cart.entity.jpa.Product;
import com.ecommerce.cart.entity.redis.CartItemRedis;
import com.ecommerce.cart.entity.redis.CartRedis;
import com.ecommerce.cart.exception.custom.BadRequestException;
import com.ecommerce.cart.exception.custom.NotFoundException;
import com.ecommerce.cart.repository.ICartRedisRepository;
import com.ecommerce.cart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final long CART_TTL_SECONDS = 60 * 60 * 24; // 1 giorno

    private final ICartRedisRepository cartRedisRepository;
    private final ProductRepository productRepository;

    //getCart
    public CartResponse getCart(Long userId) {
        CartRedis cart = getOrCreateCart(userId);
        return saveAndBuildResponse(cart);
    }

    //addItem
    @Transactional(readOnly = true)
    public CartResponse addItem(Long userId, AddItemCartRequest request) {
        validateQuantity(request.quantity());

        Product product = getProductOrThrow(request.productId());
        CartRedis cart = getOrCreateCart(userId);

        CartItemRedis existingItem = findCartItem(cart, product.getId());

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.quantity();
            existingItem.setQuantity(newQuantity);
            existingItem.setSubtotal(existingItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(newQuantity)));
        } else {
            CartItemRedis newItem = CartItemRedis.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .unitPrice(product.getPrice())
                    .quantity(request.quantity())
                    .subtotal(product.getPrice().multiply(BigDecimal.valueOf(request.quantity())))
                    .build();

            cart.getItems().add(newItem);
        }

        return saveAndBuildResponse(cart);
    }

    //udpdateItemQty
    public CartResponse updateItemQuantity(Long userId, UpdateQtyRequest request) {
        validateQuantity(request.quantity());

        CartRedis cart = getCartOrThrow(userId);
        CartItemRedis item = getCartItemOrThrow(cart, request.productId());

        item.setQuantity(request.quantity());
        item.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(request.quantity())));

        return saveAndBuildResponse(cart);
    }

    //removeItem
    public CartResponse removeItem(Long userId, Long productId) {
        CartRedis cart = getCartOrThrow(userId);

        CartItemRedis item = getCartItemOrThrow(cart, productId);
        cart.getItems().remove(item);

        return saveAndBuildResponse(cart);
    }

    //clearCart
    public void clearCart(Long userId) {
        cartRedisRepository.deleteById(buildCartId(userId));
    }

    // Helpers
    
    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
    }

    private CartRedis getOrCreateCart(Long userId) {
        return cartRedisRepository.findById(buildCartId(userId))
                .orElseGet(() -> CartRedis.builder()
                        .id(buildCartId(userId))
                        .userId(userId)
                        .items(new ArrayList<>())
                        .totalPrice(BigDecimal.ZERO)
                        .totalQuantity(0)
                        .ttl(CART_TTL_SECONDS)
                        .build());
    }

    private CartRedis getCartOrThrow(Long userId) {
        return cartRedisRepository.findById(buildCartId(userId))
                .orElseThrow(() -> new NotFoundException("Cart not found for user: " + userId));
    }

    private CartItemRedis findCartItem(CartRedis cart, Long productId) {
        return cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    private CartItemRedis getCartItemOrThrow(CartRedis cart, Long productId) {
        return cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Product not found in cart: " + productId));
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }
    }

    private String buildCartId(Long userId) {
        return "cart:user:" + userId;
    }

    private void recalculateCart(CartRedis cart) {
        int totalQuantity = cart.getItems().stream()
                .mapToInt(CartItemRedis::getQuantity)
                .sum();

        BigDecimal totalPrice = cart.getItems().stream()
                .map(CartItemRedis::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalQuantity(totalQuantity);
        cart.setTotalPrice(totalPrice);
    }

    private CartResponse saveAndBuildResponse(CartRedis cart) {
        recalculateCart(cart);
        cart.setTtl(CART_TTL_SECONDS);
        cartRedisRepository.save(cart);
        return toResponse(cart);
    }

    private CartResponse toResponse(CartRedis cart) {
        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                cart.getItems().stream()
                        .map(item -> new CartItemResponse(
                                item.getProductId(),
                                item.getProductName(),
                                item.getUnitPrice(),
                                item.getQuantity(),
                                item.getSubtotal()
                        ))
                        .toList(),
                cart.getTotalQuantity(),
                cart.getTotalPrice()
        );
    }
    
}