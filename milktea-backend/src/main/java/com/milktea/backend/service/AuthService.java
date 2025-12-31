package com.milktea.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.SystemConfigRepository;
import com.milktea.backend.repository.UserRepository;
import com.milktea.backend.repository.VerificationCodeRepository;
import com.milktea.milktea_backend.model.entity.User;
import com.milktea.milktea_backend.model.entity.VerificationCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final SystemConfigRepository systemConfigRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthService(UserRepository userRepository,
                       VerificationCodeRepository verificationCodeRepository,
                       PasswordEncoder passwordEncoder,
                       SystemConfigRepository systemConfigRepository) {
        this.userRepository = userRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.passwordEncoder = passwordEncoder;
        this.systemConfigRepository = systemConfigRepository;
    }

    /**
     * 微信授权登录
     */
    @Transactional
    public User wxLogin(String code) {
        // 1. 调用微信API换取openid
        // 从系统配置获取微信配置
        String wechatConfigJson = systemConfigRepository.findById("wechat_config")
                .map(com.milktea.milktea_backend.model.entity.SystemConfig::getConfigValue)
                .orElseThrow(() -> new ServiceException("WECHAT_CONFIG_NOT_FOUND", "微信配置未找到"));
        
        String openid;
        try {
            JsonNode configNode = objectMapper.readTree(wechatConfigJson);
            String appId = configNode.get("appId").asText();
            String secret = configNode.get("secret").asText();
            
            String url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    appId, secret, code);
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode responseNode = objectMapper.readTree(response);
            
            if (responseNode.has("errcode") && responseNode.get("errcode").asInt() != 0) {
                throw new ServiceException("WECHAT_LOGIN_ERROR", responseNode.get("errmsg").asText());
            }
            
            openid = responseNode.get("openid").asText();
        } catch (Exception e) {
            if (e instanceof ServiceException) throw (ServiceException) e;
            throw new ServiceException("WECHAT_API_ERROR", "调用微信接口失败: " + e.getMessage());
        }
        
        // 2. 查询用户，不存在则创建
        return userRepository.findByWechatOpenid(openid).orElseGet(() -> {
            User newUser = new User();
            newUser.setWechatOpenid(openid);
            newUser.setUsername("wx_" + openid.substring(0, Math.min(openid.length(), 12)));
            newUser.setPassword(passwordEncoder.encode("123456")); // 默认密码
            newUser.setRegistrationTime(LocalDateTime.now());
            newUser.setStatus("ACTIVE");
            return userRepository.save(newUser);
        });
    }

    /**
     * 发送短信验证码
     */
    @Transactional
    public void sendSms(String phone) {
        String code = String.format("%06d", new Random().nextInt(1000000));
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setPhone(phone);
        verificationCode.setCode(code);
        verificationCode.setExpireTime(LocalDateTime.now().plusMinutes(5));
        verificationCode.setUsed(false);
        verificationCodeRepository.save(verificationCode);
        
        // 从系统配置获取短信网关配置
        String smsConfig = systemConfigRepository.findById("sms_config")
                .map(com.milktea.milktea_backend.model.entity.SystemConfig::getConfigValue)
                .orElse("default_sms_gateway");
        
        // 实际场景下应调用短信服务商接口发送
        // 这里保留打印逻辑作为发送成功的记录
        System.out.println("Sending SMS via " + smsConfig + " to " + phone + ": " + code);
    }

    /**
     * 验证验证码
     */
    @Transactional
    public boolean verifyCode(String phone, String code) {
        return verificationCodeRepository.findByPhoneAndCodeAndUsedFalse(phone, code)
                .map(vc -> {
                    if (vc.getExpireTime().isAfter(LocalDateTime.now())) {
                        vc.setUsed(true);
                        verificationCodeRepository.save(vc);
                        return true;
                    }
                    return false;
                }).orElse(false);
    }
}