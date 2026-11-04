package com.gmp.defect_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_complaints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerComplaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complaint_date", nullable = false)
    private LocalDate complaintDate;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @ManyToOne
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @ManyToOne
    @JoinColumn(name = "defect_id", nullable = false)
    private DefectType defectType;

    @Column(name = "lot_number", length = 50)
    private String lotNumber;

    @Column(name = "defect_quantity", nullable = false)
    private Integer defectQuantity;

    @Column(name = "problem_description", columnDefinition = "TEXT")
    private String problemDescription;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(length = 50)
    private String status = "Open";

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}