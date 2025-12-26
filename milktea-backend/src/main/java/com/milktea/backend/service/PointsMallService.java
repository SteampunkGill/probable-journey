package com.milktea.backend.service;

import com.milktea.backend.dto.PointExchangeRecordDTO;
import com.milktea.backend.dto.PointProductDTO;
import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.*;
import com.milktea.milktea_backend.model.entity.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PointsMallService {

    private final PointExchangeItemRepository pointExchangeItemRepository;
    private final PointExchangeRecordRepository pointExchangeRecordRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final UserService userService;
    private final CouponTemplateRepository couponTemplateRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;

    public PointsMallService(PointExchangeItemRepository pointExchangeItemRepository,
                             PointExchangeRecordRepository pointExchangeRecordRepository,
                             PointTransactionRepository pointTransactionRepository,
                             UserService userService,
                             CouponTemplateRepository couponTemplateRepository,
                             UserCouponRepository userCouponRepository,
                             UserRepository userRepository) {
        this.pointExchangeItemRepository = pointExchangeItemRepository;
        this.pointExchangeRecordRepository = pointExchangeRecordRepository;
        this.pointTransactionRepository = pointTransactionRepository;
        this.userService = userService;
        this.couponTemplateRepository = couponTemplateRepository;
        this.userCouponRepository = userCouponRepository;
        this.userRepository = userRepository;
    }

    public List<PointProductDTO> getAvailableProducts(String category) {
        List<PointExchangeItem> items = pointExchangeItemRepository.findAll();
        return items.stream()
                .filter(item -> "AVAILABLE".equals(item.getStatus()))
                .filter(item -> category == null || "all".equals(category) || category.equals(item.getCategory()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getCategories() {
        // 真实逻辑：从系统配置或特定逻辑获取
        return List.of(
                Map.of("id", 1L, "name", "热门商品", "icon", "fire", "type", "HOT"),
                Map.of("id", 2L, "name", "优惠券", "icon", "coupon", "type", "COUPON"),
                Map.of("id", 3L, "name", "实物礼品", "icon", "gift", "type", "GIFT"),
                Map.of("id", 4L, "name", "饮品券", "icon", "coffee", "type", "DRINK")
        );
    }

    @Transactional
    public void exchangeProduct(Long productId) {
        User user = userService.getCurrentUser();
        PointExchangeItem item = pointExchangeItemRepository.findById(productId)
                .orElseThrow(() -> new ServiceException("商品不存在"));

        if (item.getStock() <= 0) {
            throw new ServiceException("库存不足");
        }

        if (user.getPoints() < item.getPointCost()) {
            throw new ServiceException("积分不足");
        }

        // 扣减积分
        user.setPoints(user.getPoints() - item.getPointCost());
        userRepository.save(user);

        // 记录积分变动
        PointTransaction transaction = new PointTransaction();
        transaction.setUser(user);
        transaction.setAmount(-item.getPointCost());
        transaction.setBalanceAfter(user.getPoints());
        transaction.setType("EXCHANGE");
        transaction.setRemark("兑换商品: " + item.getName());
        transaction.setDescription("兑换商品: " + item.getName());
        pointTransactionRepository.save(transaction);

        // 扣减库存
        item.setStock(item.getStock() - 1);
        pointExchangeItemRepository.save(item);

        // 创建兑换记录
        PointExchangeRecord record = new PointExchangeRecord();
        record.setUser(user);
        record.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        record.setTotalPoints(item.getPointCost());
        record.setTotalItems(1);
        record.setStatus("COMPLETED");
        pointExchangeRecordRepository.save(record);
    }

    public List<PointExchangeRecordDTO> getExchangeRecords() {
        User user = userService.getCurrentUser();
        return pointExchangeRecordRepository.findAll().stream()
                .filter(r -> r.getUser().getId().equals(user.getId()))
                .map(this::convertToRecordDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public int signIn() {
        User user = userService.getCurrentUser();
        // 检查今日是否已签到（简单逻辑：检查今日是否有 SIGN_IN 类型的交易记录）
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        boolean alreadySignedIn = pointTransactionRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .filter(t -> "SIGN_IN".equals(t.getType()))
                .anyMatch(t -> t.getCreatedAt().isAfter(startOfDay));

        if (alreadySignedIn) {
            throw new ServiceException("今日已签到，请明天再来");
        }

        int signInPoints = 10; // 签到奖励10积分
        user.setPoints(user.getPoints() + signInPoints);
        userRepository.save(user);

        // 记录积分变动
        PointTransaction transaction = new PointTransaction();
        transaction.setUser(user);
        transaction.setAmount(signInPoints);
        transaction.setBalanceAfter(user.getPoints());
        transaction.setType("SIGN_IN");
        transaction.setRemark("每日签到奖励");
        transaction.setDescription("每日签到奖励");
        pointTransactionRepository.save(transaction);

        return user.getPoints();
    }

    @Transactional
    public void exchangeCoupon(Long templateId) {
        User user = userService.getCurrentUser();
        CouponTemplate template = couponTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ServiceException("优惠券不存在"));

        // 假设积分兑换优惠券的规则：100积分兑换
        int pointCost = 100; 

        if (user.getPoints() < pointCost) {
            throw new ServiceException("积分不足");
        }

        // 扣减积分
        user.setPoints(user.getPoints() - pointCost);
        userRepository.save(user);

        // 记录积分变动
        PointTransaction transaction = new PointTransaction();
        transaction.setUser(user);
        transaction.setAmount(-pointCost);
        transaction.setBalanceAfter(user.getPoints());
        transaction.setType("EXCHANGE_COUPON");
        transaction.setRemark("积分兑换优惠券: " + template.getName());
        transaction.setDescription("积分兑换优惠券: " + template.getName());
        pointTransactionRepository.save(transaction);

        // 发放优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCouponTemplate(template);
        userCoupon.setUsed(false);
        userCoupon.setStatus("UNUSED");
        userCoupon.setReceiveTime(LocalDateTime.now());
        
        if (template.getValidityType() == CouponTemplate.ValidityType.FIXED_DAYS) {
            userCoupon.setStartTime(LocalDateTime.now());
            userCoupon.setEndTime(LocalDateTime.now().plusDays(template.getValidityDays()));
        } else {
            userCoupon.setStartTime(template.getStartTime());
            userCoupon.setEndTime(template.getEndTime());
        }
        userCouponRepository.save(userCoupon);
    }

    private PointProductDTO convertToDTO(PointExchangeItem item) {
        PointProductDTO dto = new PointProductDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setImageUrl(item.getImageUrl());
        dto.setPointCost(item.getPointCost());
        dto.setStock(item.getStock());
        dto.setStatus(item.getStatus());
        dto.setCategory(item.getCategory());
        
        // 设置额外字段
        dto.setPoints(item.getPointCost());
        dto.setOriginalPoints((int)(item.getPointCost() * 1.2)); // 模拟原价
        dto.setLimitInfo("每人限兑5件");
        dto.setHot(item.getStock() < 10); // 库存少于10件为热门
        dto.setImage(item.getImageUrl());
        
        return dto;
    }

    private PointExchangeRecordDTO convertToRecordDTO(PointExchangeRecord record) {
        PointExchangeRecordDTO dto = new PointExchangeRecordDTO();
        dto.setId(record.getId());
        dto.setProductName("积分兑换商品");
        dto.setPointCost(record.getTotalPoints());
        dto.setExchangeTime(record.getCreatedAt());
        dto.setStatus(record.getStatus());
        return dto;
    }
}