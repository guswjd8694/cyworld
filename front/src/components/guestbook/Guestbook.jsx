import React, { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import Pagination from '../common/Pagination';
import apiClient from '../../api/axiosConfig';

function Guestbook({ userId }) {
    const { currentUser } = useContext(AuthContext);
    const isOwner = currentUser && currentUser.id === userId;

    const [boardPage, setBoardPage] = useState(null);
    const [newContent, setNewContent] = useState('');
    const [isSecret, setIsSecret] = useState(false);
    const [currentPage, setCurrentPage] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    const [refetchTrigger, setRefetchTrigger] = useState(0);

    const [editingPostId, setEditingPostId] = useState(null);
    const [editedContent, setEditedContent] = useState('');
    const [editedIsSecret, setEditedIsSecret] = useState(false);

    useEffect(() => {
        const fetchGuestbook = async () => {
            setLoading(true);
            try {
                const response = await apiClient.get(`/users/${userId}/boards?type=GUESTBOOK&page=${currentPage}&size=5&sort=createdAt,desc`);
                setBoardPage(response.data);
            } catch (err) {
                if (err.response?.status !== 401) {
                    setError(err.message);
                }
            } finally {
                setLoading(false);
            }
        };
        fetchGuestbook();
    }, [userId, currentPage, refetchTrigger]);


    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!newContent.trim()) return;
        if (!currentUser) {
            alert("로그인이 필요합니다.");
            return;
        }
        try {
            await apiClient.post(`/users/${userId}/boards`, {
                content: newContent,
                type: 'GUESTBOOK',
                isPublic: !isSecret 
            });
            setNewContent('');
            setIsSecret(false);
            if (currentPage === 0) {
                setRefetchTrigger(prev => prev + 1);
            } else {
                setCurrentPage(0);
            }
        } catch (err) {
            if (err.response?.status !== 401) {
                alert(err.response?.data?.message || '방명록 작성에 실패했습니다.');
            }
        }
    };

    const handleDelete = async (boardId) => {
        if (window.confirm('정말로 삭제하시겠습니까?')) {
            setBoardPage(prevPage => ({
                ...prevPage,
                content: prevPage.content.filter(item => item.boardId !== boardId)
            }));

            try {
                await apiClient.delete(`/boards/${boardId}`);
            } catch (err) {
                if (err.response?.status !== 401) {
                    alert(err.response?.data?.message || '삭제에 실패했습니다. 목록을 새로고침합니다.');
                    setRefetchTrigger(prev => prev + 1);
                }
            }
        }
    };

    const handleEditClick = (post) => {
        setEditingPostId(post.boardId);
        setEditedContent(post.content);
        setEditedIsSecret(!post.isPublic);
    };

    const handleUpdateSubmit = async (e, boardId) => {
        e.preventDefault();
        if (!editedContent.trim()) return;
        try {
            await apiClient.put(`/boards/${boardId}`, { 
                content: editedContent,
                isPublic: !editedIsSecret
            });
            setEditingPostId(null);
            setRefetchTrigger(prev => prev + 1);
        } catch (err) {
            if (err.response?.status !== 401) {
                alert(err.response?.data?.message || '수정에 실패했습니다.');
            }
        }
    };

    const handleToggleSecret = async (post) => {
        try {
            await apiClient.put(`/boards/${post.boardId}`, { isPublic: !post.isPublic });
            setRefetchTrigger(prev => prev + 1);
        } catch (err) {
            if (err.response?.status !== 401) {
                alert(err.response?.data?.message || '공개 설정 변경에 실패했습니다.');
            }
        }
    };

    if (loading) return <div>로딩 중...</div>;
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
                    {boardPage && boardPage.content.map(item => {
                        const isAuthor = currentUser && currentUser.loginId === item.writerLoginId;
                        const canView = item.isPublic || isAuthor || isOwner;
                        const canDelete = isAuthor || isOwner;
                        const canEdit = isAuthor;
                        const canToggleSecret = isAuthor || isOwner;

                        return (
                            <li key={item.boardId} className={`guestbook_item ${!item.isPublic ? 'secret' : ''}`}>
                                <article className="guestbook_card">
                                    <header className="guestbook_header">
                                        <div className="guestbook_header_left">
                                            <em className="guestbook_number">No.{item.boardNo}</em>
                                            <Link to={`/${item.writerLoginId}`}><h4 className="guestbook_writer">{item.writerName}</h4></Link>
                                            <Link to={`/${item.writerLoginId}`} className="mini_home_btn" role="button">미니홈피</Link>
                                            <time dateTime={item.createdAt}>
                                                ({new Date(item.createdAt).toLocaleString('ko-KR', {
                                                    year: 'numeric', month: '2-digit', day: '2-digit',
                                                    hour: '2-digit', minute: '2-digit', hour12: false
                                                }).replace(/\. /g, '.').replace(/,/, '')})
                                            </time>
                                        </div>
                                        
                                        {editingPostId !== item.boardId && (
                                            <div className="guestbook-actions">
                                                {canToggleSecret && (
                                                    <button onClick={() => handleToggleSecret(item)}>
                                                        {item.isPublic ? '비밀로 하기' : '공개하기'}
                                                    </button>
                                                )}
                                                {canEdit && (
                                                    <button onClick={() => handleEditClick(item)}>수정</button>
                                                )}
                                                {canDelete && (
                                                    <button onClick={() => handleDelete(item.boardId)}>삭제</button>
                                                )}
                                            </div>
                                        )}
                                    </header>
                                    <div className="guestbook_body">
                                        <figure className="guestbook_figure">
                                            <img src="/imgs/minimi_guest.jpg" alt={`${item.writerName}의 미니미`} />
                                        </figure>
                                        <div className="guestbook_content">
                                            {!item.isPublic && (
                                                <p className="secret_message">
                                                    <img src="/imgs/icon_secret.png" alt="자물쇠 아이콘" />
                                                    <span className="secret_title">비밀이야</span>
                                                    <span className="secret_description">(이 글은 홈주인과 작성자만 볼 수 있어요)</span>
                                                </p>
                                            )}
                                            {editingPostId === item.boardId ? (
                                                <form className="guestbook-edit-form" onSubmit={(e) => handleUpdateSubmit(e, item.boardId)}>
                                                    <textarea value={editedContent} onChange={(e) => setEditedContent(e.target.value)} required />
                                                    <div className="edit-actions">
                                                        <label className="secret_checkbox">
                                                            <input 
                                                                type="checkbox" 
                                                                checked={editedIsSecret}
                                                                onChange={(e) => setEditedIsSecret(e.target.checked)}
                                                            />
                                                            비밀로 하기
                                                        </label>
                                                        <div className="edit-buttons">
                                                            <button type="button" onClick={() => setEditingPostId(null)}>취소</button>
                                                            <button type="submit">저장</button>
                                                        </div>
                                                    </div>
                                                </form>
                                            ) : (
                                                canView ? <p className="guestbook_text">{item.content}</p> : <p className="guestbook_text secret-text">비밀글입니다</p>
                                            )}
                                        </div>
                                    </div>
                                </article>
                            </li>
                        );
                    })}
                </ul>
                {boardPage && boardPage.totalPages > 1 && (
                    <Pagination
                        currentPage={currentPage}
                        totalPages={boardPage.totalPages}
                        onPageChange={setCurrentPage}
                    />
                )}
            </section>
        </div>
    );
}

export default Guestbook;
