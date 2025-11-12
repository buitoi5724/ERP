import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
// Đảm bảo bạn import đúng tên service đã export
import shoppingService from "../shopping/shoppingService"; 
import "./InvoiceShopping.css";

const InvoiceShopping = () => {
  const { invoiceId } = useParams();
  const navigate = useNavigate();
  const [invoice, setInvoice] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Logic của bạn đã rất chuẩn:
    // Dòng này ngăn chặn việc gọi API với "undefined"
    if (!invoiceId) {
      setLoading(false); // Dừng loading nếu không có ID
      return;
    }

    const fetchInvoice = async () => {
      try {
        const res = await shoppingService.getInvoiceById(invoiceId);

        // Trường hợp service của bạn trả về null (do đã kiểm tra an toàn)
        if (!res) {
          throw new Error("Không có dữ liệu hóa đơn trả về.");
        }

        // map chắc chắn mỗi item có productName, quantity, price
        if (res.items && res.items.length > 0) {
          res.items = res.items.map((item) => ({
            ...item,
            productName: item.productName ?? "Sản phẩm không xác định",
            quantity: item.quantity ?? 0,
            price: item.price ?? 0,
            // (Bạn có thể thêm imageUrl ở đây nếu DTO backend có)
          }));
        }

        setInvoice(res);
      } catch (err) {
        console.error("Lỗi khi tải hóa đơn:", err);
        alert("Không tìm thấy hóa đơn hoặc có lỗi xảy ra!");
        navigate("/cart"); // Điều hướng về giỏ hàng nếu lỗi
      } finally {
        setLoading(false);
      }
    };

    fetchInvoice();
  }, [invoiceId, navigate]); // Phụ thuộc vào invoiceId và navigate

  if (loading) return <div className="loading">🔄 Đang tải hóa đơn...</div>;
  
  // Hiển thị thông báo nếu invoice không có (sau khi đã loading xong)
  if (!invoice) return <p>Không tìm thấy thông tin hóa đơn.</p>;

  // Hàm format tiền (thêm ?? 0 để an toàn)
  const formatMoney = (num) => Number(num ?? 0).toLocaleString("vi-VN") + "đ";
  
  // Hàm format ngày
  const formatDate = (dateStr) => {
    if (!dateStr) return "Không có";
    return new Date(dateStr).toLocaleDateString("vi-VN", {
      day: '2-digit', month: '2-digit', year: 'numeric'
    });
  };

  return (
    <div className="invoice-container">
      <h2>🧾 HÓA ĐƠN MUA HÀNG</h2>

      {/* Thông tin hóa đơn */}
      <div className="invoice-section">
        <h3>📑 Thông tin hóa đơn</h3>
        <p><strong>Mã hóa đơn:</strong> {invoice.orderCode ?? "N/A"}</p>
        <p><strong>Ngày tạo đơn hàng:</strong> {formatDate(invoice.orderDate)}</p>
        <p><strong>Trạng thái:</strong>{" "}
          <span className={`status-badge ${invoice.status === "DONE" ? "done" : "doing"}`}>
            {invoice.status === "DONE" ? "Hoàn tất" : "Đang xử lý"}
          </span>
        </p>
        {/* Các trường này hiện backend chưa gửi về */}
        <p><strong>Nhân viên tạo:</strong> {invoice.createdBy ?? "Không có"}</p>
        <p><strong>Ghi chú:</strong> {invoice.note ?? "Không có ghi chú"}</p>
      </div>

      {/* Thông tin khách hàng (Các trường này hiện backend chưa gửi về) */}
      <div className="invoice-section">
        <h3>👤 Thông tin khách hàng</h3>
        <p><strong>Tên khách hàng:</strong> {invoice.customerName ?? "Không có"}</p>
        <p><strong>Số điện thoại:</strong> {invoice.phone ?? "Không có"}</p>
        <p><strong>Email:</strong> {invoice.email ?? "Không có"}</p>
        <p><strong>Địa chỉ giao hàng:</strong> {invoice.address ?? "Không có"}</p>
      </div>

      {/* Danh sách sản phẩm */}
      <div className="invoice-section">
        <h3>📦 Danh sách sản phẩm</h3>
        {invoice.items && invoice.items.length > 0 ? (
          <table className="invoice-table">
            <thead>
              <tr>
                <th>Tên sản phẩm</th>
                <th>Số lượng</th>
                <th>Đơn giá</th>
                <th>Thành tiền</th>
              </tr>
            </thead>
            <tbody>
              {invoice.items.map((item, idx) => (
                <tr key={idx}>
                  <td>{item.productName}</td>
                  <td>{item.quantity}</td>
                  <td>{formatMoney(item.price)}</td>
                  <td>{formatMoney(item.price * item.quantity)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>Không có sản phẩm trong hóa đơn này.</p>
        )}
      </div>

      {/* Tính toán tiền (Các trường này hiện backend chưa gửi về, trừ totalAmount) */}
      <div className="invoice-section total-section">
        <h3>💳 Thanh toán</h3>
        <p><strong>Tổng tiền hàng:</strong> {formatMoney(invoice.subtotal ?? 0)}</p>
        <p><strong>Thuế (VAT):</strong> {formatMoney(invoice.tax ?? 0)}</p>
        <p><strong>Phí vận chuyển:</strong> {formatMoney(invoice.shippingFee ?? 0)}</p>
        <p><strong>Giảm giá:</strong> {formatMoney(invoice.discount ?? 0)}</p>
        <hr />
        <p className="total-amount">
          <strong>Tổng cộng thanh toán:</strong> {formatMoney(invoice.totalAmount ?? 0)}
        </p>
        <p><strong>Phương thức thanh toán:</strong> {invoice.paymentMethod ?? "Chưa chọn"}</p>
      </div>

      {/* Nút hành động */}
      <div className="invoice-actions">
        <button onClick={() => navigate("/cart")} className="back-btn">
          ← Quay lại giỏ hàng
        </button>
        <button onClick={() => window.print()} className="print-btn">
          🖨️ In hóa đơn
        </button>
      </div>
    </div>
  );
};

export default InvoiceShopping;