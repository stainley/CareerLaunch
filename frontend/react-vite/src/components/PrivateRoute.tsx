// src/components/PrivateRoute.tsx
import { Navigate } from "react-router-dom"; // Ensure only Navigate is imported
import React from "react";

const PrivateRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    // Simple check for now; replace with real auth logic later (e.g., token check)
    const isAuthenticated = true; // Placeholder; implement actual auth check
    return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
};

export default PrivateRoute;
