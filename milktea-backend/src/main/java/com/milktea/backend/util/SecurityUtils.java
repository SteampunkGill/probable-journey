package com.milktea.backend.util;

import org.springframework.web.util.HtmlUtils;
import java.util.Arrays;
import java.util.List;

public class SecurityUtils {

    // 简单敏感词列表，实际项目中应从数据库或配置文件加载
    private static final List<String> SENSITIVE_WORDS = Arrays.asList("暴力", "色情", "反动", "广告");

    /**
     * 防范 XSS 攻击：转义 HTML 字符
     */
    public static String sanitize(String input) {
        if (input == null) return null;
        return HtmlUtils.htmlEscape(input.trim());
    }

    /**
     * 敏感词过滤
     */
    public static String filterSensitiveWords(String input) {
        if (input == null) return null;
        String filtered = input;
        for (String word : SENSITIVE_WORDS) {
            filtered = filtered.replace(word, "**");
        }
        return filtered;
    }

    /**
     * 综合清洗
     */
    public static String cleanText(String input) {
        return sanitize(filterSensitiveWords(input));
    }
}