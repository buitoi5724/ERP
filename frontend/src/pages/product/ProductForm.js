// Import các "viên gạch" cần thiết từ thư viện React và PrimeReact.
import { useState, useEffect } from 'react';
import { getProductById, getImage } from './productService'; // Giả sử bạn có các hàm này
import { Button } from 'primereact/button';
import { InputText } from 'primereact/inputtext';
import { FileUpload } from 'primereact/fileupload';
import { InputTextarea } from 'primereact/inputtextarea';
import { Dropdown } from 'primereact/dropdown';

/*
 * =================================================================================
 * COMPONENT PRODUCTFORM - BIỂU MẪU ĐỂ THÊM VÀ SỬA SẢN PHẨM
 * =================================================================================
 * Component này là một biểu mẫu (form) có thể tái sử dụng.
 * - Khi "thêm mới", nó sẽ là một form trống.
 * - Khi "sửa", nó sẽ tự động điền thông tin của sản phẩm cần sửa vào form.
 *
 * Nó nhận vào 3 props (tham số) từ component cha:
 * - selectedId: ID của sản phẩm cần sửa. Nếu là `null` thì là chế độ thêm mới.
 * - onSuccess: Một hàm sẽ được gọi khi thêm/sửa thành công (để component cha biết và tải lại danh sách).
 * - onCancel: Một hàm sẽ được gọi khi người dùng bấm nút "Hủy".
 */
const ProductForm = ({ selectedId, onSuccess, onCancel }) => {

    /*
     * =================================================================================
     * STATE - "BỘ NHỚ" CỦA COMPONENT
     * =================================================================================
     * useState là cách để tạo ra các "biến trạng thái" (state).
     * Khi các biến này thay đổi, component sẽ tự động được vẽ lại (re-render)
     * để hiển thị thông tin mới nhất.
     */

    // State để lưu trữ dữ liệu người dùng nhập vào form (tên, giá, mô tả...).
    const [product, setProduct] = useState({
        name: '',
        price: '',
        description: '',
        category: null, // Sẽ lưu ID của category
    });
    // State để lưu trữ thông báo lỗi của ô nhập giá.
    const [priceError, setPriceError] = useState('');
    // State để lưu trữ đối tượng file ảnh mà người dùng đã chọn.
    const [file, setFile] = useState(null);
    // State để lưu đường dẫn tạm thời của ảnh để hiển thị xem trước (preview).
    const [imageUrl, setImageUrl] = useState(null);
    // State để lưu danh sách các loại sản phẩm lấy từ API.
    const [categories, setCategories] = useState([]);


    /*
     * =================================================================================
     * USEEFFECT - "BỘ KÍCH HOẠT" CÁC HÀNH ĐỘNG PHỤ
     * =================================================================================
     * useEffect dùng để thực hiện các hành động có "tác dụng phụ" (side effects)
     * như gọi API, tương tác với DOM...
     */

    // Effect này dùng để LẤY DANH SÁCH LOẠI SẢN PHẨM từ backend.
    // Dấu `[]` rỗng ở cuối nghĩa là nó chỉ chạy đúng 1 lần khi component được hiển thị lần đầu.
    useEffect(() => {
        // NGUỒN DỮ LIỆU: Gọi đến API của backend.
        fetch('http://localhost:8080/api/product-categories')
            .then(res => res.json()) // Chuyển kết quả trả về thành JSON.
            .then(data => {
                if (Array.isArray(data)) {
                    // Chuyển đổi dữ liệu cho phù hợp với component Dropdown của PrimeReact.
                    // (cần có `label` để hiển thị và `value` để làm giá trị).
                    const formattedCategories = data.map(cat => ({ label: cat.name, value: cat.id }));
                    // Cập nhật state `categories` với danh sách đã được định dạng.
                    setCategories(formattedCategories);
                }
            })
            .catch(err => console.error('Lỗi khi lấy danh mục:', err)); // Báo lỗi nếu có sự cố.
    }, []);

    // Effect này dùng để ĐIỀN DỮ LIỆU VÀO FORM khi ở chế độ "sửa".
    // Nó sẽ chạy lại mỗi khi `selectedId` thay đổi.
    useEffect(() => {
        if (selectedId) {
            // --- CHẾ ĐỘ SỬA ---
            // NGUỒN DỮ LIỆU: Gọi hàm getProductById để lấy thông tin chi tiết của 1 sản phẩm.
            getProductById(selectedId).then(data => {
                // Cập nhật state `product` để điền thông tin vào các ô input.
                setProduct({
                    name: data.name || '',
                    price: data.price || '',
                    description: data.description || '',
                    category: data.category?.id || null,
                });
                // Nếu sản phẩm có ảnh, lấy file ảnh về để hiển thị xem trước.
                if (data.image) {
                    getImage(data).then(response => {
                        const blob = new Blob([response.data], { type: 'image/jpeg' });
                        const url = URL.createObjectURL(blob);
                        setImageUrl(url); // Cập nhật state để hiển thị ảnh.
                    });
                }
            });
        } else {
            // --- CHẾ ĐỘ THÊM MỚI ---
            // Reset tất cả các state về giá trị ban đầu (trống).
            setProduct({ name: '', price: '', description: '', category: null });
            setImageUrl(null);
            setFile(null);
            setPriceError('');
        }
    }, [selectedId]);


    /*
     * =================================================================================
     * CÁC HÀM XỬ LÝ SỰ KIỆN (EVENT HANDLERS)
     * =================================================================================
     */

    // Hàm này được gọi mỗi khi người dùng gõ chữ vào các ô InputText, InputTextarea.
    const handleChange = (e) => {
        const { name, value } = e.target;
        // Cập nhật lại state `product` với giá trị mới nhất.
        setProduct(prev => ({ ...prev, [name]: value }));
    };

    // Hàm này được gọi khi người dùng chọn một file ảnh.
    const handleFileChange = (e) => {
        const selectedFile = e.files[0];
        setFile(selectedFile); // Lưu file thật vào state `file` để chuẩn bị gửi đi.
        if (selectedFile) {
            // Tạo một URL tạm thời từ file để hiển thị xem trước.
            setImageUrl(URL.createObjectURL(selectedFile));
        }
    };

    // Hàm quan trọng nhất: được gọi khi người dùng bấm nút "Thêm mới" hoặc "Cập nhật".
    const handleSubmit = async (e) => {
        e.preventDefault(); // Ngăn trình duyệt tự động tải lại trang.

        // Kiểm tra dữ liệu đơn giản.
        if (!product.category) {
            alert('Vui lòng chọn loại sản phẩm');
            return;
        }

        // 1. CHUẨN BỊ DỮ LIỆU GỬI ĐI
        // Tạo một đối tượng chứa dữ liệu form đã được định dạng đúng.
        const productData = {
            name: product.name,
            price: Number(product.price),
            description: product.description,
            category: { id: Number(product.category) }
        };

        // Tạo một đối tượng FormData. Đây là cách tiêu chuẩn để gửi file và dữ liệu cùng lúc.
        const formData = new FormData();
        // Gói dữ liệu JSON vào một phần của FormData.
        formData.append('product', new Blob([JSON.stringify(productData)], { type: 'application/json' }));
        // Nếu có file ảnh được chọn, gói file ảnh vào một phần khác.
        if (file) {
            formData.append('image', file);
        }

        // 2. XÁC ĐỊNH ĐỊA CHỈ (URL) VÀ PHƯƠNG THỨC (METHOD)
        let url = 'http://localhost:8080/api/products';
        let method = 'POST'; // Mặc định là thêm mới

        if (selectedId) { // Nếu có selectedId, tức là đang sửa
            url = `http://localhost:8080/api/products/${selectedId}`;
            method = 'PUT'; // Chuyển sang phương thức PUT để cập nhật
        }

        // 3. GỬI DỮ LIỆU LÊN SERVER
        try {
            const response = await fetch(url, {
                method: method,
                body: formData, // Gửi gói hàng đã chuẩn bị
            });

            // 4. XỬ LÝ KẾT QUẢ
            if (response.ok) {
                onSuccess(); // Gọi hàm onSuccess để báo cho component cha biết đã thành công.
            } else {
                alert('Có lỗi xảy ra, vui lòng thử lại!');
            }
        } catch (error) {
            console.error("Lỗi kết nối:", error);
            alert("Không thể kết nối tới máy chủ!");
        }
    };

    /*
     * =================================================================================
     * JSX - CẤU TRÚC GIAO DIỆN
     * =================================================================================
     * Phần này định nghĩa những gì sẽ được hiển thị ra màn hình.
     */
    return (
        // Sử dụng thẻ <form> và gán sự kiện onSubmit cho hàm handleSubmit.
        <form onSubmit={handleSubmit}>
            <div className="p-fluid">
                <div className="p-field">
                    <label htmlFor="name">Tên sản phẩm</label>
                    {/* Kết nối ô input với state:
                        - value={product.name}: Hiển thị giá trị từ state.
                        - onChange={handleChange}: Gọi hàm handleChange khi người dùng gõ.
                    */}
                    <InputText id="name" name="name" value={product.name} onChange={handleChange} required />
                </div>
                <div className="p-field">
                    <label htmlFor="price">Giá sản phẩm</label>
                    <InputText id="price" name="price" type="number" value={product.price} onChange={handleChange} required />
                    {priceError && <small className="p-error">{priceError}</small>}
                </div>
                <div className="p-field">
                    <label>Loại sản phẩm</label>
                    <Dropdown
                        value={product.category}
                        options={categories}
                        onChange={(e) => setProduct(prev => ({ ...prev, category: e.value }))}
                        placeholder="Chọn loại sản phẩm"
                        required
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="description">Mô tả</label>
                    <InputTextarea id="description" name="description" value={product.description} onChange={handleChange} rows={5} />
                </div>
                <div className="p-field">
                    <label>Ảnh sản phẩm</label>
                    <FileUpload
                        name="image"
                        mode="basic"
                        accept="image/*"
                        onSelect={handleFileChange}
                        chooseLabel="Chọn ảnh"
                        auto // Tự động xóa file đã chọn khi chọn file mới
                    />
                    {/* Hiển thị ảnh xem trước nếu imageUrl có giá trị */}
                    {imageUrl && <img src={imageUrl} alt="Xem trước" style={{ width: '120px', marginTop: '10px' }} />}
                </div>
            </div>
            <div style={{ marginTop: '20px', textAlign: 'right' }}>
                <Button type="button" label="Hủy" className="p-button-secondary" onClick={onCancel} style={{ marginRight: '10px' }} />
                {/* Thay đổi nhãn của nút tùy theo chế độ thêm mới hay cập nhật */}
                <Button type="submit" label={selectedId ? 'Cập nhật' : 'Thêm mới'} />
            </div>
        </form>
    );
};

export default ProductForm;