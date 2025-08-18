import React, { useEffect, useState } from 'react';
import { getAllProducts, deleteProduct } from './productService';
import ProductForm from './ProductForm';
import ProductList from './ProductList';
import ProductDetail from './ProductDetail'; // ✅ import vào
import './product.css';

const Product = () => {
  const [products, setProducts] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [showForm, setShowForm] = useState(false);

  // ✅ thêm state cho chi tiết
  const [showDetail, setShowDetail] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);

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

  // ✅ thêm hàm mở chi tiết
  const handleDetail = (product) => {
    setSelectedProduct(product);
    setShowDetail(true);
  };

  return (
    <div style={{ padding: '40px' }}>
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

      {/* ✅ Truyền thêm onDetail cho ProductList */}
      <ProductList
        products={products}
        onEdit={handleEdit}
        onDelete={handleDelete}
        onDetail={handleDetail}
      />

      {/* ✅ Dialog chi tiết */}
      <ProductDetail
        visible={showDetail}
        onHide={() => setShowDetail(false)}
        product={selectedProduct}
      />
    </div>
  );
};

export default Product;
