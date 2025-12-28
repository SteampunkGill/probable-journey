package com.milktea.backend.service;

import com.milktea.backend.dto.HomeDataDTO;
import com.milktea.backend.dto.RecommendationFeedbackDTO;
import com.milktea.backend.repository.*;
import com.milktea.milktea_backend.model.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final BannerRepository bannerRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserRecommendationFeedbackRepository feedbackRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public HomeDataDTO getHomePageData() {
        HomeDataDTO dto = new HomeDataDTO();
        dto.setBanners(bannerRepository.findAll());
        dto.setHotProducts(productRepository.findHotProducts());
        dto.setNewProducts(productRepository.findNewProducts());
        dto.setAnnouncements(notificationService.getActiveAnnouncements());
        
        // 获取当前用户进行个性化推荐
        try {
            User currentUser = userRepository.findByUsername(org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName()).orElse(null);
            if (currentUser != null) {
                dto.setRecommendations(getRecommendations(currentUser.getId()));
            } else {
                dto.setRecommendations(productRepository.findByIsRecommended(true));
            }
        } catch (Exception e) {
            dto.setRecommendations(productRepository.findByIsRecommended(true));
        }
        
        return dto;
    }

    public List<Product> getRecommendations(Long userId) {
        // 根据用户历史购买行为进行推荐
        // 逻辑：获取用户买过的商品的分类，推荐这些分类下的其他热门商品
        List<Order> userOrders = orderRepository.findByUserId(userId);
        if (userOrders.isEmpty()) {
            return productRepository.findByIsRecommended(true);
        }
        
        List<Long> categoryIds = userOrders.stream()
                .flatMap(o -> o.getOrderItems().stream())
                .map(item -> item.getProduct().getCategory().getId())
                .distinct()
                .collect(Collectors.toList());
        
        if (categoryIds.isEmpty()) {
            return productRepository.findByIsRecommended(true);
        }
        
        return productRepository.findByCategoryIds(categoryIds).stream()
                .sorted(Comparator.comparing(Product::getSales).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    @Transactional
    public void handleFeedback(Long userId, RecommendationFeedbackDTO feedbackDTO) {
        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(feedbackDTO.getProductId()).orElse(null);
        
        if (user != null && product != null) {
            UserRecommendationFeedback feedback = new UserRecommendationFeedback();
            feedback.setUser(user);
            feedback.setProduct(product);
            feedback.setAction(feedbackDTO.getAction());
            feedbackRepository.save(feedback);
        }
    }
}