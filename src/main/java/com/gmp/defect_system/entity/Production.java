package com.gmp.defect_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "production_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Production {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "production_date", nullable = false)
    private LocalDate productionDate;

    @Column(nullable = false, length = 50)
    private String shift; // Contoh: "Shift 1", "Shift 2"

    @Column(name = "production_line", length = 100)
    private String line; // Contoh: "Stamping"

    @Column(name = "machine_name", length = 100)
    private String machineName; // Contoh: "Amada 1", "Mesin 55"

    @Column(name = "process_name", length = 100)
    private String processName; // Contoh: "Blank", "Bending"

    @ManyToOne
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(name = "target_quantity", nullable = false)
    private Integer targetQuantity;

    @Column(name = "produced_quantity", nullable = false)
    private Integer producedQuantity;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}