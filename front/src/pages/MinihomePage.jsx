import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
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
    const { currentUser, loading: authLoading } = useContext(AuthContext);
    const navigate = useNavigate(); // 페이지 이동을 위한 navigate 함수

    const [minihomeOwner, setMinihomeOwner] = useState(null);
    const [loading, setLoading] = useState(true);
    const [activeView, setActiveView] = useState('HOME');
    const navigateTo = (view) => setActiveView(view);

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [ilchonStatus, setIlchonStatus] = useState(null);

    useEffect(() => {
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

                if (currentUser && currentUser.id === ownerData.id) {
                    setIlchonStatus('OWNER');
                } else if (currentUser) {
                    const statusRes = await fetch(`http://localhost:8080/ilchons/status?targetUserId=${ownerData.id}`, {
                        headers: { 'Authorization': `Bearer ${localStorage.getItem('jwt-token')}` }
                    });
                    if (statusRes.ok) {
                        const statusData = await statusRes.json();
                        setIlchonStatus(statusData.status);
                    } else {
                        setIlchonStatus('NONE');
                    }
                } else {
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
    }, [loginId, currentUser, authLoading]);

    const handleIlchonRequestClick = () => {
        if (!currentUser) {
            alert('로그인이 필요합니다.');
            navigate('/login');
        } else {
            setIsModalOpen(true);
        }
    };

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
                    onIlchonClick={handleIlchonRequestClick}
                    ilchonStatus={ilchonStatus}
                >
                    {activeView === 'HOME' && <HomePage userId={minihomeOwner.id} navigateTo={navigateTo} ilchonStatus={ilchonStatus} />}
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
