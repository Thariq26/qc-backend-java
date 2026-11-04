package com.gmp.defect_system.dto;

import lombok.Data;

@Data
public class DailyProductionDto {
    private String date;
    private String day;
    private String shift;
    private Long target;
    private Long produced;
    private Long variance;
    private Integer efficiency;
    private String status;
}