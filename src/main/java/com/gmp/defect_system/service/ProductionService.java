package com.gmp.defect_system.service;

import com.gmp.defect_system.dto.BatchProductionRequest;
import com.gmp.defect_system.entity.Part;
import com.gmp.defect_system.entity.Production;
import com.gmp.defect_system.repository.PartRepository;
import com.gmp.defect_system.repository.ProductionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductionService {

    private final ProductionRepository productionRepository;
    private final PartRepository partRepository;

    public ProductionService(ProductionRepository productionRepository, PartRepository partRepository) {
        this.productionRepository = productionRepository;
        this.partRepository = partRepository;
    }

    public List<Production> getAll() {
        return productionRepository.findAllByOrderByIdDesc();
    }

    public List<Production> getByCreatedBy(String createdBy) {
        return productionRepository.findByCreatedByOrderByIdDesc(createdBy);
    }

    public Production save(Production production) {
        return productionRepository.save(production);
    }

    @Transactional
    public void saveBatchProduction(BatchProductionRequest request, String username) {
        List<Production> productionsToSave = new ArrayList<>();

        for (BatchProductionRequest.ProductionItem item : request.getItems()) {
            if (item.getProducedQuantity() == null || item.getProducedQuantity() == 0) {
                continue;
            }

            Part part = partRepository.findById(item.getPartId())
                    .orElseThrow(() -> new RuntimeException("Part tidak ditemukan!"));

            Production prod = new Production();
            prod.setProductionDate(request.getProductionDate());
            prod.setShift(request.getShift());
            prod.setLine(request.getLine());
            prod.setMachineName(item.getMachineName());
            prod.setProcessName(item.getProcessName());
            prod.setPart(part);
            prod.setProducedQuantity(item.getProducedQuantity());
            prod.setTargetQuantity(item.getTargetQuantity() != null ? item.getTargetQuantity() : 500);
            prod.setCreatedBy(username);

            productionsToSave.add(prod);
        }
        productionRepository.saveAll(productionsToSave);
    }
}