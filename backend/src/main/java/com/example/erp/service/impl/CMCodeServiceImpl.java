package com.example.erp.service.impl;

import com.example.erp.entity.CMCode;
import com.example.erp.repository.CMCodeRepository;
import com.example.erp.service.CMCodeService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CMCodeServiceImpl implements CMCodeService {

    private final CMCodeRepository cmCodeRepository;

    public CMCodeServiceImpl(
            CMCodeRepository cmCodeRepository) {

        this.cmCodeRepository = cmCodeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CMCode> getAll() {

        return cmCodeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public CMCode getById(Long id) {

        return cmCodeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy CMCode với ID: " + id
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CMCode> getByCodeGroup(
            String codeGroup) {

        return cmCodeRepository
                .findByCodeGroupAndActiveTrueOrderByDisplayOrderAsc(
                        codeGroup
                );
    }

    @Override
    @Transactional(readOnly = true)
    public CMCode getByCodeGroupAndCode(
            String codeGroup,
            String code) {

        return cmCodeRepository
                .findByCodeGroupAndCode(
                        codeGroup,
                        code
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy mã: "
                                        + code
                                        + " trong nhóm: "
                                        + codeGroup
                        )
                );
    }

    @Override
    public CMCode create(CMCode cmCode) {

        if (cmCode.getCodeGroup() == null ||
                cmCode.getCodeGroup()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "Nhóm mã không được để trống"
            );
        }

        if (cmCode.getCode() == null ||
                cmCode.getCode()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "Code không được để trống"
            );
        }

        if (cmCode.getName() == null ||
                cmCode.getName()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "Tên mã không được để trống"
            );
        }

        if (cmCodeRepository.existsByCodeGroupAndCode(
                cmCode.getCodeGroup(),
                cmCode.getCode())) {

            throw new RuntimeException(
                    "Code đã tồn tại trong nhóm: "
                            + cmCode.getCodeGroup()
                            + " - "
                            + cmCode.getCode()
            );
        }

        if (cmCode.getDisplayOrder() == null) {
            cmCode.setDisplayOrder(0);
        }

        if (cmCode.getActive() == null) {
            cmCode.setActive(true);
        }

        return cmCodeRepository.save(cmCode);
    }

    @Override
    public CMCode update(
            Long id,
            CMCode cmCode) {

        CMCode existing = getById(id);

        if (cmCode.getName() != null &&
                !cmCode.getName()
                        .trim()
                        .isEmpty()) {

            existing.setName(
                    cmCode.getName()
            );
        }

        if (cmCode.getDisplayOrder() != null) {

            existing.setDisplayOrder(
                    cmCode.getDisplayOrder()
            );
        }

        if (cmCode.getActive() != null) {

            existing.setActive(
                    cmCode.getActive()
            );
        }

        return cmCodeRepository.save(existing);
    }

    @Override
    public void delete(Long id) {

        CMCode cmCode = getById(id);

        cmCodeRepository.delete(cmCode);
    }
}