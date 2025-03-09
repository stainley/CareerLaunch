// src/components/PrivateRoute.tsx
import { Navigate, useLocation } from 'react-router-dom'; // Ensure only Navigate is imported
import React, { useEffect, useState } from 'react';


interface PrivateRouteProps {
  children: React.ReactNode;
  redirectTo?: string; // Customizable redirect path
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({
                                                     children,
                                                     redirectTo = '/login', // Default redirect path
                                                   }) => {
  const location = useLocation();
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);

  useEffect(() => {
    const checkAuth = () => {
      try {
        const token = localStorage.getItem('access_token');
        if (token) {
          setIsAuthenticated(true);
        } else {
          setIsAuthenticated(false);
        }
      } catch (error) {
        console.error(`Error checking authentication: ${error}`);
        setIsAuthenticated(false);
      }
    };

    checkAuth();
  });

  if (isAuthenticated === null) {
    return <div>Loading...</div>;
  }

  return isAuthenticated ? (
    <>
      {children}
    </>) : (
    <Navigate to={redirectTo}
              state={{ from: location }}
              replace />
  );
};

export default PrivateRoute;
