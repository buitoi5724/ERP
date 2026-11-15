package com.example.erp.service.impl;

import com.example.erp.entity.CMFile;
import com.example.erp.repository.CMFileRepository;
import com.example.erp.service.CMFileService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CMFileServiceImpl implements CMFileService {

    private final CMFileRepository cmFileRepository;

    public CMFileServiceImpl(
            CMFileRepository cmFileRepository) {

        this.cmFileRepository = cmFileRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CMFile> getAll() {

        return cmFileRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public CMFile getById(Long id) {

        return cmFileRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy file với ID: " + id
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CMFile> getByRequestId(
            Long requestId) {

        return cmFileRepository.findByRequestId(
                requestId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CMFile> getByRequestIdOrderByUploadedAtDesc(
            Long requestId) {

        return cmFileRepository
                .findByRequestIdOrderByUploadedAtDesc(
                        requestId
                );
    }

    @Override
    public CMFile create(CMFile cmFile) {

        if (cmFile.getRequest() == null) {
            throw new RuntimeException(
                    "Request không được để trống"
            );
        }

        if (cmFile.getFileName() == null ||
                cmFile.getFileName()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "Tên file không được để trống"
            );
        }

        if (cmFile.getFilePath() == null ||
                cmFile.getFilePath()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "Đường dẫn file không được để trống"
            );
        }

        if (cmFile.getUploadedAt() == null) {
            cmFile.setUploadedAt(
                    LocalDateTime.now()
            );
        }

        return cmFileRepository.save(cmFile);
    }

    @Override
    public CMFile update(
            Long id,
            CMFile cmFile) {

        CMFile existing = getById(id);

        if (cmFile.getFileName() != null &&
                !cmFile.getFileName()
                        .trim()
                        .isEmpty()) {

            existing.setFileName(
                    cmFile.getFileName()
            );
        }

        if (cmFile.getFilePath() != null &&
                !cmFile.getFilePath()
                        .trim()
                        .isEmpty()) {

            existing.setFilePath(
                    cmFile.getFilePath()
            );
        }

        if (cmFile.getFileType() != null) {
            existing.setFileType(
                    cmFile.getFileType()
            );
        }

        if (cmFile.getFileSize() != null) {
            existing.setFileSize(
                    cmFile.getFileSize()
            );
        }

        return cmFileRepository.save(existing);
    }

    @Override
    public void delete(Long id) {

        CMFile cmFile = getById(id);

        cmFileRepository.delete(cmFile);
    }
}