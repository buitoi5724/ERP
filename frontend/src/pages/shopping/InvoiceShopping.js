import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import shoppingService from "../shopping/shoppingService";
import "./InvoiceShopping.css";

/**
 * ERP NOTE:
 * - CHECKOUT / CONFIRM ORDER
 * - BANK_TRANSFER: phải thanh toán trước
 * - COD: cho confirm ngay
 */
const InvoiceShopping = () => {
  const { orderId } = useParams();
  const navigate = useNavigate();

  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [successMsg, setSuccessMsg] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

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
        setOrder(data);
      } catch (err) {
        console.error(err);
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
  const formatMoney = (n) =>
    Number(n ?? 0).toLocaleString("vi-VN") + "đ";

  const formatDate = (d) =>
    d ? new Date(d).toLocaleDateString("vi-VN") : "-";

  // =============================
  // CONFIRM ORDER
  // =============================
const handleConfirmOrder = async () => {
  try {
    setErrorMsg("");
    setSuccessMsg("");

    await shoppingService.confirmOrder(order.id);
    await shoppingService.clearCartByUser(1);

    const updated = await shoppingService.getOrderById(order.id);
    setOrder(updated);

    setSuccessMsg("Xác nhận đơn hàng thành công");

    setTimeout(() => {
      setSuccessMsg("");
      navigate("/cart");
    }, 2000);

  } catch (err) {
    const msg =
      err.response?.data?.message ||
      err.response?.data?.error ||
      err.message ||
      "Xác nhận đơn hàng thất bại";

    setErrorMsg(msg);
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
        <p>❌ Không tìm thấy đơn hàng</p>
        <button onClick={() => navigate("/cart")} className="back-btn">
          ← Quay lại giỏ hàng
        </button>
      </div>
    );
  }

  const isPending = order.status === "PENDING";
  const isBankTransfer = order.paymentMethod === "BANK_TRANSFER";
  const isPaid = order.paymentStatus === "PAID";

  return (
    <div className="invoice-container">
      <h2>📦 XÁC NHẬN ĐƠN HÀNG</h2>

      {successMsg && <div className="payment-status done">✅ {successMsg}</div>}
      {errorMsg && <div className="payment-status fail">⚠️ {errorMsg}</div>}

      {/* ===== Order Info ===== */}
      <div className="invoice-section">
        <h3>Thông tin đơn hàng</h3>
        <p><b>Mã đơn:</b> {order.code}</p>
        <p><b>Ngày tạo:</b> {formatDate(order.createdDate)}</p>
        <p>
          <b>Trạng thái:</b>{" "}
          <span className={`status-badge ${order.status}`}>
            {order.status}
          </span>
        </p>
        <p><b>Thanh toán:</b> {order.paymentMethod}</p>
        <p><b>Trạng thái thanh toán:</b> {order.paymentStatus}</p>
      </div>

      {/* ===== BANK TRANSFER QR ===== */}
      {isBankTransfer && !isPaid && (
        <div className="invoice-section qr-section">
          <h3>💳 Thanh toán chuyển khoản</h3>
          <p>Quét QR để chuyển khoản</p>
          <img
            src="/qr-bank.png"
            alt="QR chuyển khoản"
            className="qr-image"
          />
          <p>
            Nội dung chuyển khoản: <b>{order.code}</b>
          </p>
          <p className="note">
            ⏳ Sau khi thanh toán, hệ thống sẽ tự động cập nhật
          </p>
        </div>
      )}

      {/* ===== Order Items ===== */}
      <div className="invoice-section">
        <h3>Danh sách sản phẩm</h3>

        <table className="invoice-table">
          <thead>
            <tr>
              <th>Tên sản phẩm</th>
              <th className="number">SL</th>
              <th className="number">Đơn giá</th>
              <th className="number">Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            {order.items.map((i, idx) => (
              <tr key={idx}>
                <td>{i.productName}</td>
                <td className="number">{i.quantity}</td>
                <td className="number">{formatMoney(i.price)}</td>
                <td className="number">
                  {formatMoney(i.price * i.quantity)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* ===== Actions ===== */}
      <div className="invoice-actions">
        <button onClick={() => navigate("/cart")} className="back-btn">
          ← Quay lại giỏ hàng
        </button>

        {/* COD: confirm ngay */}
        {isPending && !isBankTransfer && (
          <button className="pay-btn" onClick={handleConfirmOrder}>
            ✔️ Xác nhận đơn hàng
          </button>
        )}

        {/* BANK_TRANSFER: chỉ confirm khi PAID */}
        {isPending && isBankTransfer && isPaid && (
          <button className="pay-btn" onClick={handleConfirmOrder}>
            ✔️ Xác nhận đơn hàng
          </button>
        )}

        {!isPending && (
          <span className="payment-status done">
            ✅ Đơn hàng đã được xử lý
          </span>
        )}
      </div>
    </div>
  );
};

export default InvoiceShopping;
