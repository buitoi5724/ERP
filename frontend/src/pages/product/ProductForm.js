import { useState, useEffect } from 'react';
import { Button } from 'primereact/button';
import { InputText } from 'primereact/inputtext';
import { FileUpload } from 'primereact/fileupload';
import { InputTextarea } from 'primereact/inputtextarea';
import { Dropdown } from 'primereact/dropdown';
import 'primeicons/primeicons.css';
import {
  getProductById,
  getCategories,
  createProduct,
  updateProduct,
} from './productService';
import './ProductForm.css';  // 👈 thêm file css để chỉnh layout và thumbnail

const ProductForm = ({ selectedId, onSuccess, onCancel }) => {
  const [product, setProduct] = useState({
    name: '',
    price: '',
    description: '',
    category: null,
  });

  const [priceError, setPriceError] = useState('');
  const [files, setFiles] = useState([]); // ảnh mới
  const [newPreviewUrls, setNewPreviewUrls] = useState([]); // preview ảnh mới
  const [oldPreviewUrls, setOldPreviewUrls] = useState([]); // ảnh cũ từ server
  const [categories, setCategories] = useState([]);

  // 🔹 Lấy danh mục
  useEffect(() => {
    getCategories()
      .then((data) => {
        if (Array.isArray(data)) {
          setCategories(data.map((cat) => ({ label: cat.name, value: cat.id })));
        }
      })
      .catch((err) => console.error('Lỗi khi lấy danh mục:', err));
  }, []);

  // 🔹 Load sản phẩm khi sửa
  useEffect(() => {
    if (selectedId) {
      getProductById(selectedId).then((data) => {
        setProduct({
          name: data.name || '',
          price: data.price || '',
          description: data.description || '',
          category: data.category?.id || null,
        });

        // ảnh cũ
        if (data.imageUrls && Array.isArray(data.imageUrls)) {
          setOldPreviewUrls(data.imageUrls);
        } else if (data.imageUrl) {
          setOldPreviewUrls([data.imageUrl]);
        } else {
          setOldPreviewUrls([]);
        }

        // reset ảnh mới
        setFiles([]);
        setNewPreviewUrls([]);
      });
    } else {
      // reset khi thêm mới
      setProduct({ name: '', price: '', description: '', category: null });
      setFiles([]);
      setOldPreviewUrls([]);
      setNewPreviewUrls([]);
      setPriceError('');
    }
  }, [selectedId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProduct((prev) => ({ ...prev, [name]: value }));
  };

  // 🔹 Khi chọn ảnh mới
  const handleFilesChange = (e) => {
    const selectedFiles = Array.from(e.files);
    setFiles((prev) => [...prev, ...selectedFiles]);
    const preview = selectedFiles.map((file) => URL.createObjectURL(file));
    setNewPreviewUrls((prev) => [...prev, ...preview]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!product.category) {
      alert('Vui lòng chọn loại sản phẩm');
      return;
    }

    if (!product.price || Number(product.price) <= 0) {
      setPriceError('Giá phải lớn hơn 0');
      return;
    } else {
      setPriceError('');
    }

    const productData = {
      name: product.name,
      price: Number(product.price),
      description: product.description,
      category: { id: Number(product.category) },
    };

    const formData = new FormData();
    formData.append(
      'product',
      new Blob([JSON.stringify(productData)], { type: 'application/json' })
    );

    files.forEach((f) => formData.append('images', f));

    try {
      if (selectedId) {
        await updateProduct(selectedId, formData);
      } else {
        await createProduct(formData);
      }
      onSuccess();
    } catch (error) {
      console.error('Lỗi khi lưu sản phẩm:', error);
      alert('Có lỗi xảy ra, vui lòng thử lại!');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="p-fluid product-form">
      <div className="grid form-layout">
        {/* === BÊN TRÁI === */}
        <div className="col-12 md:col-7 left-section">
          <div className="field">
            <label htmlFor="name">Tên sản phẩm</label>
            <InputText
              id="name"
              name="name"
              value={product.name}
              onChange={handleChange}
              required
            />
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
              onChange={(e) =>
                setProduct((prev) => ({ ...prev, category: e.value }))
              }
              placeholder="Chọn loại sản phẩm"
              required
            />
          </div>

          <div className="field">
            <label>Ảnh sản phẩm</label>
            <FileUpload
              name="images"
              mode="advanced"
              multiple
              accept="image/*"
              maxFileSize={5000000}
              customUpload
              onSelect={handleFilesChange}
              chooseLabel="Chọn ảnh"
              uploadLabel="Tải lên"
              cancelLabel="Hủy"
            />

            {/* ✅ Ảnh preview (gồm ảnh cũ + ảnh mới) */}
            <div className="preview-list">
              {oldPreviewUrls.map((url, i) => (
                <img
                  key={`old-${i}`}
                  src={url}
                  alt={`old-${i}`}
                  className="thumbnail"
                  title="Ảnh cũ"
                />
              ))}
              {newPreviewUrls.map((url, i) => (
                <img
                  key={`new-${i}`}
                  src={url}
                  alt={`new-${i}`}
                  className="thumbnail new"
                  title="Ảnh mới"
                />
              ))}
            </div>
          </div>
        </div>

        {/* === BÊN PHẢI === */}
        <div className="col-12 md:col-5 right-section">
          <div className="field">
            <label htmlFor="description">Mô tả chi tiết sản phẩm</label>
            <InputTextarea
              id="description"
              name="description"
              value={product.description}
              onChange={handleChange}
              rows={20}
              style={{ width: '100%', resize: 'vertical' }}
            />
          </div>
        </div>
      </div>

      {/* === Nút hành động === */}
      <div className="actions">
        <Button
          type="button"
          label="Thoát"
          className="p-button-secondary p-button-sm"
          onClick={onCancel}
        />
        <Button
          type="submit"
          label={selectedId ? 'Cập nhật' : 'Thêm mới'}
          className="p-button-sm"
        />
      </div>
    </form>
  );
};

export default ProductForm;
