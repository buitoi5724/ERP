import { useState, useEffect } from "react";
import { InputText } from "primereact/inputtext";
import { InputTextarea } from "primereact/inputtextarea";
import { Dropdown } from "primereact/dropdown";
import "primeicons/primeicons.css";
import {
  getProductById,
  getCategories,
  createProduct,
  updateProduct,
  buildImageUrl,
  deleteProductImage,
} from "./productService";
import "./ProductForm.css";

const makeId = () => `${Date.now()}_${Math.random().toString(36).slice(2, 9)}`;

const ProductForm = ({ selectedId, onSuccess, onCancel }) => {
  const [product, setProduct] = useState({
    code: "",
    name: "",
    price: "",
    status: "ACTIVE",
    category: null,
    description: "",
    sizes: "",
    colors: "",
    unit: "",
  });

  const [previewItems, setPreviewItems] = useState([]);
  const [categories, setCategories] = useState([]);

  // 📦 Load danh mục từ backend
  useEffect(() => {
    getCategories()
      .then((data) => {
        if (Array.isArray(data)) {
          setCategories(
            data.map((c) => ({
              label: `${c.name} (${c.productType || "REGULAR"})`,
              value: c.id,
            }))
          );
        }
      })
      .catch((err) => console.error("❌ getCategories error:", err));
  }, []);

  // ✏️ Load sản phẩm khi sửa
  useEffect(() => {
    if (!selectedId) return;
    getProductById(selectedId)
      .then((data) => {
        setProduct({
          code: data.code,
          name: data.name,
          price: data.price,
          status: data.status,
          category: data.category?.id || null,
          description: data.description || "",
          sizes: data.sizes || "",
          colors: data.colors || "",
          unit: data.unit || "",
        });
        if (Array.isArray(data.images)) {
          setPreviewItems(
            data.images.map((img) => ({
              id: makeId(),
              src: buildImageUrl(img.name),
              imageId: img.id,
            }))
          );
        }
      })
      .catch((err) => console.error("❌ getProductById error:", err));
  }, [selectedId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProduct((p) => ({ ...p, [name]: value }));
  };

  const handleFilesChange = (e) => {
    const files = Array.from(e.target.files);
    const existingKeys = new Set(
      previewItems.map((it) =>
        it.file ? `${it.file.name}_${it.file.size}_${it.file.lastModified}` : `SRC_${it.src}`
      )
    );

    const newItems = [];
    for (const f of files) {
      const key = `${f.name}_${f.size}_${f.lastModified}`;
      if (!existingKeys.has(key)) {
        existingKeys.add(key);
        newItems.push({ id: makeId(), src: URL.createObjectURL(f), file: f });
      }
    }

    if (newItems.length < files.length) alert("Một số ảnh đã bị bỏ qua do trùng lặp.");
    if (newItems.length) setPreviewItems((prev) => [...prev, ...newItems]);
    e.target.value = "";
  };

  const removeImage = async (id) => {
    const target = previewItems.find((p) => p.id === id);
    if (!target) return;

    if (target.src.startsWith("blob:")) {
      URL.revokeObjectURL(target.src);
      setPreviewItems((prev) => prev.filter((p) => p.id !== id));
      return;
    }

    if (target.imageId) {
      try {
        await deleteProductImage(target.imageId);
        alert("Đã xóa ảnh khỏi server!");
        setPreviewItems((prev) => prev.filter((p) => p.id !== id));
      } catch (err) {
        console.error("❌ Lỗi khi xóa ảnh:", err);
        alert("Không thể xóa ảnh trên server!");
      }
    }
  };

  useEffect(() => {
    return () => {
      previewItems.forEach((p) => p.src?.startsWith("blob:") && URL.revokeObjectURL(p.src));
    };
  }, [previewItems]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    // ✅ Kiểm tra các trường bắt buộc
    if (!product.code || !product.name || !product.price || !product.category || !product.status) {
      alert("Vui lòng điền đầy đủ các trường bắt buộc!");
      return;
    }

    const productData = {
      code: product.code,
      name: product.name,
      price: parseFloat(product.price),
      status: product.status,
      categoryId: Number(product.category),
      description: product.description,
      sizes: product.sizes,
      colors: product.colors,
      unit: product.unit,
    };

    const formData = new FormData();
    formData.append(
      "product",
      new Blob([JSON.stringify(productData)], { type: "application/json" })
    );

    previewItems.forEach((it) => {
      if (it.file) formData.append("images", it.file);
    });

    const existingImages = previewItems
      .filter((it) => !it.file && it.imageId)
      .map((it) => it.imageId);
    formData.append("existingImages", JSON.stringify(existingImages));

    try {
      if (selectedId) {
        await updateProduct(selectedId, formData);
        alert("Cập nhật sản phẩm thành công!");
      } else {
        await createProduct(formData);
        alert("Thêm sản phẩm thành công!");
      }
      onSuccess();
    } catch (err) {
      console.error("❌ Lỗi lưu sản phẩm:", err);
      alert("Có lỗi khi lưu sản phẩm. Vui lòng thử lại!");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="p-fluid product-form">
      <div className="grid form-layout">
        <div className="col-12 md:col-7 left-section">
          <div className="field">
            <label htmlFor="code">Mã sản phẩm</label>
            <InputText id="code" name="code" value={product.code} onChange={handleChange} required />
          </div>

          <div className="field">
            <label htmlFor="name">Tên sản phẩm</label>
            <InputText id="name" name="name" value={product.name} onChange={handleChange} required />
          </div>

          <div className="field">
            <label htmlFor="price">Giá</label>
            <InputText
              id="price"
              name="price"
              type="number"
              value={product.price}
              onChange={handleChange}
              required
            />
          </div>

          <div className="field">
            <label htmlFor="status">Trạng thái</label>
            <Dropdown
              value={product.status}
              options={[
                { label: "Active", value: "ACTIVE" },
                { label: "Inactive", value: "INACTIVE" },
                { label: "Discontinued", value: "DISCONTINUED" },
              ]}
              onChange={(e) => setProduct((p) => ({ ...p, status: e.value }))}
              placeholder="Chọn trạng thái"
              required
            />
          </div>

          <div className="field">
            <label>Loại sản phẩm</label>
            <Dropdown
              value={product.category}
              options={categories}
              onChange={(e) => setProduct((p) => ({ ...p, category: e.value }))}
              placeholder="Chọn loại sản phẩm"
              required
            />
          </div>

          <div className="field image-upload-section">
            <label htmlFor="images">Ảnh sản phẩm</label>
            <input
              id="images"
              className="file-input"
              type="file"
              multiple
              accept="image/*"
              onChange={handleFilesChange}
            />
          </div>

          <div className="preview-list">
            {previewItems.map((it) => (
              <div key={it.id} className="thumb-wrapper">
                <img src={it.src} alt="preview" className="thumbnail" />
                <button type="button" className="delete-btn" onClick={() => removeImage(it.id)}>✕</button>
              </div>
            ))}
          </div>
        </div>

        <div className="col-12 md:col-5 right-section">
          <div className="field">
            <label htmlFor="description">Mô tả chi tiết sản phẩm</label>
            <InputTextarea
              id="description"
              name="description"
              value={product.description}
              onChange={handleChange}
              rows={20}
              style={{ width: "100%", resize: "vertical" }}
            />
          </div>
        </div>
      </div>

      <div className="dialog-footer">
        <button type="submit" className="save-btn">Lưu sản phẩm</button>
        <button type="button" className="cancel-btn" onClick={onCancel}>Thoát</button>
      </div>
    </form>
  );
};

export default ProductForm;
