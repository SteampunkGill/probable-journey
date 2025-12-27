package com.milktea.backend.service;

import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.StoreRepository;
import com.milktea.milktea_backend.model.entity.Store;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    /**
     * 创建新的门店
     * @param store 要创建的门店实体
     * @return 已保存的门店实体
     * @throws ServiceException 如果门店信息无效
     */
    @Transactional
    public Store createStore(Store store) {
        // 验证输入
        validateStoreForCreate(store);
        
        // 检查门店编码是否已存在
        if (store.getCode() != null && storeRepository.findByCode(store.getCode()).isPresent()) {
            throw new ServiceException("STORE_CODE_EXISTS", "门店编码已存在: " + store.getCode());
        }
        
        // 设置默认值
        setDefaultValues(store);
        
        return storeRepository.save(store);
    }

    /**
     * 根据ID获取门店信息
     * @param id 门店ID
     * @return 门店实体Optional
     */
    public Optional<Store> getStoreById(Long id) {
        return storeRepository.findById(id);
    }

    /**
     * 根据门店编码获取门店信息
     * @param code 门店编码
     * @return 门店实体Optional
     */
    public Optional<Store> getStoreByCode(String code) {
        return storeRepository.findByCode(code);
    }

    /**
     * 获取所有门店列表
     * @return 门店列表
     */
    public List<Store> findAllStores() {
        try {
            return storeRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("DB_ERROR", "获取门店列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取营业中的门店列表
     * @return 门店列表
     */
    public List<Store> findOpenStores() {
        return storeRepository.findOpenStores();
    }

    /**
     * 根据区域获取营业中的门店列表
     * @param province 省
     * @param city 市
     * @param district 区
     * @return 门店列表
     */
    public List<Store> findOpenStoresByRegion(String province, String city, String district) {
        if (StringUtils.hasText(district)) {
            return storeRepository.findByDistrictAndStatus(district, "OPEN");
        } else if (StringUtils.hasText(city)) {
            return storeRepository.findByCityAndStatus(city, "OPEN");
        } else if (StringUtils.hasText(province)) {
            return storeRepository.findByProvinceAndStatus(province, "OPEN");
        }
        return findOpenStores();
    }

    /**
     * 获取支持配送的门店列表
     * @return 门店列表
     */
    public List<Store> findDeliveryStores() {
        return storeRepository.findDeliveryStores();
    }

    /**
     * 获取最近创建的门店列表
     * @return 门店列表
     */
    public List<Store> findRecentStores() {
        return storeRepository.findRecentStores();
    }

    /**
     * 更新门店信息
     * @param id 门店ID
     * @param storeDetails 要更新的门店详情
     * @return 更新后的门店实体Optional
     * @throws ServiceException 如果门店不存在或信息无效
     */
    @Transactional
    public Optional<Store> updateStore(Long id, Store storeDetails) {
        // 验证输入
        validateStoreForUpdate(storeDetails);
        
        return storeRepository.findById(id).map(store -> {
            // 检查门店编码是否已存在（如果修改了编码）
            if (storeDetails.getCode() != null && !storeDetails.getCode().equals(store.getCode())) {
                if (storeRepository.findByCode(storeDetails.getCode()).isPresent()) {
                    throw new ServiceException("STORE_CODE_EXISTS", "门店编码已存在: " + storeDetails.getCode());
                }
                store.setCode(storeDetails.getCode());
            }
            
            // 更新基本信息
            if (storeDetails.getName() != null) {
                store.setName(storeDetails.getName());
            }
            if (storeDetails.getProvince() != null) {
                store.setProvince(storeDetails.getProvince());
            }
            if (storeDetails.getCity() != null) {
                store.setCity(storeDetails.getCity());
            }
            if (storeDetails.getDistrict() != null) {
                store.setDistrict(storeDetails.getDistrict());
            }
            if (storeDetails.getAddress() != null) {
                store.setAddress(storeDetails.getAddress());
            }
            if (storeDetails.getAddressJson() != null) {
                store.setAddressJson(storeDetails.getAddressJson());
            }
            if (storeDetails.getPhone() != null) {
                store.setPhone(storeDetails.getPhone());
            }
            if (storeDetails.getManagerName() != null) {
                store.setManagerName(storeDetails.getManagerName());
            }
            if (storeDetails.getManagerPhone() != null) {
                store.setManagerPhone(storeDetails.getManagerPhone());
            }
            if (storeDetails.getStatus() != null) {
                store.setStatus(storeDetails.getStatus());
            }
            if (storeDetails.getBusinessHoursJson() != null) {
                store.setBusinessHoursJson(storeDetails.getBusinessHoursJson());
            }
            if (storeDetails.getLatitude() != null) {
                store.setLatitude(storeDetails.getLatitude());
            }
            if (storeDetails.getLongitude() != null) {
                store.setLongitude(storeDetails.getLongitude());
            }
            if (storeDetails.getDeliveryRadius() != null) {
                store.setDeliveryRadius(storeDetails.getDeliveryRadius());
            }
            if (storeDetails.getDeliveryFee() != null) {
                store.setDeliveryFee(storeDetails.getDeliveryFee());
            }
            if (storeDetails.getMinOrderAmount() != null) {
                store.setMinOrderAmount(storeDetails.getMinOrderAmount());
            }
            if (storeDetails.getIsAutoAccept() != null) {
                store.setIsAutoAccept(storeDetails.getIsAutoAccept());
            }
            if (storeDetails.getIsOnlinePayment() != null) {
                store.setIsOnlinePayment(storeDetails.getIsOnlinePayment());
            }
            if (storeDetails.getConfigJson() != null) {
                store.setConfigJson(storeDetails.getConfigJson());
            }
            
            return storeRepository.save(store);
        });
    }

    /**
     * 更新门店状态
     * @param id 门店ID
     * @param status 新状态
     * @return 更新后的门店实体Optional
     * @throws ServiceException 如果门店不存在或状态无效
     */
    @Transactional
    public Optional<Store> updateStoreStatus(Long id, String status) {
        if (!isValidStatus(status)) {
            throw new ServiceException("INVALID_STATUS", "无效的门店状态: " + status);
        }
        
        return storeRepository.findById(id).map(store -> {
            store.setStatus(status);
            return storeRepository.save(store);
        });
    }

    /**
     * 更新门店配送设置
     * @param id 门店ID
     * @param deliveryRadius 配送半径
     * @param deliveryFee 配送费
     * @param minOrderAmount 最低订单金额
     * @return 更新后的门店实体Optional
     * @throws ServiceException 如果门店不存在或参数无效
     */
    @Transactional
    public Optional<Store> updateStoreDeliverySettings(Long id, Integer deliveryRadius, 
                                                      BigDecimal deliveryFee, BigDecimal minOrderAmount) {
        // 验证参数
        if (deliveryRadius != null && deliveryRadius < 0) {
            throw new ServiceException("INVALID_DELIVERY_RADIUS", "配送半径不能为负数");
        }
        if (deliveryFee != null && deliveryFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("INVALID_DELIVERY_FEE", "配送费不能为负数");
        }
        if (minOrderAmount != null && minOrderAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("INVALID_MIN_ORDER_AMOUNT", "最低订单金额不能为负数");
        }
        
        return storeRepository.findById(id).map(store -> {
            if (deliveryRadius != null) {
                store.setDeliveryRadius(deliveryRadius);
            }
            if (deliveryFee != null) {
                store.setDeliveryFee(deliveryFee);
            }
            if (minOrderAmount != null) {
                store.setMinOrderAmount(minOrderAmount);
            }
            return storeRepository.save(store);
        });
    }

    /**
     * 根据ID删除门店
     * @param id 门店ID
     * @throws ServiceException 如果门店不存在
     */
    @Transactional
    public void deleteStore(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new ServiceException("STORE_NOT_FOUND", "门店不存在，ID: " + id);
        }
        storeRepository.deleteById(id);
    }

    /**
     * 根据门店名称模糊查询
     * @param name 门店名称
     * @return 匹配的门店列表
     */
    public List<Store> findStoresByName(String name) {
        return storeRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * 根据地址模糊查询
     * @param address 地址
     * @return 匹配的门店列表
     */
    public List<Store> findStoresByAddress(String address) {
        return storeRepository.findByAddressContainingIgnoreCase(address);
    }

    /**
     * 根据状态查找门店
     * @param status 状态
     * @return 匹配的门店列表
     */
    public List<Store> findStoresByStatus(String status) {
        return storeRepository.findByStatusIgnoreCase(status);
    }

    /**
     * 根据经纬度范围查找门店
     * @param minLat 最小纬度
     * @param maxLat 最大纬度
     * @param minLng 最小经度
     * @param maxLng 最大经度
     * @return 匹配的门店列表
     */
    public List<Store> findStoresByLocationRange(BigDecimal minLat, BigDecimal maxLat, 
                                                BigDecimal minLng, BigDecimal maxLng) {
        return storeRepository.findByLocationRange(minLat, maxLat, minLng, maxLng);
    }

    /**
     * 查找附近门店
     * @param latitude 纬度
     * @param longitude 经度
     * @param radius 半径（公里）
     * @return 匹配的门店列表
     */
    public List<Store> findNearbyStores(BigDecimal latitude, BigDecimal longitude, Double radius) {
        return storeRepository.findNearbyStores(latitude, longitude, radius);
    }

    /**
     * 获取所有营业中的门店并按距离排序
     * @param latitude 纬度
     * @param longitude 经度
     * @return 门店列表
     */
    public List<Store> findAllOpenStoresOrderByDistance(BigDecimal latitude, BigDecimal longitude) {
        return storeRepository.findAllOpenStoresOrderByDistance(latitude, longitude);
    }

    /**
     * 根据配送费范围查找门店
     * @param minFee 最小配送费
     * @param maxFee 最大配送费
     * @return 匹配的门店列表
     */
    public List<Store> findStoresByDeliveryFeeRange(BigDecimal minFee, BigDecimal maxFee) {
        return storeRepository.findByDeliveryFeeBetween(minFee, maxFee);
    }

    /**
     * 根据最低订单金额范围查找门店
     * @param minAmount 最低订单金额
     * @param maxAmount 最高订单金额
     * @return 匹配的门店列表
     */
    public List<Store> findStoresByMinOrderAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return storeRepository.findByMinOrderAmountBetween(minAmount, maxAmount);
    }

    /**
     * 根据是否自动接单查找门店
     * @param isAutoAccept 是否自动接单
     * @return 匹配的门店列表
     */
    public List<Store> findStoresByAutoAccept(Boolean isAutoAccept) {
        return storeRepository.findByIsAutoAccept(isAutoAccept);
    }

    /**
     * 根据是否支持在线支付查找门店
     * @param isOnlinePayment 是否支持在线支付
     * @return 匹配的门店列表
     */
    public List<Store> findStoresByOnlinePayment(Boolean isOnlinePayment) {
        return storeRepository.findByIsOnlinePayment(isOnlinePayment);
    }

    /**
     * 统计门店总数
     * @return 门店总数
     */
    public long countAllStores() {
        return storeRepository.count();
    }

    /**
     * 统计营业中的门店数量
     * @return 营业中门店数量
     */
    public long countOpenStores() {
        return storeRepository.countOpenStores();
    }

    /**
     * 统计支持配送的门店数量
     * @return 支持配送的门店数量
     */
    public long countDeliveryStores() {
        return storeRepository.countDeliveryStores();
    }

    /**
     * 根据状态统计门店数量
     * @param status 状态
     * @return 门店数量
     */
    public long countStoresByStatus(String status) {
        return storeRepository.countByStatus(status);
    }

    /**
     * 搜索门店（综合条件）
     * @param name 门店名称（可选）
     * @param address 地址（可选）
     * @param status 状态（可选）
     * @param isAutoAccept 是否自动接单（可选）
     * @param isOnlinePayment 是否支持在线支付（可选）
     * @return 匹配的门店列表
     */
    public List<Store> searchStores(String name, String address, String status, 
                                   Boolean isAutoAccept, Boolean isOnlinePayment) {
        // 简化处理：根据条件组合调用不同的查询
        if (name != null && !name.isEmpty() && status != null && !status.isEmpty()) {
            return storeRepository.findByNameContainingIgnoreCaseAndStatusIgnoreCase(name, status);
        } else if (name != null && !name.isEmpty()) {
            return storeRepository.findByNameContainingIgnoreCase(name);
        } else if (address != null && !address.isEmpty() && status != null && !status.isEmpty()) {
            return storeRepository.findByAddressContainingAndStatus(address, status);
        } else if (address != null && !address.isEmpty()) {
            return storeRepository.findByAddressContainingIgnoreCase(address);
        } else if (status != null && !status.isEmpty()) {
            return storeRepository.findByStatusIgnoreCase(status);
        } else if (isAutoAccept != null) {
            return storeRepository.findByIsAutoAccept(isAutoAccept);
        } else if (isOnlinePayment != null) {
            return storeRepository.findByIsOnlinePayment(isOnlinePayment);
        } else {
            return storeRepository.findAll();
        }
    }

    /**
     * 批量更新门店状态
     * @param ids 门店ID列表
     * @param status 新状态
     * @throws ServiceException 如果状态无效
     */
    @Transactional
    public void batchUpdateStoreStatus(List<Long> ids, String status) {
        if (!isValidStatus(status)) {
            throw new ServiceException("INVALID_STATUS", "无效的门店状态: " + status);
        }
        
        List<Store> stores = storeRepository.findAllById(ids);
        for (Store store : stores) {
            store.setStatus(status);
        }
        storeRepository.saveAll(stores);
    }

    /**
     * 验证门店是否营业
     * @param id 门店ID
     * @return 是否营业
     */
    public boolean isStoreOpen(Long id) {
        Optional<Store> store = storeRepository.findById(id);
        return store.isPresent() && "OPEN".equals(store.get().getStatus());
    }

    /**
     * 验证门店是否支持配送
     * @param id 门店ID
     * @return 是否支持配送
     */
    public boolean isStoreDeliveryAvailable(Long id) {
        Optional<Store> store = storeRepository.findById(id);
        return store.isPresent() && store.get().getDeliveryRadius() != null 
                && store.get().getDeliveryRadius() > 0;
    }

    /**
     * 获取门店统计信息
     * @return 门店统计信息
     */
    public StoreStats getStoreStats() {
        long totalStores = storeRepository.count();
        long openStores = storeRepository.countOpenStores();
        long deliveryStores = storeRepository.countDeliveryStores();
        return new StoreStats(totalStores, openStores, deliveryStores);
    }

    // ========== 私有方法 ==========
    
    private void setDefaultValues(Store store) {
        if (store.getStatus() == null) {
            store.setStatus("OPEN");
        }
        if (store.getBusinessStatus() == null) {
            store.setBusinessStatus(1);
        }
        if (store.getIsAutoAccept() == null) {
            store.setIsAutoAccept(false);
        }
        if (store.getIsAutoReceipt() == null) {
            store.setIsAutoReceipt(true);
        }
        if (store.getIsOnlinePayment() == null) {
            store.setIsOnlinePayment(true);
        }
        if (store.getDeliveryRadius() == null) {
            store.setDeliveryRadius(0);
        }
        if (store.getDeliveryFee() == null) {
            store.setDeliveryFee(BigDecimal.ZERO);
        }
        if (store.getMinOrderAmount() == null) {
            store.setMinOrderAmount(BigDecimal.ZERO);
        }
        if (store.getCurrentWaitTime() == null) {
            store.setCurrentWaitTime(0);
        }
        if (store.getRating() == null) {
            store.setRating(new BigDecimal("5.00"));
        }
    }

    /**
     * 验证创建门店的输入
     */
    private void validateStoreForCreate(Store store) {
        if (store == null) {
            throw new ServiceException("STORE_REQUIRED", "门店信息不能为空");
        }
        if (!StringUtils.hasText(store.getName())) {
            throw new ServiceException("STORE_NAME_REQUIRED", "门店名称不能为空");
        }
        if (!StringUtils.hasText(store.getAddress())) {
            throw new ServiceException("STORE_ADDRESS_REQUIRED", "门店地址不能为空");
        }
        // 暂时放宽电话和经纬度要求，或者在 setDefaultValues 中处理
        /*
        if (!StringUtils.hasText(store.getPhone())) {
            throw new ServiceException("STORE_PHONE_REQUIRED", "门店电话不能为空");
        }
        if (store.getLatitude() == null || store.getLongitude() == null) {
            throw new ServiceException("STORE_LOCATION_REQUIRED", "门店经纬度不能为空");
        }
        */
        
        // 验证经纬度范围（如果提供）
        if (store.getLatitude() != null) {
            if (store.getLatitude().compareTo(new BigDecimal("-90")) < 0 || 
                store.getLatitude().compareTo(new BigDecimal("90")) > 0) {
                throw new ServiceException("INVALID_LATITUDE", "纬度必须在-90到90之间");
            }
        }
        if (store.getLongitude() != null) {
            if (store.getLongitude().compareTo(new BigDecimal("-180")) < 0 || 
                store.getLongitude().compareTo(new BigDecimal("180")) > 0) {
                throw new ServiceException("INVALID_LONGITUDE", "经度必须在-180到180之间");
            }
        }
    }
    
    /**
     * 验证更新门店的输入
     */
    private void validateStoreForUpdate(Store store) {
        if (store == null) {
            throw new ServiceException("STORE_REQUIRED", "门店信息不能为空");
        }
        
        // 如果提供了经纬度，验证范围
        if (store.getLatitude() != null) {
            if (store.getLatitude().compareTo(new BigDecimal("-90")) < 0 || 
                store.getLatitude().compareTo(new BigDecimal("90")) > 0) {
                throw new ServiceException("INVALID_LATITUDE", "纬度必须在-90到90之间");
            }
        }
        if (store.getLongitude() != null) {
            if (store.getLongitude().compareTo(new BigDecimal("-180")) < 0 || 
                store.getLongitude().compareTo(new BigDecimal("180")) > 0) {
                throw new ServiceException("INVALID_LONGITUDE", "经度必须在-180到180之间");
            }
        }
        
        // 如果提供了状态，验证状态
        if (store.getStatus() != null && !isValidStatus(store.getStatus())) {
            throw new ServiceException("INVALID_STATUS", "无效的门店状态: " + store.getStatus());
        }
    }
    
    /**
     * 验证门店状态是否有效
     */
    private boolean isValidStatus(String status) {
        return status != null && 
               (status.equals("OPEN") || status.equals("CLOSED") || 
                status.equals("MAINTENANCE") || status.equals("VACATION"));
    }

    /**
     * 门店统计信息
     */
    public static class StoreStats {
        private final long totalStores;
        private final long openStores;
        private final long deliveryStores;

        public StoreStats(long totalStores, long openStores, long deliveryStores) {
            this.totalStores = totalStores;
            this.openStores = openStores;
            this.deliveryStores = deliveryStores;
        }

        public long getTotalStores() {
            return totalStores;
        }

        public long getOpenStores() {
            return openStores;
        }

        public long getDeliveryStores() {
            return deliveryStores;
        }
    }
}