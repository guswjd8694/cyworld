import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';

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

    // 6. 로그인 버튼 클릭 시 실행될 함수
    const handleSubmit = async (e) => {
        e.preventDefault(); // form의 기본 새로고침 동작을 막습니다.
        setError(''); // 이전 에러 메시지 초기화

        try {
            const response = await fetch('http://localhost:8080/users/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ loginId, password })
            });

            if (!response.ok) {
                // 로그인 실패 시 (4xx, 5xx 에러)
                throw new Error('아이디 또는 비밀번호가 일치하지 않습니다.');
            }

            // [핵심] 로그인 성공 시, 응답 헤더에서 'Authorization' 토큰을 꺼냅니다.
            const token = response.headers.get('Authorization');
            if (token) {
                const loggedInUser = login(token);
                
                if (loggedInUser && loggedInUser.loginId) {
                    navigate(`/${loggedInUser.loginId}`);
                } else {
                    navigate('/'); // 만약의 경우를 대비해 홈으로 이동
                }
            } else {
                throw new Error('로그인에 성공했지만 토큰을 받지 못했습니다.');
            }

        } catch (err) {
            setError(err.message);
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
                <li><a href="#">아이디/비밀번호 찾기</a></li>
                <li><a href="#">회원가입</a></li>
            </ul>
        </main>
    );
}

export default LoginPage;