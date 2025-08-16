import React from 'react';

function DiaryEntry({ entry, loading }) {
    if (loading) {
        return <div className="diary-entry"><h3>일기를 불러오는 중...</h3></div>;
    }

    if (!entry) {
        return <div className="diary-entry"><h3>선택된 날짜에 작성된 일기가 없습니다.</h3></div>;
    }

    return (
        <div className="diary-entry">
            <h3>{entry.title}</h3>
            <p>{entry.content}</p>
            <time>{new Date(entry.createdAt).toLocaleDateString('ko-KR')}</time>
        </div>
    );
}

export default DiaryEntry;
