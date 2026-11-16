package com.gmp.defect_system.controller;

import com.gmp.defect_system.dto.JwtResponse;
import com.gmp.defect_system.dto.LoginRequest;
import com.gmp.defect_system.dto.RegisterRequest;
import com.gmp.defect_system.entity.User;
import com.gmp.defect_system.repository.UserRepository;
import com.gmp.defect_system.security.JwtUtil;
import com.gmp.defect_system.security.LoginAttemptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                          LoginAttemptService loginAttemptService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username sudah digunakan!");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());

        userRepository.save(user);
        return ResponseEntity.ok("User berhasil didaftarkan!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {
        String username = request.getUsername() != null ? request.getUsername().trim() : "";

        // 1. Cek apakah akun sedang dalam masa penguncian (Brute-Force Lockout)
        if (loginAttemptService.isBlocked(username)) {
            long remainingSeconds = loginAttemptService.getRemainingLockTimeSeconds(username);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of(
                            "error", "ACCOUNT_LOCKED",
                            "message", "Akun Anda terkunci sementara karena salah kata sandi 3 kali. Silakan coba lagi dalam " + remainingSeconds + " detik.",
                            "remainingSeconds", remainingSeconds
                    ));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword()));

            // Login Berhasil -> Reset hitungan percobaan gagal
            loginAttemptService.loginSucceeded(username);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
            String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole());

            return ResponseEntity.ok(new JwtResponse(jwt, user.getUsername(), user.getFullName(), user.getRole()));
        } catch (BadCredentialsException e) {
            // Login Gagal -> Tambah hitungan percobaan gagal
            loginAttemptService.loginFailed(username);

            if (loginAttemptService.isBlocked(username)) {
                long remainingSeconds = loginAttemptService.getRemainingLockTimeSeconds(username);
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(Map.of(
                                "error", "ACCOUNT_LOCKED",
                                "message", "Anda telah salah memasukkan kata sandi 3 kali berturut-turut. Akun dikunci sementara selama 1 menit demi keamanan. Silakan coba lagi dalam " + remainingSeconds + " detik.",
                                "remainingSeconds", remainingSeconds
                        ));
            } else {
                int remainingAttempts = loginAttemptService.getRemainingAttempts(username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error", "BAD_CREDENTIALS",
                                "message", "Username atau kata sandi tidak sesuai. Sisa kesempatan: " + remainingAttempts + " kali percobaan lagi.",
                                "remainingAttempts", remainingAttempts
                        ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Terjadi kendala saat autentikasi: " + e.getMessage()));
        }
    }
}
