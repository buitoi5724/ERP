import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./shopping.css";
import { Dialog } from "primereact/dialog";
import {Button } from "primereact/button";

import { getAllProducts, addToCart, getImage } from "./shoppingService";
function ProductPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [notification, setNotification] = useState("");
  const [visibleDialog , setVisibleDialog ] = useState(false);
  const [selectedProduct , setSelectedProduct ]= useState(null);
  const [ selectedColor , setSelectedColor ]= useState("");
  const [selectedSize, setSelectedSize] = useState("");
const [quantity ,setQuantity ]= useState(1);

const [selectedImage, setSelectedImage] = useState("");
  const navigate = useNavigate();
  const userId = 1;

  useEffect(() => {
    getAllProducts()
      .then(setProducts)
      .catch((err) => {
        console.error("Lỗi khi tải sản phẩm:", err);
        setError("Không thể tải danh sách sản phẩm. Vui lòng thử lại sau.");
      })
      .finally(() => setLoading(false));
  }, []);

  const handleAddToCart = (product, e) => {
    e.stopPropagation();
   setSelectedProduct(product);
setVisibleDialog(true);

  };
 const handleConfirmAdd = () => {
    if (!selectedProduct) return;
    if (!selectedSize || !selectedColor) {
      alert("Vui lòng chọn kích thước và màu sắc!");
      return;
    }

    addToCart(userId, selectedProduct.id, quantity)
      .then(() => {
        setNotification("✅ Đã thêm sản phẩm vào giỏ hàng!");
        setTimeout(() => setNotification(""), 3000);
      })
      .catch((err) => {
        console.error("Lỗi khi thêm sản phẩm:", err);
        setNotification("❌ Thêm sản phẩm thất bại!");
        setTimeout(() => setNotification(""), 3000);
      });

    setVisibleDialog(false);
    setQuantity(1);
    setSelectedSize("");
    setSelectedColor("");
  };
const getImageSrc = (product) =>
  product?.image ? getImage(product.image) : "/images/default-product.png";

  if (error)
    return <p style={{ color: "red", textAlign: "center" }}>{error}</p>;
  if (loading) return <p>Đang tải sản phẩm...</p>;

  return (
    <div className="product-page">
      {notification && <div className="notification">{notification}</div>}

      <div className="header">
        <h2>Danh Sách Sản Phẩm</h2>
        <button className="btn-cart" onClick={() => navigate("/cart")}>
          Giỏ Hàng🛒
        </button>
      </div>

      <div className="product-row">
        {products.map((product) => (
          <div
            key={product.id}
            className="product-card"
            onClick={() => navigate(`/shopping/${product.id}`)}
            style={{ cursor: "pointer" }}
          >
            <div className="product-image-container">
              <img
                src={getImageSrc(product)}
                alt={product.name}
                onError={(e) => (e.target.src = "/images/default-product.png")}
              />
              {product.discount && (
                <div className="discount-sticker">
                  <span className="percent">{product.discount}%</span>
                  <span className="label">GIẢM</span>
                </div>
              )}
            </div>

            <div className="product-info">
              <div className="product-name">{product.name}</div>
              <div className="product-bottom-info">
                <span className="product-price">
                  {product.price?.toLocaleString("vi-VN")}₫
                </span>
                <span className="product-sales-info">
                  Đã bán {product.sold || 0}
                </span>
                
              </div>

              <button
                className="add-to-cart-btn"
        onClick={(e) => handleAddToCart(product, e)}
              >
                + Thêm vào giỏ
              </button>
             
            </div>
          </div>
        ))}
      </div>
       {/* 👉 Chỉ có một dialog duy nhất ở cuối */}
      <Dialog
  header={null}
  visible={visibleDialog}
  onHide={() => setVisibleDialog(false)}
  className="product-dialog"
>
  {selectedProduct && (
    <div className="dialog-container">
      {/* Cột trái: ảnh sản phẩm */}
     <div className="dialog-left">
  {/* Ảnh chính */}
  <div className="main-image">
    <img
      src={getImage(selectedImage || selectedProduct.imageUrls?.[0])}
      alt={selectedProduct.name}
    />
  </div>

  {/* Danh sách ảnh nhỏ */}
  <div className="thumbnail-list">
  {(selectedProduct.imageUrls || []).map((img, index) => (
    <img
      key={index}
      src={getImage(img)}
      alt={`thumb-${index}`}
      className={`thumbnail ${selectedImage === img ? "active" : ""}`}
      onMouseEnter={() => setSelectedImage(img)}  // ✅ ĐỔI onClick → onMouseEnter
    />
  ))}
</div>

</div>
      {/* Cột phải: thông tin chi tiết */}
      <div className="dialog-right">
        <h3 className="product-title">{selectedProduct.name}</h3>
        <p className="product-meta">
          Mã sản phẩm: <strong>{selectedProduct.code || "Đang cập nhật"}</strong>
          {" · "}
          Tình trạng: <span className="status">Còn hàng</span>
        </p>

        <div className="price-section">
          <span className="price">
            {selectedProduct.price?.toLocaleString("vi-VN")}₫
          </span>
          {selectedProduct.oldPrice && (
            <span className="old-price">
              {selectedProduct.oldPrice?.toLocaleString("vi-VN")}₫
            </span>
          )}
          {selectedProduct.discount && (
            <span className="discount">-{selectedProduct.discount}%</span>
          )}
        </div>

        {/* Chọn kích thước */}
        <div className="option-group">
          <label>Kích thước:</label>
          <div className="option-buttons">
            {["S", "M", "L", "XL"].map((size) => (
              <button
                key={size}
                className={`option-btn ${
                  selectedSize === size ? "active" : ""
                }`}
                onClick={() => setSelectedSize(size)}
              >
                {size}
              </button>
            ))}
          </div>
        </div>

        {/* Chọn màu */}
        <div className="option-group">
          <label>Màu sắc:</label>
          <div className="option-buttons">
            {["Đen", "Trắng", "Nâu"].map((color) => (
              <button
                key={color}
                className={`option-btn ${
                  selectedColor === color ? "active" : ""
                }`}
                onClick={() => setSelectedColor(color)}
              >
                {color}
              </button>
            ))}
          </div>
        </div>

        {/* Số lượng */}
     <div className="option-group quantity-group">
  <label>Số lượng:</label>
  <div className="quantity-controls">
    <button onClick={() => setQuantity(Math.max(1, quantity - 1))}>-</button>

    <input
      type="number"
      min="1"
      value={quantity}
      onChange={(e) => {
        const value = Math.max(1, Number(e.target.value) || 1);
        setQuantity(value);
      }}
      className="quantity-input"
    />

    <button onClick={() => setQuantity(quantity + 1)}>+</button>
  </div>
</div>

        {/* Nút thêm vào giỏ */}
        <button className="btn-add-cart" onClick={handleConfirmAdd}>
          THÊM VÀO GIỎ
 
        </button>

        {/* Nút chia sẻ */}
        <div className="share-section">
          <span>Chia sẻ:</span>
          <div className="social-icons">
            <i className="pi pi-facebook"></i>
            <i className="pi pi-twitter"></i>
            <i className="pi pi-send"></i>
          </div>
        </div>
      </div>
    </div>
  )}
</Dialog>

    </div>
  );
}



export default ProductPage;
