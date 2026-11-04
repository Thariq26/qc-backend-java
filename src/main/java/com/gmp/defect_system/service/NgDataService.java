package com.gmp.defect_system.service;

import com.gmp.defect_system.dto.NgDataRequest;
import com.gmp.defect_system.entity.DefectType;
import com.gmp.defect_system.entity.NgData;
import com.gmp.defect_system.entity.Part;
import com.gmp.defect_system.repository.DefectTypeRepository;
import com.gmp.defect_system.repository.NgDataRepository;
import com.gmp.defect_system.repository.PartRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NgDataService {

    private final NgDataRepository ngDataRepository;
    private final PartRepository partRepository;
    private final DefectTypeRepository defectTypeRepository;

    public NgDataService(NgDataRepository ngDataRepository, PartRepository partRepository, DefectTypeRepository defectTypeRepository) {
        this.ngDataRepository = ngDataRepository;
        this.partRepository = partRepository;
        this.defectTypeRepository = defectTypeRepository;
    }

    public List<NgData> getAllNgData() {
        return ngDataRepository.findAllByOrderByIdDesc();
    }

    public List<NgData> getByUser(String username) {
        return ngDataRepository.findByUserOrderByIdDesc(username);
    }
    public NgData saveNewDefect(NgDataRequest request, String loggedInUsername) {
        Part part = partRepository.findById(request.getPartId())
                .orElseThrow(() -> new RuntimeException("Part tidak ditemukan!"));

        DefectType defectType = defectTypeRepository.findById(request.getDefectTypeId())
                .orElseThrow(() -> new RuntimeException("Jenis Cacat tidak ditemukan!"));

        NgData newData = new NgData();
        newData.setPart(part);
        newData.setDefectType(defectType);
        newData.setProses(request.getProses());
        newData.setQuantity(request.getQuantity());
        newData.setOperatorName(request.getOperatorName());
        newData.setRemarks(request.getRemarks());
        newData.setPhotoUrl(request.getPhotoUrl());
        if (request.getShift() != null) {
            newData.setShift(request.getShift());
        }
        if (request.getNamaMesin() != null) {
            newData.setNamaMesin(request.getNamaMesin());
        }

        if (request.getDefectDate() != null) {
            newData.setDefectDate(request.getDefectDate());
        } else {
            newData.setDefectDate(LocalDate.now());
        }
        newData.setCreatedAt(LocalDateTime.now());
        newData.setCreatedBy(loggedInUsername);

        return ngDataRepository.save(newData);
    }

    // Fungsi UPDATE
    public NgData updateNgData(Long id, NgData updatedData) {
        NgData existingData = ngDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data dengan ID " + id + " tidak ditemukan"));

        existingData.setPart(updatedData.getPart());
        existingData.setDefectType(updatedData.getDefectType());
        existingData.setProses(updatedData.getProses());
        existingData.setQuantity(updatedData.getQuantity());
        existingData.setOperatorName(updatedData.getOperatorName());
        existingData.setRemarks(updatedData.getRemarks());

        return ngDataRepository.save(existingData);
    }
}