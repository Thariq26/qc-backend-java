package com.gmp.defect_system.service;

import com.gmp.defect_system.dto.ComplaintRequest;
import com.gmp.defect_system.entity.CustomerComplaint;
import com.gmp.defect_system.entity.DefectType;
import com.gmp.defect_system.entity.Part;
import com.gmp.defect_system.repository.CustomerComplaintRepository;
import com.gmp.defect_system.repository.DefectTypeRepository;
import com.gmp.defect_system.repository.PartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerComplaintService {
    private final CustomerComplaintRepository complaintRepository;
    private final PartRepository partRepository;
    private final DefectTypeRepository defectTypeRepository;
    public CustomerComplaintService(CustomerComplaintRepository complaintRepository, PartRepository partRepository, DefectTypeRepository defectTypeRepository)
    {
        this.complaintRepository = complaintRepository;
        this.partRepository = partRepository;
        this.defectTypeRepository = defectTypeRepository;
    }

    public List<CustomerComplaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public CustomerComplaint saveComplaint(ComplaintRequest request, String username) {
        Part part = partRepository.findById(request.getPartId())
                .orElseThrow(() -> new RuntimeException("Part tidak ditemukan di database!"));
        DefectType defectType = defectTypeRepository.findById(request.getDefectTypeId())
                .orElseThrow(() -> new RuntimeException("Jenis cacat tidak ditemukan di database!"));

        CustomerComplaint complaint = new CustomerComplaint();
        complaint.setComplaintDate(request.getComplaintDate());
        complaint.setCustomerName(request.getCustomerName());
        complaint.setPart(part);
        complaint.setDefectType(defectType);
        complaint.setLotNumber(request.getLotNumber());
        complaint.setDefectQuantity(request.getDefectQuantity());
        complaint.setProblemDescription(request.getProblemDescription());
        complaint.setPhotoUrl(request.getPhotoUrl());
        complaint.setCreatedBy(username);

        return complaintRepository.save(complaint);
    }
}