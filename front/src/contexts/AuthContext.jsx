import React, { createContext, useState, useEffect, useCallback } from 'react';
import { jwtDecode } from 'jwt-decode';
import apiClient from '../api/axiosConfig';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [currentUser, setCurrentUser] = useState(null);
    const [loading, setLoading] = useState(true);

    const logout = useCallback(async () => {
        try {
            await apiClient.post('/auth/logout');
        } catch (error) {
            console.error("로그아웃 API 호출 실패:", error);
        } finally {
            localStorage.removeItem('jwt-token');
            setCurrentUser(null);
        }
    }, []);

    useEffect(() => {
        const interval = setInterval(() => {
            const token = localStorage.getItem('jwt-token');
            if (token && currentUser) {
                try {
                    const decodedToken = jwtDecode(token);
                    if (decodedToken.exp * 1000 < Date.now()) {
                        alert('세션이 만료되어 자동으로 로그아웃됩니다.');
                        logout();
                    }
                } catch (e) {
                    console.error("주기적 토큰 검사 중 에러 발생:", e);
                    logout();
                }
            }
        }, 1000);

        return () => clearInterval(interval);
    }, [currentUser, logout]);


    useEffect(() => {
        const handleAuthError = () => {
            if (localStorage.getItem('jwt-token')) {
                alert('세션이 만료되어 자동으로 로그아웃됩니다.');
                logout();
            }
        };
        window.addEventListener('auth-error', handleAuthError);
        return () => {
            window.removeEventListener('auth-error', handleAuthError);
        };
    }, [logout]);


    useEffect(() => {
        const token = localStorage.getItem('jwt-token');
        if (token) {
            try {
                const decodedUser = jwtDecode(token);
                if (decodedUser.exp * 1000 < Date.now()) {
                    logout();
                } else {
                    setCurrentUser({
                        id: decodedUser.userId,
                        loginId: decodedUser.sub,
                        name: decodedUser.name
                    });
                }
            } catch (e) {
                logout();
            }
        }
        setLoading(false);
    }, [logout]);


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

    const value = { currentUser, loading, login, logout };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}