import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../../contexts/AuthContext';
import ProfileHistoryModal from './ProfileHistoryModal';

function ProfileSection({ userId }) {
    const { currentUser } = useContext(AuthContext);
    const isOwner = currentUser && currentUser.id === userId;

    const [profileData, setProfileData] = useState(null);
    const [currentEmotion, setCurrentEmotion] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isEmotionListOpen, setIsEmotionListOpen] = useState(false);
    const [emotionOptions, setEmotionOptions] = useState([]); 

    const [isEditing, setIsEditing] = useState(false);
    const [editedBio, setEditedBio] = useState('');
    const [selectedFile, setSelectedFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState('');
    const [refetchTrigger, setRefetchTrigger] = useState(0);
    const [isHistoryModalOpen, setIsHistoryModalOpen] = useState(false);

    useEffect(() => {
        if (!userId) return;
        const fetchData = async () => {
            setLoading(true);
            setError(null);
            try {
                const [profileRes, emotionRes, emotionListRes] = await Promise.all([
                    fetch(`http://localhost:8080/users/${userId}/profile`),
                    fetch(`http://localhost:8080/users/${userId}/emotion`),
                    fetch(`http://localhost:8080/emotions`)
                ]);
                if (!profileRes.ok || !emotionRes.ok || !emotionListRes.ok) throw new Error('프로필 정보를 불러오는 데 실패했습니다.');
                const profileData = await profileRes.json();
                const emotionData = await emotionRes.json();
                const emotionListData = await emotionListRes.json();
                setProfileData(profileData);
                setCurrentEmotion(emotionData.emotion);
                setEmotionOptions(emotionListData);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [userId, refetchTrigger]);

    const handleEmotionChange = async (emotionId, emotionName) => {
        const originalEmotion = currentEmotion;
        setCurrentEmotion(emotionName);
        setIsEmotionListOpen(false);
        try {
            const response = await fetch(`http://localhost:8080/users/${userId}/emotion`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('jwt-token')}`
                },
                body: JSON.stringify({ emotionId })
            });
            if (!response.ok) throw new Error('감정 업데이트에 실패했습니다.');
        } catch (err) {
            alert(err.message);
            setCurrentEmotion(originalEmotion);
        }
    };

    const handleEditClick = () => {
        setIsEditing(true);
        setEditedBio(profileData.bio);
        setPreviewUrl(profileData.imageUrl);
        setSelectedFile(null);
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setSelectedFile(file);
            setPreviewUrl(URL.createObjectURL(file));
        }
    };

    const handleUpdateSubmit = async (e) => {
        e.preventDefault();
        let finalImageUrl = profileData.imageUrl;
        try {
            if (selectedFile) {
                const formData = new FormData();
                formData.append('image', selectedFile);
                const uploadRes = await fetch('http://localhost:8080/upload/image', {
                    method: 'POST',
                    headers: { 'Authorization': `Bearer ${localStorage.getItem('jwt-token')}` },
                    body: formData,
                });
                if (!uploadRes.ok) throw new Error('이미지 업로드에 실패했습니다.');
                const uploadData = await uploadRes.json();
                finalImageUrl = uploadData.imageUrl;
            }
            const profileUpdateRes = await fetch(`http://localhost:8080/users/${userId}/profile`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('jwt-token')}`
                },
                body: JSON.stringify({
                    bio: editedBio,
                    profileImageUrl: finalImageUrl
                })
            });
            if (!profileUpdateRes.ok) throw new Error('프로필 수정에 실패했습니다.');
            setIsEditing(false);
            setRefetchTrigger(prev => prev + 1);
        } catch (err) {
            alert(err.message);
        }
    };

    if (loading) return <div className="profile_area bottom_area"><p>프로필 로딩 중...</p></div>;
    if (error) return <div className="profile_area bottom_area"><p style={{ color: 'red' }}>오류: {error}</p></div>;
    if (!profileData) return null;

    return (
        <>
            <section className="profile_area bottom_area">
                {isEditing ? (
                    <form className="profile-edit-form" onSubmit={handleUpdateSubmit}>
                        <div className="profile_main">
                            <article className="mood">
                                <div className="today_mood"><p>today is.. <span>{currentEmotion}</span></p></div>
                            </article>
                            <article className="profile_pic edit-mode">
                                <figure>
                                    <img src={previewUrl || profileData.imageUrl} alt="프로필 미리보기" />
                                </figure>
                                <label htmlFor="file-input" className="file-upload-btn">사진 바꾸기</label>
                                <input id="file-input" type="file" accept="image/*" onChange={handleFileChange} />
                            </article>
                            <article className="profile_intro">
                                <label htmlFor="bio-input" className='sr_only'>소개글</label>
                                <textarea id="bio-input" value={editedBio} onChange={(e) => setEditedBio(e.target.value)} />
                            </article>
                        </div>
                        <footer className="profile_footer">
                            <div className="profile_util edit-mode">
                                <button type="button" onClick={() => setIsEditing(false)}>CANCLE</button>
                                <button type="submit">SAVE</button>
                            </div>
                            <article className="profile_info">
                                <div className="profile_info_wrap">
                                    <p className="user_name">{profileData.name}</p>
                                    <p className="user_gender">({profileData.gender === 'Female' ? '♀' : '♂'})</p>
                                    <p className="user_birth">{profileData.birthday}</p>
                                </div>
                                <p className="user_email"><a href={`mailto:${profileData.email}`}>{profileData.email}</a></p>
                            </article>
                        </footer>
                    </form>
                ) : (
                    <>
                        <div className="profile_main">
                            <article className="mood">
                                <div className="today_mood">
                                    <p>today is..
                                        {isOwner ? (
                                            <button type="button" onClick={() => setIsEmotionListOpen(!isEmotionListOpen)}>
                                                {currentEmotion} ▼
                                            </button>
                                        ) : ( <span>{currentEmotion}</span> )}
                                    </p>
                                    {isOwner && isEmotionListOpen && (
                                        <ul id="moodList" role="listbox">
                                            {emotionOptions.map(option => (
                                                <li key={option.emotionId} role="option" onClick={() => handleEmotionChange(option.emotionId, option.emotionName)}>
                                                    {option.emotionName}
                                                </li>
                                            ))}
                                        </ul>
                                    )}
                                </div>
                            </article>
                            <article className="profile_pic">
                                <figure>
                                    <img src={profileData.imageUrl} alt="프로필 사진" />
                                </figure>
                            </article>
                            <article className="profile_intro">
                                <p>{profileData.bio}</p>
                            </article>
                        </div>
                        <footer className="profile_footer">
                            <article className="profile_util">
                                {isOwner && <button type="button" onClick={handleEditClick}>Edit</button>}
                                <button type="button" onClick={() => setIsHistoryModalOpen(true)}>History</button>
                            </article>
                            <article className="profile_info">
                                <div className="profile_info_wrap">
                                    <p className="user_name">{profileData.name}</p>
                                    <p className="user_gender">({profileData.gender === 'Female' ? '♀' : '♂'})</p>
                                    <p className="user_birth">{profileData.birthday}</p>
                                </div>
                                <p className="user_email"><a href={`mailto:${profileData.email}`}>{profileData.email}</a></p>
                            </article>
                        </footer>
                    </>
                )}
            </section>
            {isHistoryModalOpen && (
                <ProfileHistoryModal 
                    onClose={() => setIsHistoryModalOpen(false)} 
                    history={profileData.profileHistoryList} 
                />
            )}
        </>
    );
}

export default ProfileSection;
