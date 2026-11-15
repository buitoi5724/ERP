package com.example.erp.service.impl;

import com.example.erp.entity.ApprovalDelegation;
import com.example.erp.repository.ApprovalDelegationRepository;
import com.example.erp.service.ApprovalDelegationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ApprovalDelegationServiceImpl
        implements ApprovalDelegationService {

    private final ApprovalDelegationRepository approvalDelegationRepository;

    public ApprovalDelegationServiceImpl(
            ApprovalDelegationRepository approvalDelegationRepository) {

        this.approvalDelegationRepository =
                approvalDelegationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalDelegation> getAll() {
        return approvalDelegationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovalDelegation getById(Long id) {

        return approvalDelegationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy ủy quyền với ID: " + id
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalDelegation> getByFromEmployee(Long empId) {

        return approvalDelegationRepository
                .findByFromEmployeeEmpIdAndActiveTrue(empId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalDelegation> getActiveDelegations(
            Long empId,
            LocalDate date) {

        return approvalDelegationRepository
                .findByFromEmployeeEmpIdAndActiveTrueAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                        empId,
                        date,
                        date
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalDelegation> getByToEmployee(Long empId) {

        return approvalDelegationRepository
                .findByToEmployeeEmpIdAndActiveTrue(empId);
    }

    @Override
    public ApprovalDelegation create(
            ApprovalDelegation approvalDelegation) {

        if (approvalDelegation.getFromEmployee() == null) {
            throw new RuntimeException(
                    "Người ủy quyền không được để trống"
            );
        }

        if (approvalDelegation.getToEmployee() == null) {
            throw new RuntimeException(
                    "Người được ủy quyền không được để trống"
            );
        }

        if (approvalDelegation.getFromEmployee()
                .getEmpId()
                .equals(
                        approvalDelegation.getToEmployee()
                                .getEmpId()
                )) {

            throw new RuntimeException(
                    "Người ủy quyền và người được ủy quyền không được giống nhau"
            );
        }

        if (approvalDelegation.getFromDate() == null) {
            throw new RuntimeException(
                    "Ngày bắt đầu không được để trống"
            );
        }

        if (approvalDelegation.getToDate() == null) {
            throw new RuntimeException(
                    "Ngày kết thúc không được để trống"
            );
        }

        if (approvalDelegation.getFromDate()
                .isAfter(approvalDelegation.getToDate())) {

            throw new RuntimeException(
                    "Ngày bắt đầu không được lớn hơn ngày kết thúc"
            );
        }

        if (approvalDelegation.getActive() == null) {
            approvalDelegation.setActive(true);
        }

        return approvalDelegationRepository.save(
                approvalDelegation
        );
    }

    @Override
    public ApprovalDelegation update(
            Long id,
            ApprovalDelegation approvalDelegation) {

        ApprovalDelegation existing =
                getById(id);

        if (approvalDelegation.getFromEmployee() != null) {
            existing.setFromEmployee(
                    approvalDelegation.getFromEmployee()
            );
        }

        if (approvalDelegation.getToEmployee() != null) {
            existing.setToEmployee(
                    approvalDelegation.getToEmployee()
            );
        }

        if (approvalDelegation.getFromDate() != null) {
            existing.setFromDate(
                    approvalDelegation.getFromDate()
            );
        }

        if (approvalDelegation.getToDate() != null) {
            existing.setToDate(
                    approvalDelegation.getToDate()
            );
        }

        if (approvalDelegation.getActive() != null) {
            existing.setActive(
                    approvalDelegation.getActive()
            );
        }

        if (approvalDelegation.getReason() != null) {
            existing.setReason(
                    approvalDelegation.getReason()
            );
        }

        if (existing.getFromEmployee()
                .getEmpId()
                .equals(
                        existing.getToEmployee()
                                .getEmpId()
                )) {

            throw new RuntimeException(
                    "Người ủy quyền và người được ủy quyền không được giống nhau"
            );
        }

        if (existing.getFromDate()
                .isAfter(existing.getToDate())) {

            throw new RuntimeException(
                    "Ngày bắt đầu không được lớn hơn ngày kết thúc"
            );
        }

        return approvalDelegationRepository.save(existing);
    }

    @Override
    public void delete(Long id) {

        ApprovalDelegation approvalDelegation =
                getById(id);

        approvalDelegationRepository.delete(
                approvalDelegation
        );
    }
}