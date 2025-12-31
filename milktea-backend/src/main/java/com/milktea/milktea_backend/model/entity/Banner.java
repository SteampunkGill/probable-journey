package com.milktea.milktea_backend.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "link_type", length = 20)
    private String linkType;

    @Column(name = "link_value", length = 255)
    private String linkValue;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "position", length = 20)
    private String position; // HOME

    @Column(name = "start_time")
    private java.time.LocalDateTime startTime;

    @Column(name = "end_time")
    private java.time.LocalDateTime endTime;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "click_count")
    private Integer clickCount = 0;
}