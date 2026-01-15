import axios from "axios";

const API = "http://localhost:8080/api/suppliers";

export const getSuppliers = () => axios.get(API);

export const getSupplierById = (id) => axios.get(`${API}/${id}`);

export const createSupplier = (data) => axios.post(API, data);

export const updateSupplier = (id, data) => axios.put(`${API}/${id}`, data);

export const deleteSupplier = (id) => axios.delete(`${API}/${id}`);
