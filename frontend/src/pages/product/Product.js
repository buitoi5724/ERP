import React, { useEffect, useState } from "react";
import { getAllProducts, deleteProduct } from "./productService";
import ProductForm from "./ProductForm";
import ProductList from "./ProductList";
import ProductDetail from "./ProductDetail";
import "./product.css";

const Product = () => {
  const [products, setProducts] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [showForm, setShowForm] = useState(false);

  const [showDetail, setShowDetail] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);

  const loadProducts = () => {
    getAllProducts()
      .then((res) => {
        setProducts(
          res.data.map((p) => ({
            ...p,
            imageUrls: p.imageUrls || [],
          }))
        );
      })
      .catch((err) => console.error("❌ Lỗi tải sản phẩm:", err));
  };

  useEffect(() => {
    loadProducts();
  }, []);

  const handleAdd = () => {
    setSelectedId(null);
    setShowForm(true);
  };

  const handleEdit = (id) => {
    setSelectedId(id);
    setShowForm(true);
  };

  const handleDelete = (id) => {
    if (!window.confirm("Bạn có chắc muốn xoá sản phẩm này?")) return;
    deleteProduct(id).then(loadProducts);
  };

  const handleSuccess = () => {
    setShowForm(false);
    setSelectedId(null);
    loadProducts();
  };

  const handleDetail = (product) => {
    setSelectedProduct(product);
    setShowDetail(true);
  };

  return (
    <div className="product-page">
      <h1 className="mb-3">Quản lý sản phẩm</h1>

      {/* 👉 NÚT ADD NẰM TRONG ProductList */}
      <ProductList
        products={products}
        onAdd={handleAdd}
        onEdit={handleEdit}
        onDelete={handleDelete}
        onDetail={handleDetail}
      />

      {showForm && (
        <div className="dialog-overlay" onClick={() => setShowForm(false)}>
          <div className="dialog-content" onClick={(e) => e.stopPropagation()}>
            <ProductForm
              selectedId={selectedId}
              onSuccess={handleSuccess}
              onCancel={() => setShowForm(false)}
            />
          </div>
        </div>
      )}

      <ProductDetail
        visible={showDetail}
        onHide={() => setShowDetail(false)}
        product={selectedProduct}
      />
    </div>
  );
};

export default Product;
