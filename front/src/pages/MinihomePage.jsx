import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import apiClient from '../api/axiosConfig';
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
            const ownerRes = await apiClient.get(`/users/by-login-id/${loginId}`);
            const ownerData = ownerRes.data;
            setMinihomeOwner(ownerData);

            const minihomeRes = await apiClient.get(`/users/${ownerData.id}/mini-homepage`);
            const minihomeData = minihomeRes.data;
            setMinihomeData(minihomeData);

            if (currentUser) {
                if (currentUser.id === ownerData.id) {
                    setIlchonStatus('OWNER');
                    const requestsRes = await apiClient.get(`/ilchons/requests/received`);
                    const requestsData = requestsRes.data;
                    setReceivedRequests(requestsData);
                } else {
                    const statusRes = await apiClient.get(`/ilchons/status?targetUserId=${ownerData.id}`);
                    const statusData = statusRes.data;
                    setIlchonStatus(statusData.status);
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
            await apiClient.put(`/users/${minihomeOwner.id}/mini-homepage`, { title: newTitle });
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
