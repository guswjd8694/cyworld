import React, { createContext, useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [currentUser, setCurrentUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('jwt-token');
        if (token) {
            try {
                const decodedUser = jwtDecode(token);
                setCurrentUser({ 
                    id: decodedUser.userId, 
                    loginId: decodedUser.sub, 
                    name: decodedUser.name 
                });
            } catch (e) {
                console.error("Invalid token:", e);
                localStorage.removeItem('jwt-token');
            }
        }
        setLoading(false);
    }, []);

    const login = (token) => {
        const cleanToken = token.startsWith('Bearer ') ? token.substring(7) : token;
        localStorage.setItem('jwt-token', cleanToken);
        const decodedUser = jwtDecode(cleanToken);
        const user = { 
            id: decodedUser.userId, 
            loginId: decodedUser.sub, 
            name: decodedUser.name 
        };
        setCurrentUser(user);
        return user;
    };

    const logout = () => {
        localStorage.removeItem('jwt-token');
        setCurrentUser(null);
        console.log('1. AuthProvider: logout called! currentUser is now null.');
    };

    useEffect(() => {
        const handleAuthError = () => {
            alert('세션이 만료되어 로그아웃됩니다.');
            logout();
        };

        window.addEventListener('auth-error', handleAuthError);

        return () => {
            window.removeEventListener('auth-error', handleAuthError);
        };
    }, []);

    const value = { currentUser, loading, login, logout };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}