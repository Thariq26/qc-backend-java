package com.gmp.defect_system.controller;

import com.gmp.defect_system.dto.AiRequest;
import com.gmp.defect_system.service.AiPredictionService;
import com.gmp.defect_system.repository.NgDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {

    @Autowired
    private AiPredictionService aiPredictionService;

    @Autowired
    private NgDataRepository ngDataRepository;

    @PostMapping("/predict-action")
    public ResponseEntity<Map<String, String>> predictAction(@RequestBody AiRequest request) {
        String rekomendasi = aiPredictionService.mintaRekomendasiAi(
                request.getProses(),
                request.getJenis_defect()
        );
        Map<String, String> response = new HashMap<>();
        response.put("rekomendasi", rekomendasi);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-context")
    public ResponseEntity<Map<String, String>> getDefectContext(@RequestParam String defectName) {
        List<String> prosesList = ngDataRepository.findTopProsesByDefectName(defectName);
        String prosesTerbaru = (prosesList != null && !prosesList.isEmpty() && prosesList.get(0) != null)
                ? prosesList.get(0)
                : "Blank";
        Map<String, String> context = new HashMap<>();
        context.put("proses", prosesTerbaru);

        return ResponseEntity.ok(context);
    }
}