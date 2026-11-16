package com.gmp.defect_system.controller;

import com.gmp.defect_system.service.SupabaseStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final String UPLOAD_DIR = "uploads/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB Maksimal

    private final SupabaseStorageService supabaseStorageService;

    public FileUploadController(SupabaseStorageService supabaseStorageService) {
        this.supabaseStorageService = supabaseStorageService;
    }

    // Whitelist MIME Type Gambar yang Diizinkan
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    // Whitelist Ekstensi File yang Diizinkan
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png",
            "webp"
    );

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 1. Validasi Keberadaan File
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("Gagal upload: File tidak boleh kosong.");
            }

            // 2. Validasi Batas Ukuran File (Maksimal 10MB)
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body("Gagal upload: Ukuran file melebihi batas maksimum 10MB.");
            }

            // 3. Validasi MIME Content-Type
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Gagal upload: Format file tidak didukung. Hanya gambar (JPEG, PNG, WEBP) yang diizinkan.");
            }

            // 4. Validasi Ekstensi File
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
            }

            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Gagal upload: Ekstensi file tidak sah.");
            }

            Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 5. Sanitasi Nama File Menggunakan UUID Acak untuk Mencegah File Execution & Overwriting
            String safeFileName = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + "." + extension;
            Path filePath = uploadPath.resolve(safeFileName).normalize();

            // Cegah Path Traversal saat penyimpanan
            if (!filePath.startsWith(uploadPath)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses penyimpanan tidak sah.");
            }

            // Simpan salinan lokal (cache / fallback)
            byte[] fileBytes = file.getBytes();
            Files.write(filePath, fileBytes);

            // Upload ke Supabase Storage (Cloud Storage Permanen)
            supabaseStorageService.uploadFile(safeFileName, fileBytes, contentType);

            return ResponseEntity.ok(safeFileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Gagal upload gambar: " + e.getMessage());
        }
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        try {
            // Validasi dasar nama file untuk mencegah path traversal
            if (filename == null || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(filename).normalize();

            // Cegah Path Traversal
            if (!filePath.startsWith(uploadPath)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // 1. Cek file lokal terlebih dahulu
            if (Files.exists(filePath)) {
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists() && resource.isReadable()) {
                    String contentType = Files.probeContentType(filePath);
                    if (contentType == null) {
                        contentType = "image/jpeg";
                    }
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .body(resource);
                }
            }

            // 2. Jika file tidak ditemukan di lokal (misalnya kontainer Railway baru saja restart),
            // alihkan (302 Redirect) secara mulus ke Supabase Storage Publik
            String publicUrl = supabaseStorageService.getPublicUrl(filename);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(publicUrl))
                    .build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
