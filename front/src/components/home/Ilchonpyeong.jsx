import React, { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import apiClient from '../../api/axiosConfig';

function Ilchonpyeong({ userId, ilchonStatus }) {
    const { currentUser } = useContext(AuthContext);
    const isOwner = currentUser && currentUser.id === userId;

    const [ilchonpyeongList, setIlchonpyeongList] = useState([]);
    const [newContent, setNewContent] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    const [refetchTrigger, setRefetchTrigger] = useState(0);

    const [editingPostId, setEditingPostId] = useState(null);
    const [editedContent, setEditedContent] = useState('');

    useEffect(() => {
        const fetchIlchonpyeong = async () => {
            setLoading(true);

            try {
                const response = await apiClient.get(`/users/${userId}/boards?type=ILCHONPYEONG`);
                setIlchonpyeongList(response.data.content);
            } catch (err) {
                if (err.response?.status !== 401) {
                    setError(err.message);
                }
            } finally {
                setLoading(false);
            }
        };
        fetchIlchonpyeong();
    }, [userId, refetchTrigger]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!newContent.trim()) return;
        if (!currentUser) {
            alert("로그인이 필요합니다.");
            return;
        }
        try {
            await apiClient.post(`/users/${userId}/boards`, {
                content: newContent,
                type: 'ILCHONPYEONG',
                isPublic: true
            });
            setNewContent('');
            setRefetchTrigger(prev => prev + 1);
        } catch (err) {
            if (err.response?.status !== 401) {
                alert(err.response?.data?.message || '일촌평 작성에 실패했습니다.');
            }
        }
    };

    const handleDelete = async (boardId) => {
        if (window.confirm('정말로 삭제하시겠습니까?')) {
        try {
            await apiClient.delete(`/boards/${boardId}`);
            setRefetchTrigger(prev => prev + 1);
        } catch (err) {
            if (err.response?.status !== 401) {
                alert(err.response?.data?.message || '삭제에 실패했습니다.');
            }
        }
        }
    };

    const handleEditClick = (post) => {
        setEditingPostId(post.boardId);
        setEditedContent(post.content);
    };

    const handleUpdateSubmit = async (e, boardId) => {
        e.preventDefault();
        if (!editedContent.trim()) return;
        try {
            await apiClient.put(`/boards/${boardId}`, { content: editedContent });
            setEditingPostId(null);
            setRefetchTrigger(prev => prev + 1);
        } catch (err) {
            if (err.response?.status !== 401) {
                alert(err.response?.data?.message || '수정에 실패했습니다.');
            }
        }
    };

    const isIlchon = ilchonStatus === 'ACCEPTED';
    const canWrite = isOwner || isIlchon;

    return (
        <section className="ilchon" aria-labelledby="ilchon-title">
            <h2 id="ilchon-title">일촌평</h2>
            <form onSubmit={handleSubmit} className="ilchon-form">
                <label htmlFor="ilchon-input">Friends say</label>
                <input 
                    type="text" 
                    id="ilchon-input" 
                    placeholder={canWrite ? "일촌과 나누고 싶은 이야기를 남겨보세요~!" : "일촌만 작성할 수 있습니다."}
                    value={newContent}
                    onChange={(e) => setNewContent(e.target.value)}
                    disabled={!canWrite}
                />
                <button type="submit" disabled={!canWrite}>작성</button>
            </form>

            {loading && <p>일촌평 로딩 중...</p>}
            {error && <p style={{ color: 'red' }}>{error}</p>}
            
            <ul className="ilchon_lists">
                {ilchonpyeongList.map(item => {
                    const isAuthor = currentUser && currentUser.loginId === item.writerLoginId;
                    const canDelete = isAuthor || isOwner;
                    const canEdit = isAuthor;

                    return (
                        <li key={item.boardId}>
                            {editingPostId === item.boardId ? (
                                <form className="ilchon-edit-form" onSubmit={(e) => handleUpdateSubmit(e, item.boardId)}>
                                    <input 
                                        type="text"
                                        value={editedContent}
                                        onChange={(e) => setEditedContent(e.target.value)}
                                        required
                                    />
                                    <div className="edit-actions">
                                        <button type="button" onClick={() => setEditingPostId(null)}>취소</button>
                                        <button type="submit">저장</button>
                                    </div>
                                </form>
                            ) : (
                                <p className="message">
                                    {item.content}
                                    <span className="meta">
                                        (
                                        {item.writerNickname && (
                                            <span className="nickname">{item.writerNickname}</span>
                                        )}
                                        <Link to={`/${item.writerLoginId}`} className="friend_name">{item.writerName}</Link>)
                                        <time dateTime={item.createdAt}>
                                            {new Date(item.createdAt).toLocaleString('ko-KR', {
                                                year: 'numeric', month: '2-digit', day: '2-digit',
                                                hour: '2-digit', minute: '2-digit', hour12: false
                                            }).replace(/\. /g, '.').replace(/,/, '')}
                                        </time>
                                        <span className="ilchon-actions">
                                            {canEdit && <button onClick={() => handleEditClick(item)}><img src="/imgs/icon_eraser.png" alt="수정 아이콘" /></button>}
                                            {canDelete && <button onClick={() => handleDelete(item.boardId)}><img src="/imgs/icon_delete.png" alt="삭제 아이콘" /></button>}
                                        </span>
                                    </span>
                                </p>
                            )}
                        </li>
                    );
                })}
            </ul>
        </section>
    );
}

export default Ilchonpyeong;
