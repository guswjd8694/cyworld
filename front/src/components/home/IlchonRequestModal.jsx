import React from 'react';
import apiClient from '../../api/axiosConfig';
import '../../styles/Modal.scss';

function IlchonRequestModal({ onClose, requests, onUpdate }) {

    const handleAccept = async (requestId) => {
        try {
            await apiClient.put(`/ilchons/${requestId}/accept`);
            alert('일촌을 수락했습니다.');
            onUpdate();
            window.location.reload();
        } catch (err) {
            if (err.response?.status !== 401) {
                alert(err.response?.data?.message || '수락에 실패했습니다.');
            }
        }
    };

    const handleReject = async (requestId) => {
        if (window.confirm('정말로 거절하시겠습니까?')) {
            try {
                await apiClient.delete(`/api/ilchons/requests/${requestId}/reject`);
                alert('일촌 신청을 거절했습니다.');
                onUpdate();
                window.location.reload();
            } catch (err) {
                if (err.response?.status !== 401) {
                    alert(err.response?.data?.message || '거절에 실패했습니다.');
                }
            }
        }
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content ilchon-request-modal" onClick={(e) => e.stopPropagation()}>
                <header className="modal-header">
                    <h2>받은 일촌 신청</h2>
                    <button className="close-btn" onClick={onClose}>&times;</button>
                </header>
                <div className="modal-body">
                    {requests.length > 0 ? (
                        <ul className="request-list">
                            {requests.map(req => (
                                <li key={req.ilchonRequestId} className="request-item">
                                    <div className="request-info">
                                        <span className="requester-name">{req.friendName}</span>
                                        <span className="request-message">"{req.requestMessage || '...'}"</span>
                                    </div>
                                    <div className="request-actions">
                                        <button className="accept-btn" onClick={() => handleAccept(req.ilchonRequestId)}>수락</button>
                                        <button className="reject-btn" onClick={() => handleReject(req.ilchonRequestId)}>거절</button>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p className="no-requests">받은 일촌 신청이 없습니다.</p>
                    )}
                </div>
            </div>
        </div>
    );
}

export default IlchonRequestModal;
