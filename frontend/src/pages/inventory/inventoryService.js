import axios from "axios";

const API_URL = "http://localhost:8080/api/inventory";
const PRODUCTS_URL = "http://localhost:8080/api/products";

// ==================== INVENTORY ====================
export const getInventoryByProductWarehouse = (productId, warehouse) =>
  axios.get(`${API_URL}/${productId}/${warehouse}`).then(res => res.data);

export const getAllInventoryByProduct = (productId) =>
  axios.get(`${API_URL}/all/${productId}`).then(res => res.data);

// Lấy tất cả sản phẩm kèm inventory
export const getAllInventory = (warehouse = "DEFAULT") =>
  axios.get(`${API_URL}/all-with-products`, { params: { warehouse } })
       .then(res => res.data);

// Thêm tồn kho
export const addInventory = (data) =>
  axios.post(`${API_URL}/add`, data).then(res => res.data);

// Giảm tồn kho
export const removeInventory = (data) =>
  axios.post(`${API_URL}/remove`, data).then(res => res.data);

// Điều chỉnh tồn kho
export const adjustInventory = (data) =>
  axios.post(`${API_URL}/adjust`, data).then(res => res.data);

// Đặt hàng / giữ kho
export const reserveInventory = (data) =>
  axios.post(`${API_URL}/reserve`, data).then(res => res.data);

// Hủy giữ kho
export const releaseInventory = (data) =>
  axios.post(`${API_URL}/release`, data).then(res => res.data);

// ==================== PRODUCTS ====================
export const getAllProducts = () =>
  axios.get(PRODUCTS_URL).then(res => res.data);

// ==================== DEFAULT EXPORT ====================
const InventoryService = {
  getInventoryByProductWarehouse,
  getAllInventoryByProduct,
  getAllInventory,
  addInventory,
  removeInventory,
  adjustInventory,
  reserveInventory,
  releaseInventory,
  getAllProducts,
};

export default InventoryService;
