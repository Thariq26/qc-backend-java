package com.gmp.defect_system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiPredictionService {

    @Value("${ai.server.url:https://qc-ai-service-production.up.railway.app/predict-action}")
    private String aiServerUrl;

    private final RestTemplate restTemplate;

    public AiPredictionService() {
        this.restTemplate = new RestTemplate();
    }

    public String mintaRekomendasiAi(String proses, String defect) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("proses", proses);
            request.put("jenis_defect", defect);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    aiServerUrl, request, Map.class
            );
            if (response.getBody() != null && "success".equals(response.getBody().get("status"))) {
                return (String) response.getBody().get("rekomendasi");
            }

            return "AI merespons, tapi format data tidak cocok.";

        } catch (Exception e) {
            System.out.println("Error Koneksi AI: " + e.getMessage());
            throw new RuntimeException("Menunggu analisis manual (Server AI Offline/Error).");
        }
    }
}
