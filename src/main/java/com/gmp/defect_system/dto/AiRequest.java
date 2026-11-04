package com.gmp.defect_system.dto;

public class AiRequest {
    private String shift;
    private String nama_mesin;
    private String proses;
    private String jenis_defect;

    public AiRequest(String shift, String nama_mesin, String proses, String jenis_defect) {
        this.shift = shift;
        this.nama_mesin = nama_mesin;
        this.proses = proses;
        this.jenis_defect = jenis_defect;
    }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
    public String getNama_mesin() { return nama_mesin; }
    public void setNama_mesin(String nama_mesin) { this.nama_mesin = nama_mesin; }
    public String getProses() { return proses; }
    public void setProses(String proses) { this.proses = proses; }
    public String getJenis_defect() { return jenis_defect; }
    public void setJenis_defect(String jenis_defect) { this.jenis_defect = jenis_defect; }
}