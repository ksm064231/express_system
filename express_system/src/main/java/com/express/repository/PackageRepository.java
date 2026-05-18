package com.express.repository;

import com.express.entity.Package;
import com.express.entity.Package.PackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    Optional<Package> findByTrackingNumber(String trackingNumber);

    List<Package> findByRecipientPhone(String recipientPhone);

    List<Package> findByRecipientName(String recipientName);

    List<Package> findByStatus(PackageStatus status);

    List<Package> findByRoomNumber(String roomNumber);

    @Query("SELECT p FROM Package p WHERE p.status = 'STORED' AND p.arrivalTime < :threshold")
    List<Package> findOverduePackages(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT COUNT(p) FROM Package p WHERE p.arrivalTime BETWEEN :start AND :end")
    Long countByArrivalTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(p) FROM Package p WHERE p.status = 'STORED'")
    Long countStoredPackages();

    @Query("SELECT p.courierCompany, COUNT(p) FROM Package p WHERE p.arrivalTime BETWEEN :start AND :end GROUP BY p.courierCompany")
    List<Object[]> countByCompanyAndArrivalTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p.courierCompany, COUNT(p) FROM Package p WHERE p.status = 'PICKED_UP' AND p.updatedAt BETWEEN :start AND :end GROUP BY p.courierCompany")
    List<Object[]> countPickupByCompanyAndTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Package> findByTrackingNumberContainingOrRecipientNameContainingOrRecipientPhoneContaining(
            String trackingNumber, String recipientName, String recipientPhone);
}
