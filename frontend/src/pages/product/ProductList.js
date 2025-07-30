import React, { useState } from 'react';
import './productList.css'; // Import CSS

const ProductList = ({ products, onEdit, onDelete }) => {
  const [searchTerm, setSearchTerm] = useState('');

  const filteredProducts = products.filter(p =>
    p.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div>
      {/* Ô tìm kiếm */}
      <div className="product-search">
        <label><strong>Tìm kiếm theo tên:</strong> </label>
        <input
          type="text"
          placeholder="Nhập tên sản phẩm..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      {/* Danh sách sản phẩm */}
      {filteredProducts.map(p => (
        <div key={p.id} className="product-card">
          <img
            src={p.image}
            alt={p.name}
            className="product-image"
          />
          <div className="product-info">
            <h3>{p.name}</h3>
            <p><strong>Giá:</strong> {Number(p.price).toLocaleString()} đ</p>
            <p><strong>Loại:</strong> {p.category}</p>
            <p><strong>Mô tả:</strong> {p.description}</p>
            <button onClick={() => onEdit(p.id)}>Sửa</button>
            <button onClick={() => onDelete(p.id)}>Xoá</button>
          </div>
        </div>
      ))}

      {filteredProducts.length === 0 && <p>Không tìm thấy sản phẩm phù hợp.</p>}
    </div>
  );
};

export default ProductList;
