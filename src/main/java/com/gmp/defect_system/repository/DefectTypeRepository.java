package com.gmp.defect_system.repository;

import com.gmp.defect_system.entity.DefectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefectTypeRepository extends JpaRepository<DefectType, Long> {
}