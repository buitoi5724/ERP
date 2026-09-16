import React, { useContext } from "react";
import { Navigate } from "react-router-dom";

import { AuthContext }from "../pages/auth/context/AuthContext";


const ProtectedRoute = ({
    children,
    roles = []
}) => {


    const {
        user
    } = useContext(AuthContext);



    // Chưa đăng nhập
    if (!user) {

        return (
            <Navigate
                to="/login"
                replace
            />
        );

    }



    // Có yêu cầu role
    if (roles.length > 0) {


        const hasRole =
            user.roles?.some(role =>
                roles.includes(role)
            );



        // Không đủ quyền
        if (!hasRole) {

            return (
                <Navigate
                    to="/"
                    replace
                />
            );

        }


    }




    return children;


};


export default ProtectedRoute;