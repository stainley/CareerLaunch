// src/App.tsx
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Login from './components/Login';
import Callback from './components/Callback.tsx';
import PrivateRoute from './components/PrivateRoute';
import Dashboard from './components/Dashboard';
import Signup from "./components/Signup.tsx";

import './App.css';
import ActivateAccount from "./components/ActivateAccount.tsx";
import React from "react";

const App: React.FC = () => {


    return (

        <Router>
            <Routes>
                <Route path="/" element={<Login/>}/>
                <Route path="/activate" element={<ActivateAccount/>}/>
                <Route path="/login" element={<Login/>}/>
                <Route path="/signup" element={<Signup/>}/> {/* Add Signup route */}
                <Route path="/callback" element={<Callback/>}/>
                <Route
                    path="/dashboard"
                    element={
                        <PrivateRoute>
                            <Dashboard/>
                        </PrivateRoute>
                    }
                />
            </Routes>
        </Router>
    );
}

export default App;
