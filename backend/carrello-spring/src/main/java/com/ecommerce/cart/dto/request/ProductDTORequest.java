package com.ecommerce.cart.dto.request;

import com.ecommerce.cart.entity.jpa.Category;

public record ProductDTORequest(
	    Long id_prod, 
	    String name, 
	    String description,
	    Double price,   
	    Category category 
	) {}