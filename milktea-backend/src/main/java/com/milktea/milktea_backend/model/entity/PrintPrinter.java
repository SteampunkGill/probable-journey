package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "print_printers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PrintPrinter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "sn", nullable = false, length = 64)
    private String sn;

    @Column(name = "printer_key", length = 64)
    private String key;

    @Column(name = "type", length = 20)
    private String type; // FEIE, YILIAN

    @Column(name = "status")
    private Integer status; // 1:正常 0:禁用
}