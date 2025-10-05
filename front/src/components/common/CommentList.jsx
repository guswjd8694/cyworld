import React, { useState } from 'react';
import { Link } from 'react-router-dom';

const formatDateTime = (dateValue) => {
    let date;
    if (Array.isArray(dateValue)) {
        date = new Date(dateValue[0], dateValue[1] - 1, dateValue[2], dateValue[3], dateValue[4], dateValue[5]);
    } else {
        date = new Date(dateValue);
    }
    if (isNaN(date.getTime())) {
        return "";
    }
    
    return new Date(date).toLocaleString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
    }).replace(/\. /g, '.').replace(/,/, '');
};


function CommentList({ comments, currentUser, isOwner, onUpdate, onDelete }) {
    const [editingCommentId, setEditingCommentId] = useState(null);
    const [editedContent, setEditedContent] = useState('');

    const handleEditClick = (comment) => {
        setEditingCommentId(comment.commentId);
        setEditedContent(comment.content);
    };

    const handleCancelEdit = () => {
        setEditingCommentId(null);
        setEditedContent('');
    };

    const handleUpdateSubmit = (e, commentId) => {
        e.preventDefault();
        if (!editedContent.trim()) return;
        
        onUpdate(commentId, editedContent);
        handleCancelEdit(); 
    };

    
    
    

    return (
        <ul className="common-list">
            {comments.map(comment => {
                const isAuthor = currentUser && currentUser.id === comment.writerId;
                const canDelete = isAuthor || isOwner;
                const canEdit = isAuthor;
                
                return (
                    <li key={comment.commentId}>
                        {editingCommentId === comment.commentId ? (
                            
                            <form className="list-item-edit-form" onSubmit={(e) => handleUpdateSubmit(e, comment.commentId)}>
                                <input
                                    type="text"
                                    value={editedContent}
                                    onChange={(e) => setEditedContent(e.target.value)}
                                    autoFocus
                                    required
                                />
                                <div className="edit-actions">
                                    <button type="button" onClick={handleCancelEdit}>취소</button>
                                    <button type="submit">저장</button>
                                </div>
                            </form>
                        ) : (
                            <p className="list-item-content">
                                <span className="list-item-meta">
                                    <Link to={`/users/${comment.writerId}`} className="author_name">{comment.writer}</Link> : {comment.content}
                                    <time dateTime={new Date(comment.createAt).toISOString()}>
                                        {formatDateTime(comment.createAt)}
                                    </time>
                                    {(canEdit || canDelete) && (
                                        <span className="list-item-actions">
                                            {canEdit && <button onClick={() => handleEditClick(comment)}><img src="/imgs/icon_eraser.png" alt="수정 아이콘" /></button>}
                                            {canDelete && <button onClick={() => onDelete(comment.commentId)}><img src="/imgs/icon_delete.png" alt="삭제 아이콘" /></button>}
                                        </span>
                                    )}
                                </span>
                            </p>
                        )}
                    </li>
                );
            })}
        </ul>
    );
}

export default CommentList;