import React, { useRef, useState, useEffect, useContext, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import apiClient from '../api/axiosConfig';
import { AuthContext } from '../contexts/AuthContext';
import LeftPage from '../layouts/LeftPage';
import RightPageLayout from '../layouts/RightPage';
import Nav from '../layouts/Nav';
import HomePage from '../components/home/HomePage';
import Guestbook from '../components/guestbook/Guestbook';
import Profile from '../components/profile/Profile';
import Diary from '../components/diary/Diary';
import PhotoAlbum from '../components/photo/PhotoAlbum';
import IlchonModal from '../components/home/IlchonModal';
import IlchonManageModal from '../components/home/IlchonManageModal';
import BgmPlayer from '../components/common/BgmPlayer';

function MinihomePage() {
    const { loginId } = useParams();
    const { currentUser, loading: authLoading } = useContext(AuthContext);
    const navigate = useNavigate();

    const [minihomeOwner, setMinihomeOwner] = useState(null);
    const [minihomeData, setMinihomeData] = useState(null);
    const [profileData, setProfileData] = useState(null);

    const [loading, setLoading] = useState(true);
    const [pageError, setPageError] = useState(false);

    const [activeView, setActiveView] = useState({ view: 'HOME', params: {} });
    const navigateTo = (view, params = {}) => setActiveView({ view, params });

    const [isIlchonModalOpen, setIsIlchonModalOpen] = useState(false);
    const [isIlchonManageModalOpen, setIsIlchonManageModalOpen] = useState(false);
    const [ilchonStatus, setIlchonStatus] = useState(null);
    const [receivedRequests, setReceivedRequests] = useState([]);

    const fetchData = useCallback(async () => {
        if (!loginId || authLoading) return;

        setLoading(true);
        setPageError(false);

        try {
            const ownerRes = await apiClient.get(`/users/by-login-id/${loginId}`);
            const ownerData = ownerRes.data;

            if (!ownerData || !ownerData.id) {
                throw new Error('Owner not found');
            }
            
            setMinihomeOwner(ownerData);
            const ownerId = ownerData.id;

            const visitResPromise = apiClient.post(`/users/${ownerId}/mini-homepage/visit`);

            const [profileRes, statusRes, requestsRes, visitRes] = await Promise.all([
                apiClient.get(`/users/${ownerId}/profile`),
                currentUser && currentUser.id !== ownerId
                    ? apiClient.get(`/ilchons/status?targetUserId=${ownerId}`)
                    : Promise.resolve({ data: { status: currentUser ? 'OWNER' : 'NONE' } }),
                currentUser && currentUser.id === ownerId
                    ? apiClient.get(`/ilchons/requests/received`)
                    : Promise.resolve({ data: [] }),
                visitResPromise
            ]);
            
            setMinihomeData(visitRes.data);
            setProfileData(profileRes.data);
            setIlchonStatus(statusRes.data.status);
            setReceivedRequests(requestsRes.data);

        } catch (error) {
            setPageError(true);
            console.error("데이터 로딩 중 에러 발생:", error);
        } finally {
            setLoading(false);
        }
    }, [loginId, authLoading, currentUser]);

    useEffect(() => {
        setActiveView({ view: 'HOME', params: {} });
        fetchData();
    }, [loginId]);

    const handleTitleUpdate = async (newTitle) => {
        try {
            await apiClient.put(`/users/${minihomeOwner.id}/mini-homepage`, { title: newTitle });
            setMinihomeData(prev => ({ ...prev, title: newTitle }));
        } catch (err) {
            console.error("타이틀 수정 실패:", err);
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
    
    if (loading || pageError || !minihomeOwner || !minihomeData || !profileData) {
        return <div>미니홈피 로딩 중...</div>;
    }

    return (
        <>
            <div className="container">
                <main className="main_container">
                    <LeftPage 
                        userId={minihomeOwner.id} 
                        activeView={activeView.view}
                        minihomeData={minihomeData}
                        profileData={profileData}
                        setProfileData={setProfileData}
                        onDataRefetch={fetchData}
                    />
                    
                    <RightPageLayout 
                        owner={minihomeOwner}
                        title={minihomeData.title}
                        onTitleUpdate={handleTitleUpdate}
                        onIlchonClick={handleIlchonRequestClick}
                        onIlchonRequestCheckClick={() => setIsIlchonManageModalOpen(true)}
                        ilchonStatus={ilchonStatus}
                        receivedRequestCount={receivedRequests.length}
                    >
                        {activeView.view === 'HOME' && <HomePage userId={minihomeOwner.id} navigateTo={navigateTo} ilchonStatus={ilchonStatus} />}
                        {activeView.view === 'PROFILE' && <Profile userId={minihomeOwner.id} />}
                        {activeView.view === 'DIARY' && <Diary userId={minihomeOwner.id} initialDate={activeView.params.selectedDate} />}
                        {activeView.view === 'PHOTO' && <PhotoAlbum userId={minihomeOwner.id} initialPhotoId={activeView.params.boardId} />}
                        {activeView.view === 'GUESTBOOK' && <Guestbook userId={minihomeOwner.id} />}
                    </RightPageLayout>
                </main>
                <Nav 
                    navigateTo={navigateTo} 
                    activeView={activeView.view} 
                />

                {isIlchonModalOpen && <IlchonModal onClose={() => setIsIlchonModalOpen(false)} targetUser={minihomeOwner} onUpdatePage={fetchData} />}
                {isIlchonManageModalOpen && 
                    <IlchonManageModal 
                        onClose={() => setIsIlchonManageModalOpen(false)} 
                        initialTab="received"
                        onUpdatePage={fetchData}
                    />
                }
            </div>
            
            {minihomeOwner && <BgmPlayer userId={minihomeOwner.id} ownerName={minihomeOwner.name}/>}
        </>
    );
}

export default MinihomePage;
