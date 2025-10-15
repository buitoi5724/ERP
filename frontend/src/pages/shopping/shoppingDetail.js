import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./shopping.css";

function ShoppingDetail() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [selectedImage, setSelectedImage] = useState("");
  const [selectedOption, setSelectedOption] = useState("");
  const [quantity, setQuantity] = useState(1);
  const [notification, setNotification] = useState("");
  const navigate = useNavigate();
  const userId = 1; // tạm thời fix userId, có thể lấy từ context sau

  // 🔹 Lấy chi tiết sản phẩm
  useEffect(() => {
    axios
      .get(`http://localhost:8080/api/products/${id}`)
      .then((res) => {
        const data = res.data;
        setProduct(data);
        setSelectedImage(
          `http://localhost:8080/api/products/get-image/${data.id}`
        );
      })
      .catch((err) => console.error("Lỗi khi tải chi tiết sản phẩm:", err));
  }, [id]);

  // 🔹 Gọi API thêm sản phẩm vào giỏ hàng (backend)
  const handleAddToCart = async () => {
    try {
      await axios.post("http://localhost:8080/api/cart/add", null, {
        params: {
          userId: userId,
          productId: product.id,
          quantity: quantity,
          accountId: userId, // nếu backend yêu cầu
        },
      });

      setNotification("✅ Đã thêm vào giỏ hàng!");
      setTimeout(() => setNotification(""), 2000);
      // ❌ Không tự động chuyển sang giỏ hàng nữa
    } catch (error) {
      console.error("❌ Lỗi khi thêm sản phẩm vào giỏ hàng:", error);
      setNotification("❌ Thêm sản phẩm thất bại!");
      setTimeout(() => setNotification(""), 2000);
    }
  };

  const handleBuyNow = async () => {
    await handleAddToCart();
    // navigate("/checkout"); // nếu có trang thanh toán
  };

  const handleViewCart = () => {
    navigate("/cart");
  };

  if (!product) return <p>Đang tải chi tiết sản phẩm...</p>;

  return (
    <div className="shopping-detail">
      <button className="back-btn" onClick={() => navigate(-1)}>
        ⬅ Quay lại
      </button>

      {notification && <div className="notification">{notification}</div>}

      <div className="detail-container">
        {/* Hình ảnh */}
        <div className="image-section">
          <img src={selectedImage} alt={product.name} className="main-image" />
        </div>

        {/* Thông tin */}
        <div className="info-section">
          <h2>{product.name}</h2>
          <p className="price">
            <span className="new">
              {product.price?.toLocaleString("vi-VN")}₫
            </span>
          </p>

          <p className="desc">{product.description}</p>

          {/* Số lượng */}
          <div className="quantity">
            <button onClick={() => setQuantity((q) => Math.max(1, q - 1))}>
              -
            </button>
            <span>{quantity}</span>
            <button onClick={() => setQuantity((q) => q + 1)}>+</button>
          </div>

          {/* Nút hành động */}
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
    </div>
  );
}

export default ShoppingDetail;
