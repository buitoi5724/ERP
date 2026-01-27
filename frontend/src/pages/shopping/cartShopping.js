import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./cartShopping.css";
import shoppingService from "../shopping/shoppingService";

// =========================
// CONSTANTS
// =========================
const USER_ID = 1;

const PAYMENT_METHOD = {
  CASH: "CASH",
  COD: "COD",
  BANK_TRANSFER: "BANK_TRANSFER",
};

const CartShopping = () => {
  const navigate = useNavigate();

  // =========================
  // STATE
  // =========================
  const [cartItems, setCartItems] = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);
  const [selectAll, setSelectAll] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState(PAYMENT_METHOD.CASH);
  const [message, setMessage] = useState("");
const handleBackToShopping = () => {
  navigate("/shopping"); // hoặc "/products" tùy router của bạn
};

  // =========================
  // LOAD CART
  // =========================
  useEffect(() => {
    loadCart();
  }, []);

  const loadCart = async () => {
    try {
      const items = await shoppingService.getCartByUser(USER_ID);

      const enriched = await Promise.all(
        items.map(async (item) => {
          try {
            const product = await shoppingService.getProductById(item.productId);
            return {
              ...item,
              productName: product.name,
              price: product.price,
              imageUrl: product.image
                ? `http://localhost:8080${product.image}`
                : "/images/default-product.png",
            };
          } catch {
            return {
              ...item,
              productName: "Sản phẩm không tồn tại",
              price: 0,
              imageUrl: "/images/default-product.png",
            };
          }
        })
      );

      setCartItems(enriched);
    } catch (err) {
      setMessage("Không thể tải giỏ hàng");
    }
  };

  // =========================
  // SELECT
  // =========================
  useEffect(() => {
    setSelectAll(
      cartItems.length > 0 && selectedIds.length === cartItems.length
    );
  }, [selectedIds, cartItems]);

  const toggleItem = (id) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    );
  };

  const toggleAll = () => {
    setSelectedIds(selectAll ? [] : cartItems.map((i) => i.id));
    setSelectAll(!selectAll);
  };

  // =========================
  // CART ACTIONS
  // =========================
  const updateQuantity = async (cartId, quantity) => {
    const q = quantity > 0 ? quantity : 1;

    setCartItems((prev) =>
      prev.map((i) => (i.id === cartId ? { ...i, quantity: q } : i))
    );

    await shoppingService.updateCartQuantity(cartId, q);
  };

  const removeItem = async (cartId) => {
    await shoppingService.removeFromCart(cartId);
    setCartItems((prev) => prev.filter((i) => i.id !== cartId));
    setSelectedIds((prev) => prev.filter((id) => id !== cartId));
  };

  // =========================
  // TOTAL
  // =========================
  const totalAmount = cartItems
    .filter((i) => selectedIds.includes(i.id))
    .reduce((sum, i) => sum + i.price * i.quantity, 0);

  // =========================
  // PLACE ORDER
  // =========================
 const handlePlaceOrder = async () => {
  if (!selectedIds.length) {
    setMessage("Vui lòng chọn ít nhất 1 sản phẩm");
    return;
  }

  const selectedItems = cartItems.filter((i) =>
    selectedIds.includes(i.id)
  );

  if (selectedItems.some((i) => i.price <= 0)) {
    setMessage("Có sản phẩm không hợp lệ");
    return;
  }

  const payload = {
    customerId: USER_ID,
    paymentMethod,
    items: selectedItems.map((i) => ({
      productId: i.productId,
      quantity: i.quantity,
      price: i.price,
    })),
  };

  try {
    // ✅ CHỈ TẠO ORDER
    const order = await shoppingService.placeOrder(payload);

    // ❌ KHÔNG XÓA CART Ở ĐÂY
    // ❌ KHÔNG SET STATE CART

    // 👉 sang trang xác nhận / thanh toán
    navigate(`/invoice/${order.id}`);
  } catch (err) {
    setMessage(
      err?.response?.data?.message || "Đặt hàng thất bại"
    );
  }
};


  // =========================
  // RENDER
  // =========================
  return (
    <div className="cart-container">
      <h2>🛒 Giỏ hàng</h2>

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
                  onChange={toggleAll}
                />
              </th>
              <th>Sản phẩm</th>
              <th>Đơn giá</th>
              <th>Số lượng</th>
              <th>Thành tiền</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {cartItems.map((item) => (
              <tr key={item.id}>
                <td>
                  <input
                    type="checkbox"
                    checked={selectedIds.includes(item.id)}
                    onChange={() => toggleItem(item.id)}
                  />
                </td>
                <td className="product-info">
                  <img src={item.imageUrl} alt="" className="cart-img" />
                  {item.productName}
                </td>
                <td>{item.price.toLocaleString()}đ</td>
                <td>
                  <input
                    type="number"
                    min="1"
                    value={item.quantity}
                    onChange={(e) =>
                      updateQuantity(item.id, Number(e.target.value))
                    }
                  />
                </td>
                <td>
                  {(item.price * item.quantity).toLocaleString()}đ
                </td>
                <td>
                  <button onClick={() => removeItem(item.id)}>Xóa</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {/* FOOTER */}
      <div className="cart-footer">
        <div className="payment-method">
          <label>Thanh toán:</label>
          <select
            value={paymentMethod}
            onChange={(e) => setPaymentMethod(e.target.value)}
          >
            <option value={PAYMENT_METHOD.CASH}>Tiền mặt</option>
            <option value={PAYMENT_METHOD.COD}>COD</option>
            <option value={PAYMENT_METHOD.BANK_TRANSFER}>Chuyển khoản</option>
          </select>
        </div>

        <h3>Tổng tiền: {totalAmount.toLocaleString()}đ</h3>

        <button disabled={!selectedIds.length} onClick={handlePlaceOrder}>
          Đặt hàng
        </button>
      </div>

      {message && <div className="notification">{message}</div>}
    </div>
  );
};

export default CartShopping;
