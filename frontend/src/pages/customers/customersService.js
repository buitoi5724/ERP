import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/customers";

const customersService = {
  getAll: async () => {
    const response = await axios.get(`${API_BASE_URL}/all`);
    return response.data;
  },

  create: async (data) => {
    const response = await axios.post(API_BASE_URL, data);
    return response.data;
  },

  update: async (id, data) => {
    const response = await axios.put(`${API_BASE_URL}/${id}`, data);
    return response.data;
  },

  remove: async (id) => {
    await axios.delete(`${API_BASE_URL}/${id}`);
  },
};

export default customersService;
