package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpecCombinationItemId implements Serializable {
    private Long combinationId;
    private Long specItemId;
}