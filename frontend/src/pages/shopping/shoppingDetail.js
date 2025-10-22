import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./shopping.css";

function ShoppingDetail() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [selectedImage, setSelectedImage] = useState("");
  const [quantity, setQuantity] = useState(1);
  const [notification, setNotification] = useState("");
  const navigate = useNavigate();

  const userId = 1; // Demo userId
  const baseUrl = "http://localhost:8080/api/products/image/";

  useEffect(() => {
    axios
      .get(`http://localhost:8080/api/products/${id}`)
      .then((res) => {
        const productData = res.data;

        // ✅ Lấy danh sách ảnh gallery từ backend
        const galleries =
          productData.imageUrls?.map(
            (img) => `${baseUrl}${encodeURIComponent(img)}`
          ) || [];

        const mainImage = productData.image
          ? `${baseUrl}${encodeURIComponent(productData.image)}`
          : "https://via.placeholder.com/400?text=No+Image";

        // ✅ Gộp ảnh chính + gallery (loại trùng)
        const allImages = [mainImage, ...galleries].filter(
          (v, i, arr) => arr.indexOf(v) === i
        );

        setProduct({ ...productData, fullImageUrls: allImages });
        setSelectedImage(mainImage);
      })
      .catch((err) => {
        console.error("❌ Lỗi khi tải chi tiết sản phẩm:", err);
      });
  }, [id]);

  // ================== CART ==================
  const handleAddToCart = async () => {
    if (!product) return;
    try {
      await axios.post("http://localhost:8080/api/cart/add", null, {
        params: {
          userId,
          productId: product.id,
          quantity,
          accountId: userId,
        },
      });
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

  // ================== RENDER ==================
  return (
    <div className="shopping-detail">
      <button className="back-btn" onClick={() => navigate(-1)}>
        ⬅ Quay lại
      </button>

      {notification && <div className="notification">{notification}</div>}

      <div className="detail-container">
        {/* === PHẦN HÌNH ẢNH === */}
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
                className={`thumbnail ${
                  selectedImage === imgUrl ? "active" : ""
                }`}
                onMouseEnter={() => setSelectedImage(imgUrl)} // ✅ hover đổi ảnh
                onError={(e) => (e.target.src = "/images/default-product.png")}
              />
            ))}
          </div>
        </div>

        {/* === THÔNG TIN SẢN PHẨM === */}
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
            <button onClick={() => setQuantity((q) => Math.max(1, q - 1))}>
              -
            </button>
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

      {/* === PHẦN CHI TIẾT SẢN PHẨM DƯỚI CÙNG === */}
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
