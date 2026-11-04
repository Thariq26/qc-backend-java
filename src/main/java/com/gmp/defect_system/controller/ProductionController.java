package com.gmp.defect_system.controller;

import com.gmp.defect_system.dto.BatchProductionRequest;
import com.gmp.defect_system.entity.Production;
import com.gmp.defect_system.service.ProductionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/production")
@CrossOrigin(origins = "*")
public class ProductionController {

    private final ProductionService productionService;
    private final com.gmp.defect_system.repository.UserRepository userRepository;

    public ProductionController(ProductionService productionService, com.gmp.defect_system.repository.UserRepository userRepository) {
        this.productionService = productionService;
        this.userRepository = userRepository;
    }

    private String resolveTargetUser(String username, String role, boolean all) {
        if (all) {
            return null;
        }
        if (role != null) {
            String cleanRole = role.trim().toUpperCase();
            if (cleanRole.contains("ADMIN") || cleanRole.contains("QC")) {
                return null;
            }
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            boolean hasPrivileged = auth.getAuthorities().stream()
                    .anyMatch(a -> {
                        String authName = a.getAuthority().toUpperCase();
                        return authName.contains("ADMIN") || authName.contains("QC");
                    });
            if (hasPrivileged) {
                return null;
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
            if (userOpt.isPresent()) {
                String uRole = userOpt.get().getRole();
                if (uRole != null) {
                    String cleanURole = uRole.toUpperCase();
                    if (cleanURole.contains("ADMIN") || cleanURole.contains("QC")) {
                        return null;
                    }
                }
            }
        }
        return targetUser;
    }

    @GetMapping
    public ResponseEntity<List<Production>> getAll(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false, defaultValue = "false") boolean all) {
        String targetUser = resolveTargetUser(username, role, all);
        if (targetUser != null) {
            return ResponseEntity.ok(productionService.getByCreatedBy(targetUser));
        }
        return ResponseEntity.ok(productionService.getAll());
    }

    @PostMapping
    public ResponseEntity<Production> create(@RequestBody Production production) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                ? auth.getName() : null;
        if (currentUsername != null && !currentUsername.trim().isEmpty()) {
            production.setCreatedBy(currentUsername.trim());
        } else if (production.getCreatedBy() == null || production.getCreatedBy().trim().isEmpty()) {
            production.setCreatedBy("System");
        }
        return new ResponseEntity<>(productionService.save(production), HttpStatus.CREATED);
    }
    @PostMapping("/batch")
    public ResponseEntity<?> saveBatch(@RequestBody BatchProductionRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                    ? auth.getName() : null;
            String effectiveUser = currentUsername;
            if (effectiveUser == null || effectiveUser.trim().isEmpty()) {
                effectiveUser = (request.getUsername() != null && !request.getUsername().trim().isEmpty())
                        ? request.getUsername().trim()
                        : request.getCreatedBy();
            }
            if (effectiveUser == null || effectiveUser.trim().isEmpty()) {
                effectiveUser = "System";
            }

            productionService.saveBatchProduction(request, effectiveUser);
            return ResponseEntity.ok(Map.of("message", "Data produksi berhasil disimpan!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Gagal menyimpan data: " + e.getMessage()));
        }
    }
}