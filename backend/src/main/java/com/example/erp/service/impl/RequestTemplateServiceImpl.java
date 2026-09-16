package com.example.erp.service.impl;

import com.example.erp.entity.RequestTemplate;
import com.example.erp.repository.RequestTemplateRepository;
import com.example.erp.service.RequestTemplateService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RequestTemplateServiceImpl
        implements RequestTemplateService {

    private final RequestTemplateRepository requestTemplateRepository;

    public RequestTemplateServiceImpl(
            RequestTemplateRepository requestTemplateRepository) {

        this.requestTemplateRepository =
                requestTemplateRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestTemplate> getAll() {

        return requestTemplateRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public RequestTemplate getById(Long id) {

        return requestTemplateRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy template với ID: " + id
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public RequestTemplate getByTemplateCode(
            String templateCode) {

        return requestTemplateRepository
                .findByTemplateCode(templateCode)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy template với mã: "
                                        + templateCode
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestTemplate> getByRequestType(
            String requestType) {

        return requestTemplateRepository
                .findByRequestTypeAndActiveTrue(
                        requestType
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestTemplate> getActive() {

        return requestTemplateRepository.findByActiveTrue();
    }

    @Override
    public RequestTemplate create(
            RequestTemplate requestTemplate) {

        if (requestTemplate.getTemplateCode() == null ||
                requestTemplate.getTemplateCode()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "Mã template không được để trống"
            );
        }

        if (requestTemplate.getTemplateName() == null ||
                requestTemplate.getTemplateName()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "Tên template không được để trống"
            );
        }

        if (requestTemplate.getRequestType() == null ||
                requestTemplate.getRequestType()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "Loại yêu cầu không được để trống"
            );
        }

        if (requestTemplateRepository
                .existsByTemplateCode(
                        requestTemplate.getTemplateCode())) {

            throw new RuntimeException(
                    "Mã template đã tồn tại: "
                            + requestTemplate.getTemplateCode()
            );
        }

        if (requestTemplate.getVersion() == null ||
                requestTemplate.getVersion() <= 0) {

            requestTemplate.setVersion(1);
        }

        if (requestTemplate.getActive() == null) {
            requestTemplate.setActive(true);
        }

        return requestTemplateRepository.save(
                requestTemplate
        );
    }

    @Override
    public RequestTemplate update(
            Long id,
            RequestTemplate requestTemplate) {

        RequestTemplate existing =
                getById(id);

        if (requestTemplate.getTemplateName() != null &&
                !requestTemplate.getTemplateName()
                        .trim()
                        .isEmpty()) {

            existing.setTemplateName(
                    requestTemplate.getTemplateName()
            );
        }

        if (requestTemplate.getRequestType() != null &&
                !requestTemplate.getRequestType()
                        .trim()
                        .isEmpty()) {

            existing.setRequestType(
                    requestTemplate.getRequestType()
            );
        }

        if (requestTemplate.getTemplateContent() != null) {

            existing.setTemplateContent(
                    requestTemplate.getTemplateContent()
            );
        }

        if (requestTemplate.getVersion() != null &&
                requestTemplate.getVersion() > 0) {

            existing.setVersion(
                    requestTemplate.getVersion()
            );
        }

        if (requestTemplate.getActive() != null) {

            existing.setActive(
                    requestTemplate.getActive()
            );
        }

        return requestTemplateRepository.save(
                existing
        );
    }

    @Override
    public void delete(Long id) {

        RequestTemplate requestTemplate =
                getById(id);

        requestTemplateRepository.delete(
                requestTemplate
        );
    }
}