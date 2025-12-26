package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_related_map")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProductRelatedMap {

    @EmbeddedId
    private ProductRelatedMapId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("mainProductId")
    @JoinColumn(name = "main_product_id", nullable = false)
    private Product mainProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("relatedProductId")
    @JoinColumn(name = "related_product_id", nullable = false)
    private Product relatedProduct;

    @Column(name = "relation_type", length = 20)
    private String relationType; // SIMILAR, COMPLEMENTARY, BUNDLE, CROSS_SELL

    @Column(name = "sort_order")
    private Integer sortOrder;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
