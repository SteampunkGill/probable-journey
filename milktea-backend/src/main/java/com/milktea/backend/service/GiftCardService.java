package com.milktea.backend.service;

import com.milktea.backend.dto.GiftCardDTO;
import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.GiftCardRepository;
import com.milktea.backend.repository.UserRepository;
import com.milktea.milktea_backend.model.entity.GiftCard;
import com.milktea.milktea_backend.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GiftCardService {

    private final GiftCardRepository giftCardRepository;
    private final UserRepository userRepository;

    @Transactional
    public GiftCardDTO buyGiftCard(Long userId, BigDecimal faceValue) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("USER_NOT_FOUND", "用户不存在"));

        String cardNo = "GC" + System.currentTimeMillis() + (int)(Math.random() * 1000);
        String cardCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        GiftCard giftCard = GiftCard.builder()
                .cardNo(cardNo)
                .cardCode(cardCode) // 实际项目中建议加密存储
                .faceValue(faceValue)
                .balance(faceValue)
                .status("UNUSED")
                .expiryTime(LocalDateTime.now().plusYears(1))
                .build();

        giftCard = giftCardRepository.save(giftCard);
        return convertToDTO(giftCard);
    }

    @Transactional
    public GiftCardDTO activateGiftCard(Long userId, String cardNo, String cardCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("USER_NOT_FOUND", "用户不存在"));

        GiftCard giftCard = giftCardRepository.findByCardNoAndCardCode(cardNo, cardCode)
                .orElseThrow(() -> new ServiceException("INVALID_CARD", "卡号或券码错误"));

        if (!"UNUSED".equals(giftCard.getStatus())) {
            throw new ServiceException("CARD_ALREADY_USED", "该礼品卡已被激活或已失效");
        }

        if (giftCard.getExpiryTime().isBefore(LocalDateTime.now())) {
            giftCard.setStatus("EXPIRED");
            giftCardRepository.save(giftCard);
            throw new ServiceException("CARD_EXPIRED", "该礼品卡已过期");
        }

        giftCard.setUser(user);
        giftCard.setStatus("ACTIVE");
        giftCard = giftCardRepository.save(giftCard);

        return convertToDTO(giftCard);
    }

    public List<GiftCardDTO> getUserGiftCards(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("USER_NOT_FOUND", "用户不存在"));
        
        return giftCardRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void consumeGiftCard(Long giftCardId, BigDecimal amount) {
        GiftCard giftCard = giftCardRepository.findById(giftCardId)
                .orElseThrow(() -> new ServiceException("CARD_NOT_FOUND", "礼品卡不存在"));

        if (!"ACTIVE".equals(giftCard.getStatus())) {
            throw new ServiceException("INVALID_STATUS", "礼品卡状态异常");
        }

        if (giftCard.getBalance().compareTo(amount) < 0) {
            throw new ServiceException("INSUFFICIENT_BALANCE", "礼品卡余额不足");
        }

        giftCard.setBalance(giftCard.getBalance().subtract(amount));
        if (giftCard.getBalance().compareTo(BigDecimal.ZERO) == 0) {
            giftCard.setStatus("USED");
        }
        giftCardRepository.save(giftCard);
    }

    private GiftCardDTO convertToDTO(GiftCard giftCard) {
        GiftCardDTO dto = new GiftCardDTO();
        dto.setId(giftCard.getId());
        dto.setCardNo(giftCard.getCardNo());
        dto.setFaceValue(giftCard.getFaceValue());
        dto.setBalance(giftCard.getBalance());
        dto.setStatus(giftCard.getStatus());
        dto.setExpiryTime(giftCard.getExpiryTime());
        dto.setCreatedAt(giftCard.getCreatedAt());
        return dto;
    }
}