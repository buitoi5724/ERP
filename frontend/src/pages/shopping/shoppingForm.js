import { useState, useEffect } from 'react';
import { Button } from 'primereact/button';
import { InputText } from 'primereact/inputtext';
import { FileUpload } from 'primereact/fileupload';
import { InputTextarea } from 'primereact/inputtextarea';
import { Dropdown } from 'primereact/dropdown';
import 'primeicons/primeicons.css';  
import { getProductById, getImage, getCategories, createProduct, updateProduct } from './shoppingService';

const ProductForm = ({ selectedId, onSuccess, onCancel }) => {
    const [product, setProduct] = useState({
        name: '',
        price: '',
        description: '',
        category: null,
    });
    const [priceError, setPriceError] = useState('');
    const [file, setFile] = useState(null);
    const [imageUrl, setImageUrl] = useState(null);
    const [categories, setCategories] = useState([]);

    // Lấy danh mục
    useEffect(() => {
        getCategories()
            .then((data) => {
                if (Array.isArray(data)) {
                    setCategories(data.map((cat) => ({
                        label: cat.name,
                        value: cat.id
                    })));
                }
            })
            .catch((err) => console.error("Lỗi khi lấy danh mục:", err));
    }, []);

    // Load sản phẩm khi sửa
    useEffect(() => {
        if (selectedId) {
            getProductById(selectedId).then(data => {
                setProduct({
                    name: data.name || '',
                    price: data.price || '',
                    description: data.description || '',
                    category: data.category?.id || null,
                });
                if (data.image) {
                    getImage(data).then(response => {
                        const blob = new Blob([response.data], { type: 'image/jpeg' });
                        setImageUrl(URL.createObjectURL(blob));
                    });
                }
            });
        } else {
            setProduct({ name: '', price: '', description: '', category: null });
            setImageUrl(null);
            setFile(null);
            setPriceError('');
        }
    }, [selectedId]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setProduct(prev => ({ ...prev, [name]: value }));
    };

    const handleFileChange = (e) => {
        const selectedFile = e.files[0];
        setFile(selectedFile);
        if (selectedFile) {
            setImageUrl(URL.createObjectURL(selectedFile));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validate category
        if (!product.category) {
            alert('Vui lòng chọn loại sản phẩm');
            return;
        }

        // Validate price
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
            category: { id: Number(product.category) }
        };

        const formData = new FormData();
        formData.append('product', new Blob([JSON.stringify(productData)], { type: 'application/json' }));
        if (file) {
            formData.append('image', file);
        }

        try {
            if (selectedId) {
                await updateProduct(selectedId, formData); // 👈 chỉ truyền formData
            } else {
                await createProduct(formData); // 👈 chỉ truyền formData
            }
            onSuccess();
        } catch (error) {
            console.error("Lỗi khi lưu sản phẩm:", error);
            alert("Có lỗi xảy ra, vui lòng thử lại!");
        }
    };

    return (
       <form onSubmit={handleSubmit} className="p-fluid">
            <div className="grid">
                {/* Cột trái */}
                <div className="col-12 md:col-6">
                    <div className="field">
                        <label htmlFor="name">Tên sản phẩm</label>
                        <InputText id="name" name="name" value={product.name} onChange={handleChange} required />
                    </div>

                    <div className="field">
                        <label htmlFor="price">Giá sản phẩm</label>
                        <InputText id="price" name="price" type="number" value={product.price} onChange={handleChange} required />
                        {priceError && <small className="p-error">{priceError}</small>}
                    </div>

                    <div className="field">
                        <label>Loại sản phẩm</label>
                        <Dropdown
                            value={product.category}
                            options={categories}
                            onChange={(e) => setProduct(prev => ({ ...prev, category: e.value }))}
                            placeholder="Chọn loại sản phẩm"
                            required
                        />
                    </div>
                </div>

                {/* Cột phải */}
                <div className="col-12 md:col-6">
                    <div className="field">
                        <label htmlFor="description">Mô tả</label>
                        <InputTextarea
                            id="description"
                            name="description"
                            value={product.description}
                            onChange={handleChange}
                            rows={5}
                        />
                    </div>

                    <div className="field">
                        <label>Ảnh sản phẩm</label>
                        <FileUpload
                            name="image"
                            mode="basic"
                            accept="image/*"
                            onSelect={handleFileChange}
                            chooseLabel="Chọn ảnh"
                            auto={false} // không upload ngay
                        />
                        {imageUrl && <img src={imageUrl} alt="Xem trước" style={{ width: '120px', marginTop: '10px' }} />}
                    </div>
                </div>
            </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '10px' }}>
    <Button 
        type="button" 
        label="Thoát" 
        className="p-button-secondary p-button-sm" 
        style={{ width: 'auto' }} 
        onClick={onCancel} 
    />
    <Button 
        type="submit" 
        label={selectedId ? 'Cập nhật' : 'Thêm mới'} 
        className="p-button-sm" 
        style={{ width: 'auto' }} 
    />

</div>
        </form>
        

    );
};


export default ProductForm;
