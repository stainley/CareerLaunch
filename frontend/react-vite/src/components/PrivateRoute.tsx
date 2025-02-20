// src/components/PrivateRoute.tsx
import { Navigate } from "react-router-dom"; // Ensure only Navigate is imported
import React, { FC } from "react";

interface PrivateRouteProps {
    children?: React.ReactNode;
}

const PrivateRoute: FC<PrivateRouteProps> = ({ children }) => {
    const isAuthenticated = !!localStorage.getItem('access_token');

    return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
};

export default PrivateRoute;
