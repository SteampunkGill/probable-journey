package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.VerificationCode;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    /**
     * 根据手机号、验证码和是否使用查找验证码
     */
    java.util.Optional<VerificationCode> findByPhoneAndCodeAndUsedFalse(String phone, String code);
}
