import axios from "axios";

// ================== 🧩 CẤU HÌNH URL ==================
const BASE_URL = "http://localhost:8080/api";
const PRODUCT_URL = `${BASE_URL}/products`;
const CATEGORY_URL = `${BASE_URL}/product-categories`;
const IMAGE_BASE_URL = `${PRODUCT_URL}/image`;
const CART_URL = `${BASE_URL}/cart`;
const ORDER_URL = `${BASE_URL}/orders`;
const INVOICE_URL = `${BASE_URL}/orders`; // giữ nguyên

// =====================================================
// 🛍️ HÀM LIÊN QUAN SẢN PHẨM
// =====================================================
export const getAllProducts = async () => {
  const res = await axios.get(PRODUCT_URL);
  if (Array.isArray(res.data)) return res.data;
  if (res.data.content) return res.data.content;
  return [];
};

export const getProductById = async (id) => {
  const response = await axios.get(`${PRODUCT_URL}/${id}`);
  return response.data;
};

export const updateProduct = async (id, formData, productData) => {
  const response = await axios.put(`${PRODUCT_URL}/${id}`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  if (productData?.price !== undefined && productData?.price !== null) {
    await axios.post(`${PRODUCT_URL}/${id}/price-history`, null, {
      params: { price: productData.price },
    });
  }
  return response.data;
};

export const deleteProduct = (id) => axios.delete(`${PRODUCT_URL}/${id}`);

export const createProduct = async (formData, productData) => {
  const response = await axios.post(PRODUCT_URL, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  if (productData?.price !== undefined && productData?.price !== null) {
    await axios.post(`${PRODUCT_URL}/${response.data.id}/price-history`, null, {
      params: { price: productData.price },
    });
  }
  return response.data;
};

export const getPriceHistory = async (id) => {
  const response = await axios.get(`${PRODUCT_URL}/${id}/price-history`);
  return response.data;
};

export const getCategories = async () => {
  const response = await axios.get(CATEGORY_URL);
  return response.data;
};

// =====================================================
// 🛒 HÀM LIÊN QUAN ĐẾN GIỎ HÀNG
// =====================================================
export const getShoppingProductById = async (id) => {
  const res = await axios.get(`${PRODUCT_URL}/${id}`);
  const productData = res.data;

  const galleries =
    productData.imageUrls?.map((img) => `${IMAGE_BASE_URL}/${encodeURIComponent(img)}`) || [];

  const mainImage = productData.image
    ? `${IMAGE_BASE_URL}/${encodeURIComponent(productData.image)}`
    : "https://via.placeholder.com/400?text=No+Image";

  const allImages = [mainImage, ...galleries].filter((v, i, arr) => arr.indexOf(v) === i);

  return { ...productData, fullImageUrls: allImages };
};

// =========================================
// === THAY ĐỔI CHÍNH (ĐÃ SỬA) ===
// =========================================
// ✅ CHỈNH SỬA ĐỂ LẤY HÓA ĐƠN CÓ TÊN SẢN PHẨM
export const getInvoiceById = async (invoiceId) => {
  // Thêm kiểm tra "bảo vệ": Nếu invoiceId là 'undefined', 'null', hoặc rỗng,
  // chúng ta sẽ không gọi API và trả về 'null' để tránh lỗi 400 Bad Request.
  if (!invoiceId) {
    console.warn("getInvoiceById được gọi nhưng không có invoiceId. Hủy yêu cầu.");
    return Promise.resolve(null); // Trả về null một cách an toàn
  }

  // Logic cũ của bạn vẫn được giữ nguyên
  const response = await axios.get(`${INVOICE_URL}/invoice/${invoiceId}`);
  return response.data; // JSON sẽ có items[].productName
};
// =========================================

export const getImage = (imageNameOrId) => {
  if (!imageNameOrId) return "/images/default-product.png";
  return `${IMAGE_BASE_URL}/${encodeURIComponent(imageNameOrId)}`;
};

export const addToCart = async (userId, productId, quantity = 1) => {
  return axios.post(`${CART_URL}/add`, null, {
    params: { userId, productId, quantity, accountId: userId },
  });
};

export const getCartByUser = async (userId) => {
  const response = await axios.get(`${CART_URL}/${userId}`);
  return response.data;
};

export const updateCartQuantity = async (cartId, quantity) => {
  return axios.put(`${CART_URL}/update/${cartId}?quantity=${quantity}`);
};

export const removeFromCart = async (cartId) => {
  return axios.delete(`${CART_URL}/remove/${cartId}`);
};

export const getProductImage = (productId) => {
  if (!productId) return "/images/default-product.png";
  return `${PRODUCT_URL}/get-image/${productId}`;
};

// =====================================================
// 🧾 ĐẶT HÀNG
// =====================================================
export const placeOrder = async (order) => {
  const payload = {
    ...order,
    orderDate: new Date().toISOString(),
  };
  const response = await axios.post(`${ORDER_URL}`, payload);
  return response.data;
};

// =====================================================
// ✅ EXPORT DEFAULT
// =====================================================
const shoppingService = {
  getAllProducts,
  getProductById,
  updateProduct,
  deleteProduct,
  createProduct,
  getPriceHistory,
  getCategories,
  getShoppingProductById,
  getImage,
  getCartByUser,
  updateCartQuantity,
  removeFromCart,
  addToCart,
  getProductImage,
  placeOrder,
  getInvoiceById,
};

export default shoppingService;