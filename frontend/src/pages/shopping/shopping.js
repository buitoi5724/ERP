import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./shopping.css";
import { getAllProducts, addToCart, getImage } from "./shoppingService";
function ProductPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [notification, setNotification] = useState("");

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

  const handleAddToCart = (productId, e) => {
    e.stopPropagation();
    addToCart(userId, productId, 1)
      .then(() => {
        setNotification("✅ Đã thêm sản phẩm vào giỏ hàng!");
        setTimeout(() => setNotification(""), 3000);
      })
      .catch((err) => {
        console.error("Lỗi khi thêm sản phẩm:", err);
        setNotification("❌ Thêm sản phẩm thất bại!");
        setTimeout(() => setNotification(""), 3000);
      });
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
                onClick={(e) => handleAddToCart(product.id, e)} // ✅ sửa đúng
              >
                + Thêm vào giỏ
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default ProductPage;
