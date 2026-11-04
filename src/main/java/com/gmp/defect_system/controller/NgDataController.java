package com.gmp.defect_system.controller;

import com.gmp.defect_system.dto.NgDataRequest;
import com.gmp.defect_system.entity.NgData;
import com.gmp.defect_system.service.NgDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ng-data")
@CrossOrigin(origins = "*")
public class NgDataController {

    private final NgDataService ngDataService;
    private final com.gmp.defect_system.repository.UserRepository userRepository;

    public NgDataController(NgDataService ngDataService, com.gmp.defect_system.repository.UserRepository userRepository) {
        this.ngDataService = ngDataService;
        this.userRepository = userRepository;
    }

    private String resolveTargetUser(String username, String role) {
        if (role != null && (role.trim().equalsIgnoreCase("ADMIN") || role.trim().equalsIgnoreCase("ALL"))) {
            return null;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            boolean hasAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN") || a.getAuthority().equalsIgnoreCase("ADMIN"));
            if (hasAdmin) {
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
            if (userOpt.isPresent() && "ADMIN".equalsIgnoreCase(userOpt.get().getRole())) {
                return null;
            }
        }
        return targetUser;
    }

    @GetMapping
    public ResponseEntity<List<NgData>> getAll(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean all) {
        if (Boolean.TRUE.equals(all) || (role != null && (role.trim().equalsIgnoreCase("ADMIN") || role.trim().equalsIgnoreCase("ALL")))) {
            return ResponseEntity.ok(ngDataService.getAllNgData());
        }
        String targetUser = resolveTargetUser(username, role);
        if (targetUser != null) {
            return ResponseEntity.ok(ngDataService.getByUser(targetUser));
        }
        return ResponseEntity.ok(ngDataService.getAllNgData());
    }

    @PostMapping
    public ResponseEntity<NgData> create(@RequestBody NgDataRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                ? auth.getName() : null;
        String effectiveUser = currentUsername;
        if (effectiveUser == null || effectiveUser.trim().isEmpty()) {
            effectiveUser = (request.getUsername() != null && !request.getUsername().trim().isEmpty())
                    ? request.getUsername().trim()
                    : request.getOperatorName();
        }
        if (effectiveUser == null || effectiveUser.trim().isEmpty()) {
            effectiveUser = "System";
        }

        return new ResponseEntity<>(ngDataService.saveNewDefect(request, effectiveUser), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NgData> update(@PathVariable Long id, @RequestBody NgData ngData) {
        return ResponseEntity.ok(ngDataService.updateNgData(id, ngData));
    }
}