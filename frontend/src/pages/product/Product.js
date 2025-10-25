import React, { useEffect, useState } from 'react';
import { getAllProducts, deleteProduct } from './productService';
import ProductForm from './ProductForm';
import ProductList from './ProductList';
import ProductDetail from './ProductDetail';
import { Button } from 'primereact/button';
import './product.css';

const Product = () => {
  const [products, setProducts] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [showForm, setShowForm] = useState(false);

  const [showDetail, setShowDetail] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);

  // ✅ tải danh sách sản phẩm
  const loadProducts = () => {
    getAllProducts().then(res => {
      // đảm bảo mỗi sản phẩm có field imageUrls (nếu chưa có thì gán mảng rỗng)
      const productsWithImages = res.data.map(p => ({
        ...p,
        imageUrls: p.imageUrls || []
      }));
      setProducts(productsWithImages);
    });
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

  const handleDetail = (product) => {
    // ✅ truyền cả danh sách ảnh sang chi tiết
    setSelectedProduct(product);
    setShowDetail(true);
  };

  return (
    <div>
      <div className="flex justify-content-between align-items-center mb-3">
        <h1 className="m-0">Quản lý sản phẩm</h1>
        <Button
          label="Thêm sản phẩm mới"
          icon="pi pi-plus"
          severity="success"
          onClick={() => { setSelectedId(null); setShowForm(true); }}
        />
      </div>

      {/* ✅ Form thêm / sửa */}
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

      {/* ✅ Danh sách sản phẩm */}
      <ProductList
        products={products}
        onEdit={handleEdit}
        onDelete={handleDelete}
        onDetail={handleDetail}
      />

      {/* ✅ Chi tiết sản phẩm có gallery ảnh */}
      <ProductDetail
        visible={showDetail}
        onHide={() => setShowDetail(false)}
        product={selectedProduct}
      />
    </div>
  );
};

export default Product;
