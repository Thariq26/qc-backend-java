package com.gmp.defect_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KpiDto {
    private Long totalProduction;
    private Long totalNg;
    private Double defectRate;
}