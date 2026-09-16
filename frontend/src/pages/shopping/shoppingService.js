import api from "../../services/api";

// ================== CẤU HÌNH URL ==================
const BASE_URL = "http://localhost:8080/api";

const PRODUCT_URL = `${BASE_URL}/products`;
const CATEGORY_URL = `${BASE_URL}/product-categories`;
const IMAGE_BASE_URL = `${PRODUCT_URL}/image`;
const CART_URL = `${BASE_URL}/cart`;
const ORDER_URL = `${BASE_URL}/orders`;
const INVOICE_URL = `${BASE_URL}/invoices`;

// =====================================================
// 🛍️ SẢN PHẨM
// =====================================================

export const getAllProducts = async () => {
  const res = await api.get(PRODUCT_URL);

  if (Array.isArray(res.data)) {
    return res.data;
  }

  if (res.data?.content) {
    return res.data.content;
  }

  return [];
};


export const getProductById = async (id) => {
  const res = await api.get(`${PRODUCT_URL}/${id}`);
  return res.data;
};


export const updateProduct = async (id, formData, productData) => {
  const res = await api.put(`${PRODUCT_URL}/${id}`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

  if (productData?.price != null) {
    await api.post(
      `${PRODUCT_URL}/${id}/price-history`,
      null,
      {
        params: {
          price: productData.price,
        },
      }
    );
  }

  return res.data;
};


export const deleteProduct = async (id) => {
  return api.delete(`${PRODUCT_URL}/${id}`);
};


export const createProduct = async (formData, productData) => {
  const res = await api.post(PRODUCT_URL, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

  if (productData?.price != null) {
    await api.post(
      `${PRODUCT_URL}/${res.data.id}/price-history`,
      null,
      {
        params: {
          price: productData.price,
        },
      }
    );
  }

  return res.data;
};


export const getPriceHistory = async (id) => {
  const res = await api.get(
    `${PRODUCT_URL}/${id}/price-history`
  );

  return res.data;
};


export const getCategories = async () => {
  const res = await api.get(CATEGORY_URL);
  return res.data;
};


// =====================================================
// 🛍️ CHI TIẾT SẢN PHẨM
// =====================================================

export const getShoppingProductById = async (id) => {
  const res = await api.get(`${PRODUCT_URL}/${id}`);

  const productData = res.data;

  const galleries =
    productData.imageUrls?.map(
      (img) =>
        `${IMAGE_BASE_URL}/${encodeURIComponent(img)}`
    ) || [];

  const mainImage = productData.image
    ? `${IMAGE_BASE_URL}/${encodeURIComponent(
        productData.image
      )}`
    : "https://via.placeholder.com/400?text=No+Image";

  const allImages = [
    mainImage,
    ...galleries,
  ].filter(
    (v, i, arr) => arr.indexOf(v) === i
  );

  return {
    ...productData,
    fullImageUrls: allImages,
  };
};


// =====================================================
// 🧾 HÓA ĐƠN
// =====================================================

export const getInvoiceById = async (orderId) => {

  if (!orderId) {
    console.warn(
      "getInvoiceById được gọi nhưng không có orderId."
    );

    return null;
  }

  const res = await api.get(
    `${INVOICE_URL}/by-order/${orderId}`
  );

  return res.data;
};


// =====================================================
// 🖼️ HÌNH ẢNH
// =====================================================

export const getImage = (imageNameOrId) => {

  if (!imageNameOrId) {
    return "/images/default-product.png";
  }

  return `${IMAGE_BASE_URL}/${encodeURIComponent(
    imageNameOrId
  )}`;
};


// =====================================================
// 🛒 GIỎ HÀNG
// =====================================================

export const addToCart = async (
  userId,
  productId,
  quantity = 1
) => {

  const res = await api.post(
    `${CART_URL}/add`,
    null,
    {
      params: {
        userId,
        productId,
        quantity,
        accountId: userId,
      },
    }
  );

  return res.data;
};


export const getCartByUser = async (userId) => {

  const res = await api.get(
    `${CART_URL}/${userId}`
  );

  return res.data;
};


export const updateCartQuantity = async (
  cartId,
  quantity
) => {

  return api.put(
    `${CART_URL}/update/${cartId}`,
    null,
    {
      params: {
        quantity,
      },
    }
  );
};


export const removeFromCart = async (cartId) => {

  return api.delete(
    `${CART_URL}/remove/${cartId}`
  );
};


export const removeMultipleFromCart = async (
  cartIds
) => {

  return api.delete(
    `${CART_URL}/remove-multiple`,
    {
      data: cartIds,
    }
  );
};


export const clearCartByUser = async (userId) => {

  return api.delete(
    `${CART_URL}/clear/${userId}`
  );
};


export const getProductImage = (productId) => {

  if (!productId) {
    return "/images/default-product.png";
  }

  return `${PRODUCT_URL}/get-image/${productId}`;
};


// =====================================================
// 📦 ĐƠN HÀNG
// =====================================================

export const getOrderById = async (orderId) => {

  const res = await api.get(
    `${ORDER_URL}/${orderId}`
  );

  return res.data;
};


// =====================================================
// 🛒 ĐẶT HÀNG
// =====================================================

export const placeOrder = async ({
  customerId,
  paymentMethod,
  items,
}) => {

  if (!customerId) {
    throw new Error(
      "customerId là bắt buộc"
    );
  }

  const payload = {
    customerId,
    paymentMethod:
      paymentMethod || "cash",
    items,
  };

  const res = await api.post(
    ORDER_URL,
    payload
  );

  return res.data;
};


// =====================================================
// 💰 THANH TOÁN HÓA ĐƠN
// =====================================================

export const payInvoice = async (data) => {

  const res = await api.post(
    `${INVOICE_URL}/pay`,
    data
  );

  return res.data;
};


// =====================================================
// ✅ CONFIRM ORDER
// =====================================================

export const confirmOrder = async (orderId) => {

  return api.post(
    `${ORDER_URL}/${orderId}/confirm`
  );
};


// =====================================================
// 📤 EXPORT DEFAULT
// =====================================================

const shoppingService = {

  // Product
  getAllProducts,
  getProductById,
  updateProduct,
  deleteProduct,
  createProduct,
  getPriceHistory,
  getCategories,

  // Shopping
  getShoppingProductById,
  getImage,
  getProductImage,

  // Cart
  addToCart,
  getCartByUser,
  updateCartQuantity,
  removeFromCart,
  removeMultipleFromCart,
  clearCartByUser,

  // Order
  getOrderById,
  placeOrder,
  confirmOrder,

  // Invoice
  getInvoiceById,
  payInvoice,
};

export default shoppingService;