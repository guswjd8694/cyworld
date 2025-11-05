import React, { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import Pagination from '../common/Pagination';
import { useBoard } from '../../hooks/useBoard';
import apiClient from '../../api/axiosConfig';
import GuestbookItem from './GuestbookItem';

function Guestbook({ userId }) {
    const { currentUser } = useContext(AuthContext);
    const isOwner = currentUser && currentUser.id === userId;

    const {
        posts, loading, error, currentPage, totalPages,
        handlePageChange, handleSavePost, handleDelete, triggerRefetch
    } = useBoard(userId, 'GUESTBOOK');

    const [newContent, setNewContent] = useState('');
    const [isSecret, setIsSecret] = useState(false);

    const [editingPostId, setEditingPostId] = useState(null);
    const [editedContent, setEditedContent] = useState('');
    const [editedIsSecret, setEditedIsSecret] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!newContent.trim() || !currentUser) return;
        await handleSavePost({ content: newContent, isPublic: !isSecret });
        setNewContent('');
        setIsSecret(false);
    };

    const handleUpdateSubmit = async (e, boardId) => {
        e.preventDefault();
        if (!editedContent.trim()) return;
        await handleSavePost({ boardId: boardId, content: editedContent, isPublic: !editedIsSecret });
        setEditingPostId(null);
    };
    
    const handleEditClick = (post) => {
        setEditingPostId(post.boardId);
        setEditedContent(post.content);
        setEditedIsSecret(!post.isPublic);
    };
    
    const handleCancelEdit = () => {
        setEditingPostId(null);
    };

    const handleToggleSecret = async (post) => {
        try {
            await apiClient.patch(`/boards/${post.boardId}/privacy`, {
                isPublic: !post.isPublic 
            });
        } catch (err) {
            alert(err.response?.data?.message || '공개 설정 변경에 실패했습니다.');
            return;
        }

        try {
            triggerRefetch();
        } catch (refetchError) {
            alert('데이터는 변경되었으나, 화면을 새로고침하는데 실패했습니다.');
        }
    };

    if (loading && currentPage === 0) return <div>로딩 중...</div>;
    if (error) return <div>오류: {error}</div>;

    return (
        <div className="guestbook_container">
            <section className="guestbook_input_area">
                <div className="guestbook_input_inner">
                    <figure className="minimi_figure">
                        <img src="/imgs/minimi_guest.jpg" alt="기본 미니미" />
                    </figure>
                    <form onSubmit={handleSubmit} className="guestbook_form">
                        <textarea 
                            value={newContent}
                            onChange={(e) => setNewContent(e.target.value)}
                            placeholder={currentUser ? "방명록에 하고 싶은 말을 남겨보세요~!" : "로그인 후 작성할 수 있습니다."}
                            required 
                            disabled={!currentUser}
                        />
                        <div className="form_bottom">
                            <label className="secret_checkbox">
                                <input 
                                    type="checkbox" 
                                    checked={isSecret}
                                    onChange={(e) => setIsSecret(e.target.checked)}
                                    disabled={!currentUser}
                                />
                                비밀로 하기
                            </label>
                            <button type="submit" className="guestbook_btn" disabled={!currentUser}>확인</button>
                        </div>
                    </form>
                </div>
            </section>
            
            <section className="guestbook_list_area">
                <ul className="guestbook_list">
                    {posts.map(item => (
                        <GuestbookItem
                            key={item.boardId}
                            item={item}
                            currentUser={currentUser}
                            isOwner={isOwner}
                            isEditing={editingPostId === item.boardId}
                            editedContent={editedContent}
                            setEditedContent={setEditedContent}
                            editedIsSecret={editedIsSecret}
                            setEditedIsSecret={setEditedIsSecret}
                            onUpdateSubmit={handleUpdateSubmit}
                            onCancelEdit={handleCancelEdit}
                            onEdit={() => handleEditClick(item)}
                            onDelete={handleDelete}
                            onToggleSecret={handleToggleSecret}
                        />
                    ))}
                </ul>
                {totalPages > 1 && (
                    <Pagination
                        currentPage={currentPage}
                        totalPages={totalPages}
                        onPageChange={handlePageChange}
                    />
                )}
            </section>
        </div>
    );
}

export default Guestbook;