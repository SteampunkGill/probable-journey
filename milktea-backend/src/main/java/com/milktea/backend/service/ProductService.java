package com.milktea.backend.service;

import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.ProductRepository;
import com.milktea.backend.repository.CategoryRepository;
import com.milktea.backend.repository.OrderRepository;
import com.milktea.milktea_backend.model.entity.Category;
import com.milktea.milktea_backend.model.entity.Product;
import com.milktea.milktea_backend.model.entity.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * 获取所有商品
     * @return 商品列表
     */
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 获取所有激活的商品
     * @return 商品列表
     */
    public List<Product> findAllActiveProducts() {
        return productRepository.findByIsActive(true);
    }

    /**
     * 根据ID获取商品详情
     * @param id 商品ID
     * @return 商品
     */
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * 创建商品
     * @param product 商品信息
     * @return 创建成功的商品
     * @throws ServiceException 如果商品信息无效
     */
    @Transactional
    public Product createProduct(Product product) {
        // 验证商品信息
        validateProductForCreation(product);
        
        // 验证商品分类是否存在
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new ServiceException("CATEGORY_NOT_FOUND", "商品分类不存在，ID: " + product.getCategory().getId()));
            product.setCategory(category);
        }
        
        // 设置默认值
        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }
        if (product.getStock() == null) {
            product.setStock(0);
        }
        if (product.getSales() == null) {
            product.setSales(0);
        }
        if (product.getStatus() == null) {
            product.setStatus(1); // 默认上架
        }
        if (product.getIsMemberExclusive() == null) {
            product.setIsMemberExclusive(false);
        }
        if (product.getAlertThreshold() == null) {
            product.setAlertThreshold(10);
        }
        
        // 验证价格
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("INVALID_PRICE", "商品价格必须大于等于0");
        }
        
        // 验证成本价（如果提供）
        if (product.getCostPrice() != null && product.getCostPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("INVALID_COST_PRICE", "成本价不能为负数");
        }
        
        // 实际场景下, 可能需要处理商品图片、营养信息、规格等
        return productRepository.save(product);
    }

    /**
     * 更新商品信息
     * @param product 商品信息
     * @return 更新后的商品
     * @throws ServiceException 如果商品不存在或信息无效
     */
    @Transactional
    public Product updateProduct(Product product) {
        if (product.getId() == null) {
            throw new ServiceException("PRODUCT_ID_REQUIRED", "商品ID不能为空");
        }
        
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new ServiceException("PRODUCT_NOT_FOUND", "商品不存在，ID: " + product.getId()));

        // 验证更新信息
        validateProductForUpdate(product);
        
        // 更新基本信息
        if (product.getName() != null) {
            existingProduct.setName(product.getName());
        }
        if (product.getDescription() != null) {
            existingProduct.setDescription(product.getDescription());
        }
        if (product.getPrice() != null) {
            if (product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("INVALID_PRICE", "商品价格必须大于等于0");
            }
            existingProduct.setPrice(product.getPrice());
        }
        if (product.getImageUrl() != null) {
            existingProduct.setImageUrl(product.getImageUrl());
        }
        if (product.getIsActive() != null) {
            existingProduct.setIsActive(product.getIsActive());
        }
        if (product.getStock() != null) {
            if (product.getStock() < 0) {
                throw new ServiceException("INVALID_STOCK", "库存不能为负数");
            }
            existingProduct.setStock(product.getStock());
        }
        if (product.getSales() != null) {
            if (product.getSales() < 0) {
                throw new ServiceException("INVALID_SALES", "销量不能为负数");
            }
            existingProduct.setSales(product.getSales());
        }
        if (product.getStatus() != null) {
            existingProduct.setStatus(product.getStatus());
        }
        if (product.getIsMemberExclusive() != null) {
            existingProduct.setIsMemberExclusive(product.getIsMemberExclusive());
        }
        if (product.getMemberPrice() != null) {
            if (product.getMemberPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("INVALID_MEMBER_PRICE", "会员价格必须大于等于0");
            }
            existingProduct.setMemberPrice(product.getMemberPrice());
        }
        if (product.getCostPrice() != null) {
            if (product.getCostPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("INVALID_COST_PRICE", "成本价不能为负数");
            }
            existingProduct.setCostPrice(product.getCostPrice());
        }
        if (product.getAlertThreshold() != null) {
            if (product.getAlertThreshold() < 0) {
                throw new ServiceException("INVALID_ALERT_THRESHOLD", "库存警戒线不能为负数");
            }
            existingProduct.setAlertThreshold(product.getAlertThreshold());
        }
        if (product.getSugarContent() != null) {
            existingProduct.setSugarContent(product.getSugarContent());
        }
        if (product.getCalories() != null) {
            // calories 是 String 类型，只验证长度
            if (product.getCalories().length() > 50) {
                throw new ServiceException("INVALID_CALORIES", "卡路里信息不能超过50个字符");
            }
            existingProduct.setCalories(product.getCalories());
        }
        if (product.getDefaultSweetness() != null) {
            existingProduct.setDefaultSweetness(product.getDefaultSweetness());
        }
        if (product.getDefaultTemperature() != null) {
            existingProduct.setDefaultTemperature(product.getDefaultTemperature());
        }
        if (product.getSupportSweetness() != null) {
            existingProduct.setSupportSweetness(product.getSupportSweetness());
        }
        if (product.getSupportTemperature() != null) {
            existingProduct.setSupportTemperature(product.getSupportTemperature());
        }
        
        // 更新分类
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new ServiceException("CATEGORY_NOT_FOUND", "商品分类不存在，ID: " + product.getCategory().getId()));
            existingProduct.setCategory(category);
        } else if (product.getCategory() == null) {
            // 如果传入null，表示清除分类
            existingProduct.setCategory(null);
        }
        
        // 可以根据需要更新其他字段
        return productRepository.save(existingProduct);
    }

    /**
     * 删除商品
     * @param id 商品ID
     * @throws ServiceException 如果商品不存在
     */
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ServiceException("PRODUCT_NOT_FOUND", "商品不存在，ID: " + id);
        }
        productRepository.deleteById(id);
    }

    /**
     * 激活/停用商品
     * @param id 商品ID
     * @param isActive 是否激活
     * @return 更新后的商品
     * @throws ServiceException 如果商品不存在
     */
    @Transactional
    public Product updateProductStatus(Long id, Boolean isActive) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException("PRODUCT_NOT_FOUND", "商品不存在，ID: " + id));
        product.setIsActive(isActive);
        product.setStatus(isActive ? 1 : 0); // 同步更新状态
        return productRepository.save(product);
    }

    /**
     * 更新商品库存
     * @param id 商品ID
     * @param stock 库存数量
     * @return 更新后的商品
     * @throws ServiceException 如果商品不存在或库存无效
     */
    @Transactional
    public Product updateProductStock(Long id, Integer stock) {
        if (stock == null || stock < 0) {
            throw new ServiceException("INVALID_STOCK", "库存不能为负数");
        }
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException("PRODUCT_NOT_FOUND", "商品不存在，ID: " + id));
        product.setStock(stock);
        return productRepository.save(product);
    }

    /**
     * 更新商品销量
     * @param id 商品ID
     * @param quantity 销售数量
     * @return 更新后的商品
     * @throws ServiceException 如果商品不存在或数量无效
     */
    @Transactional
    public Product updateProductSales(Long id, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new ServiceException("INVALID_QUANTITY", "销售数量必须大于0");
        }
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException("PRODUCT_NOT_FOUND", "商品不存在，ID: " + id));
        Integer currentSales = product.getSales() != null ? product.getSales() : 0;
        product.setSales(currentSales + quantity);
        
        // 减少库存
        Integer currentStock = product.getStock() != null ? product.getStock() : 0;
        int newStock = currentStock - quantity;
        if (newStock < 0) {
            throw new ServiceException("INSUFFICIENT_STOCK", "库存不足，当前库存: " + currentStock + "，需求数量: " + quantity);
        }
        product.setStock(newStock);
        
        return productRepository.save(product);
    }

    /**
     * 根据分类ID获取商品列表
     * @param categoryId 分类ID
     * @return 商品列表
     */
    public List<Product> findProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    /**
     * 根据分类ID获取激活的商品列表
     * @param categoryId 分类ID
     * @return 商品列表
     */
    public List<Product> findActiveProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryIdAndIsActive(categoryId, true);
    }

    /**
     * 根据名称搜索商品
     * @param name 商品名称
     * @return 商品列表
     */
    public List<Product> searchProductsByName(String name) {
        if (!StringUtils.hasText(name)) {
            return List.of();
        }
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * 根据关键词搜索商品（名称或描述）
     * @param keyword 关键词
     * @return 商品列表
     */
    public List<Product> searchProducts(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return List.of();
        }
        return productRepository.searchProducts(keyword);
    }

    /**
     * 根据价格范围搜索商品
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 商品列表
     * @throws ServiceException 如果价格范围无效
     */
    public List<Product> findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("INVALID_MIN_PRICE", "最低价格不能为负数");
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("INVALID_MAX_PRICE", "最高价格不能为负数");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new ServiceException("INVALID_PRICE_RANGE", "最低价格不能大于最高价格");
        }
        
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    /**
     * 获取热门商品（根据销量）
     * @return 商品列表
     */
    public List<Product> findHotProducts() {
        return productRepository.findHotProducts();
    }

    /**
     * 获取新品（最近创建的商品）
     * @return 商品列表
     */
    public List<Product> findNewProducts() {
        return productRepository.findNewProducts();
    }

    /**
     * 获取会员专享商品
     * @return 商品列表
     */
    public List<Product> findMemberExclusiveProducts() {
        return productRepository.findByIsMemberExclusive(true);
    }

    /**
     * 根据糖度偏好获取商品
     * @param sweetness 糖度
     * @return 商品列表
     */
    public List<Product> findProductsBySweetness(String sweetness) {
        if (!StringUtils.hasText(sweetness)) {
            return List.of();
        }
        return productRepository.findByDefaultSweetness(sweetness);
    }

    /**
     * 根据温度偏好获取商品
     * @param temperature 温度
     * @return 商品列表
     */
    public List<Product> findProductsByTemperature(String temperature) {
        if (!StringUtils.hasText(temperature)) {
            return List.of();
        }
        return productRepository.findByDefaultTemperature(temperature);
    }

    /**
     * 获取低库存商品（库存低于警戒线）
     * @return 商品列表
     */
    public List<Product> findLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    /**
     * 获取商品数量统计
     * @return 商品总数和激活商品数
     */
    public ProductStats getProductStats() {
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.countByIsActive(true);
        return new ProductStats(totalProducts, activeProducts);
    }

    /**
     * 检查库存是否充足
     * @param id 商品ID
     * @param quantity 需求数量
     * @return 是否充足
     * @throws ServiceException 如果商品不存在或数量无效
     */
    public boolean checkStock(Long id, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new ServiceException("INVALID_QUANTITY", "需求数量必须大于0");
        }
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException("PRODUCT_NOT_FOUND", "商品不存在，ID: " + id));
        return product.getStock() != null && product.getStock() >= quantity;
    }

    /**
     * 批量更新商品状态
     * @param ids 商品ID列表
     * @param isActive 是否激活
     * @throws ServiceException 如果ID列表为空
     */
    @Transactional
    public void batchUpdateProductStatus(List<Long> ids, Boolean isActive) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("EMPTY_ID_LIST", "商品ID列表不能为空");
        }
        
        List<Product> products = productRepository.findAllById(ids);
        for (Product product : products) {
            product.setIsActive(isActive);
            product.setStatus(isActive ? 1 : 0);
        }
        productRepository.saveAll(products);
    }

    /**
     * 根据价格和分类查找商品
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param categoryId 分类ID
     * @return 商品列表
     */
    public List<Product> findProductsByPriceRangeAndCategory(BigDecimal minPrice, BigDecimal maxPrice, Long categoryId) {
        return productRepository.findByPriceRangeAndCategory(minPrice, maxPrice, categoryId);
    }

    /**
     * 获取上架商品（状态为1）
     * @return 商品列表
     */
    public List<Product> findOnShelfProducts() {
        return productRepository.findByStatus(1);
    }

    /**
     * 获取下架商品（状态为0）
     * @return 商品列表
     */
    public List<Product> findOffShelfProducts() {
        return productRepository.findByStatus(0);
    }

    /**
     * 获取个性化推荐商品（基于购买历史）
     * @param userId 用户ID
     * @return 推荐商品列表
     */
    public List<Product> getRecommendations(Long userId) {
        // 1. 获取用户最近购买过的商品分类
        List<Order> userOrders = orderRepository.findByUserId(userId);
        if (userOrders.isEmpty()) {
            // 如果没有购买历史，返回热门商品
            return productRepository.findHotProducts().stream().limit(4).collect(Collectors.toList());
        }

        Set<Long> purchasedCategoryIds = userOrders.stream()
                .flatMap(o -> o.getOrderItems().stream())
                .map(item -> item.getProduct().getCategory().getId())
                .collect(Collectors.toSet());

        // 2. 推荐这些分类下的其他热门商品
        return productRepository.findAll().stream()
                .filter(p -> p.getIsActive() && purchasedCategoryIds.contains(p.getCategory().getId()))
                .sorted((a, b) -> (b.getSales() != null ? b.getSales() : 0) - (a.getSales() != null ? a.getSales() : 0))
                .limit(4)
                .collect(Collectors.toList());
    }

    // ========== 私有方法 ==========
    
    /**
     * 验证商品创建信息
     */
    private void validateProductForCreation(Product product) {
        if (product == null) {
            throw new ServiceException("PRODUCT_REQUIRED", "商品信息不能为空");
        }
        if (!StringUtils.hasText(product.getName())) {
            throw new ServiceException("PRODUCT_NAME_REQUIRED", "商品名称不能为空");
        }
        if (product.getName().length() > 100) {
            throw new ServiceException("PRODUCT_NAME_TOO_LONG", "商品名称不能超过100个字符");
        }
        if (product.getPrice() == null) {
            throw new ServiceException("PRODUCT_PRICE_REQUIRED", "商品价格不能为空");
        }
    }
    
    /**
     * 验证商品更新信息
     */
    private void validateProductForUpdate(Product product) {
        if (product.getName() != null && product.getName().length() > 100) {
            throw new ServiceException("PRODUCT_NAME_TOO_LONG", "商品名称不能超过100个字符");
        }
        if (product.getDescription() != null && product.getDescription().length() > 500) {
            throw new ServiceException("PRODUCT_DESCRIPTION_TOO_LONG", "商品描述不能超过500个字符");
        }
    }

    /**
     * 商品统计信息
     */
    public static class ProductStats {
        private final long totalProducts;
        private final long activeProducts;

        public ProductStats(long totalProducts, long activeProducts) {
            this.totalProducts = totalProducts;
            this.activeProducts = activeProducts;
        }

        public long getTotalProducts() {
            return totalProducts;
        }

        public long getActiveProducts() {
            return activeProducts;
        }
    }
}