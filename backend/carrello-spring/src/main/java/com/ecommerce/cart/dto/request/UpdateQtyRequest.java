package com.ecommerce.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateQtyRequest(
		
		@NotNull Long productId, @NotNull @Min(1) Integer quantity) {

}