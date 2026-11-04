package com.gmp.defect_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ng_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NgData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(name = "customer_id", length = 100)
    private String customerId;

    @ManyToOne
    @JoinColumn(name = "defect_type_id", nullable = false)
    private DefectType defectType;

    @Column(nullable = false)
    private Integer quantity;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "defect_date", nullable = false)
    private LocalDate defectDate;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "operator_name", length = 100)
    private String operatorName;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "shift", length = 50)
    private String shift;

    @Column(name = "nama_mesin", length = 100)
    private String namaMesin;

    @Column(name = "proses", length = 100)
    private String proses;
}