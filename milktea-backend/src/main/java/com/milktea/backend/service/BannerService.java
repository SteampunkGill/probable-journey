package com.milktea.backend.service;

import com.milktea.backend.repository.BannerRepository;
import com.milktea.backend.repository.PageConfigRepository;
import com.milktea.milktea_backend.model.entity.Banner;
import com.milktea.milktea_backend.model.entity.PageConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;
    private final PageConfigRepository pageConfigRepository;

    public List<Banner> findAll() {
        return bannerRepository.findAll();
    }

    @Transactional
    public Banner saveOrUpdate(Banner banner) {
        if (banner.getIsActive() == null) {
            banner.setIsActive(true);
        }
        if (banner.getClickCount() == null) {
            banner.setClickCount(0);
        }
        return bannerRepository.save(banner);
    }

    @Transactional
    public void recordClick(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("广告不存在"));
        banner.setClickCount(banner.getClickCount() + 1);
        bannerRepository.save(banner);
    }

    public List<Map<String, Object>> findAllPositions() {
        // 从数据库 page_configs 表中查询 BANNER 模块的配置
        return pageConfigRepository.findAll().stream()
                .filter(pc -> "BANNER".equals(pc.getModule()))
                .map(pc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", pc.getId());
                    map.put("code", pc.getPage() + "_BANNER");
                    map.put("name", pc.getPage() + "广告位");
                    map.put("description", pc.getConfigJson());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> updatePosition(Long id, Map<String, Object> positionData) {
        // 真实更新逻辑
        PageConfig config = pageConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("广告位配置不存在"));
        
        if (positionData.containsKey("configJson")) {
            config.setConfigJson((String) positionData.get("configJson"));
        }
        pageConfigRepository.save(config);

        Map<String, Object> result = new HashMap<>(positionData);
        result.put("id", id);
        result.put("updatedAt", java.time.LocalDateTime.now());
        return result;
    }

    @Transactional
    public void delete(Long id) {
        bannerRepository.deleteById(id);
    }
}