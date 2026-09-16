import api from "../../services/api";

const approvalService = {

    // =========================
    // REQUEST APPROVE
    // =========================

    // Lấy tất cả yêu cầu
    getAllRequests: async () => {
        const response = await api.get("/request-approves");
        return response.data;
    },

    // Lấy yêu cầu theo ID
    getRequestById: async (id) => {
        const response = await api.get(`/request-approves/${id}`);
        return response.data;
    },

    // Lấy yêu cầu theo employee
    getRequestsByEmployee: async (empId) => {
        const response = await api.get(
            `/request-approves/employee/${empId}`
        );
        return response.data;
    },

    // Lấy yêu cầu theo trạng thái
    getRequestsByStatus: async (status) => {
        const response = await api.get(
            `/request-approves/status/${status}`
        );
        return response.data;
    },

    // Tạo yêu cầu
    createRequest: async (data) => {
        const response = await api.post(
            "/request-approves",
            data
        );
        return response.data;
    },

    // Hủy yêu cầu
    cancelRequest: async (id) => {
        const response = await api.put(
            `/request-approves/${id}/cancel`
        );
        return response.data;
    },


    // =========================
    // APPROVAL
    // =========================

    // Lấy Approval của Request
    getApprovalByRequestId: async (requestId) => {
        const response = await api.get(
            `/approvals/request/${requestId}`
        );
        return response.data;
    },


    // =========================
    // APPROVAL LINE
    // =========================

    // Lấy các bước duyệt
    getApprovalLines: async (approvalId) => {
        const response = await api.get(
            `/approval-lines/approval/${approvalId}`
        );
        return response.data;
    },

    // Duyệt
    approve: async (lineId, comment = "") => {
        const response = await api.put(
            `/approval-lines/${lineId}/approve`,
            null,
            {
                params: {
                    comment
                }
            }
        );

        return response.data;
    },

    // Từ chối
    reject: async (lineId, comment = "") => {
        const response = await api.put(
            `/approval-lines/${lineId}/reject`,
            null,
            {
                params: {
                    comment
                }
            }
        );

        return response.data;
    }
};

export default approvalService;