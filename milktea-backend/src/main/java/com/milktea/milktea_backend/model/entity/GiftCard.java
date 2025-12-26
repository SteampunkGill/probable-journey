package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gift_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_no", length = 32, unique = true, nullable = false)
    private String cardNo;

    @Column(name = "card_code", length = 128, nullable = false)
    private String cardCode; // 加密后的券码

    @Column(name = "face_value", precision = 10, scale = 2, nullable = false)
    private java.math.BigDecimal faceValue;

    @Column(name = "balance", precision = 10, scale = 2, nullable = false)
    private java.math.BigDecimal balance;

    @Column(name = "status", length = 20, nullable = false)
    private String status; // UNUSED, ACTIVE, EXPIRED, USED

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}