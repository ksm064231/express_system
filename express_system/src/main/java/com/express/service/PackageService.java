package com.express.service;

import com.express.dto.PackageDTO;
import com.express.dto.ApiResponse;
import com.express.entity.Package;
import com.express.entity.Package.PackageStatus;
import com.express.entity.User;
import com.express.exception.BusinessException;
import com.express.repository.PackageRepository;
import com.express.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PackageService {

    private final PackageRepository packageRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    public PackageService(PackageRepository packageRepository, UserRepository userRepository, MailService mailService) {
        this.packageRepository = packageRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    /**
     * 快递入库登记
     */
    @Transactional
    public ApiResponse<PackageDTO> storePackage(PackageDTO packageDTO) {
        // 检查运单号是否已存在
        if (packageRepository.findByTrackingNumber(packageDTO.getTrackingNumber()).isPresent()) {
            throw new BusinessException("运单号已存在");
        }

        // 生成取件码（6位随机数字）
        String pickupCode = generatePickupCode();

        Package pkg = Package.builder()
                .trackingNumber(packageDTO.getTrackingNumber())
                .courierCompany(packageDTO.getCourierCompany())
                .recipientName(packageDTO.getRecipientName())
                .recipientPhone(packageDTO.getRecipientPhone())
                .roomNumber(packageDTO.getRoomNumber())
                .pickupCode(pickupCode)
                .status(PackageStatus.STORED)
                .shelfNumber(packageDTO.getShelfNumber())
                .arrivalTime(packageDTO.getArrivalTime() != null ? packageDTO.getArrivalTime() : LocalDateTime.now())
                .storedBy(packageDTO.getStoredBy())
                .notes(packageDTO.getNotes())
                .build();

        Package saved = packageRepository.save(pkg);

        // 异步发送取件码通知邮件
        mailService.sendPickupCodeNotification(saved);

        return ApiResponse.success("入库登记成功，取件码: " + pickupCode, convertToDTO(saved));
    }

    /**
     * 根据运单号查询快递
     */
    public ApiResponse<PackageDTO> getByTrackingNumber(String trackingNumber) {
        Package pkg = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new BusinessException("未找到该运单号的快递"));
        return ApiResponse.success(convertToDTO(pkg));
    }

    /**
     * 根据ID查询快递
     */
    public ApiResponse<PackageDTO> getById(Long id) {
        Package pkg = packageRepository.findById(id)
                .orElseThrow(() -> new BusinessException("快递不存在"));
        return ApiResponse.success(convertToDTO(pkg));
    }

    /**
     * 根据收件人电话查询
     */
    public ApiResponse<List<PackageDTO>> getByRecipientPhone(String phone) {
        List<Package> packages = packageRepository.findByRecipientPhone(phone);
        return ApiResponse.success(convertToDTOList(packages));
    }

    /**
     * 根据收件人姓名查询
     */
    public ApiResponse<List<PackageDTO>> getByRecipientName(String name) {
        List<Package> packages = packageRepository.findByRecipientName(name);
        return ApiResponse.success(convertToDTOList(packages));
    }

    /**
     * 根据房号查询
     */
    public ApiResponse<List<PackageDTO>> getByRoomNumber(String roomNumber) {
        List<Package> packages = packageRepository.findByRoomNumber(roomNumber);
        return ApiResponse.success(convertToDTOList(packages));
    }

    /**
     * 获取所有快递列表
     */
    public ApiResponse<List<PackageDTO>> getAllPackages() {
        List<Package> packages = packageRepository.findAll();
        return ApiResponse.success(convertToDTOList(packages));
    }

    /**
     * 根据状态查询
     */
    public ApiResponse<List<PackageDTO>> getByStatus(String status) {
        PackageStatus packageStatus;
        try {
            packageStatus = PackageStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("无效的状态值: " + status);
        }
        List<Package> packages = packageRepository.findByStatus(packageStatus);
        return ApiResponse.success(convertToDTOList(packages));
    }

    /**
     * 搜索快递（模糊查询）
     */
    public ApiResponse<List<PackageDTO>> searchPackages(String keyword) {
        List<Package> packages = packageRepository
                .findByTrackingNumberContainingOrRecipientNameContainingOrRecipientPhoneContaining(
                        keyword, keyword, keyword);
        return ApiResponse.success(convertToDTOList(packages));
    }

    /**
     * 更新快递信息
     */
    @Transactional
    public ApiResponse<PackageDTO> updatePackage(Long id, PackageDTO packageDTO) {
        Package pkg = packageRepository.findById(id)
                .orElseThrow(() -> new BusinessException("快递不存在"));

        pkg.setCourierCompany(packageDTO.getCourierCompany());
        pkg.setRecipientName(packageDTO.getRecipientName());
        pkg.setRecipientPhone(packageDTO.getRecipientPhone());
        pkg.setRoomNumber(packageDTO.getRoomNumber());
        pkg.setShelfNumber(packageDTO.getShelfNumber());
        pkg.setNotes(packageDTO.getNotes());

        if (packageDTO.getStatus() != null) {
            pkg.setStatus(PackageStatus.valueOf(packageDTO.getStatus()));
        }

        return ApiResponse.success("更新成功", convertToDTO(packageRepository.save(pkg)));
    }

    /**
     * 删除快递
     */
    @Transactional
    public ApiResponse<Void> deletePackage(Long id) {
        if (!packageRepository.existsById(id)) {
            throw new BusinessException("快递不存在");
        }
        packageRepository.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * 获取逾期未取快递列表
     */
    public ApiResponse<List<PackageDTO>> getOverduePackages(int overdueDays) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(overdueDays);
        List<Package> overduePackages = packageRepository.findOverduePackages(threshold);
        return ApiResponse.success(convertToDTOList(overduePackages));
    }

    private PackageDTO convertToDTO(Package pkg) {
        PackageDTO dto = PackageDTO.builder()
                .id(pkg.getId())
                .trackingNumber(pkg.getTrackingNumber())
                .courierCompany(pkg.getCourierCompany())
                .recipientName(pkg.getRecipientName())
                .recipientPhone(pkg.getRecipientPhone())
                .roomNumber(pkg.getRoomNumber())
                .pickupCode(pkg.getPickupCode())
                .status(pkg.getStatus().name())
                .shelfNumber(pkg.getShelfNumber())
                .arrivalTime(pkg.getArrivalTime())
                .storedBy(pkg.getStoredBy())
                .notes(pkg.getNotes())
                .createdAt(pkg.getCreatedAt())
                .updatedAt(pkg.getUpdatedAt())
                .build();

        // 查询入库人姓名
        if (pkg.getStoredBy() != null) {
            userRepository.findById(pkg.getStoredBy())
                    .ifPresent(user -> dto.setStoredByName(user.getRealName()));
        }

        return dto;
    }

    private List<PackageDTO> convertToDTOList(List<Package> packages) {
        return packages.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private String generatePickupCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
