package com.milktea.backend.dto;

import lombok.Data;

@Data
public class MemberQueryDTO {
    private String keyword; // 用户名、昵称、手机号
    private Long memberLevelId;
    private String status;
    private Integer page = 1;
    private Integer size = 10;
}