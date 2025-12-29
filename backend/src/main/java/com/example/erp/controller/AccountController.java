package com.example.erp.controller;

import com.example.erp.entity.Account;
import com.example.erp.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional; // Import lớp Optional

/*
 * =================================================================================
 * LỚP ACCOUNTCONTROLLER - BỘ PHẬN TIẾP NHẬN YÊU CẦU VỀ "TÀI KHOẢN"
 * =================================================================================
 * Tưởng tượng đây là một người "quản lý" chuyên về tài khoản.
 * Khi có yêu cầu từ bên ngoài (frontend), người quản lý này sẽ nhận, xem yêu cầu là gì
 * (thêm, xóa, sửa...), sau đó giao việc cho "nhân viên" (chính là AccountService)
 * để xử lý, và cuối cùng nhận kết quả từ nhân viên để phản hồi lại.
 */

// 🚪 @CrossOrigin: Giống như một người bảo vệ, cho phép trang web ở địa chỉ "http://localhost:3000" được quyền gửi yêu cầu tới.
@CrossOrigin(origins = "http://localhost:3000")

// 📦 @RestController: Báo cho Spring Boot biết đây là một người quản lý API, chuyên nói chuyện bằng dữ liệu (JSON).
@RestController

// 📝 @RequestMapping: Đặt ra địa chỉ chính cho người quản lý này. Mọi yêu cầu liên quan đến tài khoản đều phải đến địa chỉ "/accounts".
@RequestMapping("/accounts")
public class AccountController {

    // Đây là "nhân viên" chuyên xử lý mọi logic, nghiệp vụ liên quan đến tài khoản.
    // Người quản lý (Controller) sẽ không tự làm, mà sẽ giao hết việc cho nhân viên này.
    private final AccountService accountService;

    // Khi người quản lý (Controller) được tạo ra, Spring Boot sẽ tự động cung cấp cho anh ta một nhân viên (Service).
    // Quá trình này gọi là "Dependency Injection" (Tiêm phụ thuộc).
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // ---------------------------------------------------------------------------------
    // CÁC NHIỆM VỤ CỤ THỂ (API ENDPOINTS)
    // ---------------------------------------------------------------------------------

    /**
     * ➕ TẠO MỘT TÀI KHOẢN MỚI
     * - Phương thức: POST
     * - Đường dẫn: /accounts
     */
    @PostMapping
    public ResponseEntity<Account> create(@RequestBody Account account) {
        // @RequestBody: DỮ LIỆU ĐƯỢC LẤY TỪ "thân" của yêu cầu.
        // Spring sẽ tự động chuyển đổi dữ liệu JSON mà client gửi lên thành một đối tượng Account trong Java.
        Account createdAccount = accountService.create(account);
        return ResponseEntity.ok(createdAccount); // Trả về tài khoản vừa tạo và mã 200 OK.
    }

    /**
     * 📖 LẤY TẤT CẢ TÀI KHOẢN
     * - Phương thức: GET
     * - Đường dẫn: /accounts
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAll() {
        // DỮ LIỆU ĐƯỢC LẤY TỪ database thông qua AccountService.
        List<Account> accounts = accountService.getAll();
        return ResponseEntity.ok(accounts); // Trả về danh sách tài khoản và mã 200 OK.
    }

    /**
     * 🆔 LẤY MỘT TÀI KHOẢN THEO ID
     * - Phương thức: GET
     * - Đường dẫn: /accounts/{id} (ví dụ: /accounts/1)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Account> getById(@PathVariable Long id) {
        // @PathVariable: DỮ LIỆU "id" ĐƯỢC LẤY TỪ chính đường dẫn URL.
        Optional<Account> optionalAccount = accountService.getById(id);

        // Optional giống như một cái hộp. Ta cần kiểm tra xem trong hộp có gì không.
        if (optionalAccount.isPresent()) {
            // Nếu có, lấy nó ra và trả về.
            Account foundAccount = optionalAccount.get();
            return ResponseEntity.ok(foundAccount); // Trả về tài khoản tìm thấy và mã 200 OK.
        } else {
            // Nếu hộp rỗng, báo không tìm thấy.
            return ResponseEntity.notFound().build(); // Trả về lỗi 404 Not Found.
        }
    }

    /**
     * 🔄 CẬP NHẬT MỘT TÀI KHOẢN
     * - Phương thức: PUT
     * - Đường dẫn: /accounts/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Account> update(@PathVariable Long id, @RequestBody Account accountDetails) {
        // DỮ LIỆU ĐƯỢC LẤY TỪ 2 NƠI:
        // 1. @PathVariable "id": ID của tài khoản cần sửa, lấy từ URL.
        // 2. @RequestBody "accountDetails": Thông tin mới cần cập nhật, lấy từ "thân" của yêu cầu.
        Account updatedAccount = accountService.update(id, accountDetails);
        return ResponseEntity.ok(updatedAccount); // Trả về tài khoản đã được cập nhật và mã 200 OK.
    }

    /**
     * 🗑️ XÓA MỘT TÀI KHOẢN
     * - Phương thức: DELETE
     * - Đường dẫn: /accounts/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // @PathVariable: DỮ LIỆU "id" ĐƯỢC LẤY TỪ URL để biết cần xóa tài khoản nào.
        accountService.delete(id);
        // Trả về mã 204 No Content. Đây là mã tiêu chuẩn báo rằng "Tôi đã xóa thành công và không có gì để gửi lại cho bạn cả".
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<Account> getBySupplierId(@PathVariable Long supplierId) {
        Optional<Account> account = accountService.getBySupplierId(supplierId);
        return account.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
}