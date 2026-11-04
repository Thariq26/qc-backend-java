package com.gmp.defect_system.repository;

import java.time.LocalDate;

public interface DailyProductionProjection {
    LocalDate getProductionDate();
    Long getTargetQuantity();
    Long getProducedQuantity();
    String getShift();
}