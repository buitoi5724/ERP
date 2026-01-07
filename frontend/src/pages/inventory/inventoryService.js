import axios from "axios";

// ==================== URL BASE ====================
const API_URL = "http://localhost:8080/api";
const INVENTORY_URL = `${API_URL}/inventory`;
const PRODUCTS_URL = `${API_URL}/products`;
const INVENTORY_ITEM_URL = `${API_URL}/inventory-items`;
const CUSTOMERS_URL = `${API_URL}/customers`;

// ==================== INVENTORY ====================
export const getInventoryByProductWarehouse = (productId, warehouse) =>
  axios.get(`${INVENTORY_URL}/${productId}/${warehouse}`).then(res => res.data);

export const getAllInventoryByProduct = (productId) =>
  axios.get(`${INVENTORY_URL}/all/${productId}`).then(res => res.data);

export const getAllInventory = (warehouse = "DEFAULT") =>
  axios.get(`${INVENTORY_URL}/all-with-products`, { params: { warehouse } })
       .then(res => res.data);

export const addInventory = (data) =>
  axios.post(`${INVENTORY_URL}/add`, data).then(res => res.data);

export const removeInventory = (data) =>
  axios.post(`${INVENTORY_URL}/remove`, data).then(res => res.data);

export const adjustInventory = (data) =>
  axios.post(`${INVENTORY_URL}/adjust`, data).then(res => res.data);

export const reserveInventory = (data) =>
  axios.post(`${INVENTORY_URL}/reserve`, data).then(res => res.data);

export const releaseInventory = (data) =>
  axios.post(`${INVENTORY_URL}/release`, data).then(res => res.data);

// ==================== PRODUCTS ====================
export const getAllProducts = () =>
  axios.get(PRODUCTS_URL).then(res => res.data);

// ==================== INVENTORY ITEMS ====================
export const getAllInventoryItems = () =>
  axios.get(INVENTORY_ITEM_URL).then(res => res.data);

export const getInventoryItemById = (id) =>
  axios.get(`${INVENTORY_ITEM_URL}/${id}`).then(res => res.data);

export const createInventoryItem = (data) =>
  axios.post(INVENTORY_ITEM_URL, data).then(res => res.data);

export const updateInventoryItem = (id, data) =>
  axios.put(`${INVENTORY_ITEM_URL}/${id}`, data).then(res => res.data);

export const deleteInventoryItem = (id) =>
  axios.delete(`${INVENTORY_ITEM_URL}/${id}`).then(res => res.data);

// ==================== CUSTOMERS ====================
export const getAllCustomers = () =>
  axios.get(CUSTOMERS_URL).then(res => res.data);

export const getCustomerById = (id) =>
  axios.get(`${CUSTOMERS_URL}/${id}`).then(res => res.data);

export const createCustomer = (data) =>
  axios.post(CUSTOMERS_URL, data).then(res => res.data);

export const updateCustomer = (id, data) =>
  axios.put(`${CUSTOMERS_URL}/${id}`, data).then(res => res.data);

export const deleteCustomer = (id) =>
  axios.delete(`${CUSTOMERS_URL}/${id}`).then(res => res.data);

// ==================== DEFAULT EXPORT ====================
const InventoryService = {
  // Inventory
  getInventoryByProductWarehouse,
  getAllInventoryByProduct,
  getAllInventory,
  addInventory,
  removeInventory,
  adjustInventory,
  reserveInventory,
  releaseInventory,

  // Products
  getAllProducts,

  // Inventory Items
  getAllInventoryItems,
  getInventoryItemById,
  createInventoryItem,
  updateInventoryItem,
  deleteInventoryItem,

  // Customers
  getAllCustomers,
  getCustomerById,
  createCustomer,
  updateCustomer,
  deleteCustomer,
};

export default InventoryService;
