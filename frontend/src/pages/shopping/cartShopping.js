import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./cartShopping.css";
import cartService from "../shopping/shoppingService";

const CartShopping = () => {
  const [cartItems, setCartItems] = useState([]);
  const [selectedItems, setSelectedItems] = useState([]);
  const [selectAll, setSelectAll] = useState(false);
  const [invoice, setInvoice] = useState(null);
    const [successMessage, setSuccessMessage] = useState(""); 
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
setSuccessMessage("Vui lòng chọn ít nhất một sản phẩm để đặt hàng!");
return;
}

const itemsToOrder = cartItems
.filter(item => selectedItems.includes(item.id))
.map(item => ({ productId: item.productId, quantity: item.quantity }));

const orderPayload = {
    userId: 1,
  accountId: 1, // <-- tạm gán accountId giả
customerName: "Khách hàng Test",
phone: "0912345678",
email: "test@gmail.com",
address: "123 Đường Test, Quận 1, TP. HCM",
note: "Giao hàng giờ hành chính.",
paymentMethod: "COD",
subtotal: getTotal(),
tax: 0,
shippingFee: 30000,
discount: 0,
items: itemsToOrder,
};

try {
// 1️⃣ Tạo hóa đơn / đặt hàng
const invoiceResult = await cartService.placeOrder(orderPayload);
console.log("✅ Hóa đơn tạo thành công:", invoiceResult);

// 2️⃣ Kiểm tra trạng thái thanh toán (chỉ xóa khi thành công)
if (invoiceResult.paymentStatus === "PAID" || invoiceResult.status === "SUCCESS") {
  await cartService.removeMultipleFromCart(selectedItems);
  setCartItems(cartItems.filter(item => !selectedItems.includes(item.id)));
  setSelectedItems([]);
  setSuccessMessage(`Đặt hàng và thanh toán thành công! Mã đơn hàng: ${invoiceResult.id}`);
} else {
  setSuccessMessage(`Đặt hàng thành công nhưng chưa thanh toán. Mã đơn hàng: ${invoiceResult.id}`);
}

setInvoice(invoiceResult);
navigate(`/invoice/${invoiceResult.id}`, { state: { invoiceData: invoiceResult } });

} catch (err) {
console.error("❌ Lỗi khi đặt hàng:", err);
const errorMessage = err.response?.data || "Đặt hàng thất bại! Vui lòng thử lại.";
setSuccessMessage(errorMessage);
}
};

  return (
    
    <div className="cart-container">
      <h2> Giỏ hàng của bạn</h2>
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
           Đặt hàng
        </button>
      </div>

      {invoice && (
        <div className="invoice-container">
          <h2> Hóa đơn</h2>
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