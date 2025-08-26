import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';
import apiClient from '../api/axiosConfig';
import '../styles/settings.scss'; 

function SettingsPage() {
    const { currentUser, logout } = useContext(AuthContext);
    const navigate = useNavigate();

    const [formData, setFormData] = useState({ email: '', phone: '' });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

    const [isConfirmingWithdrawal, setIsConfirmingWithdrawal] = useState(false);
    const [confirmationText, setConfirmationText] = useState('');

    useEffect(() => {
        if (!currentUser) return;
        
        const fetchUserData = async () => {
            try {
                const response = await apiClient.get(`/users/${currentUser.id}`);
                setFormData({
                    email: response.data.email,
                    phone: response.data.phone
                });
            } catch (err) {
                setError('사용자 정보를 불러오는 데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };
        fetchUserData();
    }, [currentUser]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccessMessage('');
        try {
            await apiClient.put(`/users/mypage`, formData);
            setSuccessMessage('성공적으로 수정되었습니다.');
        } catch (err) {
            if (err.response?.status !== 401) {
                setError(err.response?.data?.message || '수정에 실패했습니다.');
            }
        }
    };

    const handleWithdraw = async () => {
        // if (window.confirm('정말로 회원 탈퇴를 하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
        //     try {
                
        //         await apiClient.delete(`/users/${currentUser.id}`);
        //         alert('회원 탈퇴가 처리되었습니다. 이용해 주셔서 감사합니다.');
        //         logout(); // 프론트엔드 상태를 로그아웃으로 변경
        //         navigate('/login'); // 로그인 페이지로 이동
        //     } catch (err) {
        //         if (err.response?.status !== 401) {
        //             setError(err.response?.data?.message || '회원 탈퇴에 실패했습니다.');
        //         }
        //     }
        // }

        setIsConfirmingWithdrawal(true);
    };

    const handleConfirmWithdraw = async () => {
        if (confirmationText !== '탈퇴하겠습니다') {
            alert('문구를 정확히 입력해주세요.');
            return;
        }

        try {
            await apiClient.delete(`/users/${currentUser.id}`);
            alert('회원 탈퇴가 처리되었습니다. 이용해 주셔서 감사합니다.');
            logout();
            navigate('/login');
        } catch (err) {
            if (err.response?.status !== 401) {
                setError(err.response?.data?.message || '회원 탈퇴에 실패했습니다.');
            }
        }
    };

    if (loading) return <div>로딩 중...</div>;

    return (
        <div className="settings-container">
            <h2>개인정보 수정</h2>
            <form onSubmit={handleSubmit} className="setting-form">
                <div className="form-group">
                    <label htmlFor="email" className="sr_only">이메일</label>
                    <input 
                        type="email" 
                        id="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        placeholder="이메일"
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="phone" className="sr_only">핸드폰 번호</label>
                    <input 
                        type="tel" 
                        id="phone"
                        name="phone"
                        value={formData.phone}
                        onChange={handleChange}
                        placeholder="핸드폰 번호"
                        required
                    />
                </div>
                {error && <p className="error-message">{error}</p>}
                {successMessage && <p className="success-message">{successMessage}</p>}
                <button type="submit">수정하기</button>
            </form>

            <div className="withdraw-section">
                
                {isConfirmingWithdrawal ? (
                    <div className="withdraw-confirm-box">
                        <p>회원 탈퇴 시 작성하신 게시물과 정보는 복구할 수 없습니다.</p>
                        <p>정말로 탈퇴하시려면 아래에 "탈퇴하겠습니다"를 입력한 후 확인 버튼을 눌러주세요.</p>
                        <input 
                            type="text" 
                            value={confirmationText}
                            onChange={(e) => setConfirmationText(e.target.value)}
                            placeholder="탈퇴하겠습니다"
                        />
                        <div className="confirm-actions">
                            <button 
                                className="confirm-withdraw-button" 
                                onClick={handleConfirmWithdraw}
                                disabled={confirmationText !== '탈퇴하겠습니다'}
                            >
                                확인
                            </button>
                            <button className="cancel-button" onClick={() => setIsConfirmingWithdrawal(false)}>
                                취소
                            </button>
                        </div>
                    </div>
                ) : (
                    <>
                        <button className="withdraw-button" onClick={handleWithdraw}>
                            회원 탈퇴하기
                        </button>
                    </>
                )}
            </div>
        </div>
    );
}

export default SettingsPage;