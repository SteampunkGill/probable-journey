package com.milktea.backend.controller;

import com.milktea.backend.dto.*;
import com.milktea.backend.service.*;
import com.milktea.backend.util.JwtUtils;
import com.milktea.milktea_backend.model.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/app")
public class AppUserController {

    private final AuthService authService;
    private final MemberService memberService;
    private final UserService userService;
    private final AddressService addressService;
    private final ShareService shareService;
    private final SystemService systemService;
    private final PointsMallService pointsMallService;
    private final JwtUtils jwtUtils;

    public AppUserController(AuthService authService,
                             MemberService memberService,
                             UserService userService,
                             AddressService addressService,
                             ShareService shareService,
                             SystemService systemService,
                             PointsMallService pointsMallService,
                             JwtUtils jwtUtils) {
        this.authService = authService;
        this.memberService = memberService;
        this.userService = userService;
        this.addressService = addressService;
        this.shareService = shareService;
        this.systemService = systemService;
        this.pointsMallService = pointsMallService;
        this.jwtUtils = jwtUtils;
    }


    @PostMapping("/auth/wx-login")
    public ApiResponse<User> wxLogin(@RequestBody Map<String, String> body) {
        return ApiResponse.success(authService.wxLogin(body.get("code")));
    }

    @PostMapping("/auth/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequestDTO loginRequest) {
        return userService.login(loginRequest.getUsername(), loginRequest.getPassword())
                .map(user -> {
                    Map<String, Object> result = new java.util.HashMap<>();
                    
                    // 准备 JWT Claims，包含角色信息
                    Map<String, Object> claims = new java.util.HashMap<>();
                    boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getStatus());
                    // 注意：Spring Security 默认角色前缀是 ROLE_，这里存入 ADMIN，
                    // 后面在 JwtAuthenticationFilter 中需要确保 authorities 被正确加载
                    claims.put("role", isAdmin ? "ADMIN" : "USER");
                    
                    String token = jwtUtils.generateToken(user.getUsername(), claims);
                    result.put("token", token);
                    result.put("user", user);
                    result.put("isAdmin", isAdmin);
                    // 兼容前端直接取 user 字段或整个对象的情况
                    result.put("id", user.getId());
                    result.put("username", user.getUsername());
                    result.put("nickname", user.getNickname());
                    return ApiResponse.success(result);
                })
                .orElseThrow(() -> new com.milktea.backend.exception.ServiceException("LOGIN_FAILED", "用户名或密码错误"));
    }

    @GetMapping("/member/level")
    public ApiResponse<Object> getMemberLevel() {
        return ApiResponse.success(memberService.getMemberLevelInfo());
    }

    @GetMapping("/member/points")
    public ApiResponse<List<Object>> getPointsHistory(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(memberService.getPointsHistory(page, size));
    }

    @GetMapping("/member/points-rules")
    public ApiResponse<Map<String, Object>> getPointsRules() {
        return ApiResponse.success(memberService.getPointRules());
    }

    @GetMapping("/member/mall/items")
    public ApiResponse<List<PointProductDTO>> getAvailableProducts(@RequestParam(required = false) String category) {
        return ApiResponse.success(pointsMallService.getAvailableProducts(category));
    }

    @GetMapping("/member/mall/categories")
    public ApiResponse<List<Map<String, Object>>> getCategories() {
        return ApiResponse.success(pointsMallService.getCategories());
    }

    @PostMapping("/member/mall/exchange")
    public ApiResponse<Void> exchangeProduct(@RequestBody Map<String, Long> body) {
        pointsMallService.exchangeProduct(body.get("productId"));
        return ApiResponse.success(null);
    }

    @GetMapping("/member/mall/exchange-records")
    public ApiResponse<List<PointExchangeRecordDTO>> getExchangeRecords() {
        return ApiResponse.success(pointsMallService.getExchangeRecords());
    }

    @PostMapping("/member/mall/exchange-coupon")
    public ApiResponse<Void> exchangeCoupon(@RequestBody Map<String, Long> body) {
        pointsMallService.exchangeCoupon(body.get("templateId"));
        return ApiResponse.success(null);
    }

    @PostMapping("/member/mall/sign-in")
    public ApiResponse<Integer> signIn() {
        return ApiResponse.success(pointsMallService.signIn());
    }

    @GetMapping("/user/profile")
    public ApiResponse<User> getProfile() {
        return ApiResponse.success(userService.getCurrentUser());
    }

    @PutMapping("/user/profile")
    public ApiResponse<User> updateProfile(@RequestBody Map<String, Object> body) {
        User user = new User();
        if (body.get("nickname") != null) user.setNickname(body.get("nickname").toString());
        if (body.get("avatarUrl") != null) user.setAvatarUrl(body.get("avatarUrl").toString());
        if (body.get("avatar") != null) user.setAvatarUrl(body.get("avatar").toString());
        
        if (body.get("birthday") != null) {
            String birthdayStr = body.get("birthday").toString();
            if (!birthdayStr.isEmpty()) {
                try {
                    user.setBirthday(java.time.LocalDate.parse(birthdayStr));
                } catch (Exception e) {
                    // 忽略格式错误的日期
                }
            }
        }
        
        if (body.get("gender") != null) {
            try {
                user.setGender(Integer.parseInt(body.get("gender").toString()));
            } catch (Exception e) {
                // 忽略格式错误的性别
            }
        }
        
        return ApiResponse.success(userService.updateProfile(user));
    }

    @PostMapping("/user/bind-card")
    public ApiResponse<Void> bindCard(@RequestBody Map<String, String> body) {
        userService.bindMemberCard(body.get("cardNumber"));
        return ApiResponse.success(null);
    }

    @PostMapping("/user/unbind-card")
    public ApiResponse<Void> unbindCard() {
        userService.unbindMemberCard();
        return ApiResponse.success(null);
    }

    @PostMapping("/user/apply-card")
    public ApiResponse<Void> applyCard(@RequestBody Map<String, Object> body) {
        userService.applyMemberCard(body);
        return ApiResponse.success(null);
    }

    @GetMapping("/user/card-balance")
    public ApiResponse<Map<String, Object>> getCardBalance() {
        User user = userService.getCurrentUser();
        return ApiResponse.success(Map.of("balance", user.getBalance() != null ? user.getBalance() : 0.00));
    }


    @GetMapping("/address/list")
    public ApiResponse<List<com.milktea.milktea_backend.model.entity.UserAddress>> getAddressList() {
        return ApiResponse.success(addressService.listAddresses());
    }

    @PostMapping("/address/add")
    public ApiResponse<com.milktea.milktea_backend.model.entity.UserAddress> addAddress(@RequestBody com.milktea.milktea_backend.model.entity.UserAddress address) {
        return ApiResponse.success(addressService.addAddress(address));
    }

    @PutMapping("/address/update/{id}")
    public ApiResponse<com.milktea.milktea_backend.model.entity.UserAddress> updateAddress(@PathVariable Long id, @RequestBody com.milktea.milktea_backend.model.entity.UserAddress address) {
        return ApiResponse.success(addressService.updateAddress(id, address));
    }

    @DeleteMapping("/address/delete/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/address/geolocation")
    public ApiResponse<Object> getAddressByGeolocation(@RequestParam Double lat, @RequestParam Double lng) {
        return ApiResponse.success(addressService.getAddressByGeolocation(lat, lng));
    }

    @GetMapping("/address/history")
    public ApiResponse<List<AddressHistoryDTO>> getAddressHistory(@RequestParam(defaultValue = "10") int limit) {
        User user = userService.getCurrentUser();
        List<com.milktea.milktea_backend.model.entity.UserAddress> history = addressService.getAddressHistory(user.getId(), limit);
        List<AddressHistoryDTO> dtos = history.stream().map(addr -> {
            AddressHistoryDTO dto = new AddressHistoryDTO();
            dto.setAddress(addr.getProvince() + addr.getCity() + addr.getDistrict());
            dto.setDetail(addr.getDetail());
            dto.setUsedCount(addr.getUsedCount() != null ? addr.getUsedCount() : 0);
            dto.setTag(addr.getTag());
            return dto;
        }).collect(Collectors.toList());
        return ApiResponse.success(dtos);
    }


    @GetMapping("/settings/notification")
    public ApiResponse<Object> getNotificationSettings() {
        return ApiResponse.success(systemService.getNotificationSettings());
    }

    @PutMapping("/settings/notification")
    public ApiResponse<Void> updateNotificationSettings(@RequestBody Object settings) {
        systemService.updateNotificationSettings(settings);
        return ApiResponse.success(null);
    }

    @PostMapping("/settings/agreement")
    public ApiResponse<Void> confirmAgreement() {
        systemService.confirmAgreement();
        return ApiResponse.success(null);
    }

    @PostMapping("/auth/change-password")
    public ApiResponse<Void> changePassword(@RequestBody ChangePasswordRequestDTO request) {
        userService.changePassword(request.getOldPassword(), request.getNewPassword());
        return ApiResponse.success(null);
    }

    @PostMapping("/auth/register")
    public ApiResponse<User> register(@RequestBody RegisterRequestDTO request) {
        return ApiResponse.success(userService.register(request));
    }

    @PostMapping("/user/verify-deactivation")
    public ApiResponse<Boolean> verifyDeactivation() {
        return ApiResponse.success(userService.verifyDeactivation());
    }

    @PostMapping("/user/deactivate")
    public ApiResponse<Void> deactivate() {
        userService.deactivate();
        return ApiResponse.success(null);
    }

    @PostMapping("/share/generate")
    public ApiResponse<String> generateShareLink() {
        return ApiResponse.success(shareService.generateShareLink());
    }

    @GetMapping("/share/invitations")
    public ApiResponse<List<?>> getInvitations() {
        return ApiResponse.success(shareService.getInvitations());
    }

    @PostMapping("/share/receive-coupon")
    public ApiResponse<Void> receiveShareCoupon(@RequestBody Map<String, Long> body) {
        shareService.receiveShareCoupon(body.get("shareId"));
        return ApiResponse.success(null);
    }

    @GetMapping("/share/invite-rules")
    public ApiResponse<Map<String, Object>> getInviteRules() {
        return ApiResponse.success(shareService.getInviteRules());
    }

    @PostMapping("/share/reward-invite")
    public ApiResponse<Void> rewardInvite(@RequestBody Map<String, Long> body) {
        shareService.rewardInvite(body.get("inviteeId"));
        return ApiResponse.success(null);
    }
    @GetMapping("/share/stats")
    public ApiResponse<Map<String, Object>> getShareStats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("inviteCount", 0);
        stats.put("rewardPoints", 0);
        stats.put("rewardCoupons", 0);
        return ApiResponse.success(stats);
    }


    @GetMapping("/member/exclusive-products")
    public ApiResponse<List<com.milktea.milktea_backend.model.entity.Product>> getExclusiveProducts() {
        return ApiResponse.success(memberService.getExclusiveProducts());
    }

    @GetMapping("/member/exclusive-prices")
    public ApiResponse<MemberExclusivePriceDTO> getExclusivePrices() {
        return ApiResponse.success(memberService.getExclusivePrices());
    }

    @GetMapping("/member/birthday-privilege")
    public ApiResponse<Map<String, Object>> getBirthdayPrivilege() {
        return ApiResponse.success(memberService.getBirthdayPrivilege());
    }

    @PostMapping("/member/birthday-privilege/receive")
    public ApiResponse<Void> receiveBirthdayPrivilege() {
        memberService.receiveBirthdayPrivilege();
        return ApiResponse.success(null);
    }

}