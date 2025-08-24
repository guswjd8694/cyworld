import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import apiClient from '../api/axiosConfig';
import '../styles/find-account.scss';

function FindAccountPage() {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState('findId');
    
    const [findIdData, setFindIdData] = useState({ name: '', email: '' });
    const [foundId, setFoundId] = useState('');

    const [resetPwData, setResetPwData] = useState({ loginId: '', email: '', newPassword: '', confirmPassword: '' });

    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

    const handleFindIdChange = (e) => {
        const { name, value } = e.target;
        setFindIdData(prevState => ({ ...prevState, [name]: value }));
    };

    const handleResetPwChange = (e) => {
        const { name, value } = e.target;
        setResetPwData(prevState => ({ ...prevState, [name]: value }));
    };

    const handleFindIdSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccessMessage('');
        setFoundId('');
        try {
            const response = await apiClient.post('/users/find-id', findIdData);
            const result = response.data;
            setFoundId(result.loginId);
            setSuccessMessage(`회원님의 아이디는 [ ${result.loginId} ] 입니다.`);
        } catch (err) {
            setError(err.response?.data?.message || '일치하는 사용자를 찾을 수 없습니다.');
        }
    };

    const handleResetPwSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccessMessage('');
        if (resetPwData.newPassword !== resetPwData.confirmPassword) {
            setError('새 비밀번호가 일치하지 않습니다.');
            return;
        }
        try {
            await apiClient.post('/users/reset-password', {
                loginId: resetPwData.loginId,
                email: resetPwData.email,
                newPassword: resetPwData.newPassword
            });
            setSuccessMessage('비밀번호가 성공적으로 재설정되었습니다. 로그인 페이지로 이동합니다.');
            setTimeout(() => navigate('/login'), 2000);
        } catch (err) {
            setError(err.response?.data?.message || '비밀번호 재설정에 실패했습니다.');
        }
    };

    return (
        <main className="find-account-container">
            <h1 className="logo_title">
                <Link to="/">
                    <img src="/imgs/logo_cyworld.png" alt="CYWORLD" />
                </Link>
            </h1>
            <div className="tab-nav">
                <button 
                    className={activeTab === 'findId' ? 'active' : ''} 
                    onClick={() => setActiveTab('findId')}
                >
                    아이디 찾기
                </button>
                <button 
                    className={activeTab === 'resetPassword' ? 'active' : ''} 
                    onClick={() => setActiveTab('resetPassword')}
                >
                    비밀번호 재설정
                </button>
            </div>

            {activeTab === 'findId' ? (
                <form onSubmit={handleFindIdSubmit} className="find-account-form">
                    <input type="text" name="name" placeholder="이름" value={findIdData.name} onChange={handleFindIdChange} required />
                    <input type="email" name="email" placeholder="이메일" value={findIdData.email} onChange={handleFindIdChange} required />
                    <button type="submit">아이디 찾기</button>
                </form>
            ) : (
                <form onSubmit={handleResetPwSubmit} className="find-account-form">
                    <input type="text" name="loginId" placeholder="아이디" value={resetPwData.loginId} onChange={handleResetPwChange} required />
                    <input type="email" name="email" placeholder="이메일" value={resetPwData.email} onChange={handleResetPwChange} required />
                    <input type="password" name="newPassword" placeholder="새 비밀번호" value={resetPwData.newPassword} onChange={handleResetPwChange} required />
                    <input type="password" name="confirmPassword" placeholder="새 비밀번호 확인" value={resetPwData.confirmPassword} onChange={handleResetPwChange} required />
                    <button type="submit">비밀번호 재설정</button>
                </form>
            )}

            {error && <p className="error-message">{error}</p>}
            {successMessage && <p className="success-message">{successMessage}</p>}
        </main>
    );
}

export default FindAccountPage;
