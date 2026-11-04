package com.gmp.defect_system.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

    @Data
public class BatchProductionRequest {
    private LocalDate productionDate;
    private String shift;
    private String line;
    private String username;
    private String createdBy;
    private String operatorName;
    private List<ProductionItem> items;

    @Data
    public static class ProductionItem {
        private String machineName;
        private Long partId;
        private Integer producedQuantity;
        private Integer targetQuantity;
        private String processName;
    }
}