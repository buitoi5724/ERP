import React, { useState, useMemo, useCallback } from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { InputText } from "primereact/inputtext";
import { Button } from "primereact/button";
import { Tag } from "primereact/tag";
import { Dropdown } from "primereact/dropdown";
import { Dialog } from "primereact/dialog";
import { RadioButton } from "primereact/radiobutton";
import { Galleria } from "primereact/galleria";
import "./productList.css";
import { updateMainImage, buildImageUrl } from "./productService";

const ProductList = ({ products = [], onEdit, onDelete, onDetail }) => {
  const [filters, setFilters] = useState({ name: "", priceFrom: "", priceTo: "" });
  const [visible, setVisible] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [selectedMainImage, setSelectedMainImage] = useState("");
  const [activeIndex, setActiveIndex] = useState(0);

  // 🔍 Lọc sản phẩm
  const filteredProducts = useMemo(() => {
    return products.filter((product) => {
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

  const handleInputChange = (e) => setFilters((p) => ({ ...p, name: e.target.value }));

  const handlePriceChange = (e) => {
    const value = e.value || "";
    if (!value) return setFilters({ name: "", priceFrom: "", priceTo: "" });
    const [min, max] = String(value).split(",").map((v) => (v ? Number(v) : ""));
    setFilters((p) => ({ ...p, priceFrom: min, priceTo: max }));
  };

  const handleClear = () => setFilters({ name: "", priceFrom: "", priceTo: "" });

  // 🖼️ Hiển thị ảnh trong bảng
  const imageBodyTemplate = useCallback((product) => {
    const mainImage =
      product.image && product.image !== "null" && product.image !== ""
        ? buildImageUrl(product.image)
        : "/images/default-product.png";

    return (
      <img
        src={mainImage}
        alt={product.name}
        className="w-6rem border-round shadow-2 cursor-pointer"
        onClick={() => {
          if (product.imageUrls?.length > 0) {
            setSelectedProduct(product);
            setSelectedMainImage(product.image);
            setActiveIndex(0);
            setVisible(true);
          }
        }}
        onError={(e) => (e.target.src = "/images/default-product.png")}
      />
    );
  }, []);

  // 🧩 Cập nhật ảnh đại diện
  const handleSetMainImage = useCallback(
    async (imageUrl) => {
      if (!selectedProduct) return;
      try {
        await updateMainImage(selectedProduct.id, imageUrl);
        alert("✅ Đã đặt ảnh đại diện thành công!");
        setSelectedMainImage(decodeURIComponent(imageUrl));
      } catch (error) {
        console.error("❌ Lỗi cập nhật ảnh đại diện:", error);
        alert("❌ Cập nhật thất bại!");
      }
    },
    [selectedProduct]
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
    </div>
  );

  const header = (
    <div className="flex justify-content-between align-items-center gap-2">
      <h3 className="mt-0 mb-0">Danh sách sản phẩm</h3>
      <div className="flex align-items-center gap-2">
        <InputText value={filters.name} onChange={handleInputChange} placeholder="Tìm sản phẩm / Loại..." />
        <Dropdown
          value={filters.priceFrom ? `${filters.priceFrom}-${filters.priceTo}` : ""}
          options={[
            { label: "Tất cả", value: "" },
            { label: "Dưới 1 triệu", value: "0,1000000" },
            { label: "1 - 5 triệu", value: "1000000,5000000" },
            { label: "5 - 10 triệu", value: "5000000,10000000" },
            { label: "Trên 10 triệu", value: "10000000," },
          ]}
          onChange={handlePriceChange}
          placeholder="Chọn mức giá"
          className="w-12rem"
        />
        <Button icon="pi pi-filter-slash" className="p-button-text" onClick={handleClear} />
      </div>
    </div>
  );

  // 🖼️ Hiển thị từng ảnh trong Galleria
  const itemTemplate = (item) => {
    const filename = decodeURIComponent(item.split("/image/")[1] || "");
    const isSelected = selectedMainImage === filename;

    return (
      <div className="flex flex-column align-items-center gap-3">
        <img src={item} alt="Ảnh sản phẩm" className="gallery-image" />
        <div className="flex align-items-center gap-2">
          <RadioButton
            inputId={filename}
            name="mainImage"
            value={filename}
            onChange={(e) => {
              setSelectedMainImage(e.value);
              handleSetMainImage(e.value);
            }}
            checked={isSelected}
          />
          <label htmlFor={filename}>
            {isSelected ? "Ảnh đại diện hiện tại" : "Chọn làm ảnh đại diện"}
          </label>
        </div>
      </div>
    );
  };

  const thumbnailTemplate = (item) => (
    <img src={item} alt="thumb" style={{ width: "100px", height: "70px", objectFit: "cover" }} />
  );

  return (
    <>
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

      {/* 🖼️ Galleria trong Dialog */}
      <Dialog
        visible={visible}
        onHide={() => setVisible(false)}
        header={selectedProduct?.name || "Bộ sưu tập ảnh"}
        style={{ width: "60vw" }}
      >
        {selectedProduct && (
          <Galleria
            value={selectedProduct.imageUrls.map((img) => buildImageUrl(img))}
            numVisible={5}
            circular
            showThumbnails
            showItemNavigators
            activeIndex={activeIndex}
            onItemChange={(e) => setActiveIndex(e.index)}
            item={itemTemplate}
            thumbnail={thumbnailTemplate}
          />
        )}
      </Dialog>
    </>
  );
};

export default ProductList;
