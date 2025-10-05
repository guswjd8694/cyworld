import React from 'react';
import '../../styles/Modal.scss';

function ProfileHistoryModal({ onClose, history }) {

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content profile-history-modal" onClick={(e) => e.stopPropagation()}>
                <header className="modal-header">
                    <h2>프로필 히스토리</h2>
                    <button className="close-btn" onClick={onClose}>&times;</button>
                </header>
                <div className="modal-body">
                    {history && history.length > 0 ? (
                        <ul className="history-list">
                            {history.map(item => (
                                <li key={item.profileId} className="history-item">
                                    <span className="history-date">{formatDate(item.createdAt)}</span>
                                    <div className="history-content">
                                        <img src={item.imageUrl} alt="프로필 사진" className="history-image" />
                                        <p className="history-bio">{item.bio}</p>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p className="no-history">히스토리가 없습니다.</p>
                    )}
                </div>
            </div>
        </div>
    );
}

export default ProfileHistoryModal;
