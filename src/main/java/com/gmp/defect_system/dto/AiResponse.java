package com.gmp.defect_system.dto;

public class AiResponse {
    private String status;
    private String rekomendasi_tindakan;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRekomendasi_tindakan() { return rekomendasi_tindakan; }
    public void setRekomendasi_tindakan(String rekomendasi_tindakan) { this.rekomendasi_tindakan = rekomendasi_tindakan; }
}