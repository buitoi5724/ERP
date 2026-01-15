import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./cartShopping.css";
import shoppingService from "../shopping/shoppingService";

const CartShopping = () => {
  const navigate = useNavigate();
  const userId = 1; // giả lập customerId

  const [cartItems, setCartItems] = useState([]);
  const [selectedItems, setSelectedItems] = useState([]);
  const [selectAll, setSelectAll] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");

  // =========================
  // LOAD CART
  // =========================
  useEffect(() => {
    shoppingService.getCartByUser(userId).then(async (items) => {
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
              productName: "Sản phẩm không xác định",
              price: 0,
              imageUrl: "/images/default-product.png",
            };
          }
        })
      );
      setCartItems(enriched);
    });
  }, []);

  // =========================
  // SELECT ALL
  // =========================
  useEffect(() => {
    setSelectAll(
      cartItems.length > 0 && selectedItems.length === cartItems.length
    );
  }, [selectedItems, cartItems]);

  // =========================
  // CART ACTIONS
  // =========================
  const toggleSelectItem = (id) => {
    setSelectedItems((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    );
  };

  const toggleSelectAll = () => {
    setSelectedItems(selectAll ? [] : cartItems.map((i) => i.id));
    setSelectAll(!selectAll);
  };

  const removeFromCart = async (cartId) => {
    await shoppingService.removeFromCart(cartId);
    setCartItems(cartItems.filter((i) => i.id !== cartId));
    setSelectedItems(selectedItems.filter((id) => id !== cartId));
  };

  const updateQuantity = async (cartId, quantity) => {
    const q = quantity > 0 ? quantity : 1;
    setCartItems(
      cartItems.map((i) => (i.id === cartId ? { ...i, quantity: q } : i))
    );
    await shoppingService.updateCartQuantity(cartId, q);
  };

  // =========================
  // TOTAL
  // =========================
  const getTotal = () =>
    cartItems
      .filter((i) => selectedItems.includes(i.id))
      .reduce((sum, i) => sum + i.price * i.quantity, 0);

  // =========================
  // PLACE ORDER (ERP CHUẨN)
  // =========================
  const handlePlaceOrder = async () => {
    if (!selectedItems.length) {
      setSuccessMessage("Vui lòng chọn ít nhất 1 sản phẩm");
      
      return;
    }

    const orderPayload = {
      customerId: userId, // 🔴 BẮT BUỘC
      paymentMethod: "cash",
      items: cartItems
        .filter((i) => selectedItems.includes(i.id))
        .map((i) => ({
          productId: i.productId,
          quantity: i.quantity,
          price: i.price,
        })),
    };

    try {
      const order = await shoppingService.placeOrder(orderPayload);

      await shoppingService.removeMultipleFromCart(selectedItems);
      setCartItems(cartItems.filter((i) => !selectedItems.includes(i.id)));
      setSelectedItems([]);

      navigate(`/invoice/${order.id}`);
    } catch (err) {
      console.error(err);
      setSuccessMessage(
        err.response?.data || "Đặt hàng thất bại, vui lòng thử lại"
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
                  onChange={toggleSelectAll}
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
                    checked={selectedItems.includes(item.id)}
                    onChange={() => toggleSelectItem(item.id)}
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
                  <button onClick={() => removeFromCart(item.id)}>Xóa</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <div className="cart-footer">
        <h3>Tổng tiền: {getTotal().toLocaleString()}đ</h3>
        <button disabled={!selectedItems.length} onClick={handlePlaceOrder}>
          Đặt hàng
        </button>
      </div>

      {successMessage && <div className="notification">{successMessage}</div>}
    </div>
  );
};

export default CartShopping;
