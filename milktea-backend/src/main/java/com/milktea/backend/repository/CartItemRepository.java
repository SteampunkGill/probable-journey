package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.CartItem;
import com.milktea.milktea_backend.model.entity.Product;
import com.milktea.milktea_backend.model.entity.ProductSpec;
import com.milktea.milktea_backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    List<CartItem> findByUserId(Long userId);
    
    Optional<CartItem> findByUserIdAndProductIdAndSpecAndSweetnessAndTemperature(
            Long userId, Long productId, ProductSpec spec, String sweetness, String temperature);

    void deleteByUserId(Long userId);
}