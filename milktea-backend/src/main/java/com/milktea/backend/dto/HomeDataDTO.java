package com.milktea.backend.dto;

import com.milktea.milktea_backend.model.entity.Banner;
import com.milktea.milktea_backend.model.entity.Product;
import com.milktea.milktea_backend.model.entity.SysAnnouncement;
import lombok.Data;

import java.util.List;

@Data
public class HomeDataDTO {
    private List<Banner> banners;
    private List<Product> hotProducts;
    private List<Product> newProducts;
    private List<Product> recommendations;
    private List<SysAnnouncement> announcements;
}