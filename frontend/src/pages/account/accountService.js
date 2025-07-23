import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

const accountService = {
    getAccounts: async () => {
        const response = await axios.get(`${API_BASE_URL}/accounts`);
        return response.data;
    },

    getAccountById: async (id) => {
        const response = await axios.get(`${API_BASE_URL}/accounts/${id}`);
        return response.data;
    },

    createAccount: async (account) => {
        const response = await axios.post(`${API_BASE_URL}/accounts`, account);
        return response.data;
    },

    updateAccount: async (id, account) => {
        const response = await axios.put(`${API_BASE_URL}/accounts/${id}`, account);
        return response.data;
    },

    deleteAccount: async (id) => {
        await axios.delete(`${API_BASE_URL}/accounts/${id}`);
    }
};

export default accountService;
