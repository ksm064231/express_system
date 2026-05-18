package com.express.service;

import com.express.dto.ExceptionDTO;
import com.express.dto.ApiResponse;
import com.express.entity.ExceptionRecord;
import com.express.entity.ExceptionRecord.ExceptionStatus;
import com.express.entity.ExceptionRecord.ExceptionType;
import com.express.entity.Package;
import com.express.entity.Package.PackageStatus;
import com.express.exception.BusinessException;
import com.express.repository.ExceptionRecordRepository;
import com.express.repository.PackageRepository;
import com.express.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExceptionService {

    private final ExceptionRecordRepository exceptionRecordRepository;
    private final PackageRepository packageRepository;
    private final UserRepository userRepository;

    public ExceptionService(ExceptionRecordRepository exceptionRecordRepository,
                            PackageRepository packageRepository,
                            UserRepository userRepository) {
        this.exceptionRecordRepository = exceptionRecordRepository;
        this.packageRepository = packageRepository;
        this.userRepository = userRepository;
    }

    /**
     * 创建异常记录
     */
    @Transactional
    public ApiResponse<ExceptionDTO> createException(ExceptionDTO exceptionDTO) {
        // 如果关联了快递，检查快递是否存在
        if (exceptionDTO.getPackageId() != null) {
            Package pkg = packageRepository.findById(exceptionDTO.getPackageId())
                    .orElseThrow(() -> new BusinessException("关联快递不存在"));

            // 如果是错件，更新快递状态
            if ("MISMATCH".equals(exceptionDTO.getExceptionType()) ||
                    "WRONG_PICKUP".equals(exceptionDTO.getExceptionType())) {
                pkg.setStatus(PackageStatus.MISMATCH);
                packageRepository.save(pkg);
            }
            // 如果是丢件，更新快递状态
            if ("LOST".equals(exceptionDTO.getExceptionType())) {
                pkg.setStatus(PackageStatus.LOST);
                packageRepository.save(pkg);
            }
        }

        ExceptionRecord record = ExceptionRecord.builder()
                .packageId(exceptionDTO.getPackageId())
                .exceptionType(ExceptionType.valueOf(exceptionDTO.getExceptionType()))
                .description(exceptionDTO.getDescription())
                .handlerId(exceptionDTO.getHandlerId())
                .status(ExceptionStatus.PENDING)
                .build();

        return ApiResponse.success("异常记录创建成功", convertToDTO(exceptionRecordRepository.save(record)));
    }

    /**
     * 处理异常
     */
    @Transactional
    public ApiResponse<ExceptionDTO> handleException(Long id, ExceptionDTO exceptionDTO) {
        ExceptionRecord record = exceptionRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("异常记录不存在"));

        record.setHandlingResult(exceptionDTO.getHandlingResult());
        record.setHandlingTime(LocalDateTime.now());
        record.setHandlerId(exceptionDTO.getHandlerId());
        record.setStatus(ExceptionStatus.RESOLVED);

        if (exceptionDTO.getCompensationAmount() != null) {
            record.setCompensationAmount(exceptionDTO.getCompensationAmount());
        }

        return ApiResponse.success("异常处理成功", convertToDTO(exceptionRecordRepository.save(record)));
    }

    /**
     * 关闭异常记录
     */
    @Transactional
    public ApiResponse<ExceptionDTO> closeException(Long id) {
        ExceptionRecord record = exceptionRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("异常记录不存在"));
        record.setStatus(ExceptionStatus.CLOSED);
        return ApiResponse.success("异常记录已关闭", convertToDTO(exceptionRecordRepository.save(record)));
    }

    /**
     * 获取快递的异常记录
     */
    public ApiResponse<List<ExceptionDTO>> getExceptionsByPackageId(Long packageId) {
        List<ExceptionRecord> records = exceptionRecordRepository.findByPackageId(packageId);
        return ApiResponse.success(convertToDTOList(records));
    }

    /**
     * 获取所有异常记录
     */
    public ApiResponse<List<ExceptionDTO>> getAllExceptions() {
        List<ExceptionRecord> records = exceptionRecordRepository.findAll();
        return ApiResponse.success(convertToDTOList(records));
    }

    /**
     * 根据状态获取异常记录
     */
    public ApiResponse<List<ExceptionDTO>> getExceptionsByStatus(String status) {
        ExceptionStatus exceptionStatus;
        try {
            exceptionStatus = ExceptionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("无效的状态值: " + status);
        }
        List<ExceptionRecord> records = exceptionRecordRepository.findByStatus(exceptionStatus);
        return ApiResponse.success(convertToDTOList(records));
    }

    private ExceptionDTO convertToDTO(ExceptionRecord record) {
        ExceptionDTO dto = ExceptionDTO.builder()
                .id(record.getId())
                .packageId(record.getPackageId())
                .exceptionType(record.getExceptionType().name())
                .description(record.getDescription())
                .handlerId(record.getHandlerId())
                .handlingResult(record.getHandlingResult())
                .handlingTime(record.getHandlingTime())
                .status(record.getStatus().name())
                .compensationAmount(record.getCompensationAmount())
                .createdAt(record.getCreatedAt())
                .build();

        if (record.getHandlerId() != null) {
            userRepository.findById(record.getHandlerId())
                    .ifPresent(user -> dto.setHandlerName(user.getRealName()));
        }

        return dto;
    }

    private List<ExceptionDTO> convertToDTOList(List<ExceptionRecord> records) {
        return records.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
