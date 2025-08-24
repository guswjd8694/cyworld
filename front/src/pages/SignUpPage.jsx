import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import apiClient from '../api/axiosConfig';
import '../styles/signup.scss';

function SignUpPage() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        loginId: '',
        password: '',
        confirmPassword: '',
        birth: '',
        gender: 'Male',
        phone: ''
    });
    const [error, setError] = useState('');

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prevState => ({ ...prevState, [name]: value }));
    };

    const handleBirthdayChange = (e) => {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 8) value = value.slice(0, 8);
        if (value.length > 4) {
            value = `${value.slice(0, 4)}-${value.slice(4)}`;
        }
        if (value.length > 7) {
            value = `${value.slice(0, 7)}-${value.slice(7)}`;
        }
        setFormData(prevState => ({ ...prevState, birth: value }));
    };

    const handlePhoneNumberChange = (e) => {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 11) value = value.slice(0, 11);
        if (value.length > 3) {
            value = `${value.slice(0, 3)}-${value.slice(3)}`;
        }
        if (value.length > 8) {
            value = `${value.slice(0, 8)}-${value.slice(8)}`;
        }
        setFormData(prevState => ({ ...prevState, phone: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (formData.password !== formData.confirmPassword) {
            setError('비밀번호가 일치하지 않습니다.');
            return;
        }

        try {
            await apiClient.post('/users/signup', {
                name: formData.name,
                email: formData.email,
                loginId: formData.loginId,
                password: formData.password,
                birth: formData.birth.replace(/-/g, ''),
                gender: formData.gender,
                phone: formData.phone.replace(/-/g, '')
            });

            alert('회원가입에 성공했습니다! 로그인 페이지로 이동합니다.');
            navigate('/login');

        } catch (err) {
            setError(err.response?.data?.message || '회원가입에 실패했습니다.');
        }
    };

    return (
        <main className="signup-container">
            <h1 className="logo_title">
                <Link to="/">
                    <img src="/imgs/logo_cyworld.png" alt="CYWORLD" />
                </Link>
            </h1>
            <form onSubmit={handleSubmit} className="signup-form">
                <input type="text" name="loginId" placeholder="아이디" value={formData.loginId} onChange={handleInputChange} required />
                <input type="password" name="password" placeholder="비밀번호" value={formData.password} onChange={handleInputChange} required />
                <input type="password" name="confirmPassword" placeholder="비밀번호 확인" value={formData.confirmPassword} onChange={handleInputChange} required />
                <input type="email" name="email" placeholder="이메일" value={formData.email} onChange={handleInputChange} required />
                <input type="text" name="name" placeholder="이름" value={formData.name} onChange={handleInputChange} required />
                <input type="text" name="birth" placeholder="생년월일 (YYYY-MM-DD)" value={formData.birth} onChange={handleBirthdayChange} required />
                <div className="gender-select">
                    <label>
                        <input type="radio" name="gender" value="Male" checked={formData.gender === 'Male'} onChange={handleInputChange} />
                        <span>남자</span>
                    </label>
                    <label>
                        <input type="radio" name="gender" value="Female" checked={formData.gender === 'Female'} onChange={handleInputChange} />
                        <span>여자</span>
                    </label>
                </div>
                <input type="tel" name="phone" placeholder="휴대폰 번호" value={formData.phone} onChange={handlePhoneNumberChange} required />
                
                {error && <p className="error-message">{error}</p>}
                
                <button type="submit">가입하기</button>
            </form>
        </main>
    );
}

export default SignUpPage;
