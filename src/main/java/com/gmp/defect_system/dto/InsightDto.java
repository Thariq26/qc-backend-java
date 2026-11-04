package com.gmp.defect_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InsightDto {
    private String severity;
    private String title;
    private String message;
}