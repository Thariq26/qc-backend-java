package com.gmp.defect_system.controller;

import com.gmp.defect_system.dto.InsightDto;
import com.gmp.defect_system.dto.ParetoDto;
import com.gmp.defect_system.dto.KpiDto;
import com.gmp.defect_system.service.DashboardService;
import com.gmp.defect_system.service.InsightGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/insights")
@CrossOrigin(origins = "*")
public class InsightController {

    private final InsightGeneratorService insightService;
    private final DashboardService dashboardService;

    public InsightController(InsightGeneratorService insightService, DashboardService dashboardService) {
        this.insightService = insightService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/generate")
    public ResponseEntity<List<InsightDto>> generateInsights(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<ParetoDto> paretoData = dashboardService.getParetoChartData(start, end);
            KpiDto kpi = dashboardService.getKpiSummary(start, end);
            int totalProduksi = kpi.getTotalProduction().intValue();
            List<InsightDto> insights = insightService.generateRuleBasedInsights(paretoData, totalProduksi);

            return ResponseEntity.ok(insights);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}