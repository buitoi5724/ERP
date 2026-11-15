import React, {
    createContext,
    useEffect,
    useState
} from "react";

import authStorage from "../utils/authStorage";


// Tạo Context
export const AuthContext = createContext(null);


// Provider quản lý trạng thái đăng nhập
export const AuthProvider = ({ children }) => {

    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);


    // Kiểm tra user khi ứng dụng khởi động
    useEffect(() => {

        const currentUser = authStorage.getUser();

        if (currentUser) {
            setUser(currentUser);
        }

        setLoading(false);

    }, []);


    // Đăng nhập thành công
    const login = (userData) => {

        setUser(userData);

        authStorage.saveUser(userData);

    };


    // Đăng xuất
    const logout = () => {

        setUser(null);

        authStorage.clear();

    };


    // Giá trị cung cấp cho toàn bộ ứng dụng
    const value = {
        user,
        login,
        logout,
        isAuthenticated: !!user,
        loading
    };


    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );

};