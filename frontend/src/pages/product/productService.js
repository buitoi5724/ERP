import axios from "axios";

const API_URL = "http://localhost:8080/api/products";
const CATEGORY_URL = "http://localhost:8080/api/product-categories";

// 🧩 Lấy tất cả sản phẩm
export const getAllProducts = () => axios.get(API_URL);

// 🧩 Lấy 1 sản phẩm theo ID
export const getProductById = async (id) => {
  const response = await axios.get(`${API_URL}/${id}`);
  return response.data;
};

// 🧩 Tạo sản phẩm mới (nhiều ảnh)
export const createProduct = async (formData) => {
  const response = await axios.post(API_URL, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return response.data;
};

// 🧩 Cập nhật sản phẩm (nhiều ảnh)
export const updateProduct = async (id, formData) => {
  const response = await axios.put(`${API_URL}/${id}`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return response.data;
};

// 🧩 Xóa sản phẩm
export const deleteProduct = (id) => axios.delete(`${API_URL}/${id}`);

// 🧩 Lấy ảnh sản phẩm
export const getImage = (productId, imageName) =>
  axios.get(`${API_URL}/${productId}/images/${imageName}`, {
    responseType: "arraybuffer",
  });

// 🧩 Lịch sử giá
export const getPriceHistory = async (id) => {
  const response = await axios.get(`${API_URL}/${id}/price-history`);
  return response.data;
};

// 🧩 Lấy danh mục sản phẩm
export const getCategories = async () => {
  const response = await axios.get(CATEGORY_URL);
  return response.data;
};

// 🧩 Cập nhật ảnh đại diện
export const updateMainImage = (productId, imageUrl) => {
  return axios.put(`${API_URL}/${productId}/main-image`, { imageUrl });
};

// 🧩 Hàm build URL ảnh dùng chung
export const buildImageUrl = (img) => {
  const baseUrl = "http://localhost:8080/api/products/image/";
  return img?.startsWith("http") ? img : `${baseUrl}${encodeURIComponent(img)}`;
};
export const deleteProductImage = async (productId, filename) => {
  return axios.delete(`http://localhost:8080/api/products/${productId}/gallery/${filename}`);
};;