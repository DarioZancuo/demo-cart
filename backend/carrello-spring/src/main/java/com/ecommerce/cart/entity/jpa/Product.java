package com.ecommerce.cart.entity.jpa;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="t_products")
public class Product {

	@Id
	@Column(name="c_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="c_name", nullable = false)
	private String name;
	
	@Column(name="c_description", nullable = true)
	private String description;
	
	@Column(name="c_price", nullable = false)
	private BigDecimal price;
	
	
	
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
	private Category category;
	
	
	
}
