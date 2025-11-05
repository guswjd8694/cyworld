import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import apiClient from '../../api/axiosConfig';
import CommentList from '../common/CommentList';
import CommentForm from '../common/CommentForm';

function GuestbookItem({
    item, currentUser, isOwner,
    isEditing, editedContent, setEditedContent,
    editedIsSecret, setEditedIsSecret,
    onUpdateSubmit, onCancelEdit, onEdit,
    onDelete, onToggleSecret
}) {
    const isAuthor = currentUser && currentUser.loginId === item.writerLoginId;
    const canView = item.isPublic || isAuthor || isOwner;

    const [comments, setComments] = useState([]);
    const [isLoadingComments, setIsLoadingComments] = useState(false);

    const fetchComments = useCallback(async () => {
        if (!canView || !item.boardId) return;
        setIsLoadingComments(true);
        try {
            const response = await apiClient.get(`/boards/${item.boardId}/comments`);
            setComments(response.data);
        } catch (error) {
            console.error("댓글 로딩 실패:", error);
        } finally {
            setIsLoadingComments(false);
        }
    }, [item.boardId, canView]);

    useEffect(() => {
        fetchComments();
    }, [fetchComments]);


    const handleCreateComment = async (content) => {
        try {
            const response = await apiClient.post(`/boards/${item.boardId}/comments`, { content });
            setComments(prev => [...prev, response.data]);
        } catch (error) {
            alert('댓글 작성에 실패했습니다.');
            console.error(error);
            throw error;
        }
    };
    const handleUpdateComment = async (commentId, newContent) => {
        try {
            await apiClient.put(`/comments/${commentId}`, { content: newContent });
            setComments(prev => prev.map(c => c.commentId === commentId ? { ...c, content: newContent } : c));
        } catch (error) {
            alert('댓글 수정에 실패했습니다.');
            console.error(error);
        }
    };
    const handleDeleteComment = async (commentId) => {
        if (window.confirm("정말로 댓글을 삭제하시겠습니까?")) {
            try {
                await apiClient.delete(`/comments/${commentId}`);
                setComments(prev => prev.filter(c => c.commentId !== commentId));
            } catch (error) {
                alert('댓글 삭제에 실패했습니다.');
                console.error(error);
            }
        }
    };

    return (
        <li className={`guestbook_item ${!item.isPublic ? 'secret' : ''}`}>
            <article className="guestbook_card">
                <header className="guestbook_header">
                    <div className="guestbook_header_left">
                        <em className="guestbook_number">No.{item.boardNo}</em>
                        <Link to={`/${item.writerLoginId}`}><h4 className="guestbook_writer">{item.writerName}</h4></Link>
                        <Link to={`/${item.writerLoginId}`} className="mini_home_btn" role="button">미니홈피</Link>
                        <time dateTime={item.createdAt}>
                            ({new Date(item.createdAt).toLocaleString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false }).replace(/\. /g, '.').replace(/,/, '')})
                        </time>
                    </div>

                    {!isEditing && (
                        <div className="guestbook-actions">
                            {(isAuthor || isOwner) && <button onClick={() => onToggleSecret(item)}>{item.isPublic ? '비밀로 하기' : '공개하기'}</button>}
                            {isAuthor && <button onClick={() => onEdit(item)}>수정</button>}
                            {(isAuthor || isOwner) && <button onClick={() => onDelete(item.boardId)}>삭제</button>}
                        </div>
                    )}
                </header>

                <div className="guestbook_body">
                    <figure className="guestbook_figure">
                        <img src="/imgs/minimi_guest.jpg" alt={`${item.writerName}의 미니미`} />
                    </figure>

                    {isEditing ? (
                        <form className="guestbook-edit-form" onSubmit={(e) => onUpdateSubmit(e, item.boardId)}>
                            <textarea value={editedContent} onChange={(e) => setEditedContent(e.target.value)} required autoFocus />
                            <div className="edit-actions">
                                <label className="secret_checkbox">
                                    <input type="checkbox" checked={editedIsSecret} onChange={(e) => setEditedIsSecret(e.target.checked)} />
                                    비밀로 하기
                                </label>
                                <div className="edit-buttons">
                                    <button type="button" onClick={onCancelEdit}>취소</button>
                                    <button type="submit">저장</button>
                                </div>
                            </div>
                        </form>
                    ) : (
                        <div className="guestbook_content">
                            {canView ? <p className="guestbook_text">{item.content}</p> : <p className="guestbook_text secret-text"><img src="../imgs/icon_secret.png" alt="비밀글 자물쇠 아이콘" />(이 글은 홈주인과 작성자만 볼 수 있어요)</p>}
                        </div>
                    )}
                </div>
                
                {canView && (
                    <section className="comments-section">
                        {isLoadingComments ? (
                            <p>댓글 로딩 중...</p>
                        ) : (
                            <CommentList 
                                comments={comments}
                                currentUser={currentUser} 
                                isOwner={isOwner}
                                onUpdate={handleUpdateComment}
                                onDelete={handleDeleteComment}
                            />
                        )}
                        <CommentForm 
                            onSubmit={handleCreateComment} 
                            isDisabled={!currentUser}
                        />
                    </section>
                )}
            </article>
        </li>
    );
}

export default GuestbookItem;