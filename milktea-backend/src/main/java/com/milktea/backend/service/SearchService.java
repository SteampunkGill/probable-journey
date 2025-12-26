package com.milktea.backend.service;

import com.milktea.backend.repository.ProductRepository;
import com.milktea.backend.repository.UserSearchHistoryRepository;
import com.milktea.milktea_backend.model.entity.Product;
import com.milktea.milktea_backend.model.entity.UserSearchHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductRepository productRepository;
    private final UserSearchHistoryRepository searchHistoryRepository;

    public List<Product> searchProducts(Long userId, String keyword) {
        if (userId != null && keyword != null && !keyword.trim().isEmpty()) {
            saveSearchHistory(userId, keyword);
        }
        return productRepository.searchProducts(keyword);
    }

    @Transactional
    public void saveSearchHistory(Long userId, String keyword) {
        UserSearchHistory history = new UserSearchHistory();
        // 简化处理，实际应关联User实体
        // history.setUser(userRepository.getReferenceById(userId));
        history.setKeyword(keyword);
        searchHistoryRepository.save(history);
    }

    public List<String> getSearchHistory(Long userId) {
        return searchHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(UserSearchHistory::getKeyword)
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    @Transactional
    public void clearHistory(Long userId) {
        searchHistoryRepository.deleteByUserId(userId);
    }

    public List<String> getHotKeywords() {
        return searchHistoryRepository.findHotKeywords(org.springframework.data.domain.PageRequest.of(0, 10));
    }
}