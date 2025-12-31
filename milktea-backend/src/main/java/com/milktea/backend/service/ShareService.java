package com.milktea.backend.service;

import com.milktea.backend.repository.UserShareRepository;
import com.milktea.backend.repository.SystemConfigRepository;
import com.milktea.milktea_backend.model.entity.UserShare;
import com.milktea.milktea_backend.model.entity.SystemConfig;
import com.milktea.milktea_backend.model.entity.User;
import com.milktea.milktea_backend.model.entity.UserShareReward;
import com.milktea.milktea_backend.model.entity.UserInviteReward;
import com.milktea.milktea_backend.model.entity.PointTransaction;
import com.milktea.backend.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ShareService {

    private final UserShareRepository userShareRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final UserService userService;
    private final com.milktea.backend.repository.UserRepository userRepository;
    private final com.milktea.backend.repository.PointTransactionRepository pointTransactionRepository;
    private final CouponService couponService;
    private final com.milktea.backend.repository.UserShareRewardRepository userShareRewardRepository;
    private final com.milktea.backend.repository.UserInviteRewardRepository userInviteRewardRepository;

    public ShareService(UserShareRepository userShareRepository,
                        SystemConfigRepository systemConfigRepository,
                        UserService userService,
                        com.milktea.backend.repository.UserRepository userRepository,
                        com.milktea.backend.repository.PointTransactionRepository pointTransactionRepository,
                        CouponService couponService,
                        com.milktea.backend.repository.UserShareRewardRepository userShareRewardRepository,
                        com.milktea.backend.repository.UserInviteRewardRepository userInviteRewardRepository) {
        this.userShareRepository = userShareRepository;
        this.systemConfigRepository = systemConfigRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.pointTransactionRepository = pointTransactionRepository;
        this.couponService = couponService;
        this.userShareRewardRepository = userShareRewardRepository;
        this.userInviteRewardRepository = userInviteRewardRepository;
    }

    /**
     * 生成分享链接
     */
    public String generateShareLink() {
        Long userId = userService.getCurrentUser().getId();
        return "https://m.tea-milk.com/share?inviter=" + userId + "&type=APP";
    }

    /**
     * 生成分享链接
     */
    public String generateShareLink(Long userId, String type) {
        return "https://m.tea-milk.com/share?inviter=" + userId + "&type=" + type;
    }

    /**
     * 查询邀请记录
     */
    public List<UserShare> getInvitations() {
        Long userId = userService.getCurrentUser().getId();
        return getInvitationHistory(userId);
    }

    /**
     * 查询邀请记录
     */
    public List<UserShare> getInvitationHistory(Long userId) {
        return userShareRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    /**
     * 领取分享优惠券
     */
    @Transactional
    public void receiveShareCoupon(Long shareId) {
        User currentUser = userService.getCurrentUser();
        UserShare share = userShareRepository.findById(shareId)
                .orElseThrow(() -> new ServiceException("SHARE_NOT_FOUND", "分享记录不存在"));
        
        // 检查是否已经领取过该分享的奖励
        if (userShareRewardRepository.existsByUserIdAndShareId(currentUser.getId(), shareId)) {
            throw new ServiceException("ALREADY_RECEIVED", "您已经领取过该分享的奖励了");
        }
       
        try {
            couponService.receiveCoupon(3L);
        } catch (Exception e) {
            throw new ServiceException("COUPON_ERROR", "优惠券发放失败: " + e.getMessage());
        }

        // 记录奖励
        UserShareReward reward = new UserShareReward();
        reward.setUser(currentUser);
        reward.setShareId(shareId);
        reward.setRewardType("COUPON");
        reward.setRewardValue("分享专享8折券");
        userShareRewardRepository.save(reward);
    }

    /**
     * 发放邀请奖励
     */
    @Transactional
    public void rewardInvite(Long inviteeId) {
        User invitee = userRepository.findById(inviteeId)
                .orElseThrow(() -> new ServiceException("USER_NOT_FOUND", "用户不存在"));
        
        if (invitee.getInviter() == null) return;
        
        // 检查是否已经发放过奖励
        if (userInviteRewardRepository.existsByInviteeId(inviteeId)) {
            return;
        }
        
        User inviter = invitee.getInviter();
        
        // 1. 给邀请人发积分
        int inviterPoints = 50;
        inviter.setPoints((inviter.getPoints() == null ? 0 : inviter.getPoints()) + inviterPoints);
        userRepository.save(inviter);
        
        PointTransaction pt1 = new PointTransaction();
        pt1.setUser(inviter);
        pt1.setAmount(inviterPoints);
        pt1.setBalanceAfter(inviter.getPoints());
        pt1.setType("EARN");
        pt1.setRemark("邀请好友奖励: " + (invitee.getNickname() != null ? invitee.getNickname() : invitee.getUsername()));
        pointTransactionRepository.save(pt1);
        
        // 2. 给被邀请人发积分
        int inviteePoints = 100;
        invitee.setPoints((invitee.getPoints() == null ? 0 : invitee.getPoints()) + inviteePoints);
        userRepository.save(invitee);
        
        PointTransaction pt2 = new PointTransaction();
        pt2.setUser(invitee);
        pt2.setAmount(inviteePoints);
        pt2.setBalanceAfter(invitee.getPoints());
        pt2.setType("EARN");
        pt2.setRemark("受邀注册奖励");
        pointTransactionRepository.save(pt2);

        // 记录邀请奖励
        UserInviteReward reward = new UserInviteReward();
        reward.setInviter(inviter);
        reward.setInvitee(invitee);
        reward.setRewardStatus("ISSUED");
        userInviteRewardRepository.save(reward);
    }

    /**
     * 获取邀请奖励规则
     */
    public Map<String, Object> getInviteRules() {
        Map<String, Object> rules = new HashMap<>();
        
        // 从系统配置中获取规则
        String rulesDesc = systemConfigRepository.findById("invite_rules_description")
                .map(SystemConfig::getConfigValue).orElse("邀请好友注册并完成首单,双方各得奖励");
        
        Map<String, Object> inviteeReward = new HashMap<>();
        inviteeReward.put("points", 100);
        Map<String, Object> inviteeCoupon = new HashMap<>();
        inviteeCoupon.put("id", 1);
        inviteeCoupon.put("name", "新人专享5元券");
        inviteeReward.put("coupon", inviteeCoupon);

        Map<String, Object> inviterReward = new HashMap<>();
        inviterReward.put("points", 50);
        Map<String, Object> inviterCoupon = new HashMap<>();
        inviterCoupon.put("id", 2);
        inviterCoupon.put("name", "邀请专享8折券");
        inviterReward.put("coupon", inviterCoupon);

        rules.put("inviteeReward", inviteeReward);
        rules.put("inviterReward", inviterReward);
        rules.put("condition", "好友完成首单支付");
        rules.put("rules", rulesDesc);
        
        return rules;
    }
}