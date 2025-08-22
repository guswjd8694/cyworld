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
import IlchonRequestModal from '../components/home/IlchonRequestModal';

function MinihomePage() {
    const { loginId } = useParams();
    const { currentUser, loading: authLoading } = useContext(AuthContext);
    const navigate = useNavigate();

    const [minihomeOwner, setMinihomeOwner] = useState(null);
    const [minihomeData, setMinihomeData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [activeView, setActiveView] = useState('HOME');
    const navigateTo = (view) => setActiveView(view);

    // --- 모달 및 일촌 상태 관리 ---
    const [isIlchonModalOpen, setIsIlchonModalOpen] = useState(false);
    const [isIlchonRequestModalOpen, setIsIlchonRequestModalOpen] = useState(false);
    const [ilchonStatus, setIlchonStatus] = useState(null);
    const [receivedRequests, setReceivedRequests] = useState([]);

    const fetchData = async () => {
        if (!loginId || authLoading) return;
        setLoading(true);
        try {
            const ownerRes = await fetch(`http://localhost:8080/users/by-login-id/${loginId}`);
            if (!ownerRes.ok) throw new Error("미니홈피 주인을 찾을 수 없습니다.");
            const ownerData = await ownerRes.json();
            setMinihomeOwner(ownerData);

            const minihomeRes = await fetch(`http://localhost:8080/users/${ownerData.id}/mini-homepage`, {
                headers: { 'Authorization': `Bearer ${localStorage.getItem('jwt-token')}` }
            });
            if (!minihomeRes.ok) throw new Error("미니홈피 정보를 불러오는 데 실패했습니다.");
            const minihomeData = await minihomeRes.json();
            setMinihomeData(minihomeData);

            if (currentUser) {
                if (currentUser.id === ownerData.id) {
                    setIlchonStatus('OWNER');
                    const requestsRes = await fetch(`http://localhost:8080/ilchons/requests/received`, {
                        headers: { 'Authorization': `Bearer ${localStorage.getItem('jwt-token')}` }
                    });
                    if (requestsRes.ok) {
                        const requestsData = await requestsRes.json();
                        setReceivedRequests(requestsData);
                    }
                } else {
                    const statusRes = await fetch(`http://localhost:8080/ilchons/status?targetUserId=${ownerData.id}`, {
                        headers: { 'Authorization': `Bearer ${localStorage.getItem('jwt-token')}` }
                    });
                    if (statusRes.ok) {
                        const statusData = await statusRes.json();
                        setIlchonStatus(statusData.status);
                    }
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

    useEffect(() => {
        setActiveView('HOME');
        fetchData();
    }, [loginId, currentUser, authLoading]);

    const handleTitleUpdate = async (newTitle) => {
        try {
            const response = await fetch(`http://localhost:8080/users/${minihomeOwner.id}/mini-homepage`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${localStorage.getItem('jwt-token')}` },
                body: JSON.stringify({ title: newTitle })
            });
            if (!response.ok) throw new Error('타이틀 수정에 실패했습니다.');
            setMinihomeData(prev => ({ ...prev, title: newTitle }));
        } catch (err) {
            alert(err.message);
        }
    };

    const handleIlchonRequestClick = () => {
        if (!currentUser) {
            alert('로그인이 필요합니다.');
            navigate('/login');
        } else {
            setIsIlchonModalOpen(true);
        }
    };

    const pageLoading = authLoading || loading;
    if (pageLoading || !minihomeOwner || !minihomeData) {
        return <div>미니홈피 로딩 중...</div>;
    }

    return (
        <div className="container">
            <main className="main_container">
                <LeftPage userId={minihomeOwner.id} activeView={activeView} />
                
                <RightPageLayout 
                    owner={minihomeOwner}
                    title={minihomeData.title}
                    onTitleUpdate={handleTitleUpdate}
                    onIlchonClick={handleIlchonRequestClick}
                    onIlchonRequestCheckClick={() => setIsIlchonRequestModalOpen(true)}
                    ilchonStatus={ilchonStatus}
                    receivedRequestCount={receivedRequests.length}
                >
                    {activeView === 'HOME' && <HomePage userId={minihomeOwner.id} navigateTo={navigateTo} ilchonStatus={ilchonStatus} />}
                    {activeView === 'GUESTBOOK' && <Guestbook userId={minihomeOwner.id} />}
                    {activeView === 'DIARY' && <Diary userId={minihomeOwner.id} />}
                </RightPageLayout>
            </main>
            <Nav navigateTo={navigateTo} activeView={activeView} />

            {isIlchonModalOpen && <IlchonModal onClose={() => setIsIlchonModalOpen(false)} targetUser={minihomeOwner} />}
            {isIlchonRequestModalOpen && <IlchonRequestModal onClose={() => setIsIlchonRequestModalOpen(false)} requests={receivedRequests} onUpdate={fetchData} />}
        </div>
    );
}

export default MinihomePage;
