import React, { useContext } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthContext } from './contexts/AuthContext';
import './styles/reset.scss';
import './styles/layout.scss';

import LoginPage from './pages/LoginPage';
import MinihomePage from './pages/MinihomePage';
import HomePage from './components/home/HomePage';
import Guestbook from './components/guestbook/Guestbook';
import Diary from './components/diary/Diary';

function App() {
    const { currentUser, loading } = useContext(AuthContext);
    
    if (loading) return <div>로딩 중...</div>;

    const ProtectedRoute = ({ children }) => {
        return currentUser ? children : <Navigate to="/login" />;
    };

    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            
            <Route 
                path="/:loginId" 
                element={
                    <ProtectedRoute>
                        <MinihomePage />
                    </ProtectedRoute>
                }
            >
                <Route index element={<HomePage />} /> 
                <Route path="guestbook" element={<Guestbook />} />
                <Route path="diary" element={<Diary />} />
            </Route>

            <Route path="/" element={
                currentUser ? <Navigate to={`/${currentUser.loginId}`} /> : <Navigate to="/login" />
            } />
        </Routes>
    );
}

export default App;