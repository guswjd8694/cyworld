import React, { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';
import LeftPage from '../layouts/LeftPage';
import RightPageLayout from '../layouts/RightPage';
import Nav from '../layouts/Nav';
import HomePage from '../components/home/HomePage';
import Guestbook from '../components/guestbook/Guestbook';
import Diary from '../components/diary/Diary';
import IlchonModal from '../components/home/IlchonModal';

function MinihomePage() {
    const { loginId } = useParams();
    // 1. AuthContext에서 로딩 상태(authLoading)를 함께 가져옵니다.
    const { currentUser, loading: authLoading } = useContext(AuthContext);
    const [minihomeOwner, setMinihomeOwner] = useState(null);
    const [loading, setLoading] = useState(true);
    const [activeView, setActiveView] = useState('HOME');
    const navigateTo = (view) => setActiveView(view);

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [ilchonStatus, setIlchonStatus] = useState(null);

    useEffect(() => {
        // 2. 로그인 정보 로딩이 끝나기 전에는 아무것도 실행하지 않습니다.
        if (!loginId || authLoading) return;

        const fetchAllData = async () => {
            setLoading(true);
            setMinihomeOwner(null);
            setIlchonStatus(null);
            try {
                const ownerRes = await fetch(`http://localhost:8080/users/by-login-id/${loginId}`);
                if (!ownerRes.ok) throw new Error("미니홈피 주인을 찾을 수 없습니다.");
                const ownerData = await ownerRes.json();
                setMinihomeOwner(ownerData);

                // 3. 이제 currentUser 정보가 확실하므로, 여기서 일촌 관계를 확인합니다.
                if (currentUser && currentUser.id === ownerData.id) {
                    setIlchonStatus('OWNER'); // 내 미니홈피
                } else if (currentUser) { // 다른 사람의 미니홈피에 로그인해서 방문한 경우
                    const statusRes = await fetch(`http://localhost:8080/ilchons/status?targetUserId=${ownerData.id}`, {
                        headers: { 'Authorization': `Bearer ${localStorage.getItem('jwt-token')}` }
                    });
                    if (statusRes.ok) {
                        const statusData = await statusRes.json();
                        setIlchonStatus(statusData.status);
                    } else {
                        // API 호출 실패 시, 일촌이 아닌 것으로 간주합니다.
                        console.error("일촌 상태 확인 실패:", statusRes.status);
                        setIlchonStatus('NONE');
                    }
                } else { // 로그인하지 않고 방문한 경우
                    setIlchonStatus('NONE');
                }
            } catch (error) {
                console.error(error);
            } finally {
                setLoading(false);
            }
        };
        
        setActiveView('HOME');
        fetchAllData();
    }, [loginId, currentUser, authLoading]); // 4. authLoading을 의존성 배열에 추가합니다.

    const pageLoading = authLoading || loading;
    if (pageLoading || !minihomeOwner) {
        return <div>미니홈피 로딩 중...</div>;
    }

    return (
        <div className="container">
            <main className="main_container">
                <LeftPage userId={minihomeOwner.id} activeView={activeView} />
                
                <RightPageLayout 
                    owner={minihomeOwner} 
                    onIlchonClick={() => setIsModalOpen(true)} 
                    ilchonStatus={ilchonStatus}
                >
                    {activeView === 'HOME' && <HomePage userId={minihomeOwner.id} navigateTo={navigateTo} />}
                    {activeView === 'GUESTBOOK' && <Guestbook userId={minihomeOwner.id} />}
                    {activeView === 'DIARY' && <Diary userId={minihomeOwner.id} />}
                </RightPageLayout>
            </main>
            <Nav navigateTo={navigateTo} activeView={activeView} />

            {isModalOpen && (
                <IlchonModal 
                    onClose={() => setIsModalOpen(false)} 
                    targetUser={minihomeOwner} 
                />
            )}
        </div>
    );
}

export default MinihomePage;
