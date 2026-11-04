package com.gmp.defect_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "machines")
@Data
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String line; // Stamping, Welding, Welding Robot
}

