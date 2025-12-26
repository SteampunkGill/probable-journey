package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.UserSearchHistory;

import java.util.List;

@Repository
public interface UserSearchHistoryRepository extends JpaRepository<UserSearchHistory, Long> {
    List<UserSearchHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
    void deleteByUserId(Long userId);

    @Query("SELECT h.keyword FROM UserSearchHistory h GROUP BY h.keyword ORDER BY COUNT(h) DESC")
    List<String> findHotKeywords(org.springframework.data.domain.Pageable pageable);
}
