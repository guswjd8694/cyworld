import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../contexts/AuthContext';
import ProfileSection from '../components/profile/ProfileSection';
import BoardListSection from '../components/profile/BoardListSection';

function LeftPage({ userId, activeView }) {
    const [minihomeData, setMinihomeData] = useState({ todayVisit: 0, totalVisit: 0 });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { logout } = useContext(AuthContext);
    
    useEffect(() => {
        if (!userId) return;
        
        const fetchMinihomeData = async () => {
            setLoading(true);
            try {
                const response = await fetch(`http://localhost:8080/users/${userId}/mini-homepage`);
                if (!response.ok) {
                    throw new Error('방문자 수 정보를 불러오는 데 실패했습니다.');
                }
                const data = await response.json();
                setMinihomeData(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchMinihomeData();
    }, [userId]);

    const handleLogout = () => {
        logout();
    };

    const showProfile = activeView === 'HOME' || activeView === 'GUESTBOOK';

    return (
        <section className="section_left book_cover left_page">
            <h2 className="sr_only">왼쪽 페이지</h2>
            <div className="inner_page">
                <section className="today_stats top_area">
                    <dl className="visit_list">
                        <dt>today</dt>
                        <dd className="today_value">{loading ? '...' : minihomeData.todayVisit}</dd>
                        <dt>total</dt>
                        <dd>{loading ? '...' : minihomeData.totalVisit}</dd>
                    </dl>
                </section>
                
                {showProfile ? (
                    <ProfileSection userId={userId} />
                ) : (
                    <BoardListSection userId={userId} boardType={activeView} />
                )}
                <button onClick={handleLogout} className="logout-button">로그아웃</button>
            </div>
        </section>
    );
}

export default LeftPage;