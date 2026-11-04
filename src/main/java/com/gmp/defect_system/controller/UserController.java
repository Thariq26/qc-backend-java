package com.gmp.defect_system.controller;

import com.gmp.defect_system.dto.UpdateProfileRequest;
import com.gmp.defect_system.entity.User;
import com.gmp.defect_system.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestParam(required = false) String role) {
        try {
            List<User> allUsers = userRepository.findAll();
            if (role != null && !role.trim().isEmpty()) {
                String cleanRole = role.trim().toUpperCase();
                allUsers = allUsers.stream()
                        .filter(u -> u.getRole() != null && u.getRole().trim().toUpperCase().contains(cleanRole))
                        .collect(Collectors.toList());
            }
            List<Map<String, Object>> response = allUsers.stream().map(u -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", u.getId());
                map.put("username", u.getUsername());
                map.put("fullName", u.getFullName() != null && !u.getFullName().trim().isEmpty() ? u.getFullName() : u.getUsername());
                map.put("email", u.getEmail());
                map.put("role", u.getRole());
                return map;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal mengambil data user: " + e.getMessage());
        }
    }

    @GetMapping("/operators")
    public ResponseEntity<?> getOperators(@RequestParam(required = false) String role) {
        try {
            List<User> allUsers = userRepository.findAll();
            List<Map<String, Object>> operators = allUsers.stream()
                    .filter(u -> {
                        if (u.getRole() == null) return false;
                        String uRole = u.getRole().trim().toUpperCase();
                        if (role != null && !role.trim().isEmpty() && !"ALL".equalsIgnoreCase(role)) {
                            String target = role.trim().toUpperCase();
                            if ("ADMIN_ONLY".equals(target)) {
                                return uRole.contains("ADMIN");
                            }
                            if ("OPERATOR_ONLY".equals(target)) {
                                return uRole.contains("OPERATOR");
                            }
                            if (target.contains("OPERATOR") && target.contains("ADMIN")) {
                                return uRole.contains("OPERATOR") || uRole.contains("ADMIN");
                            }
                            if ("OPERATOR_PRODUKSI".equals(target)) {
                                return uRole.contains("OPERATOR") || uRole.contains("ADMIN");
                            }
                        }
                        // Default: Ambil operator lini produksi dengan Role OPERATOR_PRODUKSI dan ADMIN
                        return uRole.contains("OPERATOR") || uRole.contains("ADMIN");
                    })
                    .sorted((a, b) -> {
                        String nameA = a.getFullName() != null && !a.getFullName().trim().isEmpty() ? a.getFullName() : a.getUsername();
                        String nameB = b.getFullName() != null && !b.getFullName().trim().isEmpty() ? b.getFullName() : b.getUsername();
                        return nameA.compareToIgnoreCase(nameB);
                    })
                    .map(u -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", u.getId());
                        map.put("username", u.getUsername());
                        map.put("fullName", u.getFullName() != null && !u.getFullName().trim().isEmpty() ? u.getFullName() : u.getUsername());
                        map.put("role", u.getRole());
                        return map;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(operators);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal mengambil data operator: " + e.getMessage());
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Gagal mengupdate profil: Sesi Anda telah habis atau Token JWT tidak terbaca. Silakan Logout dan Login kembali.");
            }

            String currentUsername = auth.getName();
            User user = userRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new Exception("User dengan ID '" + currentUsername + "' tidak ditemukan di database."));
            user.setFullName(request.getFullName());

            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                user.setEmail(request.getEmail());
            }
            if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            userRepository.save(user);

            return ResponseEntity.ok().body("Profil berhasil diperbarui!");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Gagal mengupdate profil: " + e.getMessage());
        }
    }
}