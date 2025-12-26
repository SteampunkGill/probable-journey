package com.milktea.backend.service;

import com.milktea.backend.dto.*;
import com.milktea.backend.repository.*;
import com.milktea.milktea_backend.model.entity.Order;
import com.milktea.milktea_backend.model.entity.OrderItem;
import com.milktea.milktea_backend.model.entity.ProductSpecInventory;
import com.milktea.milktea_backend.model.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductSpecInventoryRepository inventoryRepository;
    private final NotificationRepository notificationRepository;

    public TodayMetricsDTO getTodayMetrics() {
        LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime startOfYesterday = startOfToday.minusDays(1);
        LocalDateTime endOfYesterday = endOfToday.minusDays(1).with(LocalTime.MAX);

        List<Order> todayOrders = orderRepository.findByOrderTimeBetween(startOfToday, endOfToday);
        List<Order> yesterdayOrders = orderRepository.findByOrderTimeBetween(startOfYesterday, endOfYesterday);

        int todayOrderCount = todayOrders.size();
        BigDecimal todaySales = todayOrders.stream()
                .map(Order::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal avgOrderValue = todayOrderCount > 0 ?
                todaySales.divide(new BigDecimal(todayOrderCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        // 客单价 = 销售额 / 下单人数
        long todayCustomers = todayOrders.stream().map(o -> o.getUser().getId()).distinct().count();
        BigDecimal customerUnitPrice = todayCustomers > 0 ?
                todaySales.divide(new BigDecimal(todayCustomers), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        // 从数据库统计新用户
        long newUsers = userRepository.countByRegistrationTimeBetween(startOfToday, endOfToday);
        
        // 转化率：今日下单人数 / 截止到今日的总用户数
        long totalUsersToToday = userRepository.countByRegistrationTimeBefore(endOfToday);
        double conversionRate = totalUsersToToday > 0 ? (double) todayCustomers / totalUsersToToday : 0.0;

        // 计算同比昨日
        int yesterdayOrderCount = yesterdayOrders.size();
        BigDecimal yesterdaySales = yesterdayOrders.stream()
                .map(Order::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long yesterdayCustomers = yesterdayOrders.stream().map(o -> o.getUser().getId()).distinct().count();
        BigDecimal yesterdayCustomerUnitPrice = yesterdayCustomers > 0 ?
                yesterdaySales.divide(new BigDecimal(yesterdayCustomers), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        Map<String, String> comparedToYesterday = new HashMap<>();
        comparedToYesterday.put("orderCount", calculateGrowth(todayOrderCount, yesterdayOrderCount));
        comparedToYesterday.put("salesAmount", calculateGrowth(todaySales, yesterdaySales));
        comparedToYesterday.put("customerUnitPrice", calculateGrowth(customerUnitPrice, yesterdayCustomerUnitPrice));

        return TodayMetricsDTO.builder()
                .orderCount(todayOrderCount)
                .salesAmount(todaySales)
                .avgOrderValue(avgOrderValue)
                .customerUnitPrice(customerUnitPrice)
                .newUsers((int) newUsers)
                .conversionRate(conversionRate)
                .comparedToYesterday(comparedToYesterday)
                .build();
    }

    private String calculateGrowth(long current, long previous) {
        if (previous == 0) return current > 0 ? "+100%" : "0%";
        double growth = (double) (current - previous) / previous * 100;
        return String.format("%s%.0f%%", growth >= 0 ? "+" : "", growth);
    }

    private String calculateGrowth(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) return current.compareTo(BigDecimal.ZERO) > 0 ? "+100%" : "0%";
        BigDecimal growth = current.subtract(previous).divide(previous, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        return String.format("%s%.0f%%", growth.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "", growth);
    }

    public RealtimeSalesDTO getRealtimeSales() {
        LocalDateTime now = LocalDateTime.now();
        List<String> timeLabels = new ArrayList<>();
        List<BigDecimal> salesData = new ArrayList<>();
        List<Integer> orderData = new ArrayList<>();

        for (int i = 7; i >= 0; i--) {
            LocalDateTime start = now.minusHours(i).withMinute(0).withSecond(0);
            LocalDateTime end = start.plusHours(1).minusSeconds(1);
            List<Order> orders = orderRepository.findByOrderTimeBetween(start, end);
            
            timeLabels.add(start.format(DateTimeFormatter.ofPattern("HH:00")));
            salesData.add(orders.stream().map(Order::getActualAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            orderData.add(orders.size());
        }

        return RealtimeSalesDTO.builder()
                .timeLabels(timeLabels)
                .salesData(salesData)
                .orderData(orderData)
                .build();
    }

    public SalesTrendDTO getSalesTrend() {
        List<String> dates = new ArrayList<>();
        List<BigDecimal> sales = new ArrayList<>();
        List<Integer> orders = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
            List<Order> dayOrders = orderRepository.findByOrderTimeBetween(start, end);

            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            sales.add(dayOrders.stream().map(Order::getActualAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            orders.add(dayOrders.size());
        }

        return SalesTrendDTO.builder()
                .dates(dates)
                .sales(sales)
                .orders(orders)
                .build();
    }

    public List<ProductRankingDTO> getProductRanking() {
        LocalDateTime startOfMonth = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        List<Order> orders = orderRepository.findByOrderTimeBetween(startOfMonth, LocalDateTime.now());
        
        Map<Long, ProductRankingDTO> rankingMap = new HashMap<>();
        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                Long productId = item.getProduct().getId();
                ProductRankingDTO dto = rankingMap.getOrDefault(productId, 
                    ProductRankingDTO.builder()
                        .productId(productId)
                        .productName(item.getProduct().getName())
                        .salesCount(0)
                        .salesAmount(BigDecimal.ZERO)
                        .build());
                dto.setSalesCount(dto.getSalesCount() + item.getQuantity());
                dto.setSalesAmount(dto.getSalesAmount().add(item.getPrice().multiply(new BigDecimal(item.getQuantity()))));
                rankingMap.put(productId, dto);
            }
        }

        return rankingMap.values().stream()
                .sorted(Comparator.comparing(ProductRankingDTO::getSalesCount).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<InventoryAlertDTO> getInventoryAlerts() {
        return inventoryRepository.findAll().stream()
                .filter(inv -> inv.getStock() <= (inv.getLowStockThreshold() != null ? inv.getLowStockThreshold() : 10))
                .map(inv -> InventoryAlertDTO.builder()
                        .productId(inv.getProduct().getId())
                        .productName(inv.getProduct().getName())
                        .specName(inv.getSpecItem().getName())
                        .currentStock(inv.getStock())
                        .threshold(inv.getLowStockThreshold() != null ? inv.getLowStockThreshold() : 10)
                        .status(inv.getStock() == 0 ? "OUT_OF_STOCK" : "LOW_STOCK")
                        .build())
                .collect(Collectors.toList());
    }

    public List<OrderAlertDTO> getOrderAlerts() {
        // 异常订单：超过30分钟未制作的已支付订单
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(30);
        List<Order> timeoutOrders = orderRepository.findByStatus("PAID").stream()
                .filter(o -> o.getPayTime() != null && o.getPayTime().isBefore(timeoutThreshold))
                .collect(Collectors.toList());

        return timeoutOrders.stream()
                .map(o -> OrderAlertDTO.builder()
                        .orderId(o.getId())
                        .orderNo(o.getOrderNo())
                        .type("TIMEOUT")
                        .message("订单制作超时，请尽快处理")
                        .alertTime(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getNotifications() {
        // 获取系统通知
        return notificationRepository.findAll().stream()
                .sorted(Comparator.comparing(Notification::getSentTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .map(n -> NotificationDTO.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .content(n.getContent())
                        .type(n.getTargetType())
                        .createdAt(n.getSentTime())
                        .isRead("READ".equals(n.getStatus()))
                        .build())
                .collect(Collectors.toList());
    }
}