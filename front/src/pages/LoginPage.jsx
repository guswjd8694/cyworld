import React, { useState, useEffect, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';
import apiClient from '../api/axiosConfig';

import '../styles/reset.scss';
import '../styles/layout.scss';
import '../styles/login.scss';

function LoginPage() {
    const { login } = useContext(AuthContext);
    const navigate = useNavigate();
    const [loginId, setLoginId] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    useEffect(() => {
        document.body.classList.add('login-page-background');
        return () => {
            document.body.classList.remove('login-page-background');
        };
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(''); 

        try {
            const response = await apiClient.post('/auth/login', { loginId, password });

            const token = response.headers['authorization'];

            if (token) {
                const loggedInUser = login(token);
                if (loggedInUser && loggedInUser.loginId) {
                    navigate(`/${loggedInUser.loginId}`);
                } else {
                    navigate('/');
                }
            } else {
                throw new Error('로그인에 성공했지만 토큰을 받지 못했습니다.');
            }
        } catch (err) {
            setError(err.response?.data?.message || '아이디 또는 비밀번호가 일치하지 않습니다.');
        }
    };

    return (
        <main className="login-container">
            <h1 className="logo_title">
                <img src="/imgs/logo_cyworld.png" alt="CYWORLD" />
            </h1>
            
            <form onSubmit={handleSubmit} className="login-form">
                <h2 className="sr_only">로그인</h2>
                <div className="form-group">
                    <label htmlFor="loginId" className="sr_only">아이디</label>
                    <input 
                        type="text" 
                        id="loginId" 
                        placeholder="아이디" 
                        value={loginId}
                        onChange={(e) => setLoginId(e.target.value)}
                        required 
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="password" className="sr_only">비밀번호</label>
                    <input 
                        type="password" 
                        id="password" 
                        placeholder="비밀번호" 
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required 
                    />
                </div>
                <button type="submit">로그인</button>
            </form>

            {error && <p style={{ color: 'red', marginTop: '10px' }}>{error}</p>}

            <ul className="sub-links">
                <li><Link to="/find">아이디/비밀번호 찾기</Link></li>
                <li><Link to="/signup">회원가입</Link></li>
            </ul>
        </main>
    );
}

export default LoginPage;