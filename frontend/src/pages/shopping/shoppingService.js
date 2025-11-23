import axios from "axios";

// ================== 🧩 CẤU HÌNH URL ==================
const BASE_URL = "http://localhost:8080/api";
const PRODUCT_URL = `${BASE_URL}/products`;
const CATEGORY_URL = `${BASE_URL}/product-categories`;
const IMAGE_BASE_URL = `${PRODUCT_URL}/image`;
const CART_URL = `${BASE_URL}/cart`;
const ORDER_URL = `${BASE_URL}/orders`;
const INVOICE_URL = `${BASE_URL}/invoices`; // ✅ Mới: dùng endpoint invoices

// =====================================================
// 🛍️ SẢN PHẨM
// =====================================================
export const getAllProducts = async () => {
  const res = await axios.get(PRODUCT_URL);
  if (Array.isArray(res.data)) return res.data;
  if (res.data.content) return res.data.content;
  return [];
};

export const getProductById = async (id) => {
  const res = await axios.get(`${PRODUCT_URL}/${id}`);
  return res.data;
};

export const updateProduct = async (id, formData, productData) => {
  const res = await axios.put(`${PRODUCT_URL}/${id}`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  if (productData?.price != null) {
    await axios.post(`${PRODUCT_URL}/${id}/price-history`, null, {
      params: { price: productData.price },
    });
  }
  return res.data;
};

export const deleteProduct = (id) => axios.delete(`${PRODUCT_URL}/${id}`);

export const createProduct = async (formData, productData) => {
  const res = await axios.post(PRODUCT_URL, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  if (productData?.price != null) {
    await axios.post(`${PRODUCT_URL}/${res.data.id}/price-history`, null, {
      params: { price: productData.price },
    });
  }
  return res.data;
};

export const getPriceHistory = async (id) => {
  const res = await axios.get(`${PRODUCT_URL}/${id}/price-history`);
  return res.data;
};

export const getCategories = async () => {
  const res = await axios.get(CATEGORY_URL);
  return res.data;
};

// =====================================================
// 🛒 GIỎ HÀNG
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

// =====================================================
// 🧾 HÓA ĐƠN (Invoice)
// =====================================================
export const getInvoiceById = async (orderId) => {
  if (!orderId) {
    console.warn("getInvoiceById được gọi nhưng không có orderId. Hủy yêu cầu.");
    return null;
  }
  const res = await axios.get(`${INVOICE_URL}/by-order/${orderId}`);
  return res.data; // JSON có items[].productName
};

// =====================================================
// 🖼️ HÌNH ẢNH
// =====================================================
export const getImage = (imageNameOrId) => {
  if (!imageNameOrId) return "/images/default-product.png";
  return `${IMAGE_BASE_URL}/${encodeURIComponent(imageNameOrId)}`;
};

// =====================================================
// 🛒 GIỎ HÀNG
// =====================================================
export const addToCart = async (userId, productId, quantity = 1) => {
  return axios.post(`${CART_URL}/add`, null, {
    params: { userId, productId, quantity, accountId: userId },
  });
};

export const getCartByUser = async (userId) => {
  const res = await axios.get(`${CART_URL}/${userId}`);
  return res.data;
};

export const updateCartQuantity = async (cartId, quantity) =>
  axios.put(`${CART_URL}/update/${cartId}?quantity=${quantity}`);

export const removeFromCart = async (cartId) =>
  axios.delete(`${CART_URL}/remove/${cartId}`);

export const getProductImage = (productId) => {
  if (!productId) return "/images/default-product.png";
  return `${PRODUCT_URL}/get-image/${productId}`;
};

// =====================================================
// 🛒 ĐẶT HÀNG
// =====================================================
export const placeOrder = async (order) => {
  const payload = { ...order, orderDate: new Date().toISOString() };
  const res = await axios.post(`${ORDER_URL}`, payload);
  return res.data;
};
export const payInvoice = async (data) => {
  const res = await axios.post(`${INVOICE_URL}/pay`, data);
  return res.data;
};
export const removeMultipleFromCart = async (cartIds) =>
  axios.delete(`${CART_URL}/remove-multiple`, { data: cartIds });

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
  payInvoice,
  removeMultipleFromCart,
};

export default shoppingService;
