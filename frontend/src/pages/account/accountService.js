import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081';

const accountService = {
    getAccounts: async () => {
        const response = await axios.get(`${API_BASE_URL}/accounts`);
        return response.data;
    },

    getAccountByEmail: async (id) => {
        const response = await axios.get(`${API_BASE_URL}/users/${id}`);
        return response.data;
    },

    createAccount: async (account) => {
        const response = await axios.post(`${API_BASE_URL}/accounts`, account);
        return response.data;
    },

    deleteAccount: async (id) => {
        await axios.delete(`${API_BASE_URL}/accounts/${id}`);
    }
};

export default accountService;
