import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import apiClient from '../../api/axiosConfig';
import DOMPurify from 'dompurify';
import CommentList from '../common/CommentList';
import CommentForm from '../common/CommentForm';

// 날짜 포맷팅 함수
const formatDateTime = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}.${month}.${day} ${hours}:${minutes}`;
};

function PhotoAlbumItem({ post, currentUser, isOwner, onEdit, onDelete }) {
    const isAuthor = currentUser && currentUser.loginId === post.writerLoginId;
    const canView = post.isPublic || isAuthor || isOwner;
    const canDelete = isAuthor || isOwner;
    const canEdit = isAuthor;

    const [comments, setComments] = useState([]);
    const [isLoadingComments, setIsLoadingComments] = useState(true);

    const fetchComments = useCallback(async () => {
        if (!post.boardId || !canView) {
            setIsLoadingComments(false);
            return;
        }
        setIsLoadingComments(true);
        try {
            const response = await apiClient.get(`/boards/${post.boardId}/comments`);
            setComments(response.data);
        } catch (error) {
            console.error("댓글을 불러오는데 실패했습니다.", error);
        } finally {
            setIsLoadingComments(false);
        }
    }, [post.boardId, canView]);

    useEffect(() => {
        fetchComments();
    }, [fetchComments]); 

    const handleCreateComment = async (content) => {
        try {
            await apiClient.post(`/boards/${post.boardId}/comments`, { content });
            fetchComments();
        } catch (error) {
            alert('댓글 작성에 실패했습니다.');
            console.error(error);
            throw error;
        }
    };
    
    const handleUpdateComment = async (commentId, newContent) => {
        try {
            await apiClient.put(`/comments/${commentId}`, { content: newContent });
            fetchComments(); 
        } catch (error) {
            console.error("댓글 수정 실패:", error);
            alert("댓글 수정에 실패했습니다.");
        }
    };

    const handleDeleteComment = async (commentId) => {
        if (window.confirm("정말로 댓글을 삭제하시겠습니까?")) {
            try {
                await apiClient.delete(`/comments/${commentId}`);
                fetchComments();
            } catch (error) {
                console.error("댓글 삭제 실패:", error);
                alert("댓글 삭제에 실패했습니다.");
            }
        }
    };

    return (
        <li>
            <article className={`photo-post ${!post.isPublic ? 'secret' : ''}`} aria-labelledby={`post-title-${post.boardId}`}>
                <header className="post-header">
                    <h2 id={`post-title-${post.boardId}`} className="post-title">{post.title}</h2>
                    <p className="post-number">No.{post.boardNo}</p> {/* post-author에서 post-number로 변경 또는 스타일 추가 */}
                    <time dateTime={new Date(post.createdAt).toISOString()} className="post-date">
                        {formatDateTime(post.createdAt)}
                    </time>
                </header>

                <div className="post-body">
                    {canView ? (
                        <>
                            <div className="post-image-gallery">
                                {post.images && post.images.map((imageUrl, index) => (
                                    <img 
                                        key={index}
                                        src={imageUrl} 
                                        alt={`${post.title} 관련 이미지 ${index + 1}`} 
                                        className="post-image" 
                                    />
                                ))}
                            </div>
                            <p className="post-content" dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(post.content) }}></p>
                        </>
                    ) : (
                        <div className="secret-post-placeholder">
                            <img src="/imgs/icon_secret.png" alt="자물쇠 아이콘" />
                            <p>비공개 글입니다</p>
                        </div>
                    )}
                </div>

                <footer className="post-footer">
                    <span className="public-status">
                        공개설정: {post.isPublic ? '전체공개' : '비공개'}
                    </span>
                    {(isAuthor || isOwner) && (
                        <div className="post-actions">
                            {canEdit && <button onClick={() => onEdit(post)}>수정</button>}
                            {canDelete && <button onClick={() => onDelete(post.boardId)}>삭제</button>}
                        </div>
                    )}
                </footer>

                {canView && (
                    <section className="comments-section" aria-label={`${post.title} 게시글의 댓글 목록`}>
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

export default PhotoAlbumItem;