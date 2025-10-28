import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./shopping.css";
import { getShoppingProductById, addToCart } from "../shopping/shoppingService";

function ShoppingDetail() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [selectedImage, setSelectedImage] = useState("");
  const [quantity, setQuantity] = useState(1);
  const [notification, setNotification] = useState("");
  const navigate = useNavigate();

  const userId = 1; // Demo userId

  useEffect(() => {
    getShoppingProductById(id)
      .then((data) => {
        setProduct(data);
        setSelectedImage(data.fullImageUrls?.[0]);
      })
      .catch((err) => {
        console.error("❌ Lỗi khi tải chi tiết sản phẩm:", err);
      });
  }, [id]);

  const handleAddToCart = async () => {
    if (!product) return;
    try {
      await addToCart(userId, product.id, quantity);
      setNotification("✅ Đã thêm vào giỏ hàng!");
      setTimeout(() => setNotification(""), 2000);
    } catch (error) {
      console.error("❌ Lỗi khi thêm vào giỏ:", error);
      setNotification("❌ Thêm vào giỏ thất bại!");
      setTimeout(() => setNotification(""), 2000);
    }
  };

  const handleBuyNow = async () => {
    await handleAddToCart();
    navigate("/cart");
  };

  const handleViewCart = () => navigate("/cart");

  if (!product) return <p className="loading">Đang tải chi tiết sản phẩm...</p>;

  return (
    <div className="shopping-detail">
      <button className="back-btn" onClick={() => navigate(-1)}>
        ⬅ Quay lại
      </button>

      {notification && <div className="notification">{notification}</div>}

      <div className="detail-container">
        <div className="image-section">
          <img
            src={selectedImage}
            alt={product.name}
            className="main-image"
            onError={(e) => (e.target.src = "/images/default-product.png")}
          />

          <div className="thumbnails">
            {product.fullImageUrls?.map((imgUrl, index) => (
              <img
                key={index}
                src={imgUrl}
                alt={`Ảnh ${index + 1}`}
                className={`thumbnail ${selectedImage === imgUrl ? "active" : ""}`}
                onMouseEnter={() => setSelectedImage(imgUrl)}
                onError={(e) => (e.target.src = "/images/default-product.png")}
              />
            ))}
          </div>
        </div>

        <div className="info-section">
          <h2 className="product-name">{product.name}</h2>
          <p className="price">
            <strong>Giá:</strong>{" "}
            <span className="new">
              {product.price
                ? product.price.toLocaleString("vi-VN") + "₫"
                : "Chưa có giá"}
            </span>
          </p>

          <div className="quantity">
            <button onClick={() => setQuantity((q) => Math.max(1, q - 1))}>-</button>
            <span>{quantity}</span>
            <button onClick={() => setQuantity((q) => q + 1)}>+</button>
          </div>

          <div className="actions">
            <button className="add-cart" onClick={handleAddToCart}>
              🛒 Thêm vào giỏ hàng
            </button>
            <button className="buy-now" onClick={handleBuyNow}>
              💸 Mua ngay
            </button>
            <button className="view-cart" onClick={handleViewCart}>
              🧺 Xem giỏ hàng
            </button>
          </div>
        </div>
      </div>

      <div className="product-detail-info">
        <h3>🔍 Chi tiết sản phẩm</h3>
        <div
          className="detail-content"
          dangerouslySetInnerHTML={{
            __html: product.description || "<i>Chưa có nội dung mô tả</i>",
          }}
        ></div>
      </div>
    </div>
  );
}

export default ShoppingDetail;
