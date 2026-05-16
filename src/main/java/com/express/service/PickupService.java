package com.express.service;

import com.express.dto.PickupDTO;
import com.express.dto.ApiResponse;
import com.express.entity.Package;
import com.express.entity.Package.PackageStatus;
import com.express.entity.PickupRecord;
import com.express.entity.User;
import com.express.exception.BusinessException;
import com.express.repository.PackageRepository;
import com.express.repository.PickupRecordRepository;
import com.express.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PickupService {

    private final PickupRecordRepository pickupRecordRepository;
    private final PackageRepository packageRepository;
    private final UserRepository userRepository;

    public PickupService(PickupRecordRepository pickupRecordRepository,
                         PackageRepository packageRepository,
                         UserRepository userRepository) {
        this.pickupRecordRepository = pickupRecordRepository;
        this.packageRepository = packageRepository;
        this.userRepository = userRepository;
    }

    /**
     * 取件操作
     */
    @Transactional
    public ApiResponse<PickupDTO> pickupPackage(PickupDTO pickupDTO) {
        Package pkg = packageRepository.findById(pickupDTO.getPackageId())
                .orElseThrow(() -> new BusinessException("快递不存在"));

        // 检查快递状态
        if (pkg.getStatus() == PackageStatus.PICKED_UP) {
            throw new BusinessException("该快递已被取走，请检查是否重复取件");
        }
        if (pkg.getStatus() == PackageStatus.RETURNED) {
            throw new BusinessException("该快递已被退回");
        }
        if (pkg.getStatus() == PackageStatus.LOST) {
            throw new BusinessException("该快递已标记为丢失");
        }

        // 验证取件码（如果提供了取件码）
        if (pickupDTO.getVerificationMethod() != null &&
                "CODE".equals(pickupDTO.getVerificationMethod()) &&
                pickupDTO.getSignature() != null &&
                !pickupDTO.getSignature().equals(pkg.getPickupCode())) {
            throw new BusinessException("取件码验证失败");
        }

        // 创建取件记录
        PickupRecord record = PickupRecord.builder()
                .packageId(pkg.getId())
                .pickupTime(pickupDTO.getPickupTime() != null ? pickupDTO.getPickupTime() : LocalDateTime.now())
                .pickupPersonName(pickupDTO.getPickupPersonName())
                .pickupPersonPhone(pickupDTO.getPickupPersonPhone())
                .signature(pickupDTO.getSignature())
                .verificationMethod(pickupDTO.getVerificationMethod() != null ?
                        PickupRecord.VerificationMethod.valueOf(pickupDTO.getVerificationMethod()) :
                        PickupRecord.VerificationMethod.CODE)
                .verifiedBy(pickupDTO.getVerifiedBy())
                .notes(pickupDTO.getNotes())
                .build();

        pickupRecordRepository.save(record);

        // 更新快递状态为已取件
        pkg.setStatus(PackageStatus.PICKED_UP);
        packageRepository.save(pkg);

        return ApiResponse.success("取件成功", convertToDTO(record));
    }

    /**
     * 根据取件码取件
     */
    @Transactional
    public ApiResponse<PickupDTO> pickupByCode(String trackingNumber, String pickupCode, PickupDTO pickupDTO) {
        Package pkg = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new BusinessException("未找到该运单号的快递"));

        if (!pickupCode.equals(pkg.getPickupCode())) {
            throw new BusinessException("取件码错误");
        }

        pickupDTO.setPackageId(pkg.getId());
        pickupDTO.setVerificationMethod("CODE");
        pickupDTO.setSignature(pickupCode);
        return pickupPackage(pickupDTO);
    }

    /**
     * 获取快递的取件记录
     */
    public ApiResponse<List<PickupDTO>> getPickupRecordsByPackageId(Long packageId) {
        List<PickupRecord> records = pickupRecordRepository.findByPackageId(packageId);
        return ApiResponse.success(convertToDTOList(records));
    }

    /**
     * 获取所有取件记录
     */
    public ApiResponse<List<PickupDTO>> getAllPickupRecords() {
        List<PickupRecord> records = pickupRecordRepository.findAll();
        return ApiResponse.success(convertToDTOList(records));
    }

    /**
     * 检查是否已被取件（用于防重复取件）
     */
    public ApiResponse<Boolean> checkIfPickedUp(Long packageId) {
        long count = pickupRecordRepository.countByPackageId(packageId);
        return ApiResponse.success(count > 0);
    }

    private PickupDTO convertToDTO(PickupRecord record) {
        PickupDTO dto = PickupDTO.builder()
                .id(record.getId())
                .packageId(record.getPackageId())
                .pickupTime(record.getPickupTime())
                .pickupPersonName(record.getPickupPersonName())
                .pickupPersonPhone(record.getPickupPersonPhone())
                .signature(record.getSignature())
                .verificationMethod(record.getVerificationMethod().name())
                .verifiedBy(record.getVerifiedBy())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .build();

        if (record.getVerifiedBy() != null) {
            userRepository.findById(record.getVerifiedBy())
                    .ifPresent(user -> dto.setVerifiedByName(user.getRealName()));
        }

        return dto;
    }

    private List<PickupDTO> convertToDTOList(List<PickupRecord> records) {
        return records.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
