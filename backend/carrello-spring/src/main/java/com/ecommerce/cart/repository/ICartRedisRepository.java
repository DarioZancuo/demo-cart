package com.ecommerce.cart.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.ecommerce.cart.entity.redis.CartRedis;

@Repository
public interface ICartRedisRepository extends CrudRepository<CartRedis, String> {
}
