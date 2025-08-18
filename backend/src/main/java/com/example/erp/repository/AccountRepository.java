package com.example.erp.repository;

import com.example.erp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * =================================================================================
 * INTERFACE ACCOUNTREPOSITORY - NGƯỜI PHỤ TRÁCH TRUY CẬP DỮ LIỆU "ACCOUNT"
 * =================================================================================
 * Tưởng tượng đây là một "thủ thư" chuyên quản lý kho sách "Account".
 * Nhiệm vụ của nó là cung cấp các phương thức để tìm kiếm, lưu, xóa, cập nhật
 * các cuốn sách (dòng dữ liệu) trong kho (bảng account).
 *
 * Đây là một `interface` chứ không phải `class`. Chúng ta chỉ cần định nghĩa
 * "chúng ta muốn làm gì", còn Spring Data JPA sẽ tự động cung cấp phần thực thi
 * "làm nó như thế nào" ở phía sau.
 */

// JpaRepository là một interface có sẵn của Spring Data JPA, cung cấp rất nhiều
// các phương thức thao tác với database.
public interface AccountRepository extends JpaRepository<Account, Long> {
    // Việc "extends JpaRepository<Account, Long>" có nghĩa là:
    // 1. Account: "Thủ thư" này sẽ quản lý các đối tượng thuộc loại "Account".
    // 2. Long: Khóa chính (ID) của "Account" có kiểu dữ liệu là "Long".

    // ==> Bằng việc kế thừa này, chúng ta tự động có các phương thức cơ bản như:
    // - save(): Lưu một tài khoản.
    // - findById(): Tìm tài khoản theo ID.
    // - findAll(): Lấy tất cả tài khoản.
    // - deleteById(): Xóa tài khoản theo ID.
    // ... và nhiều phương thức khác mà không cần phải viết thêm bất kỳ dòng code nào.


    // --- CÁC PHƯƠNG THỨC TRUY VẤN TÙY CHỈNH (CUSTOM QUERY METHODS) ---
    // Spring Data JPA có một cơ chế rất thông minh: nó sẽ tự động tạo ra câu lệnh
    // SQL dựa trên tên của phương thức mà bạn định nghĩa ở đây.

    /**
     * Tìm một tài khoản dựa trên địa chỉ email.
     * Spring sẽ tự hiểu và tạo ra câu lệnh SQL: "SELECT * FROM account WHERE email = ?"
     * @param email Địa chỉ email cần tìm.
     * @return Đối tượng Account nếu tìm thấy, ngược lại trả về null.
     */
    Account findByEmail(String email);

    /**
     * Tìm một tài khoản dựa trên tên đăng nhập (username).
     * Spring sẽ tự hiểu và tạo ra câu lệnh SQL: "SELECT * FROM account WHERE username = ?"
     * @param username Tên đăng nhập cần tìm.
     * @return Đối tượng Account nếu tìm thấy, ngược lại trả về null.
     */
    Account findByUsername(String username);

    /**
     * Tìm một tài khoản dựa trên tên (name).
     * Spring sẽ tự hiểu và tạo ra câu lệnh SQL: "SELECT * FROM account WHERE name = ?"
     * @param name Tên người dùng cần tìm.
     * @return Đối tượng Account nếu tìm thấy, ngược lại trả về null.
     */
    Account findByName(String name);
}