import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import apiClient from '../api/axiosConfig';
import '../styles/find-account.scss';

const validate = (name, value, formData) => {
    switch (name) {
        case 'email':
            if (!value) return '이메일을 입력해주세요.';
            if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) return '유효한 이메일 형식이 아닙니다.';
            return '';
        case 'name':
            if (!value) return '이름을 입력해주세요.';
            return '';
        case 'loginId':
            if (!value) return '아이디를 입력해주세요.';
            return '';
        case 'phone':
            if (!value) return '휴대폰 번호를 입력해주세요.';
            if (!/^\d{3}-\d{4}-\d{4}$/.test(value)) return '010-1234-5678 형식으로 입력해주세요.';
            return '';
        case 'newPassword':
            if (!value) return '새 비밀번호를 입력해주세요.';
            if (value.length < 8) return '비밀번호는 8자 이상이어야 합니다.';
            if (!/(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*()]).{8,}/.test(value)) return '비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.';
            return '';
        case 'confirmPassword':
            if (!value) return '비밀번호를 다시 입력해주세요.';
            if (value !== formData.newPassword) return '비밀번호가 일치하지 않습니다.';
            return '';
        default:
            return '';
    }
};

function FindAccountPage() {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState('findId');
    
    const [findIdData, setFindIdData] = useState({ name: '', email: '' });
    const [foundId, setFoundId] = useState('');

    const [resetPwData, setResetPwData] = useState({ 
        loginId: '', 
        email: '', 
        phone: '',
        newPassword: '', 
        confirmPassword: '' 
    });

    const [submitError, setSubmitError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    
    const [validationErrors, setValidationErrors] = useState({});
    
    useEffect(() => {
        setSubmitError('');
        setSuccessMessage('');
        setValidationErrors({});
    }, [activeTab]);

    const handleFindIdChange = (e) => {
        const { name, value } = e.target;
        setFindIdData(prevState => ({ ...prevState, [name]: value }));
        
        const errorMessage = validate(name, value, findIdData);
        setValidationErrors(prev => ({ ...prev, [name]: errorMessage }));
    };

    const handleResetPwChange = (e) => {
        const { name, value } = e.target;
        setResetPwData(prevState => ({ ...prevState, [name]: value }));
        
        const errorMessage = validate(name, value, resetPwData);
        setValidationErrors(prev => ({ ...prev, [name]: errorMessage }));
    };

    const handlePhoneNumberChange = (e) => {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 11) value = value.slice(0, 11);
        if (value.length > 3) value = `${value.slice(0, 3)}-${value.slice(3)}`;
        if (value.length > 8) value = `${value.slice(0, 8)}-${value.slice(8)}`;
        
        handleResetPwChange({ target: { name: 'phone', value } });
    };

    const handleFindIdSubmit = async (e) => {
        e.preventDefault();
        setSubmitError('');
        setSuccessMessage('');
        setFoundId('');

        const newErrors = {
            name: validate('name', findIdData.name, findIdData),
            email: validate('email', findIdData.email, findIdData)
        };
        setValidationErrors(newErrors);
        
        if (newErrors.name || newErrors.email) {
            setSubmitError('입력 정보를 다시 확인해주세요.');
            return;
        }

        try {
            const response = await apiClient.post('/auth/find-id', findIdData);
            const result = response.data;
            setFoundId(result.loginId);
            setSuccessMessage(`회원님의 아이디는 [ ${result.loginId} ] 입니다.`);
        } catch (err) {
            setSubmitError(err.response?.data?.message || '일치하는 사용자를 찾을 수 없습니다.');
        }
    };

    const handleResetPwSubmit = async (e) => {
        e.preventDefault();
        setSubmitError('');
        setSuccessMessage('');

        const newErrors = {
            loginId: validate('loginId', resetPwData.loginId, resetPwData),
            email: validate('email', resetPwData.email, resetPwData),
            phone: validate('phone', resetPwData.phone, resetPwData),
            newPassword: validate('newPassword', resetPwData.newPassword, resetPwData),
            confirmPassword: validate('confirmPassword', resetPwData.confirmPassword, resetPwData)
        };
        setValidationErrors(newErrors);

        if (Object.values(newErrors).some(err => err)) {
            setSubmitError('입력 정보를 다시 확인해주세요.');
            return;
        }

        try {
            await apiClient.post('/auth/reset-password', {
                loginId: resetPwData.loginId,
                email: resetPwData.email,
                phone: resetPwData.phone.replace(/-/g, ''),
                newPassword: resetPwData.newPassword
            });
            setSuccessMessage('비밀번호가 성공적으로 재설정되었습니다. 로그인 페이지로 이동합니다.');
            setTimeout(() => navigate('/login'), 2000);
        } catch (err) {
            setSubmitError(err.response?.data?.message || '비밀번호 재설정에 실패했습니다.');
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
                    <input type="text" name="name" placeholder="이름" value={findIdData.name} onChange={handleFindIdChange} required className={validationErrors.name ? 'invalid' : ''} />
                    {validationErrors.name && <p className="validation-message error">{validationErrors.name}</p>}
                    
                    <input type="email" name="email" placeholder="이메일" value={findIdData.email} onChange={handleFindIdChange} required className={validationErrors.email ? 'invalid' : ''} />
                    {validationErrors.email && <p className="validation-message error">{validationErrors.email}</p>}
                    
                    <button type="submit">아이디 찾기</button>
                </form>
            ) : (
                <form onSubmit={handleResetPwSubmit} className="find-account-form">
                    <input type="text" name="loginId" placeholder="아이디" value={resetPwData.loginId} onChange={handleResetPwChange} required className={validationErrors.loginId ? 'invalid' : ''} />
                    {validationErrors.loginId && <p className="validation-message error">{validationErrors.loginId}</p>}

                    <input type="email" name="email" placeholder="이메일" value={resetPwData.email} onChange={handleResetPwChange} required className={validationErrors.email ? 'invalid' : ''} />
                    {validationErrors.email && <p className="validation-message error">{validationErrors.email}</p>}

                    <input 
                        type="tel" 
                        name="phone" 
                        placeholder="휴대폰 번호" 
                        value={resetPwData.phone} 
                        onChange={handlePhoneNumberChange} 
                        required 
                        className={validationErrors.phone ? 'invalid' : ''} 
                    />
                    {validationErrors.phone && <p className="validation-message error">{validationErrors.phone}</p>}

                    <input type="password" name="newPassword" placeholder="새 비밀번호" value={resetPwData.newPassword} onChange={handleResetPwChange} required className={validationErrors.newPassword ? 'invalid' : ''} />
                    {validationErrors.newPassword && <p className="validation-message error">{validationErrors.newPassword}</p>}

                    <input type="password" name="confirmPassword" placeholder="새 비밀번호 확인" value={resetPwData.confirmPassword} onChange={handleResetPwChange} required className={validationErrors.confirmPassword ? 'invalid' : ''} />
                    {validationErrors.confirmPassword && <p className="validation-message error">{validationErrors.confirmPassword}</p>}
                    
                    <button type="submit">비밀번호 재설정</button>
                </form>
            )}

            {submitError && <p className="error-message">{submitError}</p>}
            {successMessage && <p className="success-message">{successMessage}</p>}
        </main>
    );
}

export default FindAccountPage;