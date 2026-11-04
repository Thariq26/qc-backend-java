package com.gmp.defect_system.controller;

import com.gmp.defect_system.dto.ComplaintRequest;
import com.gmp.defect_system.entity.CustomerComplaint;
import com.gmp.defect_system.service.CustomerComplaintService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class CustomerComplaintController {

    private final CustomerComplaintService complaintService;

    public CustomerComplaintController(CustomerComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerComplaint>> getAll() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ComplaintRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : "System";

            complaintService.saveComplaint(request, username);
            return ResponseEntity.ok(Map.of("message", "Data komplain pelanggan berhasil dicatat!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Gagal menyimpan komplain: " + e.getMessage()));
        }
    }
}