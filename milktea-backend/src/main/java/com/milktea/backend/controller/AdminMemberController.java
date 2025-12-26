package com.milktea.backend.controller;

import com.milktea.backend.dto.*;
import com.milktea.backend.service.MemberService;
import com.milktea.milktea_backend.model.entity.UserTag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/members")
public class AdminMemberController {

    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ApiResponse<Page<MemberDTO>> getMembers(MemberQueryDTO query) {
        return ApiResponse.success(memberService.queryMembers(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<MemberDTO> getMemberDetail(@PathVariable Long id) {
        return ApiResponse.success(memberService.getMemberDetail(id));
    }

    @PutMapping("/{id}/level")
    public ApiResponse<Void> updateMemberLevel(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        memberService.updateMemberLevel(id, body.get("levelId"));
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/points")
    public ApiResponse<Void> adjustPoints(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer points = (Integer) body.get("points");
        String remark = (String) body.get("remark");
        memberService.adjustPoints(id, points, remark);
        return ApiResponse.success(null);
    }

    // 标签管理
    @GetMapping("/tags")
    public ApiResponse<List<UserTag>> getTags() {
        return ApiResponse.success(memberService.getAllTags());
    }

    @PostMapping("/tags")
    public ApiResponse<UserTag> createTag(@RequestBody UserTag tag) {
        return ApiResponse.success(memberService.createTag(tag));
    }

    @PutMapping("/tags/{id}")
    public ApiResponse<UserTag> updateTag(@PathVariable Long id, @RequestBody UserTag tag) {
        tag.setId(id);
        return ApiResponse.success(memberService.updateTag(tag));
    }

    @DeleteMapping("/tags/{id}")
    public ApiResponse<Void> deleteTag(@PathVariable Long id) {
        memberService.deleteTag(id);
        return ApiResponse.success(null);
    }

    // 会员分析
    @GetMapping("/analysis/behavior")
    public ApiResponse<Map<String, Object>> getBehaviorAnalysis() {
        return ApiResponse.success(memberService.getBehaviorAnalysis());
    }

    @GetMapping("/segmentation")
    public ApiResponse<List<Map<String, Object>>> getSegmentation() {
        return ApiResponse.success(memberService.getSegmentation());
    }

    @PutMapping("/segmentation-rules")
    public ApiResponse<Void> updateSegmentationRules(@RequestBody Map<String, Object> rules) {
        memberService.updateSegmentationRules(rules);
        return ApiResponse.success(null);
    }

    @GetMapping("/analysis/retention")
    public ApiResponse<Map<String, Object>> getRetentionAnalysis() {
        return ApiResponse.success(memberService.getRetentionAnalysis());
    }
}