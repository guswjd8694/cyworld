import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import apiClient from '../../api/axiosConfig';

function PadoTaki({ userId }) {
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const dropdownRef = useRef(null);

    const [ilchonList, setIlchonList] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!userId) return;

        const fetchIlchons = async () => {
            setLoading(true);
            try {
                const response = await apiClient.get(`/users/${userId}/ilchons`);
                setIlchonList(response.data);
            } catch (error) {
                console.error("일촌 목록을 불러오는데 실패했습니다.", error);
            } finally {
                setLoading(false);
            }
        };

        fetchIlchons();
    }, [userId]);

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

    const handleDropdownItemClick = () => {
        setIsDropdownOpen(false);
    };

    return (
        <section className="pado_taki_area">
            <div className="dropdown" ref={dropdownRef}>
                <button 
                    className="pado-button" 
                    onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                    disabled={loading || ilchonList.length === 0}
                >
                    ★일촌 파도타기 
                    <span className="arrow">
                        <img src="/imgs/caret-down.svg" alt="메뉴 열기" />
                    </span>
                </button>

                {isDropdownOpen && (
                    <ul className="dropdown-menu">
                        {loading ? (
                            <li>로딩 중...</li>
                        ) : ilchonList.length > 0 ? (
                            ilchonList.map(ilchon => (
                                <li key={ilchon.friendId}>
                                    <Link 
                                        to={`/${ilchon.friendLoginId}`}
                                        onClick={handleDropdownItemClick}
                                    >
                                        {ilchon.friendName}
                                    </Link>
                                </li>
                            ))
                        ) : (
                            <li>일촌이 없습니다.</li>
                        )}
                    </ul>
                )}
            </div>
        </section>
    );
}

export default PadoTaki;