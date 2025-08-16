import React, { useState, useEffect } from 'react';

function ProfileSection({ userId }) {
    const [profileData, setProfileData] = useState(null);
    const [currentEmotion, setCurrentEmotion] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isEmotionListOpen, setIsEmotionListOpen] = useState(false);
    
    const [emotionOptions, setEmotionOptions] = useState([]); 

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            setError(null);
            try {
                const [profileRes, emotionRes, emotionListRes] = await Promise.all([
                    fetch(`http://localhost:8080/users/${userId}/profile`),
                    fetch(`http://localhost:8080/users/${userId}/emotion`),
                    fetch(`http://localhost:8080/emotions`)
                ]);

                if (!profileRes.ok || !emotionRes.ok || !emotionListRes.ok) {
                    throw new Error('프로필 정보를 불러오는 데 실패했습니다.');
                }

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
    }, [userId]);

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

    if (loading) return <div className="profile_area bottom_area"><p>프로필 로딩 중...</p></div>;
    if (error) return <div className="profile_area bottom_area"><p style={{ color: 'red' }}>오류: {error}</p></div>;
    if (!profileData) return null;

    return (
        <section className="profile_area bottom_area">
            <article className="mood">
                <div className="today_mood" data-role="admin">
                    <p>today is..
                        <button type="button" onClick={() => setIsEmotionListOpen(!isEmotionListOpen)}>
                            {currentEmotion}
                        </button>
                    </p>
                    {isEmotionListOpen && (
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
                <p dangerouslySetInnerHTML={{ __html: profileData.bio }} />
            </article>
            <article className="profile_history">
                <p>History</p>
            </article>
            <article className="profile_info">
                <div className="profile_info_wrap">
                    <p className="user_name">{profileData.name}</p>
                    <p className="user_gender">({profileData.gender === 'Female' ? '♀' : '♂'})</p>
                    <p className="user_birth">{profileData.birthday}</p>
                </div>
                <p className="user_email">
                    <a href={`mailto:${profileData.email}`}>{profileData.email}</a>
                </p>
            </article>
        </section>
    );
}

export default ProfileSection;