package com.example.erp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cm_file")
public class CMFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Yêu cầu chứa file này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private RequestApprove request;

    // Tên file gốc
    @Column(nullable = false)
    private String fileName;

    // Đường dẫn hoặc URL lưu file
    @Column(nullable = false)
    private String filePath;

    // Loại file
    private String fileType;

    // Kích thước file (bytes)
    private Long fileSize;

    // Thời điểm upload
    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    public CMFile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequestApprove getRequest() {
        return request;
    }

    public void setRequest(RequestApprove request) {
        this.request = request;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}