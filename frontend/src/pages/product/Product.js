import React, { useEffect, useState } from "react";
import { getAllProducts, deleteProduct } from "./productService";
import ProductForm from "./ProductForm";
import ProductList from "./ProductList";
import ProductDetail from "./ProductDetail";
import { Button } from "primereact/button";
import "./product.css";

const Product = () => {
  // =================================================================
  //  STATE QUẢN LÝ SẢN PHẨM
  // =================================================================
  const [products, setProducts] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [showForm, setShowForm] = useState(false);

  // State cho chi tiết sản phẩm
  const [showDetail, setShowDetail] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);

  // =================================================================
  //  LẤY DANH SÁCH SẢN PHẨM TỪ BACKEND
  // =================================================================
  const loadProducts = () => {
    getAllProducts()
      .then((res) => {
        const productsWithImages = res.data.map((p) => ({
          ...p,
          imageUrls: p.imageUrls || [], // đảm bảo luôn có imageUrls
        }));
        setProducts(productsWithImages);
      })
      .catch((err) => console.error("❌ Lỗi tải sản phẩm:", err));
  };

  useEffect(() => {
    loadProducts();
  }, []);

  // =================================================================
  //  XỬ LÝ SỰ KIỆN CRUD
  // =================================================================

  // Sửa sản phẩm
  const handleEdit = (id) => {
    setSelectedId(id);
    setShowForm(true);
  };

  // Xoá sản phẩm
  const handleDelete = (id) => {
    if (window.confirm("Bạn có chắc muốn xoá sản phẩm này?")) {
      deleteProduct(id)
        .then(() => {
          loadProducts();
        })
        .catch((err) => console.error("❌ Lỗi xoá sản phẩm:", err));
    }
  };

  // Lưu thành công -> load lại danh sách
  const handleSuccess = () => {
    setShowForm(false);
    setSelectedId(null);
    loadProducts();
  };

  // Mở chi tiết sản phẩm
  const handleDetail = (product) => {
    setSelectedProduct(product);
    setShowDetail(true);
  };

  // =================================================================
  //  GIAO DIỆN CHÍNH
  // =================================================================
  return (
    <div className="product-page">
      {/* Tiêu đề + nút thêm */}
      <div className="flex justify-content-between align-items-center mb-3">
        <h1 className="m-0">Quản lý sản phẩm</h1>
        <Button
          label="Thêm sản phẩm mới"
          icon="pi pi-plus"
          severity="success"
          onClick={() => {
            setSelectedId(null);
            setShowForm(true);
          }}
        />
      </div>

      {/*  Form thêm/sửa sản phẩm */}
{showForm && (
  <div
    className="dialog-overlay"
    onClick={() => setShowForm(false)} // 🟢 click ra ngoài => đóng
  >
    <div
      className="dialog-content"
      onClick={(e) => e.stopPropagation()} // ❌ chặn click trong form
    >
      <ProductForm
        selectedId={selectedId}
        onSuccess={handleSuccess}
        onCancel={() => setShowForm(false)}
      />
    </div>
  </div>
)}

      {/*  Danh sách sản phẩm */}
      <ProductList
        products={products}
        onEdit={handleEdit}
        onDelete={handleDelete}
        onDetail={handleDetail}
      />

      {/*  Chi tiết sản phẩm */}
      <ProductDetail
        visible={showDetail}
        onHide={() => setShowDetail(false)}
        product={selectedProduct}
      />
    </div>
  );
};

export default Product;