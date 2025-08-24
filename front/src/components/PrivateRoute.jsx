import React, { useContext } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';

export default function PrivateRoute() {
    const { currentUser, loading } = useContext(AuthContext);

    console.log('3. PrivateRoute rendered. currentUser:', currentUser);

    if (loading) {
        return null; 
    }

    return currentUser ? <Outlet /> : <Navigate to="/login" />;
}