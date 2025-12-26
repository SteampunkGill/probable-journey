package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "print_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PrintTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_default")
    private Boolean isDefault;
}