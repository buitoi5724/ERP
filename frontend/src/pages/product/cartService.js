// // src/services/cartService.js

// // Load giỏ hàng từ localStorage nếu có
// let cart = JSON.parse(localStorage.getItem("cart")) || [];

// /**
//  * Lưu giỏ hàng vào localStorage
//  */
// const saveCart = () => {
//   localStorage.setItem("cart", JSON.stringify(cart));
// };

// /**
//  * Thêm sản phẩm vào giỏ
//  * @param {Object} product - sản phẩm {id, name, price, ...}
//  */
// export const addToCart = (product) => {
//   const existing = cart.find((item) => item.id === product.id);
//   if (existing) {
//     existing.quantity += 1;
//   } else {
//     cart.push({ ...product, quantity: 1 });
//   }
//   saveCart();
//   console.log("✅ Đã thêm:", product);
//   console.log("🛒 Giỏ hàng:", cart);
// };

// /**
//  * Lấy giỏ hàng hiện tại
//  * @returns {Array}
//  */
// export const getCart = () => {
//   return cart;
// };

// /**
//  * Xóa toàn bộ giỏ hàng
//  */
// export const clearCart = () => {
//   cart = [];
//   saveCart();
//   console.log("🗑️ Giỏ hàng đã được xóa");
// };

// /**
//  * Xóa 1 sản phẩm khỏi giỏ
//  * @param {number|string} productId
//  */
// export const removeFromCart = (productId) => {
//   cart = cart.filter((item) => item.id !== productId);
//   saveCart();
//   console.log(`🗑️ Đã xóa sản phẩm id=${productId}`);
// };

// /**
//  * Cập nhật số lượng sản phẩm trong giỏ
//  * @param {number|string} productId
//  * @param {number} quantity
//  */
// export const updateQuantity = (productId, quantity) => {
//   const item = cart.find((p) => p.id === productId);
//   if (item) {
//     item.quantity = quantity;
//     if (item.quantity <= 0) {
//       removeFromCart(productId);
//     } else {
//       saveCart();
//     }
//   }
// };
