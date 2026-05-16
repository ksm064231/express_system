package com.express.repository;

import com.express.entity.ExceptionRecord;
import com.express.entity.ExceptionRecord.ExceptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExceptionRecordRepository extends JpaRepository<ExceptionRecord, Long> {

    List<ExceptionRecord> findByPackageId(Long packageId);

    List<ExceptionRecord> findByStatus(ExceptionStatus status);

    List<ExceptionRecord> findByExceptionType(ExceptionRecord.ExceptionType exceptionType);

    long countByStatus(ExceptionStatus status);
}
