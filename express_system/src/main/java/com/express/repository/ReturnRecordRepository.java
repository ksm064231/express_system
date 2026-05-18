package com.express.repository;

import com.express.entity.ReturnRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReturnRecordRepository extends JpaRepository<ReturnRecord, Long> {

    List<ReturnRecord> findByPackageId(Long packageId);

    @Query("SELECT COUNT(rr) FROM ReturnRecord rr WHERE rr.returnTime BETWEEN :start AND :end")
    Long countByReturnTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
