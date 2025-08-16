import React, { useState, useContext } from 'react';
import { AuthContext } from '../../contexts/AuthContext';
import '../../styles/Modal.scss';

function IlchonModal({ onClose, targetUser }) {
    const { currentUser } = useContext(AuthContext);

    const [friendNickname, setFriendNickname] = useState('');
    const [friendRelationDropdown, setFriendRelationDropdown] = useState('직접입력');

    const [userNickname, setUserNickname] = useState('');
    const [userRelationDropdown, setUserRelationDropdown] = useState('직접입력');

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
        
        let finalFriendNickname = friendRelationDropdown === '직접입력' 
            ? friendNickname.trim() 
            : friendRelationDropdown;
            
        if (!finalFriendNickname) {
            alert(`'${targetUser.name}'님을 부를 일촌명을 입력하거나 선택해주세요.`);
            return;
        }

        let finalUserNickname = userRelationDropdown === '직접입력'
            ? userNickname.trim()
            : userRelationDropdown;

        if (!finalUserNickname) {
            alert(`'${currentUser.name}'님을 부를 일촌명을 입력하거나 선택해주세요.`);
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/ilchons`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('jwt-token')}`
                },
                body: JSON.stringify({
                    targetUserId: targetUser.id,
                    friendNickname: finalFriendNickname,
                    userNickname: finalUserNickname,
                })
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || '일촌 신청에 실패했습니다.');
            }
            
            alert('일촌 신청을 보냈습니다.');
            onClose();
            window.location.reload();
        } catch (err) {
            alert(err.message);
        }
    };

    const relationOptions = ["직접입력", "친한친구", "직장동료", "학교동창", "가족", "평생지기", "나의반쪽"];

    if (!currentUser) {
        return null; 
    }

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content ilchon-modal" onClick={(e) => e.stopPropagation()}>
                <header className="modal-header">
                    <h2>일촌 신청</h2>
                </header>
                <div className="modal-body">
                    <div className="sender-info">
                        보낸이 : {currentUser.name} ({getCurrentDateTime()})
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
                            />
                            <span>로</span>
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
                            />
                            <span>로</span>
                            <select value={userRelationDropdown} onChange={(e) => setUserRelationDropdown(e.target.value)}>
                                {relationOptions.map(opt => <option key={opt} value={opt}>{opt}</option>)}
                            </select>
                        </div>
                        <p className="agreement-text">상대방이 동의하시면 일촌이 맺어집니다.</p>
                        <footer className="modal-footer">
                            <button type="submit" className="submit-btn">보내기</button>
                            <button type="button" className="cancel-btn" onClick={onClose}>취소</button>
                        </footer>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default IlchonModal;
