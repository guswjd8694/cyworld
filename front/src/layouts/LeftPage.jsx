import React, { useState } from 'react';
import ProfileSection from '../components/profile/ProfileSection';
import BoardListSection from '../components/profile/BoardListSection';
import ProfileHistoryModal from '../components/profile/ProfileHistoryModal';

function LeftPage({ userId, activeView, minihomeData, profileData, setProfileData, onDataRefetch }) {
    
    const [isHistoryModalOpen, setIsHistoryModalOpen] = useState(false);

    const showProfile = activeView === 'HOME' || activeView === 'GUESTBOOK';

    if (!minihomeData || !profileData) {
        return (
            <section className="section_left book_cover left_page">
                <div className="inner_page">
                    <div>데이터를 불러오는 중입니다...</div>
                </div>
            </section>
        )
    }

    return (
        <>
            <section className="section_left book_cover left_page">
                <h2 className="sr_only">왼쪽 페이지</h2>
                <div className="inner_page">
                    <section className="today_stats top_area">
                        <dl className="visit_list">
                            <dt>today</dt>
                            <dd className="today_value">{minihomeData.todayVisit}</dd>
                            <dt>total</dt>
                            <dd>{minihomeData.totalVisit}</dd>
                        </dl>
                    </section>
                    
                    {showProfile ? (
                        <ProfileSection 
                            userId={userId} 
                            profileData={profileData}
                            setProfileData={setProfileData} 
                            onHistoryClick={() => setIsHistoryModalOpen(true)}
                            setRefetchTrigger={onDataRefetch} 
                        />
                    ) : (
                        <BoardListSection userId={userId} boardType={activeView} />
                    )}
                </div>
            </section>
            
            {isHistoryModalOpen && (
                <ProfileHistoryModal 
                    onClose={() => setIsHistoryModalOpen(false)} 
                    history={profileData.history} 
                />
            )}
        </>
    );
}

export default LeftPage;
