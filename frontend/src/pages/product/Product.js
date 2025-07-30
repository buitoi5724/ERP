import React, { useEffect, useState } from 'react';
import { getAllProducts, deleteProduct } from './productService';
import ProductForm from './ProductForm';
import ProductList from './ProductList';
import './product.css'; // để dùng CSS dialog

const Product = () => {
  const [products, setProducts] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [showForm, setShowForm] = useState(false);

  const loadProducts = () => {
    getAllProducts().then(res => setProducts(res.data));
  };

  useEffect(() => {
    loadProducts();
  }, []);

  const handleEdit = (id) => {
    setSelectedId(id);
    setShowForm(true);
  };

  const handleDelete = (id) => {
    if (window.confirm("Bạn có chắc muốn xoá sản phẩm này?")) {
      deleteProduct(id).then(loadProducts);
    }
  };

  const handleSuccess = () => {
    setShowForm(false);
    setSelectedId(null);
    loadProducts();
  };

  return (
    <div style={{ padding: '30px' }}>
      <h1>Quản lý sản phẩm</h1>
      <button onClick={() => { setSelectedId(null); setShowForm(true); }}>
        Thêm sản phẩm mới
      </button>

      {showForm && (
        <div className="dialog-overlay">
          <div className="dialog-content">
            <ProductForm
              selectedId={selectedId}
              onSuccess={handleSuccess}
              onCancel={() => setShowForm(false)}
            />
          </div>
        </div>
      )}

      <ProductList products={products} onEdit={handleEdit} onDelete={handleDelete} />
    </div>
  );
};

export default Product;
