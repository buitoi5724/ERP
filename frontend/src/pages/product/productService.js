// productService.js
import axios from "axios";

const API_URL = "http://localhost:8080/api/products";
const CATEGORY_URL = "http://localhost:8080/api/product-categories";

export const getAllProducts = () => axios.get(API_URL);

export const getProductById = async (id) => {
  const response = await axios.get(`${API_URL}/${id}`);
  return response.data;
};

export const updateProduct = (id, product) => axios.put(`${API_URL}/${id}`, product);

export const deleteProduct = (id) => axios.delete(`${API_URL}/${id}`);

export const getImage = (product) =>
  axios.get(`${API_URL}/get-image/${product.id}`, { responseType: "arraybuffer" });

export const createProduct = async (product) => {
  const response = await axios.post(API_URL, product);
  return response.data;
};

export const getPriceHistory = async (id) => {
  const response = await axios.get(`${API_URL}/${id}/price-history`);
  return response.data;
};

// ✅ Hàm lấy danh mục sản phẩm
export const getCategories = async () => {
  const response = await axios.get(CATEGORY_URL);
  return response.data;
};
