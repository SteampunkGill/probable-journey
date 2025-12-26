package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.GiftCard;
import com.milktea.milktea_backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GiftCardRepository extends JpaRepository<GiftCard, Long> {
    Optional<GiftCard> findByCardNo(String cardNo);
    Optional<GiftCard> findByCardNoAndCardCode(String cardNo, String cardCode);
    List<GiftCard> findByUserOrderByCreatedAtDesc(User user);
}