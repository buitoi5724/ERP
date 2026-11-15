
import api from "../../services/api";

const approvalService = {

    // =========================================================
    // REQUEST APPROVE
    // =========================================================

    // Lấy tất cả yêu cầu
    getAllRequests: async () => {
        const response = await api.get("/request-approves");
        return response.data;
    },

    // Lấy yêu cầu do tài khoản hiện tại tạo
    getMyRequests: async () => {
        const response = await api.get("/request-approves/my");
        return response.data;
    },

    // Lấy các dòng phê duyệt đang chờ tài khoản hiện tại xử lý
    getMyPendingRequests: async () => {
        const response = await api.get(
            "/request-approves/pending-approvals"
        );
        return response.data;
    },

    // Lấy yêu cầu theo ID
    getRequestById: async (id) => {
        const response = await api.get(
            `/request-approves/${id}`
        );
        return response.data;
    },

    // Lấy yêu cầu theo mã
    getRequestByCode: async (requestCode) => {
        const response = await api.get(
            `/request-approves/code/${requestCode}`
        );
        return response.data;
    },

    // Lấy yêu cầu theo nhân viên
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

    // Cập nhật yêu cầu
    updateRequest: async (id, data) => {
        const response = await api.put(
            `/request-approves/${id}`,
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

    // =========================================================
    // APPROVAL
    // =========================================================

    // Lấy quy trình phê duyệt theo Request ID
    getApprovalByRequestId: async (requestId) => {
        const response = await api.get(
            `/approvals/request/${requestId}`
        );
        return response.data;
    },

    // =========================================================
    // APPROVAL LINE
    // =========================================================

    // Lấy danh sách bước phê duyệt theo Approval ID
    getApprovalLines: async (approvalId) => {
        const response = await api.get(
            `/approval-lines/approval/${approvalId}`
        );
        return response.data;
    },

    // API cũ: lấy danh sách chờ duyệt
    getMyPendingApprovals: async () => {
        const response = await api.get(
            "/approval-lines/my-pending"
        );
        return response.data;
    },

    // Lịch sử phê duyệt
    getMyApprovalHistory: async () => {
        const response = await api.get(
            "/approval-lines/my-history"
        );
        return response.data;
    },

    // =========================================================
    // APPROVAL ACTION
    // =========================================================

    // Duyệt đơn theo Approval Line ID
    approve: async (lineId, comment = "") => {
        const response = await api.put(
            `/approval-lines/${lineId}/approve`,
            null,
            {
                params: { comment }
            }
        );
        return response.data;
    },

    // Từ chối đơn theo Approval Line ID
    reject: async (lineId, comment = "") => {
        const response = await api.put(
            `/approval-lines/${lineId}/reject`,
            null,
            {
                params: { comment }
            }
        );
        return response.data;
    }

};

export default approvalService;
