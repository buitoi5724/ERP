import { useState, useEffect } from "react";
import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";
import { InputTextarea } from "primereact/inputtextarea";
import { Dropdown } from "primereact/dropdown";
import "primeicons/primeicons.css";
import {
  getProductById,
  getCategories,
  createProduct,
  updateProduct,
  buildImageUrl, // ✅ thêm hàm này từ service
} from "./productService";
import "./ProductForm.css";

const makeId = () => `${Date.now()}_${Math.random().toString(36).slice(2, 9)}`;

const ProductForm = ({ selectedId, onSuccess, onCancel }) => {
  const [product, setProduct] = useState({
    name: "",
    price: "",
    description: "",
    category: null,
  });

  const [priceError, setPriceError] = useState("");
  const [previewItems, setPreviewItems] = useState([]);
  const [categories, setCategories] = useState([]);

  // 📦 Load danh mục
  useEffect(() => {
    getCategories()
      .then((data) => {
        if (Array.isArray(data)) {
          setCategories(data.map((c) => ({ label: c.name, value: c.id })));
        }
      })
      .catch((err) => console.error(err));
  }, []);

  // ✏️ Load sản phẩm khi sửa
  useEffect(() => {
    if (selectedId) {
      getProductById(selectedId).then((data) => {
        setProduct({
          name: data.name || "",
          price: data.price || "",
          description: data.description || "",
          category: data.category?.id || null,
        });

        // ✅ xử lý ảnh dùng hàm trong service
        if (Array.isArray(data.imageUrls) && data.imageUrls.length) {
          const urls = data.imageUrls.map(buildImageUrl);
          const items = urls.map((u) => ({ id: makeId(), src: u }));
          setPreviewItems(items);
        } else if (data.imageUrl) {
          setPreviewItems([{ id: makeId(), src: buildImageUrl(data.imageUrl) }]);
        } else {
          setPreviewItems([]);
        }
      });
    } else {
      setProduct({ name: "", price: "", description: "", category: null });
      setPreviewItems([]);
      setPriceError("");
    }
  }, [selectedId]);

  // 🧩 Handle thay đổi input
  const handleChange = (e) => {
    const { name, value } = e.target;
    setProduct((p) => ({ ...p, [name]: value }));
  };

  // 📷 Xử lý chọn ảnh
  const handleFilesChange = (e) => {
    const files = Array.from(e.target.files);
    const existingKeys = new Set(
      previewItems.map((it) => {
        if (it.file) return `${it.file.name}_${it.file.size}_${it.file.lastModified}`;
        return `SRC_${it.src}`;
      })
    );

    const newItems = [];

    for (const f of files) {
      const key = `${f.name}_${f.size}_${f.lastModified}`;
      if (!existingKeys.has(key)) {
        existingKeys.add(key);
        const blobUrl = URL.createObjectURL(f);
        newItems.push({ id: makeId(), src: blobUrl, file: f });
      }
    }

    if (newItems.length < files.length) {
      alert("Một số ảnh đã bị bỏ qua do trùng lặp.");
    }

    if (newItems.length) {
      setPreviewItems((prev) => [...prev, ...newItems]);
    }

    e.target.value = "";
  };

  // 🗑️ Xóa ảnh
  const removeImage = (id) => {
    setPreviewItems((prev) => {
      const toRemove = prev.find((p) => p.id === id);
      if (toRemove && toRemove.src?.startsWith("blob:")) {
        URL.revokeObjectURL(toRemove.src);
      }
      return prev.filter((p) => p.id !== id);
    });
  };

  // ♻️ Cleanup blob khi unmount
  useEffect(() => {
    return () => {
      previewItems.forEach((p) => {
        if (p.src?.startsWith("blob:")) URL.revokeObjectURL(p.src);
      });
    };
  }, [previewItems]);

  // 💾 Submit form
  const handleSubmit = async (e) => {
  e.preventDefault();

  if (!product.category) {
    alert("Vui lòng chọn loại sản phẩm");
    return;
  }

  if (!product.price || Number(product.price) <= 0) {
    setPriceError("Giá phải lớn hơn 0");
    return;
  } else {
    setPriceError("");
  }

  const productData = {
    name: product.name,
    price: Number(product.price),
    description: product.description,
    category: { id: Number(product.category) },
  };

  const formData = new FormData();
  formData.append(
    "product",
    new Blob([JSON.stringify(productData)], { type: "application/json" })
  );

  // 🟢 1. Ảnh mới (file blob)
  previewItems.forEach((it) => {
    if (it.file) formData.append("images", it.file);
  });

  // 🟢 2. Ảnh cũ (đã có sẵn trên server)
  const existingImages = previewItems
    .filter((it) => !it.file && it.src && !it.src.startsWith("blob:"))
    .map((it) => decodeURIComponent(it.src.split("/image/")[1] || ""));
  formData.append("existingImages", JSON.stringify(existingImages));

  try {
    if (selectedId) {
      // 🔵 Sửa sản phẩm
      await updateProduct(selectedId, formData);
      alert("Cập nhật sản phẩm thành công!");
    } else {
      // 🟢 Thêm mới
      await createProduct(formData);
      alert("Thêm sản phẩm thành công!");
    }

    onSuccess();
  } catch (err) {
    console.error("❌ Lỗi lưu sản phẩm:", err);
    alert("Có lỗi khi lưu sản phẩm. Vui lòng thử lại!");
  }
};
  // 🧭 Render form
  return (
    <form onSubmit={handleSubmit} className="p-fluid product-form">
      <div className="grid form-layout">
        {/* LEFT */}
        <div className="col-12 md:col-7 left-section">
          <div className="field">
            <label htmlFor="name">Tên sản phẩm</label>
            <InputText id="name" name="name" value={product.name} onChange={handleChange} required />
          </div>

          <div className="field">
            <label htmlFor="price">Giá sản phẩm</label>
            <InputText
              id="price"
              name="price"
              type="number"
              value={product.price}
              onChange={handleChange}
              required
            />
            {priceError && <small className="p-error">{priceError}</small>}
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
                <button
                  type="button"
                  className="delete-btn"
                  onClick={() => removeImage(it.id)}
                >
                  ✕
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* RIGHT */}
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

      {/* FOOTER */}
      <div className="dialog-footer">
        <button type="submit" className="save-btn">Lưu sản phẩm</button>
        <button type="button" className="cancel-btn" onClick={onCancel}>
          Thoát
        </button>
      </div>
    </form>
  );
};

export default ProductForm;
