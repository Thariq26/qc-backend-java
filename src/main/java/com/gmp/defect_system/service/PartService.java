package com.gmp.defect_system.service;

import com.gmp.defect_system.entity.Part;
import com.gmp.defect_system.repository.PartRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public List<Part> getAllParts() {
        return partRepository.findAll();
    }

    public void deletePart(Long id) {
        partRepository.deleteById(id);
    }

    public Part createPart(Part part) {
        if (partRepository.existsByPartNumber(part.getPartNumber())) {
            throw new IllegalArgumentException("Part Number sudah terdaftar!");
        }
        part.setStatus("Active");

        return partRepository.save(part);
    }
}