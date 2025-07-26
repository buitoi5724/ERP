import axios from "axios";

const API_URL = "http://localhost:8080/api/products";

export const getAllProducts = () => axios.get(API_URL);

export const getProductById = (id) => axios.get(`${API_URL}/${id}`);

export const createProduct = (product) => axios.post(API_URL, product);

export const updateProduct = (id, product) => axios.put(`${API_URL}/${id}`, product);

export const deleteProduct = (id) => axios.delete(`${API_URL}/${id}`);
