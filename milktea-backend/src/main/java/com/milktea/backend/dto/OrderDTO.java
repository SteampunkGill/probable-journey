package com.milktea.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String nickname;
    private Long storeId;
    private String storeName;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private BigDecimal discountAmount;
    private BigDecimal deliveryFee;
    private String payMethod;
    private String transactionId;
    private LocalDateTime payTime;
    private String deliveryType;
    private String addressJson;
    private String pickupCode;
    private Integer queueNumber;
    private LocalDateTime estimatedReadyTime;
    private LocalDateTime actualReadyTime;
    private String remark;
    private Integer remindCount;
    private LocalDateTime lastRemindTime;
    private Boolean isCommented;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> orderItems;

    @Data
    public static class OrderItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private String productImage;
        private String specJson;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal totalPrice;
    }
}