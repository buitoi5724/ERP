package com.example.erp.dto;

import java.time.LocalDateTime;

public class ApprovalResponse {

    private Long id;

    private String status;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private RequestResponse request;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }


    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }


    public RequestResponse getRequest() {
        return request;
    }

    public void setRequest(RequestResponse request) {
        this.request = request;
    }


    // =========================================================
    // REQUEST RESPONSE
    // =========================================================

    public static class RequestResponse {

        private Long id;

        private String requestCode;

        private String requestType;

        private String title;

        private String content;

        private String status;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        private EmployeeResponse employee;


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }


        public String getRequestCode() {
            return requestCode;
        }

        public void setRequestCode(String requestCode) {
            this.requestCode = requestCode;
        }


        public String getRequestType() {
            return requestType;
        }

        public void setRequestType(String requestType) {
            this.requestType = requestType;
        }


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }


        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }


        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }


        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }


        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }


        public EmployeeResponse getEmployee() {
            return employee;
        }

        public void setEmployee(EmployeeResponse employee) {
            this.employee = employee;
        }
    }


    // =========================================================
    // EMPLOYEE RESPONSE
    // =========================================================

    public static class EmployeeResponse {

        private Long empId;

        private String empCode;

        private String empName;

        private String email;

        private Boolean active;


        public Long getEmpId() {
            return empId;
        }

        public void setEmpId(Long empId) {
            this.empId = empId;
        }


        public String getEmpCode() {
            return empCode;
        }

        public void setEmpCode(String empCode) {
            this.empCode = empCode;
        }


        public String getEmpName() {
            return empName;
        }

        public void setEmpName(String empName) {
            this.empName = empName;
        }


        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }


        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }
}