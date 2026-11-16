package com.gmp.defect_system.controller;

import com.gmp.defect_system.entity.ProductionProcess;
import com.gmp.defect_system.repository.ProductionProcessRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/production-processes", "/api/processes"})
@CrossOrigin(origins = "*")
public class ProductionProcessController {

    private final ProductionProcessRepository productionProcessRepository;

    public ProductionProcessController(ProductionProcessRepository productionProcessRepository) {
        this.productionProcessRepository = productionProcessRepository;
    }

    @GetMapping
    public ResponseEntity<List<ProductionProcess>> getAllProcesses(@RequestParam(required = false, defaultValue = "false") boolean all) {
        if (all) {
            return ResponseEntity.ok(productionProcessRepository.findAll());
        }
        return ResponseEntity.ok(productionProcessRepository.findByIsActiveTrueOrderByIdAsc());
    }

    @PostMapping
    public ResponseEntity<ProductionProcess> createProcess(@RequestBody ProductionProcess process) {
        if (process.getIsActive() == null) {
            process.setIsActive(true);
        }
        ProductionProcess saved = productionProcessRepository.save(process);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProcess(@PathVariable Long id) {
        productionProcessRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
