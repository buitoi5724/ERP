import React, { useEffect, useState } from "react";
import axios from "axios";
import "./cartShopping.css";

/**
 *  Component: CartShopping
 * Hiển thị giỏ hàng người dùng, cho phép chọn, chỉnh sửa số lượng, xóa sản phẩm, và tính tổng tiền.
 */
const CartShopping = () => {
  // =================================================================
  //  STATE KHAI BÁO
  // =================================================================
  const [cartItems, setCartItems] = useState([]);       // Danh sách sản phẩm trong giỏ
  const [selectedItems, setSelectedItems] = useState([]); // Danh sách ID sản phẩm được chọn
  const [selectAll, setSelectAll] = useState(false);     // Trạng thái checkbox "chọn tất cả"

  // =================================================================
  //  FETCH DỮ LIỆU TỪ BACKEND
  // =================================================================
  useEffect(() => {
    axios
      .get("http://localhost:8080/api/cart/1") // Giả định userId=1
      .then(async (res) => {
        const items = res.data;

        // Với mỗi sản phẩm trong giỏ hàng → lấy thêm thông tin chi tiết
        const updatedItems = await Promise.all(
          items.map(async (item) => {
            try {
              const productRes = await axios.get(
                `http://localhost:8080/api/products/${item.productId}`
              );
              return {
                ...item,
                productName: productRes.data.name,
                price: productRes.data.price,
                description: productRes.data.description,
                imageUrl: `http://localhost:8080/api/products/get-image/${productRes.data.id}`,
              };
            } catch (err) {
              console.error("Lỗi khi lấy thông tin sản phẩm:", err);
              return {
                ...item,
                productName: "Sản phẩm không xác định",
                price: 0,
              };
            }
          })
        );
        setCartItems(updatedItems);
      })
      .catch((err) => console.error("Lỗi khi tải giỏ hàng:", err));
  }, []);

  //  Tự động cập nhật trạng thái “chọn tất cả” khi selectedItems thay đổi
  useEffect(() => {
    if (cartItems.length > 0) {
      setSelectAll(selectedItems.length === cartItems.length);
    } else {
      setSelectAll(false);
    }
  }, [selectedItems, cartItems]);

  // =================================================================
  //  CÁC HÀM XỬ LÝ API & UI
  // =================================================================

  /**
   *  Xóa sản phẩm khỏi giỏ hàng
   */
  const removeFromCart = (cartId) => {
    axios
      .delete(`http://localhost:8080/api/cart/remove/${cartId}`)
      .then(() => {
        setCartItems(cartItems.filter((item) => item.id !== cartId));
        setSelectedItems(selectedItems.filter((id) => id !== cartId));
      })
      .catch((err) => console.error("Lỗi khi xóa sản phẩm:", err));
  };

  /**
   *  Thay đổi số lượng hiển thị trên giao diện
   */
  const handleQuantityChange = (cartId, quantity) => {
    const newQuantity = quantity === "" ? "" : Number(quantity);
    setCartItems(
      cartItems.map((item) =>
        item.id === cartId ? { ...item, quantity: newQuantity } : item
      )
    );
  };

  /**
   *  Gửi yêu cầu cập nhật số lượng lên server
   */
  const submitQuantityUpdate = (cartId, quantity) => {
    const newQuantity = Number(quantity) > 0 ? Number(quantity) : 1;

    // Cập nhật lại state
    setCartItems(
      cartItems.map((item) =>
        item.id === cartId ? { ...item, quantity: newQuantity } : item
      )
    );

    axios
      .put(`http://localhost:8080/api/cart/update/${cartId}?quantity=${newQuantity}`)
      .then(() => {
        console.log(`Đã cập nhật số lượng item ${cartId} = ${newQuantity}`);
      })
      .catch((err) => console.error("Lỗi khi cập nhật số lượng:", err));
  };

  /**
   *  Chọn hoặc bỏ chọn một sản phẩm
   */
  const toggleSelectItem = (id) => {
    if (selectedItems.includes(id)) {
      setSelectedItems(selectedItems.filter((item) => item !== id));
    } else {
      setSelectedItems([...selectedItems, id]);
    }
  };

  /**
   *  Chọn hoặc bỏ chọn tất cả sản phẩm
   */
  const toggleSelectAll = () => {
    if (selectAll) {
      setSelectedItems([]);
    } else {
      setSelectedItems(cartItems.map((item) => item.id));
    }
    setSelectAll(!selectAll);
  };

  /**
   * 🖼 Lấy đường dẫn ảnh sản phẩm
   */
  const getImageSrc = (productId) => {
    if (!productId) return "/images/default-product.png";
    return `http://localhost:8080/api/products/get-image/${productId}`;
  };

  /**
   * 💰 Tính tổng tiền các sản phẩm được chọn
   */
  const getTotal = () => {
    return cartItems
      .filter((item) => selectedItems.includes(item.id))
      .reduce(
        (sum, item) =>
          sum + Number(item.price ?? 0) * Number(item.quantity ?? 0),
        0
      );
  };

  // =================================================================
  //  GIAO DIỆN HIỂN THỊ
  // =================================================================
  return (
    <div className="cart-container">
      <h2>🛒 Giỏ hàng của bạn</h2>

      {/* Nếu giỏ hàng trống */}
      {cartItems.length === 0 ? (
        <p>Giỏ hàng trống</p>
      ) : (
        <table className="cart-table">
          <thead>
            <tr>
              <th>
                <input
                  type="checkbox"
                  checked={selectAll}
                  onChange={toggleSelectAll}
                />
              </th>
              <th>Sản phẩm</th>
              <th>Đơn giá</th>
              <th>Số lượng</th>
              <th>Số tiền</th>
              <th>Thao tác</th>
            </tr>
          </thead>

          <tbody>
            {cartItems.map((item) => (
              <tr key={item.id}>
                {/* Checkbox chọn sản phẩm */}
                <td>
                  <input
                    type="checkbox"
                    checked={selectedItems.includes(item.id)}
                    onChange={() => toggleSelectItem(item.id)}
                  />
                </td>

                {/* Thông tin sản phẩm */}
                <td className="product-info">
                  <img
                    src={getImageSrc(item.productId)}
                    alt={item.productName}
                    className="cart-img"
                  />
                  <span>{item.productName}</span>
                </td>

                {/* Giá */}
                <td>{Number(item.price ?? 0).toLocaleString()}đ</td>

                {/* Số lượng */}
                <td>
                  <div className="quantity-control">
                    <button
                      onClick={() =>
                        submitQuantityUpdate(item.id, Number(item.quantity) - 1)
                      }
                    >
                      -
                    </button>
                    <input
                      type="number"
                      min="1"
                      value={item.quantity}
                      onChange={(e) =>
                        handleQuantityChange(item.id, e.target.value)
                      }
                      onBlur={(e) =>
                        submitQuantityUpdate(item.id, e.target.value)
                      }
                      onKeyDown={(e) => {
                        if (e.key === "Enter") {
                          e.preventDefault();
                          submitQuantityUpdate(item.id, e.target.value);
                        }
                      }}
                    />
                    <button
                      onClick={() =>
                        submitQuantityUpdate(item.id, Number(item.quantity) + 1)
                      }
                    >
                      +
                    </button>
                  </div>
                </td>

                {/* Thành tiền */}
                <td>
                  {(
                    Number(item.price ?? 0) * Number(item.quantity ?? 0)
                  ).toLocaleString()}
                  đ
                </td>

                {/* Nút xóa */}
                <td>
                  <button
                    className="delete-btn"
                    onClick={() => removeFromCart(item.id)}
                  >
                    Xóa
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {/* Footer hiển thị tổng tiền */}
      <div className="cart-footer">
        <span>Đã chọn {selectedItems.length} sản phẩm</span>
        <h3>Tổng tiền: {getTotal().toLocaleString()}đ</h3>
      </div>
    </div>
  );
};

export default CartShopping;
