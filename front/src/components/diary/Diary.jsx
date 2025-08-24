import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../../contexts/AuthContext';
import DiaryEditor from './DiaryEditor';
import '../../styles/Diary.scss';
import apiClient from '../../api/axiosConfig';

function Diary({ userId }) {
    const { currentUser } = useContext(AuthContext);
    const isOwner = currentUser && currentUser.id === userId;

    const [displayMonth, setDisplayMonth] = useState(new Date());
    const [selectedDate, setSelectedDate] = useState(new Date());
    const [posts, setPosts] = useState({});
    const [selectedPosts, setSelectedPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    
    const [isWriting, setIsWriting] = useState(false);

    const [editingPost, setEditingPost] = useState(null);
    const [editedContent, setEditedContent] = useState('');
    const [editedWeather, setEditedWeather] = useState('');
    const [editedMood, setEditedMood] = useState('');
    const [editedIsPublic, setEditedIsPublic] = useState(true);

    const weatherOptions = { 'SUNNY': '☀️', 'PARTLY_CLOUDY': '⛅️', 'CLOUDY': '☁️', 'RAIN': '☔️', 'SNOW': '☃️' };
    const moodOptions = { 'HAPPY': '^ㅅ^ 해피', 'SAD': 'T_T 슬퍼', 'ANGRY': '-_-^ 아오', 'LOVE': '사랑해 S2' };

    const fetchDiaryData = async () => {
        if (!userId) return;
        setLoading(true);
        const year = displayMonth.getFullYear();
        const month = displayMonth.getMonth() + 1;
        try {
            const response = await apiClient.get(`/users/${userId}/boards?type=DIARY&year=${year}&month=${month}`);
            const data = response.data;

            const postsObject = {};
            data.content.forEach(post => {
                const dateKey = post.createdAt.split('T')[0];
                if (!postsObject[dateKey]) postsObject[dateKey] = [];
                postsObject[dateKey].push(post);
            });
            setPosts(postsObject);
            const currentDateKey = `${selectedDate.getFullYear()}-${String(selectedDate.getMonth() + 1).padStart(2, '0')}-${String(selectedDate.getDate()).padStart(2, '0')}`;
            setSelectedPosts(postsObject[currentDateKey] || []);
        } catch (error) {
            console.error(error);
            setPosts({});
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchDiaryData();
    }, [userId, displayMonth]);
    
    const handleSavePost = async (postData) => {
        try {
            await apiClient.post(`/users/${userId}/boards`, { ...postData, type: 'DIARY' });

            await fetchDiaryData();
            setIsWriting(false);  
            
        } catch (err) {
            if (err.response?.status !== 401) {
                alert(err.response?.data?.message || '일기 작성에 실패했습니다.');
            }
        }
    };


    const handleDelete = async (boardId) => {
        if (window.confirm('정말로 삭제하시겠습니까?')) {
            
            setSelectedPosts(prevSelectedPosts =>
                prevSelectedPosts.filter(post => post.boardId !== boardId)
            );

            setPosts(prevPosts => {
                const newPosts = { ...prevPosts };
                for (const dateKey in newPosts) {
                    newPosts[dateKey] = newPosts[dateKey].filter(post => post.boardId !== boardId);
                }
                return newPosts;
            });

            try {
                await apiClient.delete(`/boards/${boardId}`);
            } catch (err) {
                if (err.response?.status !== 401) {
                    alert(err.response?.data?.message || '삭제에 실패했습니다. 목록을 새로고침합니다.');
                    fetchDiaryData(); // 데이터 원상 복구
                }
            }
        }
    };


    const handleEditClick = (post) => {
        setEditingPost(post);
        setEditedContent(post.content);
        setEditedWeather(post.weather);
        setEditedMood(post.mood);
        setEditedIsPublic(post.isPublic);
    };

    const handleUpdateSubmit = async (e) => {
        e.preventDefault();
        if (!editedContent.trim()) {
            alert('내용을 입력해주세요.');
            return;
        }
        try {
            await apiClient.put(`/boards/${editingPost.boardId}`, { 
                content: editedContent,
                weather: editedWeather,
                mood: editedMood,
                isPublic: editedIsPublic
            });
            setEditingPost(null);
            await fetchDiaryData();
        } catch (err) {
            if (err.response?.status !== 401) {
                alert(err.response?.data?.message || '수정에 실패했습니다.');
            }
        }
    };

    const year = displayMonth.getFullYear();
    const month = displayMonth.getMonth();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const today = new Date();
    const goToPrevMonth = () => setDisplayMonth(new Date(year, month - 1, 1));
    const goToNextMonth = () => setDisplayMonth(new Date(year, month + 1, 1));
    const handleDateClick = (day) => {
        const clickedDate = new Date(year, month, day);
        setSelectedDate(clickedDate);
        const dateString = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
        setSelectedPosts(posts[dateString] || []);
    };
    const renderCalendarDayButton = (day) => {
        const isToday = day === today.getDate() && month === today.getMonth() && year === today.getFullYear();
        const isSelected = day === selectedDate.getDate() && month === selectedDate.getMonth() && year === selectedDate.getFullYear();
        const dateString = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
        const hasPost = !!posts[dateString];
        let dayClasses = 'calendar-day-item';

        if (isSelected) dayClasses += ' selected';
        if (isToday) dayClasses += ' today';
        if (hasPost) dayClasses += ' has-post';

        return (<button key={day} className={dayClasses} onClick={() => handleDateClick(day)} aria-label={`${day}일`}>{day}</button>);
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

    if (isWriting) {
        return <DiaryEditor onSave={handleSavePost} onCancel={() => setIsWriting(false)} />;
    }

    return (
        <section className="diary-page-container" aria-labelledby="diary-main-title">
            <h2 id="diary-main-title" className="sr-only">다이어리</h2>
            
            <header className="diary-calendar-area">
                <div className="today-display">
                    <span className="today-month-day">{todayFormatted.monthDay}</span>
                    <span className="today-weekday">{todayFormatted.weekday}</span>
                </div>
                <div className="calendar-main">
                    <div className="days-row">
                        <nav className="calendar-header" aria-label="월별 네비게이션">
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
                        <button className="write-button" onClick={() => setIsWriting(true)}>
                            ✏️ 글쓰기
                        </button>
                </div>
            )}

            <p className="no-schedule-message">일정/기념일이 없습니다.</p>
            
            <div className="post-list-container">
                {loading ? ( <p>로딩 중...</p> ) : 
                selectedPosts.length > 0 ? (
                    selectedPosts.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)).map(post => {
                        const wasUpdated = post.updatedAt && (new Date(post.updatedAt).getTime() - new Date(post.createdAt).getTime() > 1000);
                        return (
                            <article key={post.boardId} className="post-view-wrapper">
                                {editingPost && editingPost.boardId === post.boardId ? (
                                    <form className="edit-form" onSubmit={handleUpdateSubmit}>
                                        <textarea value={editedContent} onChange={(e) => setEditedContent(e.target.value)} required />
                                        <div className="form-footer">
                                            <div className="form-options">
                                                <label>날씨
                                                    <select value={editedWeather} onChange={(e) => setEditedWeather(e.target.value)}>
                                                        {Object.entries(weatherOptions).map(([key, value]) => (<option key={key} value={key}>{value}</option>))}
                                                    </select>
                                                </label>
                                                <label>기분
                                                    <select value={editedMood} onChange={(e) => setEditedMood(e.target.value)}>
                                                        {Object.entries(moodOptions).map(([key, value]) => (<option key={key} value={key}>{value}</option>))}
                                                    </select>
                                                </label>
                                            </div>
                                            <div className="privacy-setting">
                                                <label><input type="radio" name={`privacy-${post.boardId}`} checked={editedIsPublic} onChange={() => setEditedIsPublic(true)} />전체공개</label>
                                                <label><input type="radio" name={`privacy-${post.boardId}`} checked={!editedIsPublic} onChange={() => setEditedIsPublic(false)} />비공개</label>
                                            </div>
                                            <div className="edit-actions">
                                                <button type="button" onClick={() => setEditingPost(null)}>취소</button>
                                                <button type="submit">저장</button>
                                            </div>
                                        </div>
                                    </form>
                                ) : (
                                    <>
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
                                            <div className="post-content" dangerouslySetInnerHTML={{ __html: post.content }} />
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
                                    </>
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
