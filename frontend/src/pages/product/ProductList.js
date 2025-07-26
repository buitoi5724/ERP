import React from 'react';

const ProductList = ({ products, onEdit, onDelete }) => {
  return (
    <div>
      {products.map(p => (
        <div key={p.id} style={{
          display: 'flex',
          gap: '15px',
          border: '1px solid #ccc',
          borderRadius: '10px',
          padding: '15px',
          marginBottom: '15px',
          alignItems: 'center'
        }}>
          <img src={p.image} alt={p.name} width="120" height="120" style={{ objectFit: 'cover', borderRadius: '10px' }} />
          <div>
            <h3>{p.name}</h3>
            <p><strong>Giá:</strong> {Number(p.price).toLocaleString()} đ</p>
            <p><strong>Loại:</strong> {p.category}</p>
            <p><strong>Mô tả:</strong> {p.description}</p>
            <button onClick={() => onEdit(p.id)}>Sửa</button>
            <button onClick={() => onDelete(p.id)} style={{ marginLeft: 10 }}>Xoá</button>
          </div>
        </div>
      ))}
    </div>
  );
};

export default ProductList;
