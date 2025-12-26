package com.milktea.backend.dto;

import lombok.Data;

@Data
public class PointProductDTO {
    private Long id;
    private String name;
    private String description;
    private Integer points;
    private Integer originalPoints;
    private Integer stock;
    private String limitInfo;
    private Boolean hot;
    private String image;
    private String category;
    
    // 向后兼容的字段
    private String imageUrl;
    private Integer pointCost;
    private String status;
    
    // 构造方法
    public PointProductDTO() {}
    
    public PointProductDTO(Long id, String name, String description, Integer points, 
                          Integer originalPoints, Integer stock, String limitInfo, 
                          Boolean hot, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.points = points;
        this.originalPoints = originalPoints;
        this.stock = stock;
        this.limitInfo = limitInfo;
        this.hot = hot;
        this.image = image;
        this.imageUrl = image;
        this.pointCost = points;
        this.status = "AVAILABLE";
    }
    
    // 向后兼容的getter/setter
    public String getImageUrl() {
        return imageUrl != null ? imageUrl : image;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.image = imageUrl;
    }
    
    public Integer getPointCost() {
        return pointCost != null ? pointCost : points;
    }
    
    public void setPointCost(Integer pointCost) {
        this.pointCost = pointCost;
        this.points = pointCost;
    }
}