import React, { useState, useContext, useEffect } from 'react';
import { AuthContext } from '../../contexts/AuthContext';
import apiClient from '../../api/axiosConfig';

function ProfileSection({ userId, profileData, onHistoryClick, setRefetchTrigger }) {
    const { currentUser } = useContext(AuthContext);
    const isOwner = currentUser && currentUser.id === userId;

    const [currentEmotion, setCurrentEmotion] = useState("");
    const [isEmotionListOpen, setIsEmotionListOpen] = useState(false);
    const [emotionOptions, setEmotionOptions] = useState([]); 

    const [isEditing, setIsEditing] = useState(false);
    const [editedBio, setEditedBio] = useState('');
    const [selectedFile, setSelectedFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState('');

    useEffect(() => {
        if (!userId) return;
        const fetchEmotionData = async () => {
            try {
                const [emotionRes, emotionListRes] = await Promise.all([
                    apiClient.get(`/users/${userId}/emotion`),
                    apiClient.get(`/emotions`)
                ]);
                setCurrentEmotion(emotionRes.data.emotion);
                setEmotionOptions(emotionListRes.data);
            } catch (err) {
                if (err.response?.status !== 401) {
                    console.error("감정 데이터 로딩 실패", err);
                }
            }
        };
        fetchEmotionData();
    }, [userId]);

    const handleEmotionChange = async (emotionId, emotionName) => {
        const originalEmotion = currentEmotion;
        setCurrentEmotion(emotionName);
        setIsEmotionListOpen(false);
        try {
            await apiClient.put(`/users/${userId}/emotion`, { emotionId });
        } catch (err) {
            if (err.response?.status !== 401) {
                alert(err.response?.data?.message || '감정 업데이트에 실패했습니다.');
                setCurrentEmotion(originalEmotion);
            }
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
                
                const uploadRes = await apiClient.post('/upload/image', formData);
                finalImageUrl = uploadRes.data.imageUrl;
            }
            
            await apiClient.post(`/users/${userId}/profile`, {
                bio: editedBio,
                profileImageUrl: finalImageUrl
            });
            setIsEditing(false);
            setRefetchTrigger(prev => prev + 1);
        } catch (err) {
            if (err.response?.status !== 401) {
                alert(err.response?.data?.message || '프로필 수정에 실패했습니다.');
            }
        }
    };

    if (!profileData) {
        return <div className="profile_area bottom_area"><p>프로필 정보 로딩 중...</p></div>;
    }

    return (
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
                            <button type="button" onClick={onHistoryClick}>History</button>
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
    );
}

export default ProfileSection;