import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../contexts/AuthContext';
import ProfileSection from '../components/profile/ProfileSection';
import BoardListSection from '../components/profile/BoardListSection';
import ProfileHistoryModal from '../components/profile/ProfileHistoryModal';
import apiClient from '../api/axiosConfig';

function LeftPage({ userId, activeView }) {
    const [minihomeData, setMinihomeData] = useState({ todayVisit: 0, totalVisit: 0 });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { logout } = useContext(AuthContext);

    const [isHistoryModalOpen, setIsHistoryModalOpen] = useState(false);
    const [profileData, setProfileData] = useState(null);
    
    useEffect(() => {
        if (!userId) return;
        
        const fetchMinihomeData = async () => {
            setLoading(true);
            try {
                const [minihomeRes, profileRes] = await Promise.all([
                    apiClient.get(`/users/${userId}/mini-homepage`),
                    apiClient.get(`/users/${userId}/profile`)
                ]);

                setMinihomeData(minihomeRes.data);
                setProfileData(profileRes.data);

            } catch (err) {
                if (err.response?.status !== 401) {
                    setError(err.message);
                }
            } finally {
                setLoading(false);
            }
        };

        fetchMinihomeData();
    }, [userId]);


    const showProfile = activeView === 'HOME' || activeView === 'GUESTBOOK';

    return (
        <>
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
                    
                    {showProfile && profileData && (
                        <ProfileSection 
                            userId={userId} 
                            profileData={profileData} 
                            onHistoryClick={() => setIsHistoryModalOpen(true)} 
                        />
                    )}
                    {!showProfile && (
                        <BoardListSection userId={userId} boardType={activeView} />
                    )}
                </div>
            </section>
            
            {isHistoryModalOpen && profileData && (
                <ProfileHistoryModal 
                    onClose={() => setIsHistoryModalOpen(false)} 
                    history={profileData.profileHistoryList} 
                />
            )}
        </>
    );
}

export default LeftPage;