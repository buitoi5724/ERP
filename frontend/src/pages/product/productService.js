import axios from "axios";

const API_URL = "http://localhost:8080/api/products";

export const getAllProducts = () => axios.get(API_URL);

//export const getProductById = (id) => axios.get(`${API_URL}/${id}`);

export const updateProduct = (id, product) => axios.put(`${API_URL}/${id}`, product);

export const deleteProduct = (id) => axios.delete(`${API_URL}/${id}`);

export const getImage = (product) => axios.get(`${API_URL}/get-image/${product.id}`, {responseType: 'arraybuffer'});

export const createProduct = async (product) => {
  try {
    const response = await axios.post(API_URL, product);
    console.log(response);
    return response.data; // Trả về object product từ server
  } catch (error) {
    console.error('Lỗi tạo sản phẩm:', error);
    throw error;
  }
};

export const getProductById = async (id) => {
  try {
    const response = await axios.get(`${API_URL}/${id}`);
    console.log(response);
    return response.data; // Trả về object product từ server
  } catch (error) {
    console.error('Lỗi tạo sản phẩm:', error);
    throw error;
  }
};

