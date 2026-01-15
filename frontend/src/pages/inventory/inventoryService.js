import axios from "axios";

// ==================== URL BASE ====================
const API_URL = "http://localhost:8080/api";
const INVENTORY_URL = `${API_URL}/inventory`;
const PRODUCTS_URL = `${API_URL}/products`;
const INVENTORY_ITEM_URL = `${API_URL}/inventory-items`;
const CUSTOMERS_URL = `${API_URL}/customers`;
const SUPPLIERS_URL = `${API_URL}/suppliers`;

// ==================== INVENTORY ====================
export const getInventoryByProductWarehouse = (productId, warehouse) =>
  axios.get(`${INVENTORY_URL}/${productId}/${warehouse}`).then(res => res.data);

export const getAllInventoryByProduct = (productId) =>
  axios.get(`${INVENTORY_URL}/all/${productId}`).then(res => res.data);

export const getAllInventory = (warehouse = "DEFAULT") =>
  axios.get(`${INVENTORY_URL}/all-with-products`, { params: { warehouse } })
       .then(res => res.data);

export const addInventory = (data) =>
  axios.post(`${INVENTORY_URL}/add`, data, { headers: { "Content-Type": "application/json" } })
       .then(res => res.data);

export const removeInventory = (data) =>
  axios.post(`${INVENTORY_URL}/remove`, data, { headers: { "Content-Type": "application/json" } })
       .then(res => res.data);

export const adjustInventory = (data) =>
  axios.post(`${INVENTORY_URL}/adjust`, data, { headers: { "Content-Type": "application/json" } })
       .then(res => res.data);

export const reserveInventory = (data) =>
  axios.post(`${INVENTORY_URL}/reserve`, data, { headers: { "Content-Type": "application/json" } })
       .then(res => res.data);

export const releaseInventory = (data) =>
  axios.post(`${INVENTORY_URL}/release`, data, { headers: { "Content-Type": "application/json" } })
       .then(res => res.data);

// ==================== PRODUCTS ====================
export const getAllProducts = () =>
  axios.get(PRODUCTS_URL).then(res => res.data);

// ==================== INVENTORY ITEMS ====================
export const getAllInventoryItems = () =>
  axios.get(INVENTORY_ITEM_URL).then(res => res.data);

export const getInventoryItemById = (id) =>
  axios.get(`${INVENTORY_ITEM_URL}/${id}`).then(res => res.data);

export const createInventoryItem = (data) =>
  axios.post(INVENTORY_ITEM_URL, data, { headers: { "Content-Type": "application/json" } })
       .then(res => res.data);

export const updateInventoryItem = (id, data) =>
  axios.put(`${INVENTORY_ITEM_URL}/${id}`, data, { headers: { "Content-Type": "application/json" } })
       .then(res => res.data);

export const deleteInventoryItem = (id) =>
  axios.delete(`${INVENTORY_ITEM_URL}/${id}`).then(res => res.data);

// ==================== CUSTOMERS ====================
export const getAllCustomers = () =>
  axios.get(CUSTOMERS_URL).then(res => res.data);

export const getCustomerById = (id) =>
  axios.get(`${CUSTOMERS_URL}/${id}`).then(res => res.data);

export const createCustomer = (data) =>
  axios.post(CUSTOMERS_URL, data, { headers: { "Content-Type": "application/json" } })
       .then(res => res.data);

export const updateCustomer = (id, data) =>
  axios.put(`${CUSTOMERS_URL}/${id}`, data, { headers: { "Content-Type": "application/json" } })
       .then(res => res.data);

export const deleteCustomer = (id) =>
  axios.delete(`${CUSTOMERS_URL}/${id}`).then(res => res.data);

// ==================== SUPPLIERS ====================
export const getAllSuppliers = () =>
  axios.get(SUPPLIERS_URL).then(res => res.data);

// ==================== IMPORT INVENTORY (NHẬP KHO) ====================
export const importInventory = async (data) => {
  // ===== LOG PAYLOAD =====
  console.log("===== IMPORT INVENTORY PAYLOAD =====");
  console.log(JSON.stringify(data, null, 2));
  console.log("====================================");

  return axios.post(`${INVENTORY_URL}/import`, data, { headers: { "Content-Type": "application/json" } })
              .then(res => res.data)
              .catch(err => {
                console.error("Import Inventory Error:", err.response?.data || err.message);
                throw err;
              });
};
// ==================== EXPORT INVENTORY (XUẤT KHO) ====================
export const exportInventory = async (data) => {
  console.log("===== EXPORT INVENTORY PAYLOAD =====");
  console.log(JSON.stringify(data, null, 2));
  console.log("===================================");

  return axios
    .post(`${INVENTORY_URL}/export`, data, {
      headers: { "Content-Type": "application/json" }
    })
    .then(res => res.data)
    .catch(err => {
      console.error("Export Inventory Error:", err.response?.data || err.message);
      throw err;
    });
    
};

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
  importInventory,
    exportInventory,

  // Products
  getAllProducts,

  // Suppliers
  getAllSuppliers,

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
