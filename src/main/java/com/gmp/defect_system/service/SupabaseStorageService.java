package com.gmp.defect_system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class SupabaseStorageService {

    private static final Logger log = LoggerFactory.getLogger(SupabaseStorageService.class);

    @Value("${supabase.url:https://bamcavbfbopikycjvmxz.supabase.co}")
    private String supabaseUrl;

    @Value("${supabase.key:}")
    private String supabaseKey;

    @Value("${supabase.storage.bucket:defect-photos}")
    private String bucketName;

    private final HttpClient httpClient;

    public SupabaseStorageService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    /**
     * Upload binary image directly to Supabase Storage bucket.
     *
     * @param filename Sanitized unique file name
     * @param fileData Raw byte array of the file
     * @param contentType MIME type of the file (e.g. image/jpeg, image/png)
     * @return true if upload succeeded, false otherwise
     */
    public boolean uploadFile(String filename, byte[] fileData, String contentType) {
        if (supabaseUrl == null || supabaseUrl.isBlank() || supabaseKey == null || supabaseKey.isBlank()) {
            log.warn("Supabase Storage configuration missing. Skipping cloud upload for: {}", filename);
            return false;
        }

        try {
            String cleanUrl = supabaseUrl.replaceAll("/+$", "");
            String uploadEndpoint = String.format("%s/storage/v1/object/%s/%s", cleanUrl, bucketName, filename);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadEndpoint))
                    .timeout(Duration.ofSeconds(30))
                    .header("apikey", supabaseKey)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("Content-Type", contentType != null ? contentType : "image/jpeg")
                    .header("x-upsert", "true")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(fileData))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Successfully uploaded {} to Supabase Storage bucket '{}'. HTTP {}", filename, bucketName, response.statusCode());
                return true;
            } else {
                log.error("Failed to upload {} to Supabase Storage. HTTP {}: {}", filename, response.statusCode(), response.body());
                return false;
            }
        } catch (Exception e) {
            log.error("Exception while uploading {} to Supabase Storage: {}", filename, e.getMessage());
            return false;
        }
    }

    /**
     * Get the public URL for an object in Supabase Storage.
     *
     * @param filename Name of the file
     * @return Full public HTTPS URL to view/download the object
     */
    public String getPublicUrl(String filename) {
        String cleanUrl = (supabaseUrl != null ? supabaseUrl : "https://bamcavbfbopikycjvmxz.supabase.co").replaceAll("/+$", "");
        return String.format("%s/storage/v1/object/public/%s/%s", cleanUrl, bucketName, filename);
    }
}
