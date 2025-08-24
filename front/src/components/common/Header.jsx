import React, { useState, useEffect, useContext, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import apiClient from '../../api/axiosConfig';

function Header() {
    const { currentUser, logout } = useContext(AuthContext);
    const navigate = useNavigate();

    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [ilchonList, setIlchonList] = useState([]);
    
    const dropdownRef = useRef(null);

    useEffect(() => {
        if (currentUser) {
            const fetchIlchons = async () => {
                try {
                    const response = await apiClient.get(`/ilchons/users/${currentUser.id}`);
                    
                    setIlchonList(response.data);
                } catch (error) {
                    if (error.response?.status !== 401) {
                        console.error("일촌 목록을 불러오는 데 실패했습니다.", error);
                    }
                }
            };
            fetchIlchons();
        }
    }, [currentUser]);

    useEffect(() => {
        if (!isDropdownOpen) return;

        function handleClickOutside(event) {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setIsDropdownOpen(false);
            }
        }

        document.addEventListener('mousedown', handleClickOutside);
        
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [isDropdownOpen]);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const handleRandomVisit = () => {
        if (ilchonList.length === 0) {
            alert('방문할 일촌이 없습니다.');
            return;
        }
        const randomIndex = Math.floor(Math.random() * ilchonList.length);
        const randomIlchon = ilchonList[randomIndex];
        navigate(`/${randomIlchon.friendLoginId}`);
        setIsDropdownOpen(false);
    };

    const handleDropdownItemClick = () => {
        setIsDropdownOpen(false);
    };

    return (
        <header className="site-header">
            <div className="header-content">
                <nav className="site-nav">
                    {currentUser ? (
                        <>
                            <Link to={`/${currentUser.loginId}`}>싸이월드 내 홈피</Link>
                            <div className="dropdown" ref={dropdownRef}>
                                <button onClick={() => setIsDropdownOpen(!isDropdownOpen)}>
                                    바로가기 ▼
                                </button>
                                {isDropdownOpen && (
                                    <ul className="dropdown-menu">
                                        {ilchonList.length > 0 ? (
                                            ilchonList.map(ilchon => (
                                                <li key={ilchon.friendId}>
                                                    <Link 
                                                        to={`/${ilchon.friendLoginId}`}
                                                        onClick={handleDropdownItemClick}
                                                    >
                                                        {ilchon.friendName} ({ilchon.friendNicknameForMe})
                                                    </Link>
                                                </li>
                                            ))
                                        ) : (
                                            <li>일촌이 없습니다.</li>
                                        )}
                                    </ul>
                                )}
                            </div>
                            <button onClick={handleRandomVisit}>랜덤</button>
                            <button onClick={handleLogout} className="logout-button"><img src="/imgs/logo.png" alt="싸이월드 로고 이미지" />로그아웃</button>
                        </>
                    ) : (
                        <>
                            <Link to="/signup">회원가입</Link>
                            <Link to="/login"><img src="/imgs/logo.png" alt="싸이월드 로고 이미지" />로그인</Link>
                        </>
                    )}
                </nav>
            </div>
        </header>
    );
}

export default Header;
