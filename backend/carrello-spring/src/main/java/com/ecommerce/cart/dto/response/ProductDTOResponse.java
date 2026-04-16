package com.ecommerce.cart.dto.response;

import com.ecommerce.cart.entity.jpa.Category;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ProductDTOResponse(
	    Long id_prod, 
	    String name, 
	    Double price,
	    Category category
	) {}