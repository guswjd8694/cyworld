import React, { useState, useEffect } from 'react';

function BoardListSection({ userId, boardType }) {
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // TODO: boardType에 맞는 게시글 목록 API를 호출해야 합니다.
        console.log(`${userId}번 유저의 ${boardType} 목록을 불러옵니다.`);
        setPosts([
            { id: 1, title: `${boardType} 첫 번째 글` },
            { id: 2, title: `${boardType} 두 번째 글` },
        ]);
        setLoading(false);
    }, [userId, boardType]);

    return (
        <section className="bottom_area">
            <h3>{boardType}</h3>
            {loading ? (
                <p>목록 로딩 중...</p>
            ) : (
                <ul>
                    {posts.map(post => (
                        <li key={post.id}><a href="#">{post.title}</a></li>
                    ))}
                </ul>
            )}
        </section>
    );
}

export default BoardListSection;