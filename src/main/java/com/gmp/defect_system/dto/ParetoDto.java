package com.gmp.defect_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParetoDto {
    private String defectName;
    private String partName;
    private Long totalQuantity;
}