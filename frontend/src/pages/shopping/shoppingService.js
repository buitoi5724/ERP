// productService.js
import axios from "axios";

const API_URL = "http://localhost:8080/api/products";
const CATEGORY_URL = "http://localhost:8080/api/product-categories";

export const getAllProducts = () => axios.get(API_URL);

export const getProductById = async (id) => {
  const response = await axios.get(`${API_URL}/${id}`);
  return response.data;
};

export const updateProduct = async (id, formData, productData) => {
  // productData: object JSON chứa giá trị { name, price, ... }
  const response = await axios.put(`${API_URL}/${id}`, formData, {
    headers: { "Content-Type": "multipart/form-data" }
  });

  // Lưu lịch sử giá nếu có thay đổi
  if (productData?.price !== undefined && productData?.price !== null) {
    await axios.post(`${API_URL}/${id}/price-history`, null, {
      params: { price: productData.price }
    });
  }

  return response.data;
};

export const deleteProduct = (id) => axios.delete(`${API_URL}/${id}`);

export const getImage = (product) =>
  axios.get(`${API_URL}/get-image/${product.id}`, { responseType: "arraybuffer" });

export const createProduct = async (formData, productData) => {
  const response = await axios.post(API_URL, formData, {
    headers: { "Content-Type": "multipart/form-data" }
  });

  // Thêm vào lịch sử giá ban đầu
  if (productData?.price !== undefined && productData?.price !== null) {
    await axios.post(`${API_URL}/${response.data.id}/price-history`, null, {
      params: { price: productData.price }
    });
  }

  return response.data;
};

export const getPriceHistory = async (id) => {
  const response = await axios.get(`${API_URL}/${id}/price-history`);
  return response.data;
};

// ✅ Lấy danh mục sản phẩm
export const getCategories = async () => {
  const response = await axios.get(CATEGORY_URL);
  return response.data;
};

