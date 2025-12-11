import React, { useState, useEffect, useContext, useCallback } from 'react';
import { Link } from 'react-router-dom';
import apiClient from '../../api/axiosConfig';
import { AuthContext } from '../../contexts/AuthContext';
import '../../styles/Modal.scss';
import '../../styles/IlchonModal.scss';

function MyIlchonsTab({ ilchons, onUpdate }) {
    const [editingState, setEditingState] = useState({ 
        id: null,
        nicknameForToUser: '', 
        nicknameForFromUser: ''
    });

    const handleEditClick = (ilchon) => {
        setEditingState({ 
            id: ilchon.friendId,
            nicknameForToUser: ilchon.nicknameForToUser,
            nicknameForFromUser: ilchon.nicknameForFromUser
        });
    };

    const handleCancelEdit = () => {
        setEditingState({ id: null, nicknameForToUser: '', nicknameForFromUser: '' });
    };

    const handleSaveNickname = async () => {
        try {
            await apiClient.put(`/ilchons/nickname`, {
                targetUserId: editingState.id,
                nicknameForToUser: editingState.nicknameForToUser,
                nicknameForFromUser: editingState.nicknameForFromUser
            });
            alert("일촌명이 변경되었습니다.");
            handleCancelEdit();
            onUpdate();
        } catch (error) {
            alert(error.response?.data?.message || "일촌명 변경에 실패했습니다.");
        }
    };
    
    const handleBreakIlchon = async (targetUserId) => {
        if (window.confirm("정말로 일촌을 끊으시겠습니까?")) {
            try {
                await apiClient.delete(`/ilchons/${targetUserId}`); 
                alert("일촌 관계가 해제되었습니다.");
                onUpdate();
            } catch (error) {
                alert(error.response?.data?.message || "일촌 끊기에 실패했습니다.");
            }
        }
    };
    
    return (
        <div className="tab-pane">
            {ilchons.length > 0 ? (
                <ul className="ilchon-list">
                    {ilchons.map(ilchon => (
                        <li key={ilchon.ilchonRequestId}>
                            {editingState.id === ilchon.friendId ? (
                                <div className="edit-nickname-form">
                                    <div className='nickname-inputs'>
                                        <input 
                                            type="text" 
                                            value={editingState.nicknameForFromUser} 
                                            onChange={(e) => setEditingState(prev => ({...prev, nicknameForFromUser: e.target.value}))}
                                            placeholder="친구가 부를 이름"
                                        />
                                        <span className="separator">↔</span>
                                        <input 
                                            type="text" 
                                            value={editingState.nicknameForToUser} 
                                            onChange={(e) => setEditingState(prev => ({...prev, nicknameForToUser: e.target.value}))}
                                            placeholder="내가 부를 이름"
                                        />
                                    </div>
                                    <div className="ilchon-actions">
                                        <button className="action-btn" onClick={handleSaveNickname}>저장</button>
                                        <button className="action-btn" onClick={handleCancelEdit}>취소</button>
                                    </div>
                                </div>
                            ) : (
                                <>
                                    <div className="ilchon-link">
                                        <span className="friend-info" >
                                            <strong>
                                                <Link to={`/${ilchon.friendLoginId}`}>{ilchon.friendName}</Link>
                                            </strong>
                                            ({ilchon.nicknameForToUser})
                                        </span>
                                        <span className="separator">↔</span>
                                        <span className="my-info">
                                            <strong>나</strong>({ilchon.nicknameForFromUser})
                                        </span>
                                    </div>
                                    <div className="ilchon-actions">
                                        <button className="action-btn" onClick={() => handleEditClick(ilchon)}>이름변경</button>
                                        <button className="action-btn danger" onClick={() => handleBreakIlchon(ilchon.friendId)}>끊기</button>
                                    </div>
                                </>
                            )}
                        </li>
                    ))}
                </ul>
            ) : <p>일촌이 없습니다.</p>}
        </div>
    );
}

function SentRequestsTab({ requests, onUpdate }) {
    const handleCancelRequest = async (requestId) => {
        if (window.confirm("일촌 신청을 취소하시겠습니까?")) {
            try {
                await apiClient.delete(`/ilchons-requests/${requestId}`, {
                    status: "CANCELED"
                });
                alert("신청이 취소되었습니다.");
                onUpdate();
            } catch(error) {
                alert(error.response?.data?.message || "취소에 실패했습니다.");
            }
        }
    };

    return (
        <div className="tab-pane">
            {requests.length > 0 ? (
                <ul className="ilchon-list">
                    {requests.map(req => (
                        <li key={req.ilchonRequestId}>
                            <div className="ilchon-link">
                                <span className="friend-info">
                                    <Link to={`/${req.receiverLoginId}`}>{req.receiverName}</Link>
                                    ({req.nicknameForToUser})
                                </span>
                                <span className="separator">↔</span>
                                <span className="my-info">
                                    나 ({req.nicknameForFromUser})
                                </span>
                            </div>
                            <div className="ilchon-actions">
                                <button className="action-btn danger" onClick={() => handleCancelRequest(req.ilchonRequestId)}>신청취소</button>
                            </div>
                        </li>
                    ))}
                </ul>
            ) : <p>보낸 일촌 신청이 없습니다.</p>}
        </div>
    );
}

function RequestDetail({ request, onAccept, onReject }) {
    return (
        <div className="request-details">
            <div className="request-body">
                <figure className="minimi-figure">
                    <img src="/imgs/minimi_guest.jpg" alt={`${request.requesterName}님의 미니미`} />
                </figure>
                <div className="request-text">
                    <p><Link to={`/${request.requesterLoginId}`}>{request.requesterName}</Link>님께서 {request.receiverName}님과</p>
                    <p>일촌맺기를 희망합니다.</p>
                </div>
            </div>
            <div className="request-message-box">
                {request.requestMessage}
            </div>
            <div className="nickname-info">
                <p>아래 일촌명으로 신청하셨습니다.</p>
                <p className="nickname-display">
                    {request.requesterName}({request.nicknameForFromUser}) - {request.receiverName}({request.nicknameForToUser})
                </p>
            </div>
            <footer className="modal-footer">
                <p>일촌을 맺으시겠습니까?</p>
                <div className="ilchon-actions">
                    <button className="action-btn" onClick={() => onAccept(request.ilchonRequestId)}>예</button>
                    <button className="action-btn danger" onClick={() => onReject(request.ilchonRequestId)}>아니오</button>
                </div>
            </footer>
        </div>
    );
}


function ReceivedRequestsTab({ requests, onUpdate }) {
    const [expandedRequestId, setExpandedRequestId] = useState(null);

    const handleToggle = (requestId) => {
        setExpandedRequestId(currentId => (currentId === requestId ? null : requestId));
    };

    const handleAccept = async (requestId) => {
        try {
            await apiClient.patch(`/ilchons-requests/${requestId}`, {
                status: "ACCEPTED"
            });
            onUpdate();
        } catch (err) {
            alert(err.response?.data?.message || '수락에 실패했습니다.');
        }
    };

    const handleReject = async (requestId) => {
        try {
            await apiClient.patch(`/ilchons-requests/${requestId}`, {
                status: "REJECTED"
            });
            onUpdate();
        } catch (err) {
            alert(err.response?.data?.message || '거절에 실패했습니다.');
        }
    };

    return (
        <div className="tab-pane">
            {requests.length > 0 ? (
                <ul className="request-list accordion">
                    {requests.map(req => (
                        <li key={req.ilchonRequestId} className="request-item">
                            <div className="request-summary" onClick={() => handleToggle(req.ilchonRequestId)}>
                                <span><Link to={`/${req.requesterLoginId}`}>{req.requesterName}</Link>님의 일촌 신청</span>
                                <span className={`arrow ${expandedRequestId === req.ilchonRequestId ? 'up' : 'down'}`}>▼</span>
                            </div>
                            {expandedRequestId === req.ilchonRequestId && (
                                <RequestDetail request={req} onAccept={handleAccept} onReject={handleReject} />
                            )}
                        </li>
                    ))}
                </ul>
            ) : <p>받은 일촌 신청이 없습니다.</p>}
        </div>
    );
}

function IlchonManageModal({ onClose, initialTab = 'myIlchons', onUpdatePage }) {
    const { currentUser } = useContext(AuthContext);
    const [activeTab, setActiveTab] = useState(initialTab);
    const [data, setData] = useState({ myIlchons: [], sentRequests: [], receivedRequests: [] });
    const [loading, setLoading] = useState(true);



    const fetchData = useCallback(async () => {
        if (!currentUser) return;
        setLoading(true);
        try {
            const [myIlchonsRes, sentRes, receivedRes] = await Promise.all([
                apiClient.get(`/users/${currentUser.id}/ilchons`),
                apiClient.get('/ilchons-requests?fromUser=me&status=PENDING'),
                apiClient.get('/ilchons-requests?toUser=me&status=PENDING')
            ]);
            setData({
                myIlchons: myIlchonsRes.data,
                sentRequests: sentRes.data,
                receivedRequests: receivedRes.data
            });
            
        } catch (error) {
            console.error("일촌 정보를 불러오는데 실패했습니다.", error);
        } finally {
            setLoading(false);
        }
    }, [currentUser]);

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    const handleUpdate = () => {
        console.log("IlchonManageModal: handleUpdate 실행. '내부' fetchData와 '부모' onUpdatePage 호출 시작.");
        
        fetchData();
        if (onUpdatePage) {
            onUpdatePage();
        }
    };


    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content ilchon-manage-modal" onClick={(e) => e.stopPropagation()}>
                <header className="modal-header">
                    <h2>일촌 관리</h2>
                    <button className="close-btn" onClick={onClose}>&times;</button>
                </header>
                <nav className="modal-tabs">
                    <button onClick={() => setActiveTab('myIlchons')} className={activeTab === 'myIlchons' ? 'active' : ''}>
                        일촌 목록 ({data.myIlchons.length})
                    </button>
                    <button onClick={() => setActiveTab('sent')} className={activeTab === 'sent' ? 'active' : ''}>
                        보낸 신청 ({data.sentRequests.length})
                    </button>
                    <button onClick={() => setActiveTab('received')} className={activeTab === 'received' ? 'active' : ''}>
                        받은 신청 ({data.receivedRequests.length})
                    </button>
                </nav>
                <div className="modal-body">
                    {loading ? <p>로딩 중...</p> : (
                        <>
                            {activeTab === 'myIlchons' && <MyIlchonsTab ilchons={data.myIlchons} onUpdate={handleUpdate} />}
                            {activeTab === 'sent' && <SentRequestsTab requests={data.sentRequests} onUpdate={handleUpdate} />}
                            {activeTab === 'received' && <ReceivedRequestsTab requests={data.receivedRequests} onUpdate={handleUpdate} />}
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}

export default IlchonManageModal;

