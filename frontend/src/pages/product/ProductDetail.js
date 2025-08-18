import React from "react";
import { Dialog } from "primereact/dialog";
import { TabView, TabPanel } from "primereact/tabview";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";

const ProductDetail = ({ visible, onHide, product }) => {
  if (!product) return null;

  const priceHistory = product.priceHistory || [];

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
      style={{ width: "60vw" }}
      modal
      onHide={onHide}
    >
      <TabView>
        {/* Tab thông tin sản phẩm */}
        <TabPanel header="Thông tin sản phẩm">
          <div className="p-fluid">
            <p><strong>Tên:</strong> {product.name}</p>
            <p>
              <strong>Giá hiện tại:</strong>{" "}
              {product.price != null ? product.price.toLocaleString() + " đ" : "Chưa có"}
            </p>
            <p><strong>Loại:</strong> {product.category?.name || "Chưa có"}</p>
            <p><strong>Mô tả:</strong> {product.description || "Không có mô tả"}</p>
            <img
              src={`http://localhost:8080/api/products/get-image/${product.id}`}
              alt={product.name}
              style={{ maxWidth: "200px", marginTop: "1rem" }}
            />
          </div>
        </TabPanel>

        {/* Tab lịch sử giá */}
        <TabPanel header="Lịch sử giá">
          {priceHistory.length > 0 ? (
            <DataTable value={priceHistory} paginator rows={5} responsiveLayout="scroll">
              <Column field="id" header="ID" style={{ width: "70px" }} />
              <Column
                field="price"
                header="Giá"
                body={(rowData) => `${Number(rowData.price).toLocaleString()} đ`}
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
