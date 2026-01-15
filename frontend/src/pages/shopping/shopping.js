import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Dialog } from "primereact/dialog";
import { getAllProducts, addToCart } from "./shoppingService";
import "./shopping.css";

// === Hàm helper lấy URL ảnh đầy đủ ===
const getImage = (path) => {
  if (!path) return "/images/default-product.png";
  if (path.startsWith("http")) return path;
  return `http://localhost:8080${path}`;
};

function ProductPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [notification, setNotification] = useState("");
  const [visibleDialog, setVisibleDialog] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [selectedImage, setSelectedImage] = useState("");
  const [selectedColor, setSelectedColor] = useState("");
  const [selectedSize, setSelectedSize] = useState("");
  const [quantity, setQuantity] = useState(1);

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

  // Lấy tất cả ảnh sản phẩm (main + gallery)
  const getProductImages = (product) => {
    const main = product.image ? getImage(product.image) : "/images/default-product.png";
    const gallery = (product.imageUrls || []).map((img) => getImage(img));
    return [main, ...gallery.filter((v, i, arr) => arr.indexOf(v) === i)];
  };

  const handleAddToCart = (product, e) => {
    e.stopPropagation();
    setSelectedProduct(product);
    const images = getProductImages(product);
    setSelectedImage(images[0] || "");
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
      .catch(() => {
        setNotification("❌ Thêm sản phẩm thất bại!");
        setTimeout(() => setNotification(""), 3000);
      });

    setVisibleDialog(false);
    setQuantity(1);
    setSelectedSize("");
    setSelectedColor("");
  };

  const getImageSrc = (product) => getImage(product?.image);

  if (error) return <p style={{ color: "red", textAlign: "center" }}>{error}</p>;
  if (loading) return <p>Đang tải sản phẩm...</p>;

  return (
    <div className="product-page">
      {notification && <div className="notification">{notification}</div>}

      <div className="headerr">
        <h2>Danh Sách Sản Phẩm</h2>
        <button className="btn-cartt" onClick={() => navigate("/cart")}>🛒</button>
      </div>

      <div className="product-row">
        {products.map((product) => (
          <div
            key={product.id}
            className="product-card"
            onClick={() => navigate(`/shopping/${product.id}`)}
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
                <span className="product-price">{product.price?.toLocaleString("vi-VN")}₫</span>
                <span className="product-sales-info">Đã bán {product.sold || 0}</span>
              </div>
              <button className="add-to-cart-btn" onClick={(e) => handleAddToCart(product, e)}>+ Thêm vào giỏ</button>
            </div>
          </div>
        ))}
      </div>

      <Dialog
        visible={visibleDialog}
        onHide={() => setVisibleDialog(false)}
        className="product-dialog"
        dismissableMask
        header={null}
      >
        {selectedProduct && (
          <div className="dialog-container">
            <div className="dialog-left">
              <div className="main-image">
                <img
                  src={selectedImage}
                  alt={selectedProduct.name}
                  onError={(e) => (e.target.src = "/images/default-product.png")}
                />
              </div>

              <div className="thumbnail-list">
                {getProductImages(selectedProduct).map((img, idx) => (
                  <img
                    key={idx}
                    src={img}
                    alt={`thumb-${idx}`}
                    className={`thumbnail ${selectedImage === img ? "active" : ""}`}
                    onMouseEnter={() => setSelectedImage(img)}
                    onClick={() => setSelectedImage(img)}
                    onError={(e) => (e.target.src = "/images/default-product.png")}
                  />
                ))}
              </div>
            </div>

            <div className="dialog-right">
              <h3 className="product-title">{selectedProduct.name}</h3>
              <p className="product-meta">
                Mã sản phẩm: <strong>{selectedProduct.code}</strong> · Tình trạng: <span className="status">Còn hàng</span>
              </p>

              <div className="price-section">
                <span className="price">{selectedProduct.price?.toLocaleString("vi-VN")}₫</span>
                {selectedProduct.oldPrice && (
                  <span className="old-price">{selectedProduct.oldPrice?.toLocaleString("vi-VN")}₫</span>
                )}
                {selectedProduct.discount && <span className="discount">-{selectedProduct.discount}%</span>}
              </div>

              <div className="option-group">
                <label>Kích thước:</label>
                <div className="option-buttons">
                  {["S", "M", "L", "XL"].map((size) => (
                    <button
                      key={size}
                      className={`option-btn ${selectedSize === size ? "active" : ""}`}
                      onClick={() => setSelectedSize(size)}
                    >
                      {size}
                    </button>
                  ))}
                </div>
              </div>

              <div className="option-group">
                <label>Màu sắc:</label>
                <div className="option-buttons">
                  {["Đen", "Trắng", "Nâu"].map((color) => (
                    <button
                      key={color}
                      className={`option-btn ${selectedColor === color ? "active" : ""}`}
                      onClick={() => setSelectedColor(color)}
                    >
                      {color}
                    </button>
                  ))}
                </div>
              </div>

              <div className="option-group quantity-group">
                <label>Số lượng:</label>
                <div className="quantity-controls">
                  <button onClick={() => setQuantity(Math.max(1, quantity - 1))}>-</button>
                  <input
                    type="number"
                    min="1"
                    value={quantity}
                    onChange={(e) => setQuantity(Math.max(1, Number(e.target.value) || 1))}
                    className="quantity-input"
                  />
                  <button onClick={() => setQuantity(quantity + 1)}>+</button>
                </div>
              </div>

              <button className="btn-add-cart" onClick={handleConfirmAdd}>THÊM VÀO GIỎ</button>
            </div>
          </div>
        )}
      </Dialog>
    </div>
  );
}

export default ProductPage;
