package com.gmp.defect_system.security;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * LoginAttemptService: Proteksi Brute-Force & Rate Limiting Login
 * Membatasi maksimal 3 kali percobaan gagal dengan durasi kunci 1 menit.
 */
@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPTS = 3;
    public static final long LOCK_TIME_DURATION_MS = TimeUnit.MINUTES.toMillis(1); // 1 Menit (60 detik)

    private static class AttemptData {
        int attempts;
        long lastAttemptTime;
        long lockedUntil;

        AttemptData() {
            this.attempts = 1;
            this.lastAttemptTime = System.currentTimeMillis();
            this.lockedUntil = 0;
        }
    }

    private final ConcurrentHashMap<String, AttemptData> attemptsCache = new ConcurrentHashMap<>();

    /**
     * Dipanggil saat login berhasil untuk mereset riwayat kegagalan akun.
     */
    public void loginSucceeded(String username) {
        if (username != null) {
            attemptsCache.remove(username.trim().toLowerCase());
        }
    }

    /**
     * Dipanggil saat login gagal (BadCredentialsException).
     */
    public void loginFailed(String username) {
        if (username == null) return;
        String key = username.trim().toLowerCase();
        long now = System.currentTimeMillis();

        attemptsCache.compute(key, (k, data) -> {
            if (data == null) {
                return new AttemptData();
            }

            // Jika durasi kunci sebelumnya sudah kedaluwarsa, mulai hitungan baru
            if (data.lockedUntil > 0 && now > data.lockedUntil) {
                data.attempts = 1;
                data.lockedUntil = 0;
                data.lastAttemptTime = now;
                return data;
            }

            data.attempts++;
            data.lastAttemptTime = now;

            // Jika sudah mencapai 3 kali gagal, kunci selama 1 menit
            if (data.attempts >= MAX_ATTEMPTS) {
                data.lockedUntil = now + LOCK_TIME_DURATION_MS;
            }
            return data;
        });
    }

    /**
     * Mengecek apakah akun sedang dalam masa penguncian.
     */
    public boolean isBlocked(String username) {
        if (username == null) return false;
        String key = username.trim().toLowerCase();
        AttemptData data = attemptsCache.get(key);
        if (data == null) return false;

        long now = System.currentTimeMillis();
        if (data.lockedUntil > 0) {
            if (now < data.lockedUntil) {
                return true;
            } else {
                // Masa kunci 1 menit sudah berakhir, hapus riwayat penguncian
                attemptsCache.remove(key);
                return false;
            }
        }
        return false;
    }

    /**
     * Menghitung sisa detik sebelum akun dapat mencoba login kembali.
     */
    public long getRemainingLockTimeSeconds(String username) {
        if (username == null) return 0;
        String key = username.trim().toLowerCase();
        AttemptData data = attemptsCache.get(key);
        if (data == null || data.lockedUntil <= 0) return 0;

        long remainingMs = data.lockedUntil - System.currentTimeMillis();
        return remainingMs > 0 ? (remainingMs / 1000) + 1 : 0;
    }

    /**
     * Menghitung sisa percobaan login sebelum akun terkunci.
     */
    public int getRemainingAttempts(String username) {
        if (username == null) return MAX_ATTEMPTS;
        String key = username.trim().toLowerCase();
        AttemptData data = attemptsCache.get(key);
        if (data == null) return MAX_ATTEMPTS;

        int remaining = MAX_ATTEMPTS - data.attempts;
        return Math.max(0, remaining);
    }
}
