package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_allergens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProductAllergen {

    @EmbeddedId
    private ProductAllergenId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("allergenId")
    @JoinColumn(name = "allergen_id", nullable = false)
    private Allergen allergen;
}
