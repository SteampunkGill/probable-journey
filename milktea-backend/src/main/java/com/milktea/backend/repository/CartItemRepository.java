package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.CartItem;
import com.milktea.milktea_backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    List<CartItem> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}