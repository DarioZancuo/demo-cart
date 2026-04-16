package com.ecommerce.cart.service;

import com.ecommerce.cart.dto.request.AddItemCartRequest;
import com.ecommerce.cart.dto.request.UpdateQtyRequest;
import com.ecommerce.cart.dto.response.CartItemResponse;
import com.ecommerce.cart.dto.response.CartResponse;
import com.ecommerce.cart.entity.jpa.Product;
import com.ecommerce.cart.entity.redis.CartItemRedis;
import com.ecommerce.cart.entity.redis.CartRedis;
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

    private final ICartRedisRepository cartRedisRepository;
    private final ProductRepository productRepository;

    private static final long CART_TTL_SECONDS = 60 * 60 * 24 * 1; // 1d

    public CartResponse getCart(Long userId) {
        CartRedis cart = getOrCreateCart(userId);
        recalculateCart(cart);
        cartRedisRepository.save(cart);
        return toResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse addItem(Long userId, AddItemCartRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + request.productId()));

        CartRedis cart = getOrCreateCart(userId);

        CartItemRedis existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.quantity());
            existingItem.setSubtotal(existingItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(existingItem.getQuantity())));
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

        recalculateCart(cart);
        cart.setTtl(CART_TTL_SECONDS);
        cartRedisRepository.save(cart);

        return toResponse(cart);
    }

    public CartResponse updateItemQuantity(Long userId, UpdateQtyRequest request) {
        CartRedis cart = getCartOrThrow(userId);

        CartItemRedis item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.productId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Product not found in cart: " + request.productId()));

        item.setQuantity(request.quantity());
        item.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(request.quantity())));

        recalculateCart(cart);
        cart.setTtl(CART_TTL_SECONDS);
        cartRedisRepository.save(cart);

        return toResponse(cart);
    }

    public CartResponse removeItem(Long userId, Long productId) {
        CartRedis cart = getCartOrThrow(userId);

        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        if (!removed) {
            throw new NotFoundException("Product not found in cart: " + productId);
        }

        recalculateCart(cart);
        cart.setTtl(CART_TTL_SECONDS);
        cartRedisRepository.save(cart);

        return toResponse(cart);
    }

    public void clearCart(Long userId) {
        String cartId = buildCartId(userId);
        cartRedisRepository.deleteById(cartId);
    }

    private CartRedis getOrCreateCart(Long userId) {
        return cartRedisRepository.findById(buildCartId(userId))
                .orElse(
                        CartRedis.builder()
                                .id(buildCartId(userId))
                                .userId(userId)
                                .items(new ArrayList<>())
                                .totalPrice(BigDecimal.ZERO)
                                .totalQuantity(0)
                                .ttl(CART_TTL_SECONDS)
                                .build()
                );
    }

    private CartRedis getCartOrThrow(Long userId) {
        return cartRedisRepository.findById(buildCartId(userId))
                .orElseThrow(() -> new NotFoundException("Cart not found for user: " + userId));
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