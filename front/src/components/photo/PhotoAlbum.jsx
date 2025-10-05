import React, { useContext } from 'react';
import { AuthContext } from '../../contexts/AuthContext';
import Editor from '../common/Editor';
import PhotoAlbumList from './PhotoAlbumList';
import { useBoard } from '../../hooks/useBoard';
import Pagination from '../common/Pagination';

function PhotoAlbum({ userId }) {
    const { currentUser } = useContext(AuthContext);
    const isOwner = currentUser && currentUser.id === userId;

    const {
        posts,
        loading,
        error,
        isEditing,
        editingPost,
        currentPage,
        totalPages,
        handlePageChange,
        handleSavePost,
        handleDelete,
        handleEditClick,
        handleWriteClick,
        handleCancelEdit
    } = useBoard(userId, 'PHOTO');

    if (isEditing) {
        return (
            <Editor
                boardType="PHOTO"
                initialData={editingPost || {}}
                onSave={handleSavePost}
                onCancel={handleCancelEdit}
            />
        );
    }

    if (loading && currentPage === 0) return <div aria-live="polite">로딩 중...</div>;
    if (error) return <div role="alert">오류: {error}</div>;

    return (
        <>
            {isOwner && (
                <div className="photo-album-actions">
                    <button className="write-button" onClick={handleWriteClick}>
                        ✏️ 글쓰기
                    </button>
                </div>
            )}
            <PhotoAlbumList
                posts={posts}
                currentUser={currentUser}
                isOwner={isOwner}
                onEdit={handleEditClick}
                onDelete={handleDelete}
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
            />

            <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
            />
        </>
    );
}

export default PhotoAlbum;