import React, { useState, useMemo } from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { InputText } from "primereact/inputtext";
import { Button } from "primereact/button";
import { Tag } from "primereact/tag";
import { Dropdown } from "primereact/dropdown"; 
import "./productList.css";
import { addToCart } from "./cartService";

const ProductList = ({ products = [], onEdit, onDelete, onDetail }) => {
  const [filters, setFilters] = useState({
    name: "",
    priceFrom: "",
    priceTo: ""
  });

  // 👉 danh sách mức giá
  const priceOptions = [
  { label: "Tất cả", value: "" },
  { label: "Dưới 1 triệu", value: "0,1000000" },
  { label: "1 - 5 triệu", value: "1000000,5000000" },
  { label: "5 - 10 triệu", value: "5000000,10000000" },
  { label: "Trên 10 triệu", value: "10000000," },
];

  // Lọc sản phẩm
  const filteredProducts = useMemo(() => {
    return products.filter(product => {
      const nameMatch =
        (product.name || "").toLowerCase().includes(filters.name.toLowerCase()) ||
        (product.category?.name || "").toLowerCase().includes(filters.name.toLowerCase());

      const price = parseFloat(product.price);
      const priceFrom = parseFloat(filters.priceFrom);
      const priceTo = parseFloat(filters.priceTo);

      const priceFromMatch = isNaN(priceFrom) || price >= priceFrom;
      const priceToMatch = isNaN(priceTo) || price <= priceTo;

      return nameMatch && priceFromMatch && priceToMatch;
    });
  }, [products, filters]);

  // Nhập từ khóa
  const handleInputChange = (e) => {
    const { value } = e.target;
    setFilters(prev => ({ ...prev, name: value }));
  };

  // Chọn giá
 
  const handlePriceChange = (e) => {
  const value = e.value || ""; // PrimeReact Dropdown trả về e.value
  if (!value) {
    setFilters((prev) => ({ ...prev, priceFrom: "", priceTo: "" }));
    return;
  }
  const [min, max] = String(value).split(",").map((v) => (v ? Number(v) : ""));
  setFilters((prev) => ({ ...prev, priceFrom: min, priceTo: max }));
};

  // Reset
  const handleClear = () => {
    setFilters({ name: "", priceFrom: "", priceTo: "" });
  };

  // Template ảnh
  const getImageSrc = (product) => {
    if (!product?.id) return "/images/default-product.png";
    return `http://localhost:8080/api/products/get-image/${product.id}`;
  };
  const imageBodyTemplate = (product) => (
    <img src={getImageSrc(product)} alt={product.name} className="w-6rem shadow-2 border-round" />
  );
  const priceBodyTemplate = (product) =>
    product.price != null
      ? product.price.toLocaleString("vi-VN", { style: "currency", currency: "VND" })
      : "Chưa có";
  const statusBodyTemplate = (product) => {
    const severity =
      product?.inventoryStatus === "INSTOCK"
        ? "success"
        : product?.inventoryStatus === "LOWSTOCK"
        ? "warning"
        : "danger";
    return <Tag value={product?.inventoryStatus || "Chưa rõ"} severity={severity}></Tag>;
  };
  const actionBodyTemplate = (product) => (
    <div className="flex gap-2">
      <Button icon="pi pi-eye" rounded text severity="info" tooltip="Chi tiết" onClick={() => onDetail(product)} />
      <Button icon="pi pi-pencil" rounded text severity="warning" tooltip="Sửa" onClick={() => onEdit(product.id)} />
      <Button icon="pi pi-trash" rounded text severity="danger" tooltip="Xoá" onClick={() => onDelete(product.id)} />
            <Button

        className="p-button-sm p-button-success"
        style={{ width: 'auto' }}
        onClick={() => addToCart(product)}
    />
    </div>
  );

  // 👉 Header có search + dropdown
  const header = (
    <div className="flex justify-content-between align-items-center gap-2">
      <h3 className="mt-0 mb-0">Danh sách sản phẩm</h3>

      <div className="flex align-items-center gap-2">
        <InputText
          value={filters.name}
          onChange={handleInputChange}
          placeholder="Tìm sản phẩm / Thương hiệu / Shop / Loại..."
        />
        <Dropdown
          value={filters.priceFrom ? `${filters.priceFrom}-${filters.priceTo}` : ""}
          options={priceOptions}
          onChange={handlePriceChange}
          placeholder="Chọn mức giá"
          className="w-12rem"
        />
        <Button
          icon="pi pi-filter-slash"
          className="p-button-text"
          onClick={handleClear}
         
        />
      </div>
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
      >
        <Column field="name" header="Tên sản phẩm" sortable />
        <Column header="Ảnh" body={imageBodyTemplate} />
        <Column field="price" header="Giá" body={priceBodyTemplate} sortable />
        <Column field="category.name" header="Loại" />
        <Column header="Trạng thái" body={statusBodyTemplate} />
        <Column header="Hành động" body={actionBodyTemplate} />
      </DataTable>
    </div>
  );
};

export default ProductList;
