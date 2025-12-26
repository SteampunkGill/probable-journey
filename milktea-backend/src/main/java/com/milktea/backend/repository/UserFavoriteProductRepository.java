package com.milktea.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.UserFavoriteProduct;
import com.milktea.milktea_backend.model.entity.User;
import com.milktea.milktea_backend.model.entity.Product;

import java.util.Optional;

@Repository
public interface UserFavoriteProductRepository extends JpaRepository<UserFavoriteProduct, Long> {
    Page<UserFavoriteProduct> findByUser(User user, Pageable pageable);
    Optional<UserFavoriteProduct> findByUserAndProduct(User user, Product product);
    void deleteByUser(User user);
    boolean existsByUserAndProduct(User user, Product product);
}
