import React, { useState, useEffect } from 'react';
import { getProductById, updateProduct } from './productService';

const ProductForm = ({ selectedId, onSuccess, onCancel }) => {
  const [product, setProduct] = useState({
    name: '',
    price: '',
    image: '',
    category: '',
    description: ''
  });

  const [priceError, setPriceError] = useState('');
  const [file, setFile] = useState(null); // để chứa file ảnh

  useEffect(() => {
    if (selectedId) {
      getProductById(selectedId).then(res => setProduct(res.data));
    }
  }, [selectedId]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === 'price') {
      if (/^[a-zA-Z]/.test(value)) {
        setPriceError('Bạn nhập giá bằng số đầu tiên');
      } else {
        setPriceError('');
      }
    }

    setProduct({ ...product, [name]: value });
  };

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (/^[a-zA-Z]/.test(product.price)) {
      setPriceError('Bạn nhập giá bằng số đầu tiên');
      return;
    }

    if (product.price === '' || isNaN(Number(product.price))) {
      setPriceError('Giá phải là số hợp lệ');
      return;
    }

    const finalProduct = { ...product, price: Number(product.price) };

    const formData = new FormData();
    formData.append('product', new Blob([JSON.stringify(finalProduct)], { type: 'application/json' }));
    if (file) {
      formData.append('image', file);
    } else {
      // Nếu không chọn ảnh thì gửi ảnh rỗng hoặc ảnh cũ (tùy backend xử lý)
    }

    const response = await fetch('http://localhost:8080/api/products', {
      method: 'POST',
      body: formData
    });

    if (response.ok) {
      onSuccess();
      setProduct({ name: '', price: '', image: '', category: '', description: '' });
      setFile(null);
      setPriceError('');
    } else {
      alert('Lỗi khi gửi dữ liệu!');
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginTop: 20 }}>
      <input name="name" placeholder="Tên sản phẩm" value={product.name} onChange={handleChange} required /><br />

      <input name="price" placeholder="Giá sản phẩm" value={product.price} onChange={handleChange} required /><br />
      {priceError && <span style={{ color: 'red' }}>{priceError}</span>}<br />

      <input type="file" accept="image/*" onChange={handleFileChange} /><br />
      {product.image && <img src={`http://localhost:8080${product.image}`} alt="preview" width="100" />}<br />

      <input name="category" placeholder="Loại sản phẩm" value={product.category} onChange={handleChange} /><br />
      <textarea name="description" placeholder="Miêu tả" value={product.description} onChange={handleChange} /><br />

      <button type="submit">{selectedId ? 'Cập nhật' : 'Thêm mới'}</button>
      <button type="button" onClick={onCancel} style={{ marginLeft: 10 }}>Huỷ</button>
    </form>
  );
};

export default ProductForm;
