import React, { useEffect, useState } from "react";
import { Dialog } from "primereact/dialog";
import { TabView, TabPanel } from "primereact/tabview";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { getPriceHistory } from "./productService";
import "./product.css";

const ProductDetail = ({ visible, onHide, product }) => {
  const [priceHistory, setPriceHistory] = useState([]);
  const [activeImage, setActiveImage] = useState(null);
  const [imageList, setImageList] = useState([]);

  const baseUrl = "http://localhost:8080/api/products/image/"; // ✅ đường dẫn ảnh backend

  useEffect(() => {
    if (product?.id) {
      // 🧾 Lấy lịch sử giá
      getPriceHistory(product.id).then((data) => setPriceHistory(data));

      // 🖼️ Xử lý danh sách ảnh
      if (product.imageUrls && product.imageUrls.length > 0) {
        // Nếu dữ liệu chỉ là tên file → thêm baseUrl phía trước
        const urls = product.imageUrls.map((img) =>
          img.startsWith("http") ? img : `${baseUrl}${encodeURIComponent(img)}`
        );
        setImageList(urls);
        setActiveImage(urls[0]);
      } else if (product.image) {
        // Nếu chỉ có 1 ảnh chính
        setImageList([`${baseUrl}${encodeURIComponent(product.image)}`]);
        setActiveImage(`${baseUrl}${encodeURIComponent(product.image)}`);
      } else {
        // fallback: ảnh mặc định
        setImageList(["/images/default-product.png"]);
        setActiveImage("/images/default-product.png");
      }
    }
  }, [product]);

  if (!product) return null;

  const formatDate = (dateStr) => {
    if (!dateStr) return "";
    return new Intl.DateTimeFormat("vi-VN", {
      dateStyle: "short",
      timeStyle: "short",
    }).format(new Date(dateStr));
  };

  return (
    <Dialog
      header={`Chi tiết sản phẩm: ${product.name}`}
      visible={visible}
      style={{ width: "70vw" }}
      modal
      onHide={onHide}
    >
      <TabView>
        {/* 🧾 Tab thông tin sản phẩm */}
        <TabPanel header="Thông tin sản phẩm">
          <div className="p-fluid">
            <div className="grid">
              {/* Cột thông tin chi tiết */}
              <div className="col-12 md:col-6">
                <p><strong>Tên:</strong> {product.name}</p>
                <p>
                  <strong>Giá hiện tại:</strong>{" "}
                  {product.price != null
                    ? product.price.toLocaleString("vi-VN") + " đ"
                    : "Chưa có"}
                </p>
                <p><strong>Loại:</strong> {product.category?.name || "Chưa có"}</p>
                <p><strong>Mô tả:</strong> {product.description || "Không có mô tả"}</p>
              </div>

              {/* 🖼️ Cột hình ảnh sản phẩm */}
              <div className="col-12 md:col-6 flex flex-column align-items-center">
                {activeImage && (
                  <img
                    src={activeImage}
                    alt="Ảnh chính"
                    className="main-image"
                    onError={(e) => (e.target.src = "/images/default-product.png")}
                  />
                )}
                <div className="thumbs-row mt-3">
                  {imageList.map((img, idx) => (
                    <img
                      key={idx}
                      src={img}
                      alt={`thumb-${idx}`}
                      className={`thumb ${activeImage === img ? "active" : ""}`}
                      onClick={() => setActiveImage(img)}
                      onError={(e) => (e.target.src = "/images/default-product.png")}
                    />
                  ))}
                </div>
              </div>
            </div>
          </div>
        </TabPanel>

        {/* 📈 Tab lịch sử giá */}
        <TabPanel header="Lịch sử giá">
          {priceHistory.length > 0 ? (
            <DataTable
              value={priceHistory}
              paginator
              rows={5}
              responsiveLayout="scroll"
            >
              <Column field="id" header="ID" style={{ width: "70px" }} />
              <Column
                field="price"
                header="Giá"
                body={(rowData) =>
                  `${Number(rowData.price).toLocaleString()} đ`
                }
              />
              <Column
                field="active"
                header="Kích hoạt"
                body={(rowData) => (rowData.active ? "✔️" : "❌")}
              />
              <Column
                field="createdAt"
                header="Ngày tạo"
                body={(rowData) => formatDate(rowData.createdAt)}
              />
            </DataTable>
          ) : (
            <p>Chưa có lịch sử giá.</p>
          )}
        </TabPanel>
      </TabView>
    </Dialog>
  );
};

export default ProductDetail;
