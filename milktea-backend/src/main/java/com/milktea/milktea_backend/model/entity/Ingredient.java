package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Ingredient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    @Column(name = "unit", length = 20)
    private String unit;
    
    @Column(name = "stock", precision = 10, scale = 2)
    private java.math.BigDecimal stock;
    
    @Column(name = "alert_threshold", precision = 10, scale = 2)
    private java.math.BigDecimal alertThreshold;

    @Column(name = "cost_per_unit", precision = 10, scale = 2)
    private java.math.BigDecimal costPerUnit;
}