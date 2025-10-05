import React, { useContext } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthContext } from './contexts/AuthContext';
import Header from './components/common/Header'; 
import PrivateRoute from './components/PrivateRoute';

import './styles/reset.scss';
import './styles/header.scss';

import LoginPage from './pages/LoginPage';
import SignUpPage from './pages/SignUpPage';
import FindAccountPage from './pages/FindAccountPage';
import MinihomePage from './pages/MinihomePage';
import SettingsPage from './pages/SettingsPage';
import HomePage from './components/home/HomePage';
import Guestbook from './components/guestbook/Guestbook';
import Diary from './components/diary/Diary';

function App() {
    const { currentUser, loading } = useContext(AuthContext);

    if (loading) return <div>로딩 중...</div>;

    return (
        <div className="app-container">
            <Header />
            <main className="main-content">
                <Routes>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/signup" element={<SignUpPage />} />
                    <Route path="/find" element={<FindAccountPage />} />
                    <Route 
                        path="/:loginId" 
                        element={<MinihomePage />}
                    >
                        <Route index element={<HomePage />} /> 
                        <Route path="guestbook" element={<Guestbook />} />
                        <Route path="diary" element={<Diary />} />
                    </Route>

                    <Route element={<PrivateRoute />}>
                        <Route path="/settings" element={<SettingsPage />} />
                    </Route>

                    <Route path="/" element={
                        currentUser ? <Navigate to={`/${currentUser.loginId}`} /> : <Navigate to="/user1" />
                    } />
                </Routes>
            </main>
        </div>
    );
}

export default App;
