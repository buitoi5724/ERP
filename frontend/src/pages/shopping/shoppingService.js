// shoppingService.js
import axios from "axios";

// ================== 🧩 CẤU HÌNH URL ==================
const API_URL = "http://localhost:8080/api/products";
const CATEGORY_URL = "http://localhost:8080/api/product-categories";
const BASE_URL = "http://localhost:8080/api/products";
const IMAGE_BASE_URL = `${BASE_URL}/image`;
const CART_URL = "http://localhost:8080/api/cart";

// ================== 🛍️ HÀM SẢN PHẨM (CRUD) ==================

// Lấy tất cả sản phẩm
export const getAllProducts = async () => {
  const res = await axios.get(API_URL);

  // Một số backend trả về dạng object có "content" => cần xử lý
  if (Array.isArray(res.data)) return res.data;
  if (res.data.content) return res.data.content;
  return [];
};

// Lấy sản phẩm theo ID
export const getProductById = async (id) => {
  const response = await axios.get(`${API_URL}/${id}`);
  return response.data;
};

// Cập nhật sản phẩm
export const updateProduct = async (id, formData, productData) => {
  const response = await axios.put(`${API_URL}/${id}`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });

  // Nếu có thay đổi giá => lưu lịch sử giá
  if (productData?.price !== undefined && productData?.price !== null) {
    await axios.post(`${API_URL}/${id}/price-history`, null, {
      params: { price: productData.price },
    });
  }

  return response.data;
};

// Xóa sản phẩm
export const deleteProduct = (id) => axios.delete(`${API_URL}/${id}`);

// Tạo sản phẩm mới
export const createProduct = async (formData, productData) => {
  const response = await axios.post(API_URL, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });

  // Ghi lịch sử giá ban đầu
  if (productData?.price !== undefined && productData?.price !== null) {
    await axios.post(`${API_URL}/${response.data.id}/price-history`, null, {
      params: { price: productData.price },
    });
  }

  return response.data;
};

// Lấy lịch sử giá
export const getPriceHistory = async (id) => {
  const response = await axios.get(`${API_URL}/${id}/price-history`);
  return response.data;
};

// Lấy danh mục sản phẩm
export const getCategories = async () => {
  const response = await axios.get(CATEGORY_URL);
  return response.data;
};

// ================== 🛒 HÀM DÀNH CHO SHOPPING PAGE ==================

// ✅ Lấy chi tiết sản phẩm cho trang shopping (có xử lý ảnh)
export const getShoppingProductById = async (id) => {
  const res = await axios.get(`${BASE_URL}/${id}`);
  const productData = res.data;

  // ✅ Xử lý ảnh (nếu có)
  const galleries =
    productData.imageUrls?.map(
      (img) => `${IMAGE_BASE_URL}/${encodeURIComponent(img)}`
    ) || [];

  const mainImage = productData.image
    ? `${IMAGE_BASE_URL}/${encodeURIComponent(productData.image)}`
    : "https://via.placeholder.com/400?text=No+Image";

  const allImages = [mainImage, ...galleries].filter(
    (v, i, arr) => arr.indexOf(v) === i
  );

  return { ...productData, fullImageUrls: allImages };
};

// ✅ Trả về URL ảnh sản phẩm (nhận tên ảnh, không phải ID)
export const getImage = (imageName) => {
  if (!imageName) return "/images/default-product.png";
  return `${IMAGE_BASE_URL}/${encodeURIComponent(imageName)}`;
};

// ✅ Thêm sản phẩm vào giỏ hàng
export const addToCart = async (userId, productId, quantity = 1) => {
  return axios.post(`${CART_URL}/add`, null, {
    params: { userId, productId, quantity, accountId: userId },
  });
};
