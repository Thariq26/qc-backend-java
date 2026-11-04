package com.gmp.defect_system.controller;

import com.gmp.defect_system.dto.DailyProductionDto;
import com.gmp.defect_system.dto.ParetoDto;
import com.gmp.defect_system.dto.KpiDto;
import com.gmp.defect_system.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;
    private final com.gmp.defect_system.repository.UserRepository userRepository;

    public DashboardController(DashboardService dashboardService, com.gmp.defect_system.repository.UserRepository userRepository) {
        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
    }
    private LocalDate[] checkDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().minusDays(7);
        if (endDate == null) endDate = LocalDate.now();
        return new LocalDate[]{startDate, endDate};
    }

    private String resolveTargetUser(String username, String role) {
        if (role != null && role.trim().equalsIgnoreCase("ADMIN")) {
            return null; // Explicit Admin role request -> Global data
        }

        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            boolean hasAdminAuthority = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN") || a.getAuthority().equalsIgnoreCase("ADMIN"));
            if (hasAdminAuthority) {
                return null; // Admin authority -> Global data
            }
        }

        String currentUsername = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                ? auth.getName() : null;
        String targetUser = (username != null && !username.trim().isEmpty()) ? username.trim() : currentUsername;

        if (targetUser != null) {
            if (targetUser.equalsIgnoreCase("admin")) {
                return null;
            }
            java.util.Optional<com.gmp.defect_system.entity.User> userOpt = userRepository.findByUsername(targetUser);
            if (userOpt.isPresent() && "ADMIN".equalsIgnoreCase(userOpt.get().getRole())) {
                return null;
            }
        }
        return targetUser;
    }

    @GetMapping("/pareto")
    public ResponseEntity<List<ParetoDto>> getParetoData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role) {
        LocalDate[] dates = checkDates(startDate, endDate);
        return ResponseEntity.ok(dashboardService.getParetoChartData(dates[0], dates[1], resolveTargetUser(username, role)));
    }

    @GetMapping("/summary")
    public ResponseEntity<KpiDto> getSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role) {
        LocalDate[] dates = checkDates(startDate, endDate);
        return ResponseEntity.ok(dashboardService.getKpiSummary(dates[0], dates[1], resolveTargetUser(username, role)));
    }

    @GetMapping("/daily-production")
    public ResponseEntity<List<DailyProductionDto>> getDailyProduction(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role) {
        LocalDate[] dates = checkDates(startDate, endDate);
        return ResponseEntity.ok(dashboardService.getDailyProductionData(dates[0], dates[1], resolveTargetUser(username, role)));
    }

    @GetMapping("/complaints-summary")
    public ResponseEntity<Map<String, Long>> getComplaintSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role) {
        LocalDate[] dates = checkDates(startDate, endDate);
        long total = dashboardService.getComplaintCount(dates[0], dates[1], resolveTargetUser(username, role));
        Map<String, Long> response = new HashMap<>();
        response.put("totalComplaints", total);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/complaints-pareto")
    public ResponseEntity<List<Map<String, Object>>> getComplaintParetoData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LocalDate[] dates = checkDates(startDate, endDate);
        return ResponseEntity.ok(dashboardService.getComplaintParetoData(dates[0], dates[1]));
    }
}