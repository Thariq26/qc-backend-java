package com.gmp.defect_system.repository;

import com.gmp.defect_system.entity.CustomerComplaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface CustomerComplaintRepository extends JpaRepository<CustomerComplaint, Long> {
    @Query("SELECT COUNT(c) FROM CustomerComplaint c WHERE c.complaintDate BETWEEN :startDate AND :endDate")
    long getComplaintCountByDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(c) FROM CustomerComplaint c WHERE c.complaintDate BETWEEN :startDate AND :endDate AND (LOWER(c.createdBy) = LOWER(:username))")
    long getComplaintCountByDateAndUser(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("username") String username);

    @Query("SELECT new map(d.name as defectName, p.partName as partName, SUM(c.defectQuantity) as totalQuantity) " +
            "FROM CustomerComplaint c JOIN c.defectType d JOIN c.part p " +
            "WHERE c.complaintDate BETWEEN :startDate AND :endDate " +
            "GROUP BY d.name, p.partName " +
            "ORDER BY SUM(c.defectQuantity) DESC")
    List<Map<String, Object>> getComplaintParetoByDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}