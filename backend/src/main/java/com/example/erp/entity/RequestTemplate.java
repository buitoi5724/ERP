package com.example.erp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "request_template")
public class RequestTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mã mẫu
    @Column(nullable = false, unique = true)
    private String templateCode;

    // Tên mẫu
    @Column(nullable = false)
    private String templateName;

    // Loại yêu cầu mà mẫu này áp dụng
    @Column(nullable = false)
    private String requestType;

    // Nội dung/cấu trúc mẫu
    @Column(columnDefinition = "TEXT")
    private String templateContent;

    // Phiên bản mẫu
    @Column(nullable = false)
    private Integer version = 1;

    // Đang sử dụng
    @Column(nullable = false)
    private Boolean active = true;

    public RequestTemplate() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}