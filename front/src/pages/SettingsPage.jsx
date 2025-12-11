import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';
import apiClient from '../api/axiosConfig';
import '../styles/settings.scss'; 

const validate = (name, value, formData) => {
    switch (name) {
        case 'email':
            if (!value) return '이메일을 입력해주세요.';
            if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) return '유효한 이메일 형식이 아닙니다.';
            return '';
        case 'phone':
            if (!value) return '휴대폰 번호를 입력해주세요.';
            if (!/^\d{3}-\d{4}-\d{4}$/.test(value)) return '010-1234-5678 형식으로 입력해주세요.';
            return '';
        case 'name':
            if (!value) return '이름을 입력해주세요.';
            if (!/^[가-힣]+$/.test(value)) return '이름은 한글만 입력 가능합니다.';
            if (value.length < 2 || value.length > 5) return '이름은 2자 이상 5자 이하로 입력해주세요.';
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
        case 'gender':
            if (!value) return '성별을 선택해주세요.';
            return '';
        default:
            return '';
    }
};

function SettingsPage() {
    const { currentUser, logout } = useContext(AuthContext);
    const navigate = useNavigate();

    const [formData, setFormData] = useState({ 
        name: '', 
        email: '', 
        phone: '',
        birth: '',
        gender: ''
    });

    const [loading, setLoading] = useState(true);
    const [submitError, setSubmitError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [validationErrors, setValidationErrors] = useState({});
    const [isConfirmingWithdrawal, setIsConfirmingWithdrawal] = useState(false);
    const [confirmationText, setConfirmationText] = useState('');

    useEffect(() => {
        if (!currentUser) return;
        
        const fetchUserData = async () => {
            try {
                if (!currentUser.id) {
                    throw new Error("currentUser.id is not available");
                }
                const response = await apiClient.get(`/users/me`);
                const userData = response.data;

                let formattedGender = '';
                if (userData.gender) {
                    if (userData.gender.toUpperCase() === 'MALE') {
                        formattedGender = 'Male';
                    } else if (userData.gender.toUpperCase() === 'FEMALE') {
                        formattedGender = 'Female';
                    } else {
                        formattedGender = userData.gender;
                    }
                }

                let formattedBirth = '';
                if (userData.birth) {
                    const birthOnlyDigits = userData.birth.replace(/-/g, '');
                    if (birthOnlyDigits.length === 8 && /^\d{8}$/.test(birthOnlyDigits)) {
                        formattedBirth = `${birthOnlyDigits.slice(0, 4)}-${birthOnlyDigits.slice(4, 6)}-${birthOnlyDigits.slice(6, 8)}`;
                    } else if (/^\d{4}-\d{2}-\d{2}$/.test(userData.birth)) {
                        formattedBirth = userData.birth;
                    }
                }

                let formattedPhone = '';
                if (userData.phone) {
                    const phoneOnlyDigits = userData.phone.replace(/-/g, '');
                    if (phoneOnlyDigits.length === 11) {
                        formattedPhone = `${phoneOnlyDigits.slice(0, 3)}-${phoneOnlyDigits.slice(3, 7)}-${phoneOnlyDigits.slice(7, 11)}`;
                    } else if (phoneOnlyDigits.length === 10) {
                        formattedPhone = `${phoneOnlyDigits.slice(0, 3)}-${phoneOnlyDigits.slice(3, 6)}-${phoneOnlyDigits.slice(6, 10)}`;
                    } else {
                        formattedPhone = userData.phone;
                    }
                }

                setFormData({
                    name: userData.name || '',
                    email: userData.email || '',
                    phone: formattedPhone || '',
                    birth: formattedBirth,
                    gender: formattedGender
                });

            } catch (err) {
                setSubmitError('사용자 정보를 불러오는 데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };
        fetchUserData();
    }, [currentUser]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));

        const errorMessage = validate(name, value, formData);
        setValidationErrors(prev => ({ ...prev, [name]: errorMessage }));
    };

    const handlePhoneNumberChange = (e) => {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 11) value = value.slice(0, 11);
        if (value.length > 3) value = `${value.slice(0, 3)}-${value.slice(3)}`;
        if (value.length > 8) value = `${value.slice(0, 8)}-${value.slice(8)}`;
        
        handleInputChange({ target: { name: 'phone', value } });
    };

    const handleBirthChange = (e) => {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 8) value = value.slice(0, 8);
        if (value.length > 4) value = `${value.slice(0, 4)}-${value.slice(4)}`;
        if (value.length > 7) value = `${value.slice(0, 7)}-${value.slice(7)}`;
        
        handleInputChange({ target: { name: 'birth', value } });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSubmitError('');
        setSuccessMessage('');

        const newErrors = {
            name: validate('name', formData.name, formData),
            email: validate('email', formData.email, formData),
            phone: validate('phone', formData.phone, formData),
            birth: validate('birth', formData.birth, formData),
            gender: validate('gender', formData.gender, formData)
        };
        setValidationErrors(newErrors);

        if (newErrors.name || newErrors.email || newErrors.phone || newErrors.birth || newErrors.gender) {
            setSubmitError('입력 정보를 다시 확인해주세요.');
            return;
        }

        try {
            await apiClient.put(`/users/${currentUser.id}`, {
                name: formData.name,
                email: formData.email,
                phone: formData.phone.replace(/-/g, ''),
                birth: formData.birth.replace(/-/g, ''),
                gender: formData.gender
            });
            setSuccessMessage('성공적으로 수정되었습니다.');
        } catch (err) {
            if (err.response?.status !== 401) {
                setSubmitError(err.response?.data?.message || '수정에 실패했습니다.');
            }
        }
    };

    
    const handleWithdraw = async () => {
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
                setSubmitError(err.response?.data?.message || '회원 탈퇴에 실패했습니다.');
            }
        }
    };


    if (loading) return <div>로딩 중...</div>;

    return (
        <div className="settings-container">
            <h2>개인정보 수정</h2>
            <form onSubmit={handleSubmit} className="setting-form">

                <div className="form-group">
                    <label htmlFor="name" className="sr_only">이름</label>
                    <input 
                        type="text" 
                        id="name"
                        name="name"
                        value={formData.name}
                        onChange={handleInputChange}
                        placeholder="이름"
                        required
                        className={validationErrors.name ? 'invalid' : ''} 
                    />
                    {validationErrors.name && <p className="validation-message error">{validationErrors.name}</p>}
                </div>

                <div className="form-group">
                    <label htmlFor="email" className="sr_only">이메일</label>
                    <input 
                        type="email" 
                        id="email"
                        name="email"
                        value={formData.email}
                        onChange={handleInputChange}
                        placeholder="이메일"
                        required
                        className={validationErrors.email ? 'invalid' : ''} 
                    />
                    {validationErrors.email && <p className="validation-message error">{validationErrors.email}</p>}
                </div>

                <div className="form-group">
                    <label htmlFor="phone" className="sr_only">휴대폰 번호</label>
                    <input 
                        type="tel" 
                        id="phone"
                        name="phone"
                        value={formData.phone}
                        onChange={handlePhoneNumberChange}
                        placeholder="휴대폰 번호"
                        required
                        className={validationErrors.phone ? 'invalid' : ''} 
                    />
                    {validationErrors.phone && <p className="validation-message error">{validationErrors.phone}</p>}
                </div>

                <div className="form-group">
                    <label htmlFor="birth" className="sr_only">생년월일</label>
                    <input 
                        type="text"
                        id="birth"
                        name="birth"
                        value={formData.birth}
                        onChange={handleBirthChange}
                        placeholder="생년월일 (YYYY-MM-DD)"
                        required
                        className={validationErrors.birth ? 'invalid' : ''} 
                    />
                    {validationErrors.birth && <p className="validation-message error">{validationErrors.birth}</p>}
                </div>

                <div className="form-group"> 
                    <div className="gender-select">
                        <label>
                            <input 
                                type="radio" 
                                name="gender" 
                                value="Male" 
                                checked={formData.gender === 'Male'}
                                onChange={handleInputChange} 
                            />
                            <span>남자</span>
                        </label>
                        <label>
                            <input 
                                type="radio" 
                                name="gender" 
                                value="Female" 
                                checked={formData.gender === 'Female'}
                                onChange={handleInputChange}
                            />
                            <span>여자</span>
                        </label>
                    </div>
                </div>

                {submitError && <p className="error-message">{submitError}</p>}
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