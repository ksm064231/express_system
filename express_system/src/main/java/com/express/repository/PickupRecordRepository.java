package com.express.repository;

import com.express.entity.PickupRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PickupRecordRepository extends JpaRepository<PickupRecord, Long> {

    List<PickupRecord> findByPackageId(Long packageId);

    Optional<PickupRecord> findTopByPackageIdOrderByPickupTimeDesc(Long packageId);

    @Query("SELECT COUNT(pr) FROM PickupRecord pr WHERE pr.pickupTime BETWEEN :start AND :end")
    Long countByPickupTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(pr) FROM PickupRecord pr WHERE pr.packageId = :packageId")
    Long countByPackageId(@Param("packageId") Long packageId);
}
