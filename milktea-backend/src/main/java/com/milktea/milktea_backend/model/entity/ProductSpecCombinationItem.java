package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_spec_combination_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProductSpecCombinationItem {

    @EmbeddedId
    private ProductSpecCombinationItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("combinationId")
    @JoinColumn(name = "combination_id", nullable = false)
    private ProductSpecCombination combination;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("specItemId")
    @JoinColumn(name = "spec_item_id", nullable = false)
    private ProductSpecItem specItem;
}
