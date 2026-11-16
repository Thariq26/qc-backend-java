package com.gmp.defect_system.repository;

import com.gmp.defect_system.entity.ProductionProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionProcessRepository extends JpaRepository<ProductionProcess, Long> {
    List<ProductionProcess> findByIsActiveTrueOrderByIdAsc();
    List<ProductionProcess> findByIsActiveTrueOrderByProcessNameAsc();
}
