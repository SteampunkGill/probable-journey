package com.milktea.backend.dto;

import lombok.Data;

@Data
public class InviteRulesDTO {
    private RewardInfo inviteeReward;
    private RewardInfo inviterReward;
    private String condition;
    private String rules;

    @Data
    public static class RewardInfo {
        private Integer points;
        private CouponInfo coupon;
    }

    @Data
    public static class CouponInfo {
        private Long id;
        private String name;
    }
}