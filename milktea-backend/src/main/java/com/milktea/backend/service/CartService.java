package com.milktea.backend.service;

import com.milktea.backend.dto.*;
import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.*;
import com.milktea.milktea_backend.model.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductSpecRepository productSpecRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    public List<CheckoutPrepareDTO.CartItemDTO> getCart(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        return cartItems.stream().map(this::convertToCartItemDTO).collect(Collectors.toList());
    }

    private CheckoutPrepareDTO.CartItemDTO convertToCartItemDTO(CartItem item) {
        CheckoutPrepareDTO.CartItemDTO dto = new CheckoutPrepareDTO.CartItemDTO();
        dto.setId(item.getId());
        dto.setProductName(item.getProduct().getName());
        dto.setProductImage(item.getProduct().getImageUrl());
        dto.setQuantity(item.getQuantity());
        
        BigDecimal price = item.getProduct().getPrice();
        StringBuilder desc = new StringBuilder();
        
        if (item.getSpec() != null) {
            desc.append(item.getSpec().getName()).append(" ");
        }
        
        if (item.getSweetness() != null) desc.append(item.getSweetness()).append(" ");
        if (item.getTemperature() != null) desc.append(item.getTemperature()).append(" ");
        
        if (item.getCustomizations() != null) {
            for (CartItemCustomization customization : item.getCustomizations()) {
                ProductOption option = customization.getOption();
                price = price.add(option.getPrice());
                desc.append(option.getName()).append(" ");
            }
        }
        
        dto.setPrice(price);
        dto.setSpecDescription(desc.toString().trim());
        return dto;
    }

    @Transactional
    public void addToCart(Long userId, CartAddRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("USER_NOT_FOUND", "用户不存在"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ServiceException("PRODUCT_NOT_FOUND", "商品不存在"));
        Store store = null;
        if (request.getStoreId() != null) {
            store = storeRepository.findById(request.getStoreId()).orElse(null);
        }

        ProductSpec spec = null;
        if (request.getSpecId() != null) {
            spec = productSpecRepository.findById(request.getSpecId()).orElse(null);
        }

        // 尝试查找已存在的相同配置的购物车项
        // 注意：这里简化了加料的比较，实际业务中可能需要更复杂的逻辑
        java.util.Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductIdAndSpecAndSweetnessAndTemperature(
                userId, request.getProductId(), spec, request.getSweetness(), request.getTemperature());

        CartItem item;
        if (existingItem.isPresent()) {
            item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            item = new CartItem();
            item.setUser(user);
            item.setStore(store);
            item.setProduct(product);
            item.setQuantity(request.getQuantity());
            item.setSweetness(request.getSweetness());
            item.setTemperature(request.getTemperature());
            item.setIsSelected(true);
            item.setIsValid(true);
            item.setPriceAtAdd(product.getPrice());
            item.setSpec(spec);

            if (request.getToppings() != null && !request.getToppings().isEmpty()) {
                List<ProductOption> options = productOptionRepository.findAllById(request.getToppings());
                List<CartItemCustomization> customizations = options.stream().map(option -> {
                    CartItemCustomization c = new CartItemCustomization();
                    c.setCartItem(item);
                    c.setOption(option);
                    c.setQuantity(1);
                    return c;
                }).collect(Collectors.toList());
                item.setCustomizations(customizations);
            }
        }
        
        cartItemRepository.save(item);
    }

    @Transactional
    public void updateQuantity(Long userId, CartUpdateDTO updateDTO) {
        CartItem item = cartItemRepository.findById(updateDTO.getCartItemId())
                .orElseThrow(() -> new ServiceException("CART_ITEM_NOT_FOUND", "购物车项不存在"));
        if (!item.getUser().getId().equals(userId)) {
            throw new ServiceException("PERMISSION_DENIED", "无权操作此购物车项");
        }
        item.setQuantity(updateDTO.getQuantity());
        cartItemRepository.save(item);
    }

    @Transactional
    public void removeFromCart(Long userId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ServiceException("CART_ITEM_NOT_FOUND", "购物车项不存在"));
        if (!item.getUser().getId().equals(userId)) {
            throw new ServiceException("PERMISSION_DENIED", "无权操作此购物车项");
        }
        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        cartItemRepository.deleteAll(items);
    }


    public CouponMatchResponseDTO autoMatchCoupons(Long userId, CouponMatchRequestDTO request) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return new CouponMatchResponseDTO();

        List<UserCoupon> coupons = userCouponRepository.findByUserAndUsedFalseAndEndTimeAfter(user, LocalDateTime.now());
        
        CouponMatchResponseDTO response = new CouponMatchResponseDTO();
        List<CouponMatchResponseDTO.CouponInfo> matches = new ArrayList<>();
        
        for (UserCoupon uc : coupons) {
            CouponTemplate temp = uc.getCouponTemplate();
            if (request.getTotalAmount().compareTo(temp.getMinAmount()) >= 0) {
                BigDecimal saving = BigDecimal.ZERO;
                if ("REDUCTION".equals(temp.getType())) {
                    saving = temp.getValue();
                } else if ("DISCOUNT".equals(temp.getType())) {
                    saving = request.getTotalAmount().multiply(BigDecimal.ONE.subtract(temp.getValue()));
                }
                
                matches.add(new CouponMatchResponseDTO.CouponInfo(
                    uc.getId(), temp.getName(), temp.getType(), temp.getValue(), saving, "满" + temp.getMinAmount() + "元可用"
                ));
            }
        }
        
        matches.sort((a, b) -> b.getSaving().compareTo(a.getSaving()));
        
        if (!matches.isEmpty()) {
            response.setBestMatch(matches.get(0));
            if (matches.size() > 1) {
                response.setAlternativeMatches(matches.subList(1, matches.size()));
            }
        }
        
        response.setSuggestion("再买一点更优惠哦");
        return response;
    }

    public CheckoutPrepareDTO prepareCheckout(Long userId) {
        List<CheckoutPrepareDTO.CartItemDTO> items = getCart(userId);
        CheckoutPrepareDTO dto = new CheckoutPrepareDTO();
        dto.setItems(items);
        
        BigDecimal total = items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        dto.setTotalAmount(total);
        dto.setDefaultAddress(userAddressRepository.findByUserIdAndIsDefaultTrue(userId).orElse(null));
        
        // 自动匹配优惠券
        CouponMatchRequestDTO matchRequest = new CouponMatchRequestDTO();
        matchRequest.setTotalAmount(total);
        CouponMatchResponseDTO couponMatch = autoMatchCoupons(userId, matchRequest);
        
        BigDecimal discount = BigDecimal.ZERO;
        if (couponMatch.getBestMatch() != null) {
            discount = couponMatch.getBestMatch().getSaving();
        }
        
        dto.setDiscountAmount(discount);
        dto.setPayAmount(total.subtract(discount).max(BigDecimal.ZERO));
        
        // 真实计算预估等待时间：基于当前门店待制作订单数
        // 假设每单平均制作时间为5分钟
        long pendingOrders = orderRepository.countByStatusIn(List.of("PAID", "MAKING"));
        int estimatedWaitTime = (int) (pendingOrders + 1) * 5;
        dto.setEstimatedWaitTime(estimatedWaitTime);
        
        return dto;
    }

    public PriceCalculationResponseDTO calculatePrice(Long productId, PriceCalculationRequestDTO request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ServiceException("PRODUCT_NOT_FOUND", "商品不存在"));
        
        PriceCalculationResponseDTO response = new PriceCalculationResponseDTO();
        List<PriceCalculationResponseDTO.PriceBreakdownItem> breakdown = new ArrayList<>();
        
        BigDecimal unitPrice = product.getPrice();
        breakdown.add(new PriceCalculationResponseDTO.PriceBreakdownItem("基础价格", product.getPrice()));
        
        if (request.getSpecId() != null) {
            // 规格逻辑
        }
        
        if (request.getToppings() != null && !request.getToppings().isEmpty()) {
            List<ProductOption> options = productOptionRepository.findAllById(request.getToppings());
            for (ProductOption option : options) {
                unitPrice = unitPrice.add(option.getPrice());
                breakdown.add(new PriceCalculationResponseDTO.PriceBreakdownItem(option.getName(), option.getPrice()));
            }
        }
        
        response.setUnitPrice(unitPrice);
        response.setTotalPrice(unitPrice.multiply(new BigDecimal(request.getQuantity())));
        response.setBreakdown(breakdown);
        
        return response;
    }
}