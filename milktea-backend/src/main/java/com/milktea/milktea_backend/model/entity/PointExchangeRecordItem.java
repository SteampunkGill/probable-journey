package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "point_exchange_record_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PointExchangeRecordItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private PointExchangeRecord record;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private PointExchangeItem item;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "points_per_item", nullable = false)
    private Integer pointsPerItem;

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;
}