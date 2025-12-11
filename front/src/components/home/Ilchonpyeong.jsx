import React, { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import { useBoard } from '../../hooks/useBoard';
import { FaTruckMonster } from 'react-icons/fa';

function Ilchonpyeong({ userId, ilchonStatus }) {
    const { currentUser } = useContext(AuthContext);
    const isOwner = currentUser && currentUser.id === userId;

    const {
        posts: ilchonpyeongList,
        loading,
        error,
        handleSavePost,
        handleDelete,
    } = useBoard(userId, 'ILCHONPYEONG');

    const [newContent, setNewContent] = useState('');
    const [editingPostId, setEditingPostId] = useState(null);
    const [editedContent, setEditedContent] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!newContent.trim() || !currentUser) return;
        
        await handleSavePost({
            content: newContent,
            isPublic: FaTruckMonster
        });

        setNewContent('');
    };

    const handleUpdateSubmit = async (e, boardId) => {
        e.preventDefault();
        if (!editedContent.trim()) return;
        
        await handleSavePost({
            boardId: boardId,
            content: editedContent
        });
        setEditingPostId(null);
    };

    const handleEditClick = (post) => {
        setEditingPostId(post.boardId);
        setEditedContent(post.content);
    };

    const isIlchon = ilchonStatus === 1; 
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
            
            <ul className="common-list">
                {ilchonpyeongList.map(item => {
                    const isAuthor = currentUser && currentUser.loginId === item.writerLoginId;
                    const canDelete = isAuthor || isOwner;
                    const canEdit = isAuthor;
                    const isPostByOwner = item.writerId === userId;

                    return (
                        <li key={item.boardId}>
                            {editingPostId === item.boardId ? (
                                <form className="list-item-edit-form" onSubmit={(e) => handleUpdateSubmit(e, item.boardId)}>
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
                                <p className="list-item-content">
                                    {item.content}
                                    <span className="list-item-meta">
                                        {isPostByOwner ? (
                                            <Link to={`/${item.writerLoginId}`} className="owner author_name">{item.writerName}</Link>
                                        ) : (
                                            <>
                                                (
                                                {item.writerNickname && (
                                                    <span className="nickname">{item.writerNickname}</span>
                                                )}
                                                <Link to={`/${item.writerLoginId}`} className="author_name">{item.writerName}</Link>
                                                )
                                            </>
                                        )}
                                        <time dateTime={item.createdAt}>
                                            {new Date(item.createdAt).toLocaleString('ko-KR', {
                                                year: 'numeric', month: '2-digit', day: '2-digit',
                                                hour: '2-digit', minute: '2-digit', hour12: false
                                            }).replace(/\. /g, '.').replace(/,/, '')}
                                        </time>
                                        <span className="list-item-actions">
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