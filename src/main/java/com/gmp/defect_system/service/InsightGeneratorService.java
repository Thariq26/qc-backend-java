package com.gmp.defect_system.service;

import com.gmp.defect_system.dto.InsightDto;
import com.gmp.defect_system.dto.ParetoDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InsightGeneratorService {
    public List<InsightDto> generateRuleBasedInsights(List<ParetoDto> paretoList, int totalProduction) {
        List<InsightDto> insights = new ArrayList<>();
        // no empty
        if (paretoList == null || paretoList.isEmpty()) {
            insights.add(new InsightDto("INFO", "Data Kosong", "Tidak ada laporan cacat yang dianalisis saat ini."));
            return insights;
        }
        long totalDefects = paretoList.stream().mapToLong(ParetoDto::getTotalQuantity).sum();
        // Defect Rate
        double defectRate = totalProduction > 0 ? ((double) totalDefects / totalProduction) * 100 : 0;
        if (defectRate > 5.0) {
            insights.add(new InsightDto(
                    "CRITICAL",
                    "Tingkat Cacat Kritis!",
                    String.format("Defect rate mencapai %.1f%% (Melebihi batas toleransi 5%%). Evaluasi menyeluruh pada lini produksi harus segera dilakukan.", defectRate)
            ));
        } else if (defectRate > 0 && defectRate <= 2.0) {
            insights.add(new InsightDto(
                    "SUCCESS",
                    "Kualitas Terkendali",
                    String.format("Defect rate berada di angka yang sangat baik (%.1f%%). Pertahankan standar operasional saat ini.", defectRate)
            ));
        }
        // Detect Anomali
        ParetoDto topDefect = paretoList.get(0);
        double topDefectShare = ((double) topDefect.getTotalQuantity() / totalDefects) * 100;

        if (topDefectShare >= 40.0) {
            insights.add(new InsightDto(
                    "WARNING",
                    "Anomali Dominan: " + topDefect.getDefectName(),
                    String.format("Jenis cacat '%s' sangat mendominasi (Menyumbang %.1f%% dari seluruh masalah). Fokuskan investigasi tim QC 100%% pada mesin, item, ataupun proses yang menyebabkan masalah ini.", topDefect.getDefectName(), topDefectShare)
            ));
        }
        return insights;
    }
}
