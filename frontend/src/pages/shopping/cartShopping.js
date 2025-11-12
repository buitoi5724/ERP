import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom"; // ⬅️ thêm dòng này
import "./cartShopping.css";
import cartService from "../shopping/shoppingService";

const CartShopping = () => {
  const [cartItems, setCartItems] = useState([]);
  const [selectedItems, setSelectedItems] = useState([]);
  const [selectAll, setSelectAll] = useState(false);
  const [invoice, setInvoice] = useState(null);
const navigate = useNavigate();

  useEffect(() => {
    cartService.getCartByUser(1).then(async (items) => {
      const updatedItems = await Promise.all(
        items.map(async (item) => {
          try {
            const product = await cartService.getProductById(item.productId);
            return {
              ...item,
              productName: product.name || "Sản phẩm không xác định",
              price: product.price || 0,
              description: product.description || "",
              imageUrl: cartService.getProductImage(product.id),
            };
          } catch {
            return { ...item, productName: "Sản phẩm không xác định", price: 0 };
          }
        })
      );
      setCartItems(updatedItems);
    }).catch(console.error);
  }, []);

  useEffect(() => {
    setSelectAll(cartItems.length > 0 && selectedItems.length === cartItems.length);
  }, [selectedItems, cartItems]);

  const removeFromCart = (cartId) => {
    cartService.removeFromCart(cartId).then(() => {
      setCartItems(cartItems.filter((item) => item.id !== cartId));
      setSelectedItems(selectedItems.filter((id) => id !== cartId));
    }).catch(console.error);
  };

  const handleQuantityChange = (cartId, quantity) => {
    const newQuantity = quantity === "" ? "" : Number(quantity);
    setCartItems(cartItems.map((item) => item.id === cartId ? { ...item, quantity: newQuantity } : item));
  };

  const submitQuantityUpdate = (cartId, quantity) => {
    const newQuantity = Number(quantity) > 0 ? Number(quantity) : 1;
    setCartItems(cartItems.map((item) => item.id === cartId ? { ...item, quantity: newQuantity } : item));
    cartService.updateCartQuantity(cartId, newQuantity).catch(console.error);
  };

  const toggleSelectItem = (id) => {
    setSelectedItems(prev => prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]);
  };

  const toggleSelectAll = () => {
    setSelectedItems(selectAll ? [] : cartItems.map((item) => item.id));
    setSelectAll(!selectAll);
  };

  const getImageSrc = (productId) => cartService.getProductImage(productId);

  const getTotal = () =>
    cartItems.filter((item) => selectedItems.includes(item.id))
      .reduce((sum, item) => sum + Number(item.price ?? 0) * Number(item.quantity ?? 0), 0);

  const handlePlaceOrder = async () => {
    if (!selectedItems.length) {
      alert("Vui lòng chọn ít nhất một sản phẩm để đặt hàng!");
      return;
    }

    // Bước 1: Chuẩn bị danh sách 'items' (Đã đúng)
    const itemsToOrder = cartItems
      .filter(item => selectedItems.includes(item.id)) 
      .map(item => ({
        productId: item.productId, 
        quantity: item.quantity,
      }));

    // === BƯỚC 2: SỬA LỖI TẠI ĐÂY ===
    // Chuẩn bị 'orderPayload' đầy đủ thông tin khách hàng
    const orderPayload = {
      // --- Thông tin khách hàng (Dữ liệu test tạm thời) ---
      // (Sau này bạn sẽ lấy từ các ô <input>)
      customerName: "Khách hàng Test",
      phone: "0912345678",
      email: "test@gmail.com",
      address: "123 Đường Test, Quận 1, TP. HCM",
      note: "Giao hàng giờ hành chính.",
      paymentMethod: "COD",
      
      // --- Thông tin thanh toán (Gửi 0, Backend sẽ tự tính) ---
      subtotal: 0.0,
      tax: 0.0,
      shippingFee: 30000.0, // (Bạn có thể gửi phí ship nếu có)
      discount: 0.0,
      
      // --- Danh sách sản phẩm (Đã đúng) ---
      items: itemsToOrder,
    };
    
    // In ra để kiểm tra
    console.log("Đang gửi đi Order Payload:", orderPayload);

    try {
      // Bước 3: Gọi API (Giữ nguyên)
      const result = await cartService.placeOrder(orderPayload);
      
      setCartItems(cartItems.filter(item => !selectedItems.includes(item.id)));
      setSelectedItems([]);

      alert(`Đặt hàng thành công! Mã đơn hàng: ${result.code || result.id}`);

      // Bước 4: Điều hướng (Đã đúng)
      navigate(`/invoice/${result.id}`, { state: { invoiceData: result } });
      
    } catch (err) {
      // Bước 5: Bắt lỗi (Đã đúng)
      console.error(err);
      const errorMessage = err.response?.data || "Đặt hàng thất bại! Vui lòng thử lại.";
      alert(errorMessage);
    }
  };

  return (
    
    <div className="cart-container">
      <h2>🛒 Giỏ hàng của bạn</h2>
      {cartItems.length === 0 ? <p>Giỏ hàng trống</p> : (
        <table className="cart-table">
          <thead>
            <tr>
              <th><input type="checkbox" checked={selectAll} onChange={toggleSelectAll} /></th>
              <th>Sản phẩm</th>
              <th>Đơn giá</th>
              <th>Số lượng</th>
              <th>Thành tiền</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {cartItems.map(item => (
              <tr key={item.id}>
                <td><input type="checkbox" checked={selectedItems.includes(item.id)} onChange={() => toggleSelectItem(item.id)} /></td>
                <td className="product-info">
                  <img src={getImageSrc(item.productId)} alt={item.productName} className="cart-img" />
                  <span>{item.productName}</span>
                </td>
                <td>{Number(item.price ?? 0).toLocaleString()}đ</td>
                <td>
                  <div className="quantity-control">
                    <button onClick={() => submitQuantityUpdate(item.id, Number(item.quantity) - 1)}>-</button>
                    <input
                      type="number"
                      min="1"
                      value={item.quantity}
                      onChange={(e) => handleQuantityChange(item.id, e.target.value)}
                      onBlur={(e) => submitQuantityUpdate(item.id, e.target.value)}
                    />
                    <button onClick={() => submitQuantityUpdate(item.id, Number(item.quantity) + 1)}>+</button>
                  </div>
                </td>
                <td>{(Number(item.price ?? 0) * Number(item.quantity ?? 0)).toLocaleString()}đ</td>
                <td><button className="delete-btn" onClick={() => removeFromCart(item.id)}>Xóa</button></td>
              </tr>
              
            ))}
          </tbody>
        </table>
      )}

      <div className="cart-footer">
        <div>
          <span>Đã chọn {selectedItems.length} sản phẩm</span>
          <h3>Tổng tiền: {getTotal().toLocaleString()}đ</h3>
        </div>
        <button className="order-btn" disabled={!selectedItems.length} onClick={handlePlaceOrder}>
          🛍️ Đặt hàng
        </button>
      </div>

      {invoice && (
        <div className="invoice-container">
          <h2>🧾 Hóa đơn</h2>
          <p>Mã hóa đơn: {invoice.id || invoice.invoiceId}</p>
          <p>Tổng tiền: {(invoice.totalAmount ?? 0).toLocaleString()}đ</p>
          {invoice.items && invoice.items.length > 0 && (
            <table className="invoice-table">
              <thead>
                <tr>
                  <th>Sản phẩm</th>
                  <th>Số lượng</th>
                  <th>Đơn giá</th>
                  <th>Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                {invoice.items.map((item, idx) => (
                  <tr key={item.productId || idx}>
                    <td>{item.productName}</td>
                    <td>{item.quantity}</td>
                    <td>{Number(item.price ?? 0).toLocaleString()}đ</td>
                    <td>{(Number(item.price ?? 0) * Number(item.quantity ?? 0)).toLocaleString()}đ</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  );
};

export default CartShopping;
