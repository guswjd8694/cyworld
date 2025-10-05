import React, { useState, useEffect } from 'react';
import Ilchonpyeong from './Ilchonpyeong';
import apiClient from '../../api/axiosConfig';

function HomePage({ userId, navigateTo, ilchonStatus }) {
    const [recentPosts, setRecentPosts] = useState([]);
    const [boardCounts, setBoardCounts] = useState(null);
    const [loading, setLoading] = useState(true);
    
    useEffect(() => {
        const fetchData = async () => {
            if (!userId) return;
            setLoading(true);
            try {
                const [postsResponse, countsResponse] = await Promise.all([
                    apiClient.get(`/users/${userId}/boards/recent`),
                    apiClient.get(`/users/${userId}/board-counts`)
                ]);
                
                const thirtyDaysAgo = new Date();
                thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);

                const recentPostsWithin = postsResponse.data.filter(post => {
                    const postDate = new Date(post.createdAt);
                    return postDate >= thirtyDaysAgo;
                });

                setRecentPosts(recentPostsWithin);
                setBoardCounts(countsResponse.data);

            } catch (error) {
                console.error("홈페이지 데이터를 불러오는데 실패했습니다.", error);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [userId]);

    const getTagClassName = (type) => {
        switch (type) {
            case 'PHOTO': return 'tag_orange';
            case 'DIARY': return 'tag_pink';
            case 'JUKEBOX': return 'tag_blue';
            default: return 'tag_gray';
        }
    };

    const handlePostClick = (post) => {
        const params = {
            selectedDate: post.createdAt,
            boardId: post.boardId
        };
        navigateTo(post.type, params);
    };

    const miniroomImageSrc = userId === 1 
        ? "/imgs/miniroom_01.png" 
        : "/imgs/miniroom_default_f.png";

    return (
        <div className="grid_container">
            <section aria-labelledby="recent-posts-title" className="recent_posts">
                <h2 id="recent-posts-title">최근 게시물</h2>
                
                {loading ? (
                    <p className="loading-message">게시물을 불러오는 중입니다...</p>
                ) : recentPosts.length > 0 ? (
                    <ul className="post_lists">
                        {recentPosts.map(post => (
                            <li key={post.boardId}>
                                <article>
                                    <a href="#" onClick={(e) => { e.preventDefault(); handlePostClick(post); }}>
                                        <span className={`tag ${getTagClassName(post.type)}`}>{post.type}</span>
                                        <h3>{post.type === 'PHOTO' ? post.title : post.previewContent}</h3>
                                    </a>
                                </article>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p className="no-posts">
                        등록된 게시물이 없습니다<br />
                        소식이 뜸한 친구에게 마음의 한마디를<br />
                        남겨주세요
                    </p>
                )}
            </section>
            
            <section aria-labelledby="grid-links-title" className="links">
                <h2 id="grid-links-title" className="sr_only">바로가기 링크</h2>
                <ul className="grid-2x2" role="group">
                    <li>
                        <a href="#" className="grid-item" onClick={(e) => { e.preventDefault(); navigateTo('JUKEBOX'); }}>
                            <strong>쥬크박스</strong>
                            <span className="post_count">{boardCounts ? `${boardCounts.JUKEBOX.newCount}/${boardCounts.JUKEBOX.totalCount}` : '...'}</span>
                            {/* ✨ boardCounts를 사용하도록 수정 */}
                            {boardCounts && boardCounts.JUKEBOX.newCount > 0 &&
                                <img src="/imgs/icon_new.png" alt="새 글 아이콘" className="new_icon" />
                            }
                        </a>
                    </li>
                    <li>
                        <a href="#" className="grid-item" onClick={(e) => { e.preventDefault(); navigateTo('PHOTO'); }}>
                            <strong>사진첩</strong>
                            <span className="post_count">{boardCounts ? `${boardCounts.PHOTO.newCount}/${boardCounts.PHOTO.totalCount}` : '...'}</span>
                             {/* ✨ boardCounts를 사용하도록 수정 */}
                            {boardCounts && boardCounts.PHOTO.newCount > 0 &&
                                <img src="/imgs/icon_new.png" alt="새 글 아이콘" className="new_icon" />
                            }
                        </a>
                    </li>
                    <li>
                        <a href="#" className="grid-item" onClick={(e) => { e.preventDefault(); navigateTo('DIARY'); }}>
                            <strong>다이어리</strong>
                            <span className="post_count">{boardCounts ? `${boardCounts.DIARY.newCount}/${boardCounts.DIARY.totalCount}` : '...'}</span>
                             {/* ✨ boardCounts를 사용하도록 수정 */}
                            {boardCounts && boardCounts.DIARY.newCount > 0 &&
                                <img src="/imgs/icon_new.png" alt="새 글 아이콘" className="new_icon" />
                            }
                        </a>
                    </li>
                    <li>
                        <a href="#" className="grid-item" onClick={(e) => { e.preventDefault(); navigateTo('GUESTBOOK'); }}>
                            <strong>방명록</strong>
                            <span className="post_count">{boardCounts ? `${boardCounts.GUESTBOOK.newCount}/${boardCounts.GUESTBOOK.totalCount}` : '...'}</span>
                            {boardCounts && boardCounts.GUESTBOOK.newCount > 0 &&
                                <img src="/imgs/icon_new.png" alt="새 글 아이콘" className="new_icon" />
                            }
                        </a>
                    </li>
                </ul>
            </section>

            <section className="miniroom" aria-labelledby="miniroom">
                <h2 id="miniroom-title">미니룸</h2>
                <figure>
                    <img src={miniroomImageSrc} alt="미니룸 이미지" />
                </figure>
            </section>
            
            <Ilchonpyeong userId={userId} ilchonStatus={ilchonStatus} />
        </div>
    );
}

export default HomePage;