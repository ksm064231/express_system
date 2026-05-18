package com.express.repository;

import com.express.entity.OverdueReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OverdueReminderRepository extends JpaRepository<OverdueReminder, Long> {

    List<OverdueReminder> findByPackageId(Long packageId);

    List<OverdueReminder> findByIsResolvedFalse();

    long countByIsResolvedFalse();
}
