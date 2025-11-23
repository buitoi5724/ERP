import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import shoppingService from "../shopping/shoppingService"; 
import "./InvoiceShopping.css";

const InvoiceShopping = () => {
  const { invoiceId } = useParams();
  const navigate = useNavigate();
  const [invoice, setInvoice] = useState(null);
  const [loading, setLoading] = useState(true);
const [showSuccess, setShowSuccess] = useState(false);
const [showFail, setShowFail] = useState(false);
const [failMessage, setFailMessage] = useState("");
  useEffect(() => {
    if (!invoiceId) {
      setLoading(false);
      return;

      
    }

    const fetchInvoice = async () => {
  try {
    const res = await shoppingService.getInvoiceById(invoiceId);

  
    // Giả lập trường status để disable nút thanh toán
    // Nếu muốn chính xác cần backend trả trường `isPaid` hoặc `status`
    const isPaid = res.paid || false; // nếu backend có trường `paid` hay `isPaid`
    setInvoice({ ...res, status: isPaid ? "DONE" : "PENDING" });

  } catch (err) {
    console.error("Lỗi khi tải hóa đơn:", err);
    alert("Không tìm thấy hóa đơn hoặc có lỗi xảy ra!");
    navigate("/cart");
  } finally {
    setLoading(false);
  }
};
    
    fetchInvoice();
  }, [invoiceId, navigate]);
  if (loading) return <div className="loading">🔄 Đang tải hóa đơn...</div>;
  if (!invoice) return <p>Không tìm thấy thông tin hóa đơn.</p>;

  const formatMoney = (num) =>
    Number(num ?? 0).toLocaleString("vi-VN") + "đ";

  const formatDate = (dateStr) => {
    if (!dateStr) return "Không có";
    return new Date(dateStr).toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };
const handlePayment = async () => {
  try {
    const data = {
      invoiceId: invoice.id,
      paymentMethod: invoice.paymentMethod,
      accountId: 1,
    };

    await shoppingService.payInvoice(data);

    setInvoice((prev) => ({ ...prev, status: "DONE" }));

    // Bật thông báo thành công
    setShowSuccess(true);
    setShowFail(false);
    setTimeout(() => setShowSuccess(false), 3000);

  } catch (err) {
    const msg = err.response?.data || err.message || "Thanh toán thất bại!";
    setFailMessage(msg);
    setShowFail(true);
    setShowSuccess(false);
    setTimeout(() => setShowFail(false), 4000);

    // Nếu backend báo hóa đơn đã thanh toán
    if (msg.includes("Invoice đã được thanh toán")) {
      setInvoice((prev) => ({ ...prev, status: "DONE" }));
    }

    console.error("Lỗi thanh toán:", msg);
  }
};
  return (
    <div className="invoice-container">
      <h2>🧾 HÓA ĐƠN MUA HÀNG</h2>
{showSuccess && (
  <span className="payment-status done">💰 Thanh toán thành công!</span>
)}
{showFail && (
  <span className="payment-status fail">⚠️ {failMessage}</span>
)}
      {/* Thông tin hóa đơn */}
      <div className="invoice-section">
        <h3>📑 Thông tin hóa đơn</h3>
        <p><strong>Mã hóa đơn:</strong> {invoice.orderCode ?? "N/A"}</p>
        <p><strong>Ngày tạo đơn hàng:</strong> {formatDate(invoice.orderDate)}</p>
        <p>
          <strong>Trạng thái:</strong>{" "}
          <span
            className={`status-badge ${
              invoice.status === "DONE" ? "done" : "doing"
            }`}
          >
            {invoice.status === "DONE" ? "Hoàn tất" : "Đang xử lý"}
          </span>
        </p>
        <p><strong>Nhân viên tạo:</strong> {invoice.createdBy ?? "Không có"}</p>
        <p><strong>Ghi chú:</strong> {invoice.note ?? "Không có ghi chú"}</p>
      </div>

  {/* Danh sách sản phẩm */}
<div className="invoice-section">
  <h3>📦 Danh sách sản phẩm</h3>
  {invoice.items && invoice.items.length > 0 ? (
    <table className="invoice-table">
      <thead>
        <tr>
          <th style={{ textAlign: "left" }}>Tên sản phẩm</th>
          <th style={{ textAlign: "right" }}>Số lượng</th>
          <th style={{ textAlign: "right" }}>Đơn giá</th>
          {/* <th style={{ textAlign: "right" }}>Thành tiền</th> */}
        </tr>
      </thead>
      <tbody>
        {invoice.items.map((item, idx) => (
          <tr key={idx}>
            <td style={{ textAlign: "left" }}>{item.productName}</td>
            <td style={{ textAlign: "right" }}>{item.quantity}</td>
            <td style={{ textAlign: "right" }}>{formatMoney(item.price)}</td>
            {/* <td style={{ textAlign: "right" }}>{formatMoney(item.price * item.quantity)}</td> */}
          </tr>
        ))}
      </tbody>
    </table>
  ) : (
    <p>Không có sản phẩm trong hóa đơn này.</p>
  )}
      
      </div>

      {/* Thanh toán */}
 
  {/* Phương thức thanh toán */}
<div className="payment-method">
  <label>
    <input
      type="radio"
      value="cash"
      checked={invoice.paymentMethod === "cash"}
      onChange={(e) =>
        setInvoice((prev) => ({ ...prev, paymentMethod: e.target.value }))
      }
    />
    Tiền mặt
  </label>
  <label>
    <input
      type="radio"
      value="bank"
      checked={invoice.paymentMethod === "bank"}
      onChange={(e) =>
        setInvoice((prev) => ({ ...prev, paymentMethod: e.target.value }))
      }
    />
    Chuyển khoản
  </label>
</div>

{/* Tổng tiền */}
<p><strong>Tổng tiền hàng:</strong> {formatMoney(invoice.subtotal ?? 0)}</p>

{/* Nút hành động */}
<div className="invoice-actions">
  <button onClick={() => navigate("/cart")} className="back-btn">
    ← Quay lại giỏ hàng
  </button>
  <button onClick={() => window.print()} className="print-btn">
    🖨️ In hóa đơn
  </button>

  {/* Hiển thị nút thanh toán chỉ khi chưa thanh toán */}
  {invoice.status !== "DONE" ? (
    <button className="pay-btn" onClick={handlePayment}>
      💰 Thanh toán
    </button>
  ) : (
    // Nếu đã thanh toán, hiển thị chữ Đã thanh toán thay nút
    <span className="payment-status done">
      ✅ Đã thanh toán
    </span>
  )}
</div>
</div>
  );
};

export default InvoiceShopping;
