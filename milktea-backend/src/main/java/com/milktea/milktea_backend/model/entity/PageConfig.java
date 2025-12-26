package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "page_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PageConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "page", length = 50, nullable = false)
    private String page;

    @Column(name = "module", length = 50, nullable = false)
    private String module;

    @Column(name = "config_json", columnDefinition = "JSON", nullable = false)
    private String configJson;

    @Column(name = "sort_order")
    private Integer sortOrder;
}