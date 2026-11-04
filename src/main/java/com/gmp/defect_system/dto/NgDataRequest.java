package com.gmp.defect_system.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class NgDataRequest {
    private Long partId;
    private Long defectTypeId;
    private String proses;
    private Integer quantity;
    private String operatorName;
    private String username;
    private String remarks;
    private String photoUrl;
    private LocalDate defectDate;
    private String shift;
    private String namaMesin;
}