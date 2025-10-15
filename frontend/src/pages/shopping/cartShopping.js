import React, { useEffect, useState } from "react";
import axios from "axios";
import "./shopping.css"; 

const CartShopping = () => {
 
  const [cartItems, setCartItems] = useState([]);
  const [selectedItems, setSelectedItems] = useState([]);
  const [selectAll, setSelectAll] = useState(false);

 

  // Lấy dữ liệu giỏ hàng từ backend khi component được mount
  useEffect(() => {
    axios
      .get("http://localhost:8080/api/cart/1") // userId=1
      .then(async (res) => {
        const items = res.data;
        // Với mỗi item trong cart, fetch thêm chi tiết sản phẩm (tên, giá)
        const updatedItems = await Promise.all(
          items.map(async (item) => {
            try {
              const productRes = await axios.get(
                `http://localhost:8080/api/products/${item.productId}`
              );
             return {
  ...item,
  productName: productRes.data.name,   // ✅ tên đúng trong backend
  price: productRes.data.price,
  imageUrl: `http://localhost:8080/api/products/get-image/${productRes.data.id}`, // ✅ thêm ảnh
  description: productRes.data.description, // ✅ thêm mô tả nếu muốn
};
            } catch (err) {
              console.error("Lỗi khi lấy thông tin sản phẩm:", err);
              return { ...item, productName: "Sản phẩm không xác định", price: 0 };
            }
          })
        );
        setCartItems(updatedItems);
      })
      .catch((err) => console.error("Lỗi khi tải giỏ hàng:", err));
  }, []);

  // =================================================================
  // 3. API & UI HANDLERS
  // =================================================================

  /**
   * @description Gửi yêu cầu xóa một sản phẩm khỏi giỏ hàng và cập nhật lại state.
   */
  const removeFromCart = (cartId) => {
    axios.delete(`http://localhost:8080/api/cart/remove/${cartId}`)
      .then(() => {
        setCartItems(cartItems.filter((item) => item.id !== cartId));
        setSelectedItems(selectedItems.filter((id) => id !== cartId));
      })
      .catch((err) => console.error("Lỗi khi xóa sản phẩm: ", err));
  };

  /**
   * @description Cập nhật số lượng trên giao diện ngay lập tức khi người dùng gõ.
   */
  const handleQuantityChange = (cartId, quantity) => {
    const newQuantity = quantity === '' ? '' : Number(quantity);
    setCartItems(
      cartItems.map((item) =>
        item.id === cartId ? { ...item, quantity: newQuantity } : item
      )
    );
  };

  /**
   * @description Gửi yêu cầu cập nhật số lượng lên server.
   */
  const submitQuantityUpdate = (cartId, quantity) => {
    const newQuantity = Number(quantity) > 0 ? Number(quantity) : 1;
    
    // Cập nhật lại state với giá trị hợp lệ cuối cùng
    setCartItems(
        cartItems.map((item) =>
          item.id === cartId ? { ...item, quantity: newQuantity } : item
        )
      );

    axios
      .put(`http://localhost:8080/api/cart/update/${cartId}?quantity=${newQuantity}`)
      .then(() => {
        console.log(`Đã cập nhật số lượng cho item ${cartId} thành ${newQuantity}`);
      })
      .catch((err) => {
        console.error("Lỗi khi cập nhật số lượng:", err);
      });
  };

  /**
   * @description Xử lý sự kiện khi người dùng chọn hoặc bỏ chọn một sản phẩm.
   */
  const toggleSelectItem = (id) => {
    if (selectedItems.includes(id)) {
      setSelectedItems(selectedItems.filter((item) => item !== id));
    } else {
      setSelectedItems([...selectedItems, id]);
    }
  };

  /**
   * @description Xử lý sự kiện khi người dùng chọn hoặc bỏ chọn tất cả sản phẩm.
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
   * @description Tạo đường dẫn URL cho ảnh sản phẩm.
   */
  const getImageSrc = (productId) => {
    if (!productId) return "/images/default-product.png";
    return `http://localhost:8080/api/products/get-image/${productId}`;
  };

 
  const getTotal = () => {
    return cartItems
      .filter((item) => selectedItems.includes(item.id))
      .reduce(
        (sum, item) => sum + Number(item.price ?? 0) * Number(item.quantity ?? 0),
        0
      );
  };



  return (
    <div className="cart-container">
      <h2>🛒 Giỏ hàng của bạn</h2>

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
                <td>
                  <input
                    type="checkbox"
                    checked={selectedItems.includes(item.id)}
                    onChange={() => toggleSelectItem(item.id)}
                  />
                </td>
           <td className="product-info">
  <img
    src={getImageSrc(item.productId)}
    alt={item.productName}
    className="cart-img"
  />
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
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          e.preventDefault();
                          submitQuantityUpdate(item.id, e.target.value);
                        }
                      }}
                    />
                    <button onClick={() => submitQuantityUpdate(item.id, Number(item.quantity) + 1)}>+</button>
                  </div>
                </td>
                <td>
                  {(Number(item.price ?? 0) * Number(item.quantity ?? 0)).toLocaleString()}đ
                </td>
                <td>
                  <button className="delete-btn" onClick={() => removeFromCart(item.id)}>
                    Xóa
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <div className="cart-footer">
        <span>Đã chọn {selectedItems.length} sản phẩm</span>
        <h3>Tổng tiền: {getTotal().toLocaleString()}đ</h3>
      </div>
    </div>
  );
};

export default CartShopping;