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
    
    /**
     * 根据分类查找商品
     * @param category 分类
     * @return 商品列表
     */
    List<Product> findByCategory(Category category);
    
    /**
     * 根据分类ID查找商品
     * @param categoryId 分类ID
     * @return 商品列表
     */
    List<Product> findByCategoryId(Long categoryId);
    
    /**
     * 根据商品名称模糊查询
     * @param name 商品名称
     * @return 商品列表
     */
    List<Product> findByNameContainingIgnoreCase(String name);
    
    /**
     * 根据商品描述模糊查询
     * @param description 商品描述
     * @return 商品列表
     */
    List<Product> findByDescriptionContainingIgnoreCase(String description);
    
    /**
     * 根据商品状态查找
     * @param isActive 是否激活
     * @return 商品列表
     */
    List<Product> findByIsActive(Boolean isActive);
    
    /**
     * 根据价格范围查找商品
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 商品列表
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * 根据价格降序查找商品
     * @return 商品列表
     */
    List<Product> findAllByOrderByPriceDesc();
    
    /**
     * 根据价格升序查找商品
     * @return 商品列表
     */
    List<Product> findAllByOrderByPriceAsc();
    
    /**
     * 根据创建时间降序查找商品
     * @return 商品列表
     */
    List<Product> findAllByOrderByCreatedAtDesc();
    
    /**
     * 根据销量降序查找商品
     * @return 商品列表
     */
    List<Product> findAllByOrderBySalesDesc();
    
    /**
     * 根据分类和状态查找商品
     * @param category 分类
     * @param isActive 是否激活
     * @return 商品列表
     */
    List<Product> findByCategoryAndIsActive(Category category, Boolean isActive);
    
    /**
     * 根据分类ID和状态查找商品
     * @param categoryId 分类ID
     * @param isActive 是否激活
     * @return 商品列表
     */
    List<Product> findByCategoryIdAndIsActive(Long categoryId, Boolean isActive);
    
    /**
     * 根据商品名称和分类查找
     * @param name 商品名称
     * @param category 分类
     * @return 商品列表
     */
    List<Product> findByNameContainingIgnoreCaseAndCategory(String name, Category category);
    
    /**
     * 根据商品SKU查找
     * @param sku 商品SKU
     * @return 商品Optional
     */
    Optional<Product> findBySku(String sku);
    
    /**
     * 根据会员专享状态查找商品
     * @param isMemberExclusive 是否会员专享
     * @return 商品列表
     */
    List<Product> findByIsMemberExclusive(Boolean isMemberExclusive);
    
    /**
     * 根据默认糖度查找商品
     * @param defaultSweetness 默认糖度
     * @return 商品列表
     */
    List<Product> findByDefaultSweetness(String defaultSweetness);
    
    /**
     * 根据默认温度查找商品
     * @param defaultTemperature 默认温度
     * @return 商品列表
     */
    List<Product> findByDefaultTemperature(String defaultTemperature);
    
    /**
     * 根据状态查找商品
     * @param status 状态
     * @return 商品列表
     */
    List<Product> findByStatus(Integer status);
    
    /**
     * 统计分类下的商品数量
     * @param categoryId 分类ID
     * @return 商品数量
     */
    long countByCategoryId(Long categoryId);
    
    /**
     * 统计激活商品数量
     * @param isActive 是否激活
     * @return 商品数量
     */
    long countByIsActive(Boolean isActive);
    
    /**
     * 统计会员专享商品数量
     * @param isMemberExclusive 是否会员专享
     * @return 商品数量
     */
    long countByIsMemberExclusive(Boolean isMemberExclusive);
    
    /**
     * 查找推荐商品
     * @param isRecommended 是否推荐
     * @return 商品列表
     */
    List<Product> findByIsRecommended(Boolean isRecommended);
    
    /**
     * 查找热门商品（根据销量）
     * @return 商品列表
     */
    @Query("SELECT p FROM Product p ORDER BY p.sales DESC")
    List<Product> findHotProducts();
    
    /**
     * 查找新品（最近创建的商品）
     * @return 商品列表
     */
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findNewProducts();
    
    /**
     * 根据多个分类ID查找商品
     * @param categoryIds 分类ID列表
     * @return 商品列表
     */
    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds")
    List<Product> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds);
    
    /**
     * 根据商品标签查找商品
     * @param tag 标签
     * @return 商品列表
     */
    @Query("SELECT p FROM Product p WHERE p.tags LIKE %:tag%")
    List<Product> findByTag(@Param("tag") String tag);
    
    /**
     * 搜索商品（名称或描述）
     * @param keyword 关键词
     * @return 商品列表
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);
    
    /**
     * 查找低库存商品（库存低于警戒线）
     * @return 商品列表
     */
    @Query("SELECT p FROM Product p WHERE p.stock <= p.alertThreshold")
    List<Product> findLowStockProducts();
    
    /**
     * 根据价格和分类查找商品
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param categoryId 分类ID
     * @return 商品列表
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.category.id = :categoryId")
    List<Product> findByPriceRangeAndCategory(@Param("minPrice") BigDecimal minPrice, 
                                             @Param("maxPrice") BigDecimal maxPrice, 
                                             @Param("categoryId") Long categoryId);
}
