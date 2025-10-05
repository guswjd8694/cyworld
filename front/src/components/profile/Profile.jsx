import React, { useState, useEffect } from 'react';
import apiClient from '../../api/axiosConfig';
import '../../styles/photoalbum.scss';


function PhotoAlbum({ userId }) {
    const [photos, setPhotos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchPhotos = async () => {
            if (!userId) return;
            setLoading(true);
            setError(null);
            try {
                const response = await apiClient.get(`/users/${userId}/boards?type=PROFILE`);
                setPhotos(response.data.content);
            } catch (err) {
                console.error("프로필 데이터를 불러오는 데 실패했습니다.", err);
                setError("데이터를 불러올 수 없습니다.");
            } finally {
                setLoading(false);
            }
        };

        fetchPhotos();
    }, [userId]);

    if (loading) {
        return <div className="photo-album-container"><p>프로필을 불러오는 중입니다...</p></div>;
    }

    if (error) {
        return <div className="photo-album-container"><p style={{ color: 'red' }}>{error}</p></div>;
    }

    return (
        <>
            <div style={{ textAlign: 'center', marginTop: '20px' }}>
                <h2 style={{ marginBottom: '70px' }}>곧 만나요</h2>
                <img src="/imgs/acting_minimi_02.gif" alt="" />
            </div>
        </>
    );
}

export default PhotoAlbum;