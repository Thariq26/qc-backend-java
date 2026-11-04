package com.gmp.defect_system.controller;

import com.gmp.defect_system.entity.DefectType;
import com.gmp.defect_system.service.DefectTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/defects")
@CrossOrigin(origins = "*")
public class DefectTypeController {

    private final DefectTypeService defectTypeService;

    public DefectTypeController(DefectTypeService defectTypeService) {
        this.defectTypeService = defectTypeService;
    }

    @GetMapping
    public ResponseEntity<List<DefectType>> getAllDefects() {
        return ResponseEntity.ok(defectTypeService.getAllDefects());
    }

    @PostMapping
    public ResponseEntity<DefectType> createDefect(@RequestBody DefectType defectType) {
        return new ResponseEntity<>(defectTypeService.createDefect(defectType), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDefect(@PathVariable Long id) {
        defectTypeService.deleteDefect(id);
        return ResponseEntity.ok().build();
    }
}