package com.example.erp.entity;

import java.io.Serializable;

import com.example.erp.util.BaseEntity;

import jakarta.persistence.*; // Import các công cụ để làm việc với database.

/*
 * =================================================================================
 * LỚP ACCOUNT - KHUÔN MẪU CHO BẢNG "ACCOUNT" TRONG DATABASE
 * =================================================================================
 * Tưởng tượng lớp này như một bản thiết kế chi tiết cho một "Tài khoản".
 * JPA (Java Persistence API) sẽ đọc bản thiết kế này và tự động tạo ra một bảng
 * trong database có các cột tương ứng.
 * Mỗi một đối tượng (instance) của lớp Account sẽ tương ứng với một dòng trong bảng đó.
 */

// 📝 @Entity: Annotation quan trọng nhất, báo cho JPA biết "Đây là một bản thiết kế
// cho một bảng trong database". Tên bảng thường sẽ được tự động suy ra từ tên class (ví dụ: "account").
@Entity
public class Account extends BaseEntity  implements Serializable {

	private static final long serialVersionUID = 1L;


	// 🔑 @Id: Đánh dấu trường "id" này là khóa chính (primary key) của bảng.
    // Khóa chính là một giá trị độc nhất, không trùng lặp, dùng để xác định từng dòng.
    @Id
    // 🚀 @GeneratedValue: Cấu hình cách tạo ra giá trị cho khóa chính.
    // "strategy = GenerationType.IDENTITY": Giao lại việc tạo và tự động tăng ID
    // cho chính database (ví dụ: ID tự tăng từ 1, 2, 3...).
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // --- CÁC TRƯỜNG DỮ LIỆU THÔNG THƯỜNG ---
    // Các trường này sẽ được tự động ánh xạ (map) thành các cột trong bảng "account".

    private String username; // Cột `username`
    private String email;    // Cột `email`
    private String name;     // Cột `name`
    private String password; // Cột `password`


    // --- CÁC CONSTRUCTOR (HÀM DỰNG) ---

    // Constructor rỗng (không có tham số).
    // JPA yêu cầu phải có hàm này để có thể tạo ra các đối tượng Account một cách tự động.
    public Account() {}

    // Constructor có tham số, giúp tạo nhanh một đối tượng Account với đầy đủ thông tin ban đầu.
    public Account(String username, String email ,String name ,String password) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.password = password;
    }


    // --- CÁC GETTER VÀ SETTER ---
    // Đây là các phương thức công khai (public) để các lớp khác có thể đọc (get)
    // hoặc thay đổi (set) giá trị của các trường dữ liệu (vốn là private).
    // Các thư viện như Spring và Jackson (xử lý JSON) cũng dùng chúng rất nhiều.

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}