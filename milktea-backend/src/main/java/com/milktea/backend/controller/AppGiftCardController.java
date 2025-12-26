package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.dto.GiftCardActivateRequest;
import com.milktea.backend.dto.GiftCardDTO;
import com.milktea.backend.service.GiftCardService;
import com.milktea.backend.service.UserService;
import com.milktea.milktea_backend.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/app/gift-card")
@RequiredArgsConstructor
public class AppGiftCardController {

    private final GiftCardService giftCardService;
    private final UserService userService;

    @PostMapping("/buy")
    public ApiResponse<GiftCardDTO> buyGiftCard(@RequestBody Map<String, BigDecimal> body) {
        User user = userService.getCurrentUser();
        return ApiResponse.success(giftCardService.buyGiftCard(user.getId(), body.get("faceValue")));
    }

    @PostMapping("/activate")
    public ApiResponse<GiftCardDTO> activateGiftCard(@RequestBody GiftCardActivateRequest request) {
        User user = userService.getCurrentUser();
        return ApiResponse.success(giftCardService.activateGiftCard(user.getId(), request.getCardNo(), request.getCardCode()));
    }

    @GetMapping("/list")
    public ApiResponse<List<GiftCardDTO>> getUserGiftCards() {
        User user = userService.getCurrentUser();
        return ApiResponse.success(giftCardService.getUserGiftCards(user.getId()));
    }
}