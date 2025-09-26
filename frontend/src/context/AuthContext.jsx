// src/context/AuthContext.jsx
import React, { createContext, useState, useContext, useEffect } from 'react';

const AuthContext = createContext();

const API_BASE_URL = 'http://localhost:8080/api'; // **Adjust port if necessary**

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [role, setRole] = useState(null);

    // Simple token decoding (JWTs are base64, payload is second part)
    const decodeToken = (t) => {
        if (!t) return null;
        try {
            const payload = JSON.parse(atob(t.split('.')[1]));
            // Assuming your token has 'sub' (username) and 'authorities' (ROLE_USER, ROLE_ADMIN)
            return {
                username: payload.sub,
                role: payload.authorities.includes('ROLE_ADMIN') ? 'ADMIN' : 'USER'
            };
        } catch (e) {
            console.error("Failed to decode token:", e);
            return null;
        }
    };

    useEffect(() => {
        if (token) {
            localStorage.setItem('token', token);
            const decodedUser = decodeToken(token);
            if (decodedUser) {
                setUser(decodedUser.username);
                setRole(decodedUser.role);
            } else {
                logout();
            }
        } else {
            localStorage.removeItem('token');
            setUser(null);
            setRole(null);
        }
    }, [token]);

    const login = async (username, password) => {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Login failed');
        }
        const data = await response.json();
        setToken(data.token);
        return data.token;
    };

    const register = async (username, password) => {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        });
        if (!response.ok) {
             const error = await response.json();
             throw new Error(error.message || 'Registration failed. Username might exist.');
        }
        return response.json();
    };

    const logout = () => {
        setToken(null);
    };

    const value = {
        user,
        role,
        token,
        login,
        register,
        logout,
        isAdmin: role === 'ADMIN',
        API_BASE_URL
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};