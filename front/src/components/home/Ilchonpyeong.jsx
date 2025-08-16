import React, { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';

function Ilchonpyeong({ userId }) {
    const { currentUser } = useContext(AuthContext);
    const isOwner = currentUser && currentUser.id === userId;

    const [ilchonpyeongList, setIlchonpyeongList] = useState([]);
    const [newContent, setNewContent] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    const [refetchTrigger, setRefetchTrigger] = useState(0);

    // --- 수정 기능을 위한 상태 추가 ---
    const [editingPostId, setEditingPostId] = useState(null);
    const [editedContent, setEditedContent] = useState('');

    useEffect(() => {
        const fetchIlchonpyeong = async () => {
            setLoading(true);
            try {
                // 일촌평은 페이지네이션 없이 모두 가져옵니다.
                const response = await fetch(`http://localhost:8080/users/${userId}/boards?type=ILCHONPYEONG`);
                if (!response.ok) throw new Error('일촌평을 불러오는 데 실패했습니다.');
                const data = await response.json();
                setIlchonpyeongList(data.content);
            } catch (err) {
                setError(err.message);
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
            const response = await fetch(`http://localhost:8080/users/${userId}/boards`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('jwt-token')}`
                },
                body: JSON.stringify({
                    content: newContent,
                    type: 'ILCHONPYEONG',
                    isPublic: true
                })
            });
            if (!response.ok) throw new Error('일촌평 작성에 실패했습니다.');
            setNewContent('');
            setRefetchTrigger(prev => prev + 1); // 목록 새로고침
        } catch (err) {
            alert(err.message);
        }
    };

    // --- 수정/삭제 핸들러 추가 ---
    const handleDelete = async (boardId) => {
        if (window.confirm('정말로 삭제하시겠습니까?')) {
            try {
                const response = await fetch(`http://localhost:8080/boards/${boardId}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${localStorage.getItem('jwt-token')}` }
                });
                if (!response.ok) throw new Error('삭제에 실패했습니다.');
                setRefetchTrigger(prev => prev + 1);
            } catch (err) {
                alert(err.message);
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
            const response = await fetch(`http://localhost:8080/boards/${boardId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('jwt-token')}`
                },
                body: JSON.stringify({ content: editedContent })
            });
            if (!response.ok) throw new Error('수정에 실패했습니다.');
            setEditingPostId(null);
            setRefetchTrigger(prev => prev + 1);
        } catch (err) {
            alert(err.message);
        }
    };

    return (
        <section className="ilchon" aria-labelledby="ilchon-title">
            <h2 id="ilchon-title">일촌평</h2>
            <form onSubmit={handleSubmit} className="ilchon-form">
                <label htmlFor="ilchon-input">Friends say</label>
                <input 
                    type="text" 
                    id="ilchon-input" 
                    placeholder="일촌과 나누고 싶은 이야기를 남겨보세요~!" 
                    value={newContent}
                    onChange={(e) => setNewContent(e.target.value)}
                />
                <button type="submit">작성</button>
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
