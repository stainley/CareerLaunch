// src/App.tsx
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from '@components/Login';

import Callback from '@components/Callback.tsx';
import PrivateRoute from '@components/PrivateRoute';
import Signup from '@components/Signup.tsx';


import './App.css';
import ActivateAccount from '@components/ActivateAccount.tsx';
import React from 'react';
import Dashboard from '@components/Dashboard/Dashboard.tsx';
import { Settings } from '@mui/icons-material';

const App: React.FC = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/activate" element={<ActivateAccount />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} /> {/* Add Signup route */}
        <Route path="/callback" element={<Callback />} />
        <Route path="/settings" element={<Settings />} />
        <Route
          path="/dashboard"
          element={
            <PrivateRoute>
              <Dashboard />
            </PrivateRoute>
          }
        />
      </Routes>
    </Router>
  );
};

export default App;
