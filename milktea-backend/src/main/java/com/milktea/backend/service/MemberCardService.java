package com.milktea.backend.service;

import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.UserRepository;
import com.milktea.milktea_backend.model.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class MemberCardService {

    private final UserRepository userRepository;

    public MemberCardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 绑定会员卡
     */
    @Transactional
    public void bindCard(Long userId, String cardNo) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setMemberCardNo(cardNo);
            userRepository.save(user);
        });
    }

    /**
     * 查询会员卡余额
     */
    public BigDecimal getCardBalance(Long userId) {
        return userRepository.findById(userId)
                .map(User::getBalance)
                .orElse(BigDecimal.ZERO);
    }
}