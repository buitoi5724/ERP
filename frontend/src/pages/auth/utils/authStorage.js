const authStorage = {


    // lưu token JWT
    saveToken(token) {

        localStorage.setItem(
            "token",
            token
        );

    },


    // lấy token
    getToken() {

        return localStorage.getItem(
            "token"
        );

    },


    // lưu thông tin user
    saveUser(user) {

        localStorage.setItem(
            "user",
            JSON.stringify(user)
        );

    },


    // lấy user hiện tại
    getUser() {

        const user =
            localStorage.getItem("user");


        return user
            ? JSON.parse(user)
            : null;

    },


    // xóa khi logout
    clear() {

        localStorage.removeItem(
            "token"
        );


        localStorage.removeItem(
            "user"
        );

    }


};


export default authStorage;