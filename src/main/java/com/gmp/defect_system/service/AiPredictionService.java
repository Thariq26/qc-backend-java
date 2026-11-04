package com.gmp.defect_system.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiPredictionService {

    private final String AI_SERVER_URL = "https://qc-ai-service.onrender.com/predict-action";
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
                    AI_SERVER_URL, request, Map.class
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