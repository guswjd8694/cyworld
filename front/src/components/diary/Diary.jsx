import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../../contexts/AuthContext';
import Editor from '../common/Editor';
import '../../styles/Diary.scss';
import DOMPurify from 'dompurify';
import { useBoard } from '../../hooks/useBoard';

function Diary({ userId, initialDate }) {
    const { currentUser } = useContext(AuthContext);
    const isOwner = currentUser && currentUser.id === userId;

    const {
        posts,
        loading,
        error,
        isEditing,
        editingPost,
        handleSavePost,
        handleDelete,
        handleEditClick,
        handleWriteClick,
        handleCancelEdit
    } = useBoard(userId, 'DIARY');

    const [displayMonth, setDisplayMonth] = useState(initialDate ? new Date(initialDate) : new Date());
    const [selectedDate, setSelectedDate] = useState(initialDate ? new Date(initialDate) : new Date());
    
    const selectedPosts = posts.filter(post => {
        const postDate = new Date(post.createdAt);
        return postDate.getFullYear() === selectedDate.getFullYear() &&
                postDate.getMonth() === selectedDate.getMonth() &&
                postDate.getDate() === selectedDate.getDate();
    });

    const weatherOptions = { 'SUNNY': '☀️', 'PARTLY_CLOUDY': '⛅️', 'CLOUDY': '☁️', 'RAIN': '☔️', 'SNOW': '☃️' };
    const moodOptions = { 'HAPPY': '^ㅅ^ 해피', 'SAD': 'T_T 슬퍼', 'ANGRY': '-_-^ 아오', 'LOVE': '사랑해 S2' };

    const year = displayMonth.getFullYear();
    const month = displayMonth.getMonth();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const today = new Date();
    const goToPrevMonth = () => setDisplayMonth(new Date(year, month - 1, 1));
    const goToNextMonth = () => setDisplayMonth(new Date(year, month + 1, 1));
    const handleDateClick = (day) => {
        const clickedDate = new Date(year, month, day);
        setSelectedDate(clickedDate);
    };

    const postsByDate = posts.reduce((acc, post) => {
        const dateKey = post.createdAt.split('T')[0];
        acc[dateKey] = true;
        return acc;
    }, {});

    const renderCalendarDayButton = (day) => {
        const isToday = day === today.getDate() && month === today.getMonth() && year === today.getFullYear();
        const isSelected = day === selectedDate.getDate() && month === selectedDate.getMonth() && year === selectedDate.getFullYear();
        const dateString = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
        const hasPost = postsByDate[dateString];
        let dayClasses = 'calendar-day-item';
        if (isSelected) dayClasses += ' selected';
        if (isToday) dayClasses += ' today';
        if (hasPost) dayClasses += ' has-post';
        return (<button key={day} className={dayClasses} onClick={() => handleDateClick(day)}>{day}</button>);
    };
    const todayFormatted = {
        monthDay: `${String(today.getMonth() + 1).padStart(2, '0')}.${String(today.getDate()).padStart(2, '0')}`,
        weekday: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'][today.getDay()]
    };
    const formatPostDate = (dateString) => {
        const date = new Date(dateString);
        return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')} ${['일', '월', '화', '수', '목', '금', '토'][date.getDay()]}`;
    };
    const formatUpdateDate = (dateString) => {
        const date = new Date(dateString);
        return `(${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')} 수정됨)`;
    };

    if (isEditing) {
        return <Editor 
                    boardType="DIARY"
                    initialData={editingPost || {}}
                    onSave={handleSavePost} 
                    onCancel={handleCancelEdit} 
                />;
    }

    return (
        <section className="diary-page-container">
            <h2 className="sr-only">다이어리</h2>
            
            <header className="diary-calendar-area">
                <div className="today-display">
                    <span className="today-month-day">{todayFormatted.monthDay}</span>
                    <span className="today-weekday">{todayFormatted.weekday}</span>
                </div>
                <div className="calendar-main">
                    <div className="days-row">
                        <nav className="calendar-header">
                            <button onClick={goToPrevMonth}>◀</button>
                            <h3>{year}.{String(month + 1).padStart(2, '0')}</h3>
                            <button onClick={goToNextMonth}>▶</button>
                        </nav>
                        {Array.from({ length: 13 }, (_, i) => i + 1).map(day => renderCalendarDayButton(day))}
                    </div>
                    <div className="days-row second-row">
                        {Array.from({ length: daysInMonth - 13 }, (_, i) => i + 14).map(day => renderCalendarDayButton(day))}
                    </div>
                </div>
            </header>
            
            {isOwner && (
                <div className="diary-actions">
                    <button className="write-button" onClick={handleWriteClick}>
                        ✏️ 글쓰기
                    </button>
                </div>
            )}

            <p className="no-schedule-message">일정/기념일이 없습니다.</p>

            <div className="post-list-container">
                {loading ? ( <p>로딩 중...</p> ) : 
                selectedPosts.length > 0 ? (
                    selectedPosts.map(post => {
                        const wasUpdated = post.updatedAt && (new Date(post.updatedAt).getTime() - new Date(post.createdAt).getTime() > 1000);
                        return (
                            <article key={post.boardId} className="post-view-wrapper">
                                <div className="post-entry">
                                    <header className="post-header">
                                        <time dateTime={post.createdAt}>
                                            {formatPostDate(post.createdAt)}
                                            {post.weather && <span className="post-weather">{weatherOptions[post.weather]}</span>}
                                        </time>
                                        <span className="post-meta">
                                            <span className="post-emotion">{post.mood && moodOptions[post.mood]}</span>
                                        </span>
                                    </header>
                                    <div className="post-content" dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(post.content) }} />
                                </div>
                                {isOwner && (
                                    <footer className="post-footer">
                                        <div className="footer-left">
                                            <span className="privacy-display">
                                                공개설정 : {post.isPublic ? '전체공개' : '비공개'}
                                            </span>
                                            {wasUpdated && (
                                                <span className="updated-timestamp">
                                                    {formatUpdateDate(post.updatedAt)}
                                                </span>
                                            )}
                                        </div>
                                        <div className="post-actions">
                                            <button onClick={() => handleEditClick(post)}>수정</button>
                                            <button onClick={() => handleDelete(post.boardId)}>삭제</button>
                                        </div>
                                    </footer>
                                )}
                            </article>
                        );
                    })
                ) : (
                    <div className="post-view-wrapper"><p className="no-post-message">선택된 날짜에 작성된 글이 없습니다.</p></div>
                )}
            </div>
        </section>
    );
}

export default Diary;