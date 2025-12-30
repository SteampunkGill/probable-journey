package com.milktea.milktea_backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "wechat_openid", length = 64, unique = true)
    private String wechatOpenid;
    
    @Column(name = "wechat_unionid", length = 64)
    private String wechatUnionid;
    
    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;
    
    @Column(name = "password", length = 255, nullable = false) // 密码哈希
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String password;
    
    @Column(name = "phone", length = 20, unique = true)
    private String phone;

    @Column(name = "email", length = 100, unique = true)
    private String email;
    
    @Column(name = "nickname", length = 64)
    private String nickname;
    
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "gender")
    private Integer gender; // 0:保密, 1:男, 2:女
    
    @Column(name = "birthday")
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_level_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MemberLevel memberLevel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User inviter;
    
    @Column(name = "growth_value")
    private Integer growthValue;
    
    @Column(name = "points")
    private Integer points;
    
    @Column(name = "balance", precision = 10, scale = 2)
    private java.math.BigDecimal balance;
    
    @Column(name = "member_card_no", length = 50)
    private String memberCardNo;

    @Column(name = "push_notification_enabled")
    private Boolean pushNotificationEnabled = true;

    @Column(name = "push_order_update")
    private Boolean pushOrderUpdate = true;

    @Column(name = "push_marketing")
    private Boolean pushMarketing = false;

    @Column(name = "invite_code", length = 20, unique = true)
    private String inviteCode;
    
    @Column(name = "status", length = 20, nullable = false) // 用户状态：ACTIVE, INACTIVE, LOCKED
    private String status = "ACTIVE";

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "registration_time", updatable = false, nullable = false)
    private LocalDateTime registrationTime;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 获取会员等级名称，用于前端显示
     * 增加 @Transient 确保不映射到数据库字段
     */
    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("levelName")
    public String getLevelName() {
        if (memberLevel != null) {
            try {
                return memberLevel.getName();
            } catch (Exception e) {
                return "普通会员";
            }
        }
        return "普通会员";
    }

    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if ("ADMIN".equalsIgnoreCase(this.status)) {
            return java.util.Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
            );
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isAccountNonLocked() {
        return !"LOCKED".equals(status);
    }

    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEnabled() {
        // 允许 ADMIN 状态的用户登录
        return Boolean.TRUE.equals(isActive) && ("ACTIVE".equals(status) || "ADMIN".equals(status));
    }
}