package com.gmp.defect_system.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ComplaintRequest {
    private LocalDate complaintDate;
    private String customerName;
    private Long partId;
    private Long defectTypeId;
    private String lotNumber;
    private Integer defectQuantity;
    private String problemDescription;
    private String photoUrl;
}