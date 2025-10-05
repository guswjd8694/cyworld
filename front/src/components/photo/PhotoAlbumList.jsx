import React from 'react';
import PhotoAlbumItem from './PhotoAlbumItem';

function PhotoAlbumList({ posts, currentUser, isOwner, loading, hasMore, onLoadMore, onEdit, onDelete }) {
    return (
        <section className="photo-album-container" aria-labelledby="photo-album-title">
            <h1 id="photo-album-title" className="sr_only">사진첩</h1>
            
            {posts.length === 0 ? (
                <p aria-live="polite">게시물이 없습니다.</p>
            ) : (
                <ul className="post-list">
                    {posts.map(post => (
                        <PhotoAlbumItem
                            key={post.boardId}
                            post={post}
                            currentUser={currentUser}
                            isOwner={isOwner}
                            onEdit={onEdit}
                            onDelete={onDelete}
                        />
                    ))}
                </ul>
            )}
        </section>
    );
}

export default PhotoAlbumList;