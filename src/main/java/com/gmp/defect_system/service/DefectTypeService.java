package com.gmp.defect_system.service;

import com.gmp.defect_system.entity.DefectType;
import com.gmp.defect_system.repository.DefectTypeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DefectTypeService {

    private final DefectTypeRepository defectTypeRepository;

    public DefectTypeService(DefectTypeRepository defectTypeRepository) {
        this.defectTypeRepository = defectTypeRepository;
    }

    public List<DefectType> getAllDefects() {
        return defectTypeRepository.findAll();
    }

    public DefectType createDefect(DefectType defectType) {
        return defectTypeRepository.save(defectType);
    }

    public void deleteDefect(Long id) {
        defectTypeRepository.deleteById(id);
    }
}