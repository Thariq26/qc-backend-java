package com.gmp.defect_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "defect_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefectType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // contoh: Scratches, Dents

    @Column(name = "part_id")
    private String partId; // contoh: PT-1001

    @Column(name = "proses")
    private String proses; // 1) Blank 2) Bending 3) Welding Spot 4) Welding Manual 5) Welding Robot

    @Column(columnDefinition = "TEXT")
    private String description; // Deskripsi

    @Column(nullable = false, length = 20)
    private String category; // 1) Low 2) Medium 3) High 4) Critical

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}