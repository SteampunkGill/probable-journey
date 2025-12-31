package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.Category;
import com.milktea.milktea_backend.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(Category category);

    List<Product> findByCategoryId(Long categoryId);
    

    List<Product> findByNameContainingIgnoreCase(String name);
    

    List<Product> findByDescriptionContainingIgnoreCase(String description);

    List<Product> findByIsActive(Boolean isActive);

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findAllByOrderByPriceDesc();

    List<Product> findAllByOrderByPriceAsc();

    List<Product> findAllByOrderByCreatedAtDesc();

    List<Product> findAllByOrderBySalesDesc();

    List<Product> findByCategoryAndIsActive(Category category, Boolean isActive);

    List<Product> findByCategoryIdAndIsActive(Long categoryId, Boolean isActive);

    List<Product> findByNameContainingIgnoreCaseAndCategory(String name, Category category);

    Optional<Product> findBySku(String sku);

    List<Product> findByIsMemberExclusive(Boolean isMemberExclusive);
    

    List<Product> findByDefaultSweetness(String defaultSweetness);

    List<Product> findByDefaultTemperature(String defaultTemperature);
    

    List<Product> findByStatus(Integer status);

    long countByCategoryId(Long categoryId);
    

    long countByIsActive(Boolean isActive);

    long countByIsMemberExclusive(Boolean isMemberExclusive);
    

    List<Product> findByIsRecommended(Boolean isRecommended);

    @Query("SELECT p FROM Product p ORDER BY p.sales DESC")
    List<Product> findHotProducts();

    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findNewProducts();

    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds")
    List<Product> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds);
    

    @Query("SELECT p FROM Product p WHERE p.tags LIKE %:tag%")
    List<Product> findByTag(@Param("tag") String tag);
    

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE p.stock <= p.alertThreshold")
    List<Product> findLowStockProducts();
    

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.category.id = :categoryId")
    List<Product> findByPriceRangeAndCategory(@Param("minPrice") BigDecimal minPrice, 
                                             @Param("maxPrice") BigDecimal maxPrice, 
                                             @Param("categoryId") Long categoryId);
}
