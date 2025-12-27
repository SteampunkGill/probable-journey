package com.milktea.backend.service;

import com.milktea.backend.dto.MemberDTO;
import com.milktea.backend.dto.MemberExclusivePriceDTO;
import com.milktea.backend.dto.MemberQueryDTO;
import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.*;
import com.milktea.milktea_backend.model.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberLevelRepository memberLevelRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final UserRepository userRepository;
    private final UserTagRepository userTagRepository;
    private final UserTagRelationRepository userTagRelationRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public MemberService(MemberLevelRepository memberLevelRepository,
                         PointTransactionRepository pointTransactionRepository,
                         SystemConfigRepository systemConfigRepository,
                         UserRepository userRepository,
                         UserTagRepository userTagRepository,
                         UserTagRelationRepository userTagRelationRepository,
                         UserService userService,
                         ProductRepository productRepository,
                         OrderRepository orderRepository,
                         NotificationService notificationService) {
        this.memberLevelRepository = memberLevelRepository;
        this.pointTransactionRepository = pointTransactionRepository;
        this.systemConfigRepository = systemConfigRepository;
        this.userRepository = userRepository;
        this.userTagRepository = userTagRepository;
        this.userTagRelationRepository = userTagRelationRepository;
        this.userService = userService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    /**
     * 获取所有会员等级信息
     */
    public List<MemberLevel> getAllMemberLevels() {
        return memberLevelRepository.findAll();
    }

    /**
     * 分页查询用户积分流水
     */
    public Page<PointTransaction> getUserPointsHistory(Long userId, Pageable pageable) {
        return pointTransactionRepository.findByUserId(userId, pageable);
    }

    /**
     * 获取会员等级权益信息
     */
    public Map<String, Object> getMemberLevelInfo() {
        User user = userService.getCurrentUser();
        Map<String, Object> info = new HashMap<>();
        
        // 当前用户信息
        info.put("points", user.getPoints());
        info.put("growthValue", user.getGrowthValue());
        
        if (user.getMemberLevel() != null) {
            info.put("currentLevel", user.getMemberLevel().getName());
            info.put("discount", user.getMemberLevel().getDiscountRate());
        } else {
            info.put("currentLevel", "普通会员");
            info.put("discount", 1.0);
        }
        
        // 所有等级信息，用于前端计算进度
        List<MemberLevel> allLevels = memberLevelRepository.findAll();
        info.put("allLevels", allLevels);
        
        String benefits = systemConfigRepository.findById("member_benefits")
                .map(SystemConfig::getConfigValue).orElse("生日双倍积分,每月领券");
        info.put("benefits", Arrays.asList(benefits.split(",")));
        return info;
    }

    /**
     * 获取积分流水
     */
    public List<Object> getPointsHistory(int page, int size) {
        User user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PointTransaction> history = pointTransactionRepository.findByUserId(user.getId(), pageable);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return history.getContent().stream().map(t -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", t.getId());
            map.put("points", t.getAmount());
            map.put("type", t.getType());
            map.put("time", t.getCreatedAt().format(formatter));
            map.put("remark", t.getRemark());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 获取积分抵现规则
     */
    public Map<String, Object> getPointRules() {
        Map<String, Object> rules = new HashMap<>();
        
        // 从系统配置中获取规则，如果不存在则使用默认值
        int pointsPerYuan = systemConfigRepository.findById("points_per_yuan")
                .map(c -> Integer.parseInt(c.getConfigValue())).orElse(1);
        int maxPointsPerOrder = systemConfigRepository.findById("max_points_per_order")
                .map(c -> Integer.parseInt(c.getConfigValue())).orElse(100);
        int pointsToCashRate = systemConfigRepository.findById("points_to_cash_rate")
                .map(c -> Integer.parseInt(c.getConfigValue())).orElse(100);
        int minPointsToUse = systemConfigRepository.findById("min_points_to_use")
                .map(c -> Integer.parseInt(c.getConfigValue())).orElse(100);
        String rulesDesc = systemConfigRepository.findById("points_rules_description")
                .map(SystemConfig::getConfigValue).orElse("每消费1元得1积分,100积分可抵1元现金");

        rules.put("pointsPerYuan", pointsPerYuan);
        rules.put("maxPointsPerOrder", maxPointsPerOrder);
        rules.put("pointsToCashRate", pointsToCashRate);
        rules.put("minPointsToUse", minPointsToUse);
        rules.put("rules", rulesDesc);
        
        return rules;
    }

    public Page<MemberDTO> queryMembers(MemberQueryDTO query) {
        Pageable pageable = PageRequest.of(query.getPage() - 1, query.getSize());
        Page<User> userPage;
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            // 简单实现关键字搜索
            List<User> users = userRepository.findByNicknameContaining(query.getKeyword());
            userPage = new PageImpl<>(users, pageable, users.size());
        } else if (query.getMemberLevelId() != null) {
            List<User> users = userRepository.findByMemberLevelId(query.getMemberLevelId());
            userPage = new PageImpl<>(users, pageable, users.size());
        } else {
            userPage = userRepository.findAll(pageable);
        }
        
        List<MemberDTO> dtos = userPage.getContent().stream()
                .map(user -> {
                    MemberDTO dto = convertToDTO(user);
                    // 填充标签
                    List<String> tags = userTagRelationRepository.findByUserId(user.getId()).stream()
                            .map(r -> r.getTag().getName())
                            .collect(Collectors.toList());
                    dto.setTags(tags);
                    return dto;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, userPage.getTotalElements());
    }

    public MemberDTO getMemberDetail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ServiceException("用户不存在"));
        MemberDTO dto = convertToDTO(user);
        
        // 获取用户标签
        List<String> tags = userTagRelationRepository.findByUserId(id).stream()
                .map(r -> r.getTag().getName())
                .collect(Collectors.toList());
        dto.setTags(tags);
        
        return dto;
    }

    @Transactional
    public void updateMemberLevel(Long id, Long levelId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ServiceException("用户不存在"));
        MemberLevel level = memberLevelRepository.findById(levelId)
                .orElseThrow(() -> new ServiceException("等级不存在"));
        user.setMemberLevel(level);
        userRepository.save(user);
        
        // 触发等级变更通知
        Map<String, Object> notice = new HashMap<>();
        notice.put("title", "会员等级变更");
        notice.put("content", "恭喜您升级为 " + level.getName() + "！");
        notice.put("targetType", "USER");
        notice.put("targetValue", user.getId().toString());
        notice.put("triggerType", "BEHAVIOR_TRIGGER");
        notice.put("behavior", "LEVEL_UP");
        notificationService.sendActivityNotice(notice);
    }

    @Transactional
    public void adjustPoints(Long id, Integer points, String remark) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ServiceException("用户不存在"));
        user.setPoints(user.getPoints() + points);
        userRepository.save(user);

        PointTransaction transaction = new PointTransaction();
        transaction.setUser(user);
        transaction.setAmount(points);
        transaction.setBalanceAfter(user.getPoints());
        transaction.setType(points > 0 ? "EARN" : "SPEND");
        transaction.setRemark(remark);
        pointTransactionRepository.save(transaction);
    }

    public List<UserTag> getAllTags() {
        return userTagRepository.findAll();
    }

    @Transactional
    public UserTag createTag(UserTag tag) {
        return userTagRepository.save(tag);
    }

    @Transactional
    public UserTag updateTag(UserTag tag) {
        return userTagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(Long id) {
        userTagRepository.deleteById(id);
    }

    public Map<String, Object> getBehaviorAnalysis() {
        User user = userService.getCurrentUser();
        List<Order> orders = orderRepository.findByUserId(user.getId());
        
        Map<String, Object> analysis = new HashMap<>();
        if (orders.isEmpty()) {
            analysis.put("avgOrderValue", BigDecimal.ZERO);
            analysis.put("orderFrequency", "0 times/month");
            analysis.put("favoriteCategory", "无");
            return analysis;
        }

        BigDecimal totalAmount = orders.stream()
                .map(Order::getActualAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal avgOrderValue = totalAmount.divide(new BigDecimal(orders.size()), 2, BigDecimal.ROUND_HALF_UP);
        
        // 计算频率 (简单逻辑：总订单数 / 注册月数)
        long months = java.time.temporal.ChronoUnit.MONTHS.between(user.getCreatedAt(), LocalDateTime.now());
        if (months == 0) months = 1;
        double frequency = (double) orders.size() / months;

        // 最喜欢的分类
        String favoriteCategory = orders.stream()
                .flatMap(o -> o.getOrderItems().stream())
                .map(item -> item.getProduct().getCategory().getName())
                .collect(Collectors.groupingBy(name -> name, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("无");

        analysis.put("avgOrderValue", avgOrderValue);
        analysis.put("orderFrequency", String.format("%.1f times/month", frequency));
        analysis.put("favoriteCategory", favoriteCategory);
        return analysis;
    }

    public List<Map<String, Object>> getSegmentation() {
        // 增强的 RFM 模型分层
        // R (Recency): 最近一次消费时间
        // F (Frequency): 消费频率
        // M (Monetary): 消费金额
        
        long totalUsers = userRepository.count();
        if (totalUsers == 0) return Collections.emptyList();

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);

        // 1. 核心客户 (R < 30天, F > 5次, M > 500)
        long coreCount = orderRepository.countCoreUsers(thirtyDaysAgo, 5L, new BigDecimal("500"));
        
        // 2. 潜力客户 (R < 60天, M > 100)
        long potentialCount = orderRepository.countPotentialUsers(LocalDateTime.now().minusDays(60), new BigDecimal("100"));
        
        // 3. 需挽留客户 (R > 90天)
        long riskCount = orderRepository.countChurnedUsers(ninetyDaysAgo);
        
        // 4. 新客户 (注册 < 30天)
        long newCount = userRepository.countByCreatedAtAfter(thirtyDaysAgo);

        List<Map<String, Object>> result = new ArrayList<>();
        result.add(createSegmentMap("核心客户", coreCount, totalUsers));
        result.add(createSegmentMap("潜力客户", potentialCount, totalUsers));
        result.add(createSegmentMap("流失风险", riskCount, totalUsers));
        result.add(createSegmentMap("新晋客户", newCount, totalUsers));
        return result;
    }

    private Map<String, Object> createSegmentMap(String name, long count, long total) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("count", count);
        map.put("percentage", total > 0 ? (double) count / total * 100 : 0);
        return map;
    }

    @Transactional
    public void updateSegmentationRules(Map<String, Object> rules) {
        // 保存分层规则到系统配置
        rules.forEach((key, value) -> {
            SystemConfig config = systemConfigRepository.findById("segmentation_rule_" + key)
                    .orElse(new SystemConfig());
            config.setConfigKey("segmentation_rule_" + key);
            config.setConfigValue(value.toString());
            systemConfigRepository.save(config);
        });
    }

    public Map<String, Object> getRetentionAnalysis() {
        long totalUsers = userRepository.count();
        if (totalUsers == 0) {
            return Map.of("retentionRate", 0.0, "churnRate", 0.0);
        }

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);

        // 使用数据库聚合查询代替内存循环
        long activeUsers = orderRepository.countActiveUsers(thirtyDaysAgo);
        long churnedUsers = orderRepository.countChurnedUsers(ninetyDaysAgo);

        Map<String, Object> analysis = new HashMap<>();
        analysis.put("retentionRate", (double) activeUsers / totalUsers);
        analysis.put("churnRate", (double) churnedUsers / totalUsers);
        return analysis;
    }

    // --- App 接口方法 ---

    public List<Product> getExclusiveProducts() {
        return productRepository.findByIsMemberExclusive(true);
    }

    public MemberExclusivePriceDTO getExclusivePrices() {
        User user = userService.getCurrentUser();
        MemberExclusivePriceDTO dto = new MemberExclusivePriceDTO();
        if (user.getMemberLevel() != null) {
            dto.setLevel(user.getMemberLevel().getName());
            dto.setDiscountRate(user.getMemberLevel().getDiscountRate());
        } else {
            dto.setLevel("普通会员");
            dto.setDiscountRate(BigDecimal.ONE);
        }

        List<MemberExclusivePriceDTO.ProductPriceDTO> products = new ArrayList<>();
        productRepository.findByIsMemberExclusive(true).forEach(p -> {
            MemberExclusivePriceDTO.ProductPriceDTO pPrice = new MemberExclusivePriceDTO.ProductPriceDTO();
            pPrice.setProductId(p.getId());
            pPrice.setProductName(p.getName());
            pPrice.setOriginalPrice(p.getPrice());
            pPrice.setMemberPrice(p.getMemberPrice() != null ? p.getMemberPrice() : p.getPrice().multiply(dto.getDiscountRate()));
            pPrice.setDiscount(dto.getDiscountRate());
            products.add(pPrice);
        });
        dto.setProducts(products);
        return dto;
    }

    public Map<String, Object> getBirthdayPrivilege() {
        User user = userService.getCurrentUser();
        boolean isBirthday = false;
        if (user.getBirthday() != null) {
            LocalDateTime now = LocalDateTime.now();
            isBirthday = user.getBirthday().getMonth() == now.getMonth() &&
                         user.getBirthday().getDayOfMonth() == now.getDayOfMonth();
        }

        Map<String, Object> privilege = new HashMap<>();
        privilege.put("name", "生日特权");
        privilege.put("description", systemConfigRepository.findById("birthday_privilege_desc")
                .map(SystemConfig::getConfigValue).orElse("生日当天可领取双倍积分及专享优惠券"));
        privilege.put("status", isBirthday ? "AVAILABLE" : "NOT_BIRTHDAY");
        return privilege;
    }

    @Transactional
    public void receiveBirthdayPrivilege() {
        User user = userService.getCurrentUser();
        if (user.getBirthday() == null) {
            throw new ServiceException("请先完善生日信息");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean isBirthday = user.getBirthday().getMonth() == now.getMonth() &&
                             user.getBirthday().getDayOfMonth() == now.getDayOfMonth();
        
        if (!isBirthday) {
            throw new ServiceException("今天不是您的生日");
        }

        // 检查是否已领取 (简单逻辑：检查今天是否有生日奖励的积分记录)
        boolean alreadyReceived = pointTransactionRepository.findByUserId(user.getId(), PageRequest.of(0, 100))
                .getContent().stream()
                .anyMatch(t -> "BIRTHDAY_REWARD".equals(t.getType()) &&
                               t.getCreatedAt().toLocalDate().equals(now.toLocalDate()));
        
        if (alreadyReceived) {
            throw new ServiceException("您今天已经领取过生日特权了");
        }

        // 发放奖励：100积分
        adjustPoints(user.getId(), 100, "生日特权奖励");
        
        // 记录为生日奖励类型
        PointTransaction lastTransaction = pointTransactionRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId());
        if (lastTransaction != null) {
            lastTransaction.setType("BIRTHDAY_REWARD");
            pointTransactionRepository.save(lastTransaction);
        }
    }

    private MemberDTO convertToDTO(User user) {
        MemberDTO dto = new MemberDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setPhone(user.getPhone());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBirthday(user.getBirthday());
        if (user.getMemberLevel() != null) {
            dto.setMemberLevelId(user.getMemberLevel().getId());
            dto.setMemberLevelName(user.getMemberLevel().getName());
        }
        dto.setGrowthValue(user.getGrowthValue());
        dto.setPoints(user.getPoints());
        dto.setBalance(user.getBalance());
        dto.setMemberCardNo(user.getMemberCardNo());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}