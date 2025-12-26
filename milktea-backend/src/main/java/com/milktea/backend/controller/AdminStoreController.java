package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.dto.StoreDTO;
import com.milktea.backend.service.StoreService;
import com.milktea.milktea_backend.model.entity.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin/stores")
@RequiredArgsConstructor
public class AdminStoreController {

    private final StoreService storeService;

    @GetMapping
    public ApiResponse<List<StoreDTO>> getStoreList() {
        try {
            log.info("Fetching all stores for admin");
            List<Store> stores = storeService.findAllStores();
            List<StoreDTO> dtos = stores.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            log.error("Error fetching store list", e);
            throw e;
        }
    }

    @PostMapping
    public ApiResponse<StoreDTO> createStore(@RequestBody StoreDTO storeDTO) {
        log.info("Creating new store: {}", storeDTO.getName());
        Store store = convertToEntity(storeDTO);
        Store savedStore = storeService.createStore(store);
        return ApiResponse.success(convertToDTO(savedStore));
    }

    @PutMapping("/{id}")
    public ApiResponse<StoreDTO> updateStore(@PathVariable Long id, @RequestBody StoreDTO storeDTO) {
        log.info("Updating store id {}: {}", id, storeDTO.getName());
        Store store = convertToEntity(storeDTO);
        return storeService.updateStore(id, store)
                .map(updatedStore -> ApiResponse.success(convertToDTO(updatedStore)))
                .orElseThrow(() -> new com.milktea.backend.exception.ServiceException("STORE_NOT_FOUND", "门店不存在"));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStore(@PathVariable Long id) {
        log.info("Deleting store id {}", id);
        storeService.deleteStore(id);
        return ApiResponse.success(null);
    }

    private StoreDTO convertToDTO(Store store) {
        if (store == null) return null;
        StoreDTO dto = new StoreDTO();
        dto.setId(store.getId());
        dto.setName(store.getName());
        dto.setCode(store.getCode());
        dto.setAddress(store.getAddress());
        dto.setPhone(store.getPhone());
        dto.setManagerName(store.getManagerName());
        dto.setManagerPhone(store.getManagerPhone());
        dto.setStatus(store.getStatus());
        dto.setLatitude(store.getLatitude());
        dto.setLongitude(store.getLongitude());
        dto.setDeliveryRadius(store.getDeliveryRadius());
        dto.setDeliveryFee(store.getDeliveryFee());
        dto.setMinOrderAmount(store.getMinOrderAmount());
        dto.setIsAutoAccept(store.getIsAutoAccept() != null ? store.getIsAutoAccept() : false);
        dto.setIsOnlinePayment(store.getIsOnlinePayment() != null ? store.getIsOnlinePayment() : true);
        return dto;
    }

    private Store convertToEntity(StoreDTO dto) {
        Store store = new Store();
        store.setId(dto.getId());
        store.setName(dto.getName());
        store.setCode(dto.getCode());
        store.setAddress(dto.getAddress());
        store.setPhone(dto.getPhone());
        store.setManagerName(dto.getManagerName());
        store.setManagerPhone(dto.getManagerPhone());
        store.setStatus(dto.getStatus());
        store.setLatitude(dto.getLatitude());
        store.setLongitude(dto.getLongitude());
        store.setDeliveryRadius(dto.getDeliveryRadius());
        store.setDeliveryFee(dto.getDeliveryFee());
        store.setMinOrderAmount(dto.getMinOrderAmount());
        store.setIsAutoAccept(dto.getIsAutoAccept());
        store.setIsOnlinePayment(dto.getIsOnlinePayment());
        
        // 设置实体中必填但DTO中没有的字段默认值，防止数据库约束错误
        if (store.getStatus() == null) store.setStatus("OPEN");
        if (store.getIsAutoReceipt() == null) store.setIsAutoReceipt(true);
        if (store.getCurrentWaitTime() == null) store.setCurrentWaitTime(0);
        if (store.getBusinessStatus() == null) store.setBusinessStatus(1);
        
        return store;
    }
}