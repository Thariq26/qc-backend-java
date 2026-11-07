package com.gmp.defect_system.service;

import com.gmp.defect_system.dto.DailyProductionDto;
import com.gmp.defect_system.repository.DailyProductionProjection;
import com.gmp.defect_system.dto.ParetoDto;
import com.gmp.defect_system.dto.KpiDto;
import com.gmp.defect_system.repository.ProductionRepository;
import com.gmp.defect_system.repository.NgDataRepository;
import com.gmp.defect_system.repository.CustomerComplaintRepository;

import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;

@Service
public class DashboardService {
    private final NgDataRepository ngDataRepository;
    private final ProductionRepository productionRepository;
    private final CustomerComplaintRepository complaintRepository;

    public DashboardService(NgDataRepository ngDataRepository, ProductionRepository productionRepository, CustomerComplaintRepository complaintRepository) {
        this.ngDataRepository = ngDataRepository;
        this.productionRepository = productionRepository;
        this.complaintRepository = complaintRepository;
    }
    public List<ParetoDto> getParetoChartData(LocalDate start, LocalDate end) {
        return getParetoChartData(start, end, null);
    }

    public List<ParetoDto> getParetoChartData(LocalDate start, LocalDate end, String username) {
        if (username != null && !username.trim().isEmpty() && !username.equalsIgnoreCase("admin")) {
            return ngDataRepository.getParetoChartDataByUser(start, end, username.trim());
        }
        return ngDataRepository.getParetoChartData(start, end);
    }

    public KpiDto getKpiSummary(LocalDate start, LocalDate end) {
        return getKpiSummary(start, end, null);
    }

    public KpiDto getKpiSummary(LocalDate start, LocalDate end, String username) {
        if (username != null && !username.trim().isEmpty() && !username.equalsIgnoreCase("admin")) {
            Long userNg = ngDataRepository.getTotalNgCountByDateAndUser(start, end, username.trim());
            long totalNg = (userNg != null) ? userNg : 0L;

            Long userProd = productionRepository.getTotalProducedQuantityByDateAndUser(start, end, username.trim());
            long totalProduction = (userProd != null) ? userProd : 0L;

            double defectRate = 0.0;
            if (totalProduction > 0) {
                defectRate = Math.round(((double) totalNg / totalProduction * 100) * 100.0) / 100.0;
            }
            return new KpiDto(totalProduction, totalNg, defectRate);
        }

        List<Object[]> paretoData = ngDataRepository.getTotalDefectsByType(start, end);
        long totalNg = 0;
        for (Object[] row : paretoData) {
            totalNg += (Long) row[1];
        }

        Long prodSum = productionRepository.getTotalProducedQuantityByDate(start, end);
        long totalProduction = (prodSum != null) ? prodSum : 0L;

        double defectRate = 0.0;
        if (totalProduction > 0) {
            defectRate = Math.round(((double) totalNg / totalProduction * 100) * 100.0) / 100.0;
        }

        return new KpiDto(totalProduction, totalNg, defectRate);
    }

    public List<DailyProductionDto> getDailyProductionData(LocalDate start, LocalDate end) {
        return getDailyProductionData(start, end, null);
    }

    public List<DailyProductionDto> getDailyProductionData(LocalDate start, LocalDate end, String username) {
        List<DailyProductionProjection> aggregations;
        if (username != null && !username.trim().isEmpty() && !username.equalsIgnoreCase("admin")) {
            aggregations = productionRepository.getDailyProductionAggregationByUser(start, end, username.trim());
        } else {
            aggregations = productionRepository.getDailyProductionAggregation(start, end);
        }

        List<DailyProductionDto> result = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH);
        for (DailyProductionProjection agg : aggregations) {
            DailyProductionDto dto = new DailyProductionDto();
            dto.setDate(agg.getProductionDate().format(dateFormatter));
            dto.setDay(agg.getProductionDate().format(dayFormatter));
            String shiftData = agg.getShift();
            dto.setShift(shiftData != null && !shiftData.isEmpty() ? shiftData : "-");
            Long target = agg.getTargetQuantity() != null ? agg.getTargetQuantity() : 0L;
            Long produced = agg.getProducedQuantity() != null ? agg.getProducedQuantity() : 0L;
            dto.setTarget(target);
            dto.setProduced(produced);
            dto.setVariance(produced - target);
            int efficiency = 0;
            if (target > 0) {
                efficiency = (int) Math.round((produced.doubleValue() / target.doubleValue()) * 100);
            }
            dto.setEfficiency(efficiency);
            if (efficiency >= 100) {
                dto.setStatus("On Target");
            } else {
                dto.setStatus("Below Target");
            }
            result.add(dto);
        }
        return result;
    }

    public long getComplaintCount(LocalDate start, LocalDate end) {
        return getComplaintCount(start, end, null);
    }

    public long getComplaintCount(LocalDate start, LocalDate end, String username) {
        if (username != null && !username.trim().isEmpty() && !username.equalsIgnoreCase("admin")) {
            return complaintRepository.getComplaintCountByDateAndUser(start, end, username.trim());
        }
        return complaintRepository.getComplaintCountByDate(start, end);
    }

    public List<Map<String, Object>> getComplaintParetoData(LocalDate start, LocalDate end) {
        return complaintRepository.getComplaintParetoByDate(start, end);
    }
}