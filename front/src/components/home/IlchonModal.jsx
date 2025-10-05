import React, { useState, useContext } from 'react';
import { AuthContext } from '../../contexts/AuthContext';
import apiClient from '../../api/axiosConfig';
import '../../styles/Modal.scss';
import '../../styles/IlchonModal.scss';

function IlchonModal({ onClose, targetUser, onUpdatePage }) {
    const { currentUser } = useContext(AuthContext);

    const [friendNickname, setFriendNickname] = useState('');
    const [friendRelationDropdown, setFriendRelationDropdown] = useState('직접입력');
    const [userNickname, setUserNickname] = useState('');
    const [userRelationDropdown, setUserRelationDropdown] = useState('직접입력');
    const [requestMessage, setRequestMessage] = useState('');

    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState('');

    const getCurrentDateTime = () => {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        return `${year}.${month}.${day} ${hours}:${minutes}`;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        let finalFriendNickname = friendRelationDropdown === '직접입력' 
            ? friendNickname.trim() 
            : friendRelationDropdown;
            
        if (!finalFriendNickname) {
            setError(`'${targetUser.name}'님을 부를 일촌명을 입력하거나 선택해주세요.`);
            return;
        }

        let finalUserNickname = userRelationDropdown === '직접입력'
            ? userNickname.trim()
            : userRelationDropdown;

        if (!finalUserNickname) {
            setError(`'${currentUser.name}'님을 부를 일촌명을 입력하거나 선택해주세요.`);
            return;
        }
        
        setIsSubmitting(true);
        try {
            await apiClient.post(`/ilchons`, {
                targetUserId: targetUser.id,
                friendNickname: finalFriendNickname,
                userNickname: finalUserNickname,
                requestMessage: requestMessage
            });
            
            onUpdatePage();
            onClose();

        } catch (err) {
            const message = err.response?.data?.message || '일촌 신청에 실패했습니다.';
            setError(message);
        } finally {
            setIsSubmitting(false);
        }
    };

    const relationOptions = ["직접입력", "친한친구", "직장동료", "학교동창", "가족", "평생지기", "나의반쪽", "공주", "왕자"];

    if (!currentUser) {
        return null; 
    }

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content ilchon-modal" onClick={(e) => e.stopPropagation()}>
                <header className="modal-header">
                    <h2>일촌 신청</h2>
                    <button className="close-btn" onClick={onClose}>&times;</button>
                </header>
                <div className="modal-body">
                    <div className="sender-info">
                        보낸이 : {currentUser.name} <time>({getCurrentDateTime()})</time>
                    </div>
                    <div className="request-body">
                        <figure className="minimi-figure">
                            <img src="/imgs/minimi_guest.jpg" alt={`${currentUser.name}님의 미니미`} />
                        </figure>
                        <div className="request-text">
                            <p><strong>{targetUser.name}</strong>님께</p>
                            <p>일촌을 신청합니다.</p>
                        </div>
                    </div>
                    <form onSubmit={handleSubmit} className="ilchon-relation-form">
                        <div className="relation-group">
                            <span>{targetUser.name}님을 {currentUser.name}님의</span>
                            <input 
                                type="text" 
                                value={friendNickname} 
                                onChange={(e) => setFriendNickname(e.target.value)}
                                placeholder=""
                            />
                            <span>(으)로</span>
                            <select value={friendRelationDropdown} onChange={(e) => setFriendRelationDropdown(e.target.value)}>
                                {relationOptions.map(opt => <option key={opt} value={opt}>{opt}</option>)}
                            </select>
                        </div>
                        <div className="relation-group">
                            <span>{currentUser.name}님을 {targetUser.name}님의</span>
                            <input 
                                type="text" 
                                value={userNickname} 
                                onChange={(e) => setUserNickname(e.target.value)}
                                placeholder=""
                            />
                            <span>(으)로</span>
                            <select value={userRelationDropdown} onChange={(e) => setUserRelationDropdown(e.target.value)}>
                                {relationOptions.map(opt => <option key={opt} value={opt}>{opt}</option>)}
                            </select>
                        </div>

                        <textarea
                            className="request-message-input"
                            placeholder="일촌 신청 메시지를 남겨보세요."
                            value={requestMessage}
                            onChange={(e) => setRequestMessage(e.target.value)}
                            maxLength="100"
                        />

                        <p className="agreement-text">상대방이 동의하시면 일촌이 맺어집니다.</p>
                        
                        {error && <p className="error-message">{error}</p>}

                        <footer className="modal-footer">
                            {isSubmitting ? (
                                <p>보내는 중...</p>
                            ) : (
                                <>
                                    <button type="submit" className="submit-btn">보내기</button>
                                    <button type="button" className="cancel-btn" onClick={onClose}>취소</button>
                                </>
                            )}
                        </footer>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default IlchonModal;

