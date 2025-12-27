package com.milktea.backend.service;

import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.UserAddressRepository;
import com.milktea.milktea_backend.model.entity.UserAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Optional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserService userService;
    private final RestTemplate restTemplate;

    @Value("${amap.key}")
    private String amapKey;

    @Value("${amap.regeo-url}")
    private String regeoUrl;

    // 简单的本地缓存，实际生产环境建议使用 Redis
    private final Map<String, Object> geoCache = new ConcurrentHashMap<>();

    public AddressService(UserAddressRepository userAddressRepository, UserService userService) {
        this.userAddressRepository = userAddressRepository;
        this.userService = userService;
        this.restTemplate = new RestTemplate();
    }

    /**
     * 获取当前用户地址列表
     */
    public List<UserAddress> listAddresses() {
        return userAddressRepository.findByUserIdAndIsHistoryFalseOrderByIsDefaultDescCreatedAtDesc(userService.getCurrentUser().getId());
    }

    /**
     * 获取用户地址列表
     */
    public List<UserAddress> getUserAddresses(Long userId) {
        return userAddressRepository.findByUserIdAndIsHistoryFalseOrderByIsDefaultDescCreatedAtDesc(userId);
    }

    /**
     * 根据坐标获取地址
     */
    public Object getAddressByGeolocation(Double lat, Double lng) {
        if (lat == null || lng == null) {
            throw new ServiceException("INVALID_PARAMS", "经纬度不能为空");
        }

        // 1. 检查缓存
        String cacheKey = String.format("%.4f,%.4f", lat, lng);
        if (geoCache.containsKey(cacheKey)) {
            log.info("命中定位缓存: {}", cacheKey);
            return geoCache.get(cacheKey);
        }

        try {
            // 2. 调用高德地图逆地理编码 API
            String location = String.format("%.6f,%.6f", lng, lat);
            String url = UriComponentsBuilder.fromHttpUrl(regeoUrl)
                    .queryParam("key", amapKey)
                    .queryParam("location", location)
                    .queryParam("extensions", "base")
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !"1".equals(response.get("status"))) {
                log.error("高德地图 API 调用失败: {}", response);
                throw new ServiceException("GEO_API_ERROR", "获取地理位置失败");
            }

            Map<String, Object> regeocode = (Map<String, Object>) response.get("regeocode");
            String formattedAddress = (String) regeocode.get("formatted_address");
            Map<String, Object> addressComponent = (Map<String, Object>) regeocode.get("addressComponent");

            Map<String, Object> result = new HashMap<>();
            result.put("address", formattedAddress);
            result.put("latitude", lat);
            result.put("longitude", lng);
            result.put("province", addressComponent.get("province"));
            result.put("city", addressComponent.get("city"));
            result.put("district", addressComponent.get("district"));
            result.put("detail", formattedAddress);

            // 3. 写入缓存
            geoCache.put(cacheKey, result);
            
            return result;
        } catch (Exception e) {
            log.error("逆地理编码异常", e);
            if (e instanceof ServiceException) throw e;
            
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("address", "定位服务暂时不可用");
            fallback.put("latitude", lat);
            fallback.put("longitude", lng);
            return fallback;
        }
    }

    /**
     * 添加地址
     */
    @Transactional
    public UserAddress addAddress(UserAddress address) {
        try {
            com.milktea.milktea_backend.model.entity.User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                throw new ServiceException("USER_NOT_LOGGED_IN", "用户未登录或会话已过期");
            }
            address.setUser(currentUser);
            
            if (address.getUsedCount() == null) address.setUsedCount(0);
            if (address.getIsDefault() == null) address.setIsDefault(false);
            if (address.getLongitude() == null) address.setLongitude(java.math.BigDecimal.ZERO);
            if (address.getLatitude() == null) address.setLatitude(java.math.BigDecimal.ZERO);
            
            if (Boolean.TRUE.equals(address.getIsDefault())) {
                resetDefaultAddress(currentUser.getId());
            }
            return userAddressRepository.save(address);
        } catch (Exception e) {
            if (e instanceof ServiceException) throw e;
            throw new ServiceException("ADDRESS_ADD_FAILED", "添加地址失败: " + e.getMessage());
        }
    }

    /**
     * 更新地址
     */
    @Transactional
    public UserAddress updateAddress(Long addressId, UserAddress addressDetails) {
        return userAddressRepository.findById(addressId).map(address -> {
            if (Boolean.TRUE.equals(addressDetails.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
                resetDefaultAddress(address.getUser().getId());
            }
            address.setName(addressDetails.getName());
            address.setPhone(addressDetails.getPhone());
            address.setProvince(addressDetails.getProvince());
            address.setCity(addressDetails.getCity());
            address.setDistrict(addressDetails.getDistrict());
            address.setDetail(addressDetails.getDetail());
            address.setTag(addressDetails.getTag());
            address.setIsDefault(addressDetails.getIsDefault());
            address.setLatitude(addressDetails.getLatitude());
            address.setLongitude(addressDetails.getLongitude());
            return userAddressRepository.save(address);
        }).orElseThrow(() -> new ServiceException("ADDRESS_NOT_FOUND", "地址不存在"));
    }

    /**
     * 删除地址
     */
    @Transactional
    public void deleteAddress(Long addressId) {
        userAddressRepository.deleteById(addressId);
    }

    /**
     * 获取地址历史记录
     */
    public List<UserAddress> getAddressHistory(Long userId, int limit) {
        return userAddressRepository.findByUserIdAndIsHistoryTrueOrderByLastUsedAtDesc(userId);
    }

    /**
     * 保存地址到历史记录
     */
    @Transactional
    public void saveToHistory(com.milktea.milktea_backend.model.entity.User user, String addressJson) {
        try {
            if (addressJson == null || addressJson.isEmpty()) return;
            
            // 解析地址JSON
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> addrMap = mapper.readValue(addressJson, Map.class);
            
            String province = (String) addrMap.get("province");
            String city = (String) addrMap.get("city");
            String district = (String) addrMap.get("district");
            String detail = (String) addrMap.get("detail");
            String name = (String) addrMap.get("name");
            String phone = (String) addrMap.get("phone");

            if (province == null || city == null || detail == null) return;

            // 查找是否存在相同的历史地址
            List<UserAddress> histories = userAddressRepository.findByUserIdAndIsHistoryTrueOrderByLastUsedAtDesc(user.getId());
            Optional<UserAddress> existing = histories.stream()
                    .filter(a -> province.equals(a.getProvince()) &&
                                city.equals(a.getCity()) &&
                                district.equals(a.getDistrict()) &&
                                detail.equals(a.getDetail()))
                    .findFirst();

            if (existing.isPresent()) {
                UserAddress addr = existing.get();
                addr.setUsedCount((addr.getUsedCount() == null ? 0 : addr.getUsedCount()) + 1);
                addr.setLastUsedAt(java.time.LocalDateTime.now());
                userAddressRepository.save(addr);
            } else {
                UserAddress addr = new UserAddress();
                addr.setUser(user);
                addr.setName(name != null ? name : "历史地址");
                addr.setPhone(phone != null ? phone : "");
                addr.setProvince(province);
                addr.setCity(city);
                addr.setDistrict(district);
                addr.setDetail(detail);
                addr.setIsHistory(true);
                addr.setIsDefault(false);
                addr.setUsedCount(1);
                addr.setLastUsedAt(java.time.LocalDateTime.now());
                userAddressRepository.save(addr);
            }
        } catch (Exception e) {
            log.error("保存地址历史记录失败", e);
        }
    }

    @Transactional
    public void resetDefaultAddress(Long userId) {
        userAddressRepository.resetDefaultAddressByUserId(userId);
    }
}