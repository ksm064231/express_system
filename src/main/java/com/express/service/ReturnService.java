package com.express.service;

import com.express.dto.ReturnDTO;
import com.express.dto.ApiResponse;
import com.express.entity.Package;
import com.express.entity.Package.PackageStatus;
import com.express.entity.ReturnRecord;
import com.express.entity.User;
import com.express.exception.BusinessException;
import com.express.repository.PackageRepository;
import com.express.repository.ReturnRecordRepository;
import com.express.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReturnService {

    private final ReturnRecordRepository returnRecordRepository;
    private final PackageRepository packageRepository;
    private final UserRepository userRepository;

    public ReturnService(ReturnRecordRepository returnRecordRepository,
                         PackageRepository packageRepository,
                         UserRepository userRepository) {
        this.returnRecordRepository = returnRecordRepository;
        this.packageRepository = packageRepository;
        this.userRepository = userRepository;
    }

    /**
     * 退件处理
     */
    @Transactional
    public ApiResponse<ReturnDTO> processReturn(ReturnDTO returnDTO) {
        Package pkg = packageRepository.findById(returnDTO.getPackageId())
                .orElseThrow(() -> new BusinessException("快递不存在"));

        if (pkg.getStatus() == PackageStatus.PICKED_UP) {
            throw new BusinessException("该快递已被取走，无法退回");
        }
        if (pkg.getStatus() == PackageStatus.RETURNED) {
            throw new BusinessException("该快递已被退回");
        }

        ReturnRecord record = ReturnRecord.builder()
                .packageId(pkg.getId())
                .returnReason(returnDTO.getReturnReason())
                .returnTime(returnDTO.getReturnTime() != null ? returnDTO.getReturnTime() : LocalDateTime.now())
                .processedBy(returnDTO.getProcessedBy())
                .courierName(returnDTO.getCourierName())
                .returnNotes(returnDTO.getReturnNotes())
                .build();

        returnRecordRepository.save(record);

        // 更新快递状态为已退回
        pkg.setStatus(PackageStatus.RETURNED);
        packageRepository.save(pkg);

        return ApiResponse.success("退件处理成功", convertToDTO(record));
    }

    /**
     * 获取快递的退件记录
     */
    public ApiResponse<List<ReturnDTO>> getReturnRecordsByPackageId(Long packageId) {
        List<ReturnRecord> records = returnRecordRepository.findByPackageId(packageId);
        return ApiResponse.success(convertToDTOList(records));
    }

    /**
     * 获取所有退件记录
     */
    public ApiResponse<List<ReturnDTO>> getAllReturnRecords() {
        List<ReturnRecord> records = returnRecordRepository.findAll();
        return ApiResponse.success(convertToDTOList(records));
    }

    private ReturnDTO convertToDTO(ReturnRecord record) {
        ReturnDTO dto = ReturnDTO.builder()
                .id(record.getId())
                .packageId(record.getPackageId())
                .returnReason(record.getReturnReason())
                .returnTime(record.getReturnTime())
                .processedBy(record.getProcessedBy())
                .courierName(record.getCourierName())
                .returnNotes(record.getReturnNotes())
                .createdAt(record.getCreatedAt())
                .build();

        if (record.getProcessedBy() != null) {
            userRepository.findById(record.getProcessedBy())
                    .ifPresent(user -> dto.setProcessedByName(user.getRealName()));
        }

        return dto;
    }

    private List<ReturnDTO> convertToDTOList(List<ReturnRecord> records) {
        return records.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
