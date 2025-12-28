package com.milktea.backend.service;

import com.milktea.backend.dto.CouponDTO;
import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.CouponTemplateRepository;
import com.milktea.backend.repository.UserCouponRepository;
import com.milktea.backend.repository.UserRepository;
import com.milktea.milktea_backend.model.entity.CouponTemplate;
import com.milktea.milktea_backend.model.entity.User;
import com.milktea.milktea_backend.model.entity.UserCoupon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CouponService {

    private final CouponTemplateRepository couponTemplateRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public CouponService(CouponTemplateRepository couponTemplateRepository,
                         UserCouponRepository userCouponRepository,
                         UserService userService,
                         UserRepository userRepository) {
        this.couponTemplateRepository = couponTemplateRepository;
        this.userCouponRepository = userCouponRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // --- App 接口方法 ---

    public List<CouponDTO> getAvailableCoupons() {
        LocalDateTime now = LocalDateTime.now();
        return couponTemplateRepository.findAll().stream()
                .filter(t -> (t.getTotalQuantity() == -1 || t.getIssuedQuantity() < t.getTotalQuantity()))
                .filter(t -> t.getValidityType() == CouponTemplate.ValidityType.FIXED_DAYS || 
                            ((t.getStartTime() == null || t.getStartTime().isBefore(now)) && 
                             (t.getEndTime() == null || t.getEndTime().isAfter(now))))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void receiveCoupon(Long templateId) {
        User user = userService.getCurrentUser();
        CouponTemplate template = couponTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ServiceException("优惠券不存在"));

        if (template.getTotalQuantity() != -1 && template.getIssuedQuantity() >= template.getTotalQuantity()) {
            throw new ServiceException("优惠券已领完");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCouponTemplate(template);
        userCoupon.setUsed(false);
        userCoupon.setStatus("UNUSED");
        userCoupon.setReceiveTime(LocalDateTime.now());
        // 设置使用次数，如果是活动打折逻辑，可以设置更多次数
        if (template.getRuleJson() != null && template.getRuleJson().contains("999")) {
            userCoupon.setRemainingUses(999);
        } else {
            userCoupon.setRemainingUses(1);
        }

        if (template.getValidityType() == CouponTemplate.ValidityType.FIXED_DAYS) {
            userCoupon.setStartTime(LocalDateTime.now());
            userCoupon.setEndTime(LocalDateTime.now().plusDays(template.getValidityDays()));
        } else {
            userCoupon.setStartTime(template.getStartTime());
            userCoupon.setEndTime(template.getEndTime());
        }

        userCouponRepository.save(userCoupon);

        template.setIssuedQuantity(template.getIssuedQuantity() + 1);
        couponTemplateRepository.save(template);
    }

    public List<CouponDTO> getMyCoupons() {
        User user = userService.getCurrentUser();
        return userCouponRepository.findByUserId(user.getId()).stream()
                .map(this::convertToUserCouponDTO)
                .collect(Collectors.toList());
    }

    public CouponDTO getCouponDetail(Long id) {
        CouponTemplate template = couponTemplateRepository.findById(id)
                .orElseThrow(() -> new ServiceException("优惠券不存在"));
        return convertToDTO(template);
    }

    /**
     * 为订单自动匹配最佳优惠券
     */
    public CouponDTO findBestCoupon(BigDecimal orderAmount) {
        User user = userService.getCurrentUser();
        List<UserCoupon> myCoupons = userCouponRepository.findByUserIdAndStatus(user.getId(), "UNUSED");
        
        UserCoupon bestCoupon = null;
        BigDecimal maxDiscount = BigDecimal.ZERO;
        
        for (UserCoupon uc : myCoupons) {
            CouponTemplate template = uc.getCouponTemplate();
            if (orderAmount.compareTo(template.getMinAmount()) >= 0) {
                BigDecimal currentDiscount = BigDecimal.ZERO;
                if ("REDUCTION".equals(template.getType())) {
                    currentDiscount = template.getValue();
                } else if ("DISCOUNT".equals(template.getType())) {
                    currentDiscount = orderAmount.subtract(orderAmount.multiply(template.getValue()).setScale(2, BigDecimal.ROUND_HALF_UP));
                }
                
                if (currentDiscount.compareTo(maxDiscount) > 0) {
                    maxDiscount = currentDiscount;
                    bestCoupon = uc;
                }
            }
        }
        
        return bestCoupon != null ? convertToUserCouponDTO(bestCoupon) : null;
    }

    // --- Admin 接口方法 ---

    public List<CouponTemplate> findAllTemplates() {
        return couponTemplateRepository.findAll();
    }

    @Transactional
    public CouponTemplate createTemplate(CouponTemplate template) {
        if (template.getIssuedQuantity() == null) {
            template.setIssuedQuantity(0);
        }
        return couponTemplateRepository.save(template);
    }

    @Transactional
    public void distribute(Long templateId, List<Long> userIds) {
        CouponTemplate template = couponTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ServiceException("模板不存在"));
        
        for (Long userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ServiceException("用户不存在: " + userId));
            
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUser(user);
            userCoupon.setCouponTemplate(template);
            userCoupon.setUsed(false);
            userCoupon.setStatus("UNUSED");
            userCoupon.setReceiveTime(LocalDateTime.now());
            // 设置使用次数，如果是活动打折逻辑，可以设置更多次数
            if (template.getRuleJson() != null && template.getRuleJson().contains("999")) {
                userCoupon.setRemainingUses(999);
            } else {
                userCoupon.setRemainingUses(1);
            }
            
            if (template.getValidityType() == CouponTemplate.ValidityType.FIXED_DAYS) {
                userCoupon.setStartTime(LocalDateTime.now());
                userCoupon.setEndTime(LocalDateTime.now().plusDays(template.getValidityDays()));
            } else {
                userCoupon.setStartTime(template.getStartTime());
                userCoupon.setEndTime(template.getEndTime());
            }
            
            userCouponRepository.save(userCoupon);
            template.setIssuedQuantity(template.getIssuedQuantity() + 1);
        }
        couponTemplateRepository.save(template);
    }

    public Map<String, Object> getStatistics(Long id) {
        CouponTemplate template = couponTemplateRepository.findById(id)
                .orElseThrow(() -> new ServiceException("模板不存在"));
        
        long usedCount = userCouponRepository.countByCouponTemplateIdAndUsedTrue(id);
        long receivedCount = userCouponRepository.countByCouponTemplateId(id);

        Map<String, Object> stats = new HashMap<>();
        stats.put("templateName", template.getName());
        stats.put("totalIssued", template.getIssuedQuantity());
        stats.put("receivedCount", receivedCount);
        stats.put("usedCount", usedCount);
        stats.put("usageRate", receivedCount > 0 ? (double) usedCount / receivedCount * 100 : 0);
        return stats;
    }

    public List<UserCoupon> findAllDistributionRecords() {
        return userCouponRepository.findAll();
    }

    // --- 辅助方法 ---

    private CouponDTO convertToDTO(CouponTemplate template) {
        CouponDTO dto = new CouponDTO();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setType(template.getType());
        dto.setValue(template.getValue());
        dto.setMinAmount(template.getMinAmount());
        dto.setValidityType(template.getValidityType().name());
        dto.setValidityDays(template.getValidityDays());
        dto.setStartTime(template.getStartTime());
        dto.setEndTime(template.getEndTime());
        return dto;
    }

    private CouponDTO convertToUserCouponDTO(UserCoupon userCoupon) {
        CouponDTO dto = convertToDTO(userCoupon.getCouponTemplate());
        dto.setId(userCoupon.getId()); // 使用 UserCoupon 的 ID
        dto.setStatus(userCoupon.getStatus());
        dto.setStartTime(userCoupon.getStartTime());
        dto.setEndTime(userCoupon.getEndTime());
        return dto;
    }
}