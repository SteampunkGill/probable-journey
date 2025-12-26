package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class DailyStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "new_users")
    private Integer newUsers;

    @Column(name = "total_orders")
    private Integer totalOrders;

    @Column(name = "total_sales", precision = 10, scale = 2)
    private java.math.BigDecimal totalSales;

    @Column(name = "avg_order_value", precision = 10, scale = 2)
    private java.math.BigDecimal avgOrderValue;

    @Column(name = "total_points_earned")
    private Integer totalPointsEarned;

    @Column(name = "total_points_spent")
    private Integer totalPointsSpent;
}