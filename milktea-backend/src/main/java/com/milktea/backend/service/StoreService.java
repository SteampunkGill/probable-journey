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


    public Optional<Store> getStoreById(Long id) {
        return storeRepository.findById(id);
    }


    public Optional<Store> getStoreByCode(String code) {
        return storeRepository.findByCode(code);
    }


    public List<Store> findAllStores() {
        try {
            return storeRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("DB_ERROR", "获取门店列表失败: " + e.getMessage());
        }
    }


    public List<Store> findOpenStores() {
        return storeRepository.findOpenStores();
    }


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


    public List<Store> findDeliveryStores() {
        return storeRepository.findDeliveryStores();
    }


    public List<Store> findRecentStores() {
        return storeRepository.findRecentStores();
    }


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
            if (storeDetails.getBusinessHours() != null) {
                store.setBusinessHours(storeDetails.getBusinessHours());
            }
            // 15. 后台增加一个每个店铺营业时间调整
            if (storeDetails.getOpenTime() != null) {
                store.setOpenTime(storeDetails.getOpenTime());
            }
            if (storeDetails.getCloseTime() != null) {
                store.setCloseTime(storeDetails.getCloseTime());
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

    @Transactional
    public Optional<Store> updateStoreStatus(Long id, String status) {
        if (!isValidStatus(status)) {
            throw new ServiceException("INVALID_STATUS", "无效的门店状态: " + status);
        }
        
        return storeRepository.findById(id).map(store -> {
            store.setStatus(status);
            // 同步更新 businessStatus
            if ("OPEN".equals(status)) {
                store.setBusinessStatus(1);
            } else if ("CLOSED".equals(status)) {
                store.setBusinessStatus(0);
            }
            return storeRepository.save(store);
        });
    }


    @Transactional
    public Optional<Store> updateStoreBusinessStatus(Long id, String status) {
        return updateStoreStatus(id, status);
    }


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


    @Transactional
    public void deleteStore(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new ServiceException("STORE_NOT_FOUND", "门店不存在，ID: " + id);
        }
        storeRepository.deleteById(id);
    }


    public List<Store> findStoresByName(String name) {
        return storeRepository.findByNameContainingIgnoreCase(name);
    }


    public List<Store> findStoresByAddress(String address) {
        return storeRepository.findByAddressContainingIgnoreCase(address);
    }


    public List<Store> findStoresByStatus(String status) {
        return storeRepository.findByStatusIgnoreCase(status);
    }


    public List<Store> findStoresByLocationRange(BigDecimal minLat, BigDecimal maxLat, 
                                                BigDecimal minLng, BigDecimal maxLng) {
        return storeRepository.findByLocationRange(minLat, maxLat, minLng, maxLng);
    }


    public List<Store> findNearbyStores(BigDecimal latitude, BigDecimal longitude, Double radius) {
        return storeRepository.findNearbyStores(latitude, longitude, radius);
    }


    public List<Store> findAllOpenStoresOrderByDistance(BigDecimal latitude, BigDecimal longitude) {
        return storeRepository.findAllOpenStoresOrderByDistance(latitude, longitude);
    }

    public List<Store> findStoresByDeliveryFeeRange(BigDecimal minFee, BigDecimal maxFee) {
        return storeRepository.findByDeliveryFeeBetween(minFee, maxFee);
    }


    public List<Store> findStoresByMinOrderAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return storeRepository.findByMinOrderAmountBetween(minAmount, maxAmount);
    }


    public List<Store> findStoresByAutoAccept(Boolean isAutoAccept) {
        return storeRepository.findByIsAutoAccept(isAutoAccept);
    }


    public List<Store> findStoresByOnlinePayment(Boolean isOnlinePayment) {
        return storeRepository.findByIsOnlinePayment(isOnlinePayment);
    }


    public long countAllStores() {
        return storeRepository.count();
    }


    public long countOpenStores() {
        return storeRepository.countOpenStores();
    }

    public long countDeliveryStores() {
        return storeRepository.countDeliveryStores();
    }


    public long countStoresByStatus(String status) {
        return storeRepository.countByStatus(status);
    }


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


    public boolean isStoreOpen(Long id) {
        Optional<Store> store = storeRepository.findById(id);
        return store.isPresent() && "OPEN".equals(store.get().getStatus());
    }


    public boolean isStoreDeliveryAvailable(Long id) {
        Optional<Store> store = storeRepository.findById(id);
        return store.isPresent() && store.get().getDeliveryRadius() != null 
                && store.get().getDeliveryRadius() > 0;
    }


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
    

    private boolean isValidStatus(String status) {
        return status != null && 
               (status.equals("OPEN") || status.equals("CLOSED") || 
                status.equals("MAINTENANCE") || status.equals("VACATION"));
    }


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