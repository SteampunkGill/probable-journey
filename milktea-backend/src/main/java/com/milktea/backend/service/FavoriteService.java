package com.milktea.backend.service;

import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.ProductRepository;
import com.milktea.backend.repository.UserFavoriteProductRepository;
import com.milktea.milktea_backend.model.entity.Product;
import com.milktea.milktea_backend.model.entity.User;
import com.milktea.milktea_backend.model.entity.UserFavoriteProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserFavoriteProductRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public Page<UserFavoriteProduct> getMyFavorites(int page, int size) {
        User user = userService.getCurrentUser();
        return favoriteRepository.findByUser(user, PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional
    public void addFavorite(Long productId) {
        User user = userService.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ServiceException("PRODUCT_NOT_FOUND", "商品不存在"));

        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            return;
        }

        UserFavoriteProduct favorite = new UserFavoriteProduct();
        favorite.setUser(user);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long productId) {
        User user = userService.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ServiceException("PRODUCT_NOT_FOUND", "商品不存在"));

        favoriteRepository.findByUserAndProduct(user, product)
                .ifPresent(favoriteRepository::delete);
    }

    @Transactional
    public void clearAll() {
        User user = userService.getCurrentUser();
        favoriteRepository.deleteByUser(user);
    }

    public boolean isFavorite(Long productId) {
        User user = userService.getCurrentUser();
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return false;
        return favoriteRepository.existsByUserAndProduct(user, product);
    }
}