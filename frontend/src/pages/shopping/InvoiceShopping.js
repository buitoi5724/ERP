import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import shoppingService from "../shopping/shoppingService";
import "./InvoiceShopping.css";

/**
 * ERP NOTE:
 * - CHECKOUT / CONFIRM ORDER
 * - KHÔNG phải Invoice thật
 */
const InvoiceShopping = () => {
  const { orderId } = useParams();
  const navigate = useNavigate();

  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showSuccess, setShowSuccess] = useState(false);
  const [showFail, setShowFail] = useState(false);
  const [failMessage, setFailMessage] = useState("");

  // =============================
  // LOAD ORDER
  // =============================
  useEffect(() => {
     
    if (!orderId) {
      setLoading(false);
      return;
    }

    const fetchOrder = async () => {
      try {
        const data = await shoppingService.getOrderById(orderId);

        if (!data || !data.id) {
          throw new Error("Order rỗng");
        }

        setOrder(data);
      } catch (err) {
        console.error("Lỗi khi tải đơn hàng:", err);
        setOrder(null);
      } finally {
        setLoading(false);
      }
    };

    fetchOrder();
  }, [orderId]);

  // =============================
  // FORMAT
  // =============================
  const formatMoney = (num) =>
    Number(num ?? 0).toLocaleString("vi-VN") + "đ";

  const formatDate = (dateStr) =>
    dateStr ? new Date(dateStr).toLocaleDateString("vi-VN") : "Không có";

  // =============================
  // CONFIRM ORDER
  // =============================
  const handleConfirmOrder = async () => {
    try {
      await shoppingService.confirmOrder(order.id);

      // reload order sau confirm
      const updated = await shoppingService.getOrderById(order.id);
      setOrder(updated);

      setShowSuccess(true);
      setShowFail(false);
      setTimeout(() => setShowSuccess(false), 3000);
    } catch (err) {
      const msg = err.response?.data || err.message;
      setFailMessage(msg);
      setShowFail(true);
      setTimeout(() => setShowFail(false), 4000);
    }
  };

  // =============================
  // RENDER
  // =============================
  if (loading) {
    return <div className="loading">🔄 Đang tải đơn hàng...</div>;
  }

  if (!order) {
    
    return (
      <div className="invoice-container">
        <p>❌ Không tìm thấy thông tin đơn hàng.</p>
        <button onClick={() => navigate("/cart")} className="back-btn">
          ← Quay lại giỏ hàng
        </button>
      </div>
    );
  }

  return (
    <div className="invoice-container">
      <h2>📦 XÁC NHẬN ĐƠN HÀNG</h2>

      {showSuccess && (
        <span className="payment-status done">
          ✅ Xác nhận đơn hàng thành công
        </span>
      )}

      {showFail && (
        <span className="payment-status fail">⚠️ {failMessage}</span>
      )}

      {/* ================= Thông tin đơn hàng ================= */}
      <div className="invoice-section">
        <h3>Thông tin đơn hàng</h3>
        <p><strong>Mã đơn:</strong> {order.code}</p>
        <p><strong>Ngày tạo:</strong> {formatDate(order.createdDate)}</p>
        <p>
          <strong>Trạng thái:</strong>{" "}
          <span className={`status-badge ${order.status}`}>
            {order.status}
          </span>
        </p>
        <p><strong>Thanh toán:</strong> {order.paymentMethod}</p>
      </div>

 {/* ================= Danh sách sản phẩm ================= */}
<div className="invoice-section">
  <h3>Danh sách sản phẩm</h3>

  {order.items && order.items.length > 0 ? (
  <table className="invoice-table">
  <thead>
    <tr>
      <th>Tên sản phẩm</th>
      <th className="number">Số lượng</th>
      <th className="number">Đơn giá</th>
      <th className="number">Thành tiền</th>
    </tr>
  </thead>
  <tbody>
    {order.items.map((item, idx) => (
      <tr key={idx}>
        <td>{item.productName || "Sản phẩm"}</td>
        <td className="number">{item.quantity}</td>
        <td className="number">{formatMoney(item.price)}</td>
        <td className="number">{formatMoney(item.quantity * item.price)}</td>
      </tr>
    ))}
  </tbody>
</table>
  ) : (
    <p>Không có sản phẩm trong đơn.</p>
  )}
</div>

      {/* ================= Actions ================= */}
      <div className="invoice-actions">
        <button onClick={() => navigate("/cart")} className="back-btn">
          ← Quay lại giỏ hàng
        </button>

        {order.status === "PENDING" ? (
          <button className="pay-btn" onClick={handleConfirmOrder}>
            ✔️ Xác nhận đơn hàng
          </button>
        ) : (
          <span className="payment-status done">
            ✅ Đơn hàng đã được xử lý
          </span>
        )}
      </div>
    </div>
  );
};

export default InvoiceShopping;
