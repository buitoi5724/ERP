package com.example.erp.service;

import com.example.erp.entity.Account;
import com.example.erp.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Đây là lớp Service cho Account.
 * Vai trò: Chứa toàn bộ logic nghiệp vụ (business logic) liên quan đến tài khoản.
 * Nó là trung gian giữa Controller (tầng giao tiếp) và Repository (tầng dữ liệu).
 */
@Service // Đánh dấu lớp này là một 'Service', một thành phần quan trọng trong kiến trúc ứng dụng.
public class AccountService {

    // Service sẽ phụ thuộc vào Repository để có thể thao tác với CSDL.
    private final AccountRepository accountRepository;

    // Sử dụng 'constructor injection' để tiêm (inject) AccountRepository vào.
    // Đây là cách làm tốt nhất để đảm bảo Service luôn có Repository để hoạt động.
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Phương thức tạo mới một tài khoản với các logic kiểm tra ràng buộc.
     * @Transactional: Đảm bảo tất cả các thao tác CSDL trong phương thức này được thực hiện
     * như một giao dịch (transaction) duy nhất. Nếu có lỗi xảy ra ở bất kỳ bước nào,
     * toàn bộ thay đổi sẽ được hoàn tác (rollback), giúp đảm bảo tính toàn vẹn dữ liệu.
     */
    @Transactional
    public Account create(Account account) {
        // --- Bắt đầu Logic nghiệp vụ ---
        // 1. Kiểm tra xem email đã tồn tại chưa.
        Account existingByEmail = accountRepository.findByEmail(account.getEmail());
        if (existingByEmail != null) {
            // Nếu đã tồn tại, ném ra một lỗi để dừng quá trình.
            throw new RuntimeException("Email đã tồn tại: " + account.getEmail());
        }

        // 2. Kiểm tra xem username đã tồn tại chưa.
        Account existingByUsername = accountRepository.findByUsername(account.getUsername());
        if (existingByUsername != null) {
            throw new RuntimeException("Username đã tồn tại: " + account.getUsername());
        }

        // 3. Kiểm tra xem name đã tồn tại chưa.
        Account existingByName = accountRepository.findByName(account.getName());
        if (existingByName != null) {
            throw new RuntimeException("Name đã tồn tại: " + account.getName());
        }
        // --- Kết thúc Logic nghiệp vụ ---

        // Nếu tất cả kiểm tra đều qua, tiến hành lưu vào CSDL.
        return accountRepository.save(account);
    }

    // Phương thức lấy tất cả tài khoản, không có logic phức tạp, chỉ cần gọi Repository.
    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    // Lấy tài khoản theo ID.
    public Optional<Account> getById(Long id) {
        return accountRepository.findById(id);
    }

    // Các phương thức kiểm tra riêng lẻ, có thể được Controller gọi để validate realtime.
    public Account checkEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Account checkUsername(String username) {
        return accountRepository.findByUsername(username);
    }
    public Account checkName(String name) {
        return accountRepository.findByName(name);
    }

    /**
     * Phương thức cập nhật thông tin tài khoản.
     * @Transactional cũng được dùng ở đây để đảm bảo tính toàn vẹn khi cập nhật.
     */
    @Transactional
    public Account update(Long id, Account newAccount) {
        // Sử dụng Optional để xử lý trường hợp không tìm thấy tài khoản một cách an toàn và sạch sẽ.
        return accountRepository.findById(id)
                .map(account -> { // Nếu tìm thấy tài khoản (account) với id tương ứng...
                    // Cập nhật các thuộc tính của tài khoản đã tìm thấy bằng thông tin mới.
                    account.setEmail(newAccount.getEmail());
                    account.setName(newAccount.getName());
                    account.setPassword(newAccount.getPassword());
                    account.setUsername(newAccount.getUsername());
                    // Lưu lại tài khoản đã được cập nhật.
                    return accountRepository.save(account);
                })
                // Nếu không tìm thấy tài khoản, ném ra một lỗi.
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }

    // Xóa tài khoản, không cần logic phức tạp.
    public void delete(Long id) {
        accountRepository.deleteById(id);
    }
}