import React, { useState } from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { InputText } from "primereact/inputtext";
import { Button } from "primereact/button";
import { Tag } from "primereact/tag";
import "./productList.css";

const ProductList = ({ products = [], onEdit, onDelete, onDetail }) => {
  const [searchTerm, setSearchTerm] = useState("");

  // Lọc theo tên
  const filteredProducts = products.filter((p) => {
    const name = (p?.name || "").toLowerCase();
    return name.includes(searchTerm.toLowerCase());
  });

  // Hàm lấy ảnh
  const getImageSrc = (product) => {
    if (!product?.id) return "/images/default-product.png";
    return `http://localhost:8080/api/products/get-image/${product.id}`;
  };
  // Template ảnh
  const imageBodyTemplate = (product) => {
    return (
      <img
        src={getImageSrc(product)}
        alt={product?.name || "Không có tên"}
        className="w-6rem shadow-2 border-round"
      />
    );
  };
  // Template giá
  const priceBodyTemplate = (product) => {
    return product?.price != null
      ? product.price.toLocaleString("vi-VN", {
          style: "currency",
          currency: "VND",
        })
      : "Chưa có";
  };
  // Template trạng thái (demo, bạn có thể thay bằng field `inventoryStatus` nếu backend có)
  const statusBodyTemplate = (product) => {
    const severity =
      product?.inventoryStatus === "INSTOCK"
        ? "success"
        : product?.inventoryStatus === "LOWSTOCK"
        ? "warning"
        : "danger";
    return (
      <Tag
        value={product?.inventoryStatus || "Chưa rõ"}
        severity={severity}
      ></Tag>
    );
  };
  // Template nút hành động
  const actionBodyTemplate = (product) => {
    return (
      <div className="flex gap-2">
        <Button
          icon="pi pi-eye"
          rounded
          text
          severity="info"
          tooltip="Chi tiết"
          onClick={() => onDetail(product)}
        />
        <Button
          icon="pi pi-pencil"
          rounded
          text
          severity="warning"
          tooltip="Sửa"
          onClick={() => onEdit(product.id)}
        />
        <Button
          icon="pi pi-trash"
          rounded
          text
          severity="danger"
          tooltip="Xoá"
          onClick={() => onDelete(product.id)}
        />
      </div>
    );
  };
  const header = (
    <div className="flex justify-content-between align-items-center">
      <h3>Danh sách sản phẩm</h3>
      <span className="">
        <InputText
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Tìm kiếm theo tên..."
        />
      </span>
    </div>
  );

  return (
    <div className="card">
      <DataTable
        value={filteredProducts}
        header={header}
        paginator
        rows={5}
        rowsPerPageOptions={[5, 10, 20]}
        emptyMessage="Không tìm thấy sản phẩm phù hợp."
        tableStyle={{ minWidth: "70rem" }}
      >
        <Column field="name" header="Tên sản phẩm" sortable></Column>
        <Column header="Ảnh" body={imageBodyTemplate}></Column>
        <Column field="price" header="Giá" body={priceBodyTemplate} sortable></Column>
        <Column field="category.name" header="Loại"></Column>
        <Column header="Trạng thái" body={statusBodyTemplate}></Column>
        <Column header="Hành động" body={actionBodyTemplate}></Column>
      </DataTable>
    </div>
  );
};

export default ProductList;
