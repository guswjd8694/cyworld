import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import apiClient from '../api/axiosConfig';
import '../styles/signup.scss';

const validate = (name, value, formData) => {
    switch (name) {
        case 'loginId':
            if (!value) return '아이디를 입력해주세요.';
            if (value.length < 5 || value.length > 20) return '아이디는 5자 이상 20자 이하로 입력해주세요.';
            if (!/^[a-z0-9]+$/.test(value)) return '아이디는 영문 소문자와 숫자만 사용할 수 있습니다.';
            return '';
        case 'password':
            if (!value) return '비밀번호를 입력해주세요.';
            if (value.length < 8) return '비밀번호는 8자 이상이어야 합니다.';
            if (!/(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*()]).{8,}/.test(value)) return '비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.';
            return '';
        case 'confirmPassword':
            if (!value) return '비밀번호를 다시 입력해주세요.';
            if (value !== formData.password) return '비밀번호가 일치하지 않습니다.';
            return '';
        case 'email':
            if (!value) return '이메일을 입력해주세요.';
            if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) return '유효한 이메일 형식이 아닙니다.';
            return '';
        case 'name':
            if (!value) return '이름을 입력해주세요.';
            return '';
        case 'birth':
            if (!value) return '생년월일을 입력해주세요.';
            if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) return 'YYYY-MM-DD 형식으로 입력해주세요.';
            const [year, month, day] = value.split('-').map(Number);
            const date = new Date(year, month - 1, day);
            if (date.getFullYear() !== year || date.getMonth() !== month - 1 || date.getDate() !== day) {
                return '유효하지 않은 날짜입니다.';
            }
            return '';
        case 'phone':
            if (!value) return '휴대폰 번호를 입력해주세요.';
            if (!/^\d{3}-\d{4}-\d{4}$/.test(value)) return '010-1234-5678 형식으로 입력해주세요.';
            return '';
        default:
            return '';
    }
};

function SignUpPage() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '', email: '', loginId: '', password: '',
        confirmPassword: '', birth: '', gender: 'Male', phone: ''
    });
    const [errors, setErrors] = useState({});
    const [apiValidation, setApiValidation] = useState({
        loginIdStatus: 'idle',
        emailStatus: 'idle'
    });
    const [submitError, setSubmitError] = useState('');
    const [isCapsLockOn, setIsCapsLockOn] = useState(false);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prevState => ({ ...prevState, [name]: value }));
        const errorMessage = validate(name, value, formData);
        setErrors(prev => ({ ...prev, [name]: errorMessage }));
    };

    const handleBirthdayChange = (e) => {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 8) value = value.slice(0, 8);
        if (value.length > 4) value = `${value.slice(0, 4)}-${value.slice(4)}`;
        if (value.length > 7) value = `${value.slice(0, 7)}-${value.slice(7)}`;
        setFormData(prevState => ({ ...prevState, birth: value }));
    };

    const handlePhoneNumberChange = (e) => {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 11) value = value.slice(0, 11);
        if (value.length > 3) value = `${value.slice(0, 3)}-${value.slice(3)}`;
        if (value.length > 8) value = `${value.slice(0, 8)}-${value.slice(8)}`;
        setFormData(prevState => ({ ...prevState, phone: value }));
    };

    const handleKeyDown = (e) => {
        if (typeof e.getModifierState === 'function') {
            setIsCapsLockOn(e.getModifierState('CapsLock'));
        }
    };

    useEffect(() => {
        if (!formData.loginId || errors.loginId) {
            setApiValidation(prev => ({...prev, loginIdStatus: 'idle' }));
            return;
        }
        setApiValidation(prev => ({...prev, loginIdStatus: 'checking' }));
        const debounce = setTimeout(async () => {
            try {
                const response = await apiClient.post('/users/check-loginId', { loginId: formData.loginId });
                setApiValidation(prev => ({...prev, loginIdStatus: response.data.isAvailable ? 'available' : 'unavailable' }));
            } catch (error) {
                console.error("아이디 중복 체크 실패:", error);
                setApiValidation(prev => ({...prev, loginIdStatus: 'idle' }));
            }
        }, 500);
        return () => clearTimeout(debounce);
    }, [formData.loginId, errors.loginId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSubmitError('');
        const newErrors = {};
        Object.keys(formData).forEach(key => {
            const errorMessage = validate(key, formData[key], formData);
            if (errorMessage) newErrors[key] = errorMessage;
        });

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            setSubmitError('입력 정보를 다시 확인해주세요.');
            return;
        }
        if (apiValidation.loginIdStatus !== 'available') {
            setSubmitError('아이디 중복 확인이 필요합니다.');
            return;
        }
        try {
            await apiClient.post('/users/signup', {
                name: formData.name, email: formData.email, loginId: formData.loginId,
                password: formData.password, birth: formData.birth.replace(/-/g, ''),
                gender: formData.gender, phone: formData.phone.replace(/-/g, '')
            });
            alert('회원가입에 성공했습니다! 로그인 페이지로 이동합니다.');
            navigate('/login');
        } catch (err) {
            setSubmitError(err.response?.data?.message || '회원가입에 실패했습니다.');
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
                <div className="input-group">
                    <input type="text" name="loginId" placeholder="아이디" value={formData.loginId} onChange={handleInputChange} required className={errors.loginId || apiValidation.loginIdStatus === 'unavailable' ? 'invalid' : ''} />
                    {errors.loginId && <p className="validation-message error">{errors.loginId}</p>}
                    {apiValidation.loginIdStatus === 'checking' && <p className="validation-message">중복 확인 중...</p>}
                    {apiValidation.loginIdStatus === 'available' && <p className="validation-message success">사용 가능한 아이디입니다.</p>}
                    {apiValidation.loginIdStatus === 'unavailable' && <p className="validation-message error">이미 사용 중인 아이디입니다.</p>}
                </div>
                <div className="input-group">
                    <input type="password" name="password" placeholder="비밀번호" value={formData.password} onChange={handleInputChange} onKeyDown={handleKeyDown} required className={errors.password ? 'invalid' : ''} />
                    {errors.password && <p className="validation-message error">{errors.password}</p>}
                    {isCapsLockOn && <p className="validation-message caps-lock">Caps Lock이 켜져 있습니다.</p>}
                </div>
                <div className="input-group">
                    <input type="password" name="confirmPassword" placeholder="비밀번호 확인" value={formData.confirmPassword} onChange={handleInputChange} onKeyDown={handleKeyDown} required className={errors.confirmPassword ? 'invalid' : ''} />
                    {errors.confirmPassword && <p className="validation-message error">{errors.confirmPassword}</p>}
                    {isCapsLockOn && <p className="validation-message caps-lock">Caps Lock이 켜져 있습니다.</p>}
                </div>
                <div className="input-group">
                    <input type="email" name="email" placeholder="이메일" value={formData.email} onChange={handleInputChange} required className={errors.email ? 'invalid' : ''} />
                    {errors.email && <p className="validation-message error">{errors.email}</p>}
                </div>
                <div className="input-group">
                    <input type="text" name="name" placeholder="이름" value={formData.name} onChange={handleInputChange} required className={errors.name ? 'invalid' : ''} />
                    {errors.name && <p className="validation-message error">{errors.name}</p>}
                </div>
                <div className="input-group">
                    <input type="text" name="birth" placeholder="생년월일 (YYYY-MM-DD)" value={formData.birth} onChange={handleBirthdayChange} required className={errors.birth ? 'invalid' : ''} />
                    {errors.birth && <p className="validation-message error">{errors.birth}</p>}
                </div>
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
                <div className="input-group">
                    <input type="tel" name="phone" placeholder="휴대폰 번호" value={formData.phone} onChange={handlePhoneNumberChange} required className={errors.phone ? 'invalid' : ''} />
                    {errors.phone && <p className="validation-message error">{errors.phone}</p>}
                </div>
                
                {submitError && <p className="error-message">{submitError}</p>}
                
                <button type="submit">가입하기</button>
            </form>
        </main>
    );
}

export default SignUpPage;

