import React, { useState, useEffect, useRef, useContext } from 'react';
import { PlayIcon, PauseIcon, StopIcon, PrevIcon, NextIcon, VolumeIcon } from './BgmIcons';
import { Link } from 'react-router-dom';
import apiClient from '../../api/axiosConfig';
import '../../styles/BgmPlayer.scss';
import { AuthContext } from '../../contexts/AuthContext';

function BgmPlayer({ userId, ownerName }) {
    const { currentUser } = useContext(AuthContext);
    const isOwner = currentUser && currentUser.id === userId;

    const [playlist, setPlaylist] = useState([]);
    const [currentTrackIndex, setCurrentTrackIndex] = useState(0);
    const [isPlaying, setIsPlaying] = useState(false);
    const [volume, setVolume] = useState(0.5);
    const [isPlaylistVisible, setIsPlaylistVisible] = useState(false);
    const audioRef = useRef(null);
    const marqueeRef = useRef(null);
    const [hasInteracted, setHasInteracted] = useState(false);
    const loadedSrcRef = useRef(null);

    
    const [marqueeKey, setMarqueeKey] = useState(0);

    const [activeTab, setActiveTab] = useState('recommend');
    const [recommendations, setRecommendations] = useState([]);
    const [fanCount, setFanCount] = useState(0);
    const [relationship, setRelationship] = useState(null);
    const [loadingUI, setLoadingUI] = useState(true);

    const currentTrack = playlist.length > 0 ? playlist[currentTrackIndex] : null;

    useEffect(() => {
        const fetchPlaylist = async () => {
            if (!userId) return;
            try {
                const response = await apiClient.get(`/users/${userId}/bgms`);
                setPlaylist(response.data);
            } catch (error) {
                console.error("BGM 목록을 불러오는 데 실패했습니다.", error);
            }
        };
        fetchPlaylist();
    }, [userId]);

    useEffect(() => {
        const fetchUIData = async () => {
            setLoadingUI(true);
            try {
                setFanCount(3);
            } catch (error) { console.error("팬 수 로딩 실패:", error); }

            if (isOwner) {
                try {
                    const response = await apiClient.get('/users/random-recommendation');
                    setRecommendations(response.data ? [response.data] : []);
                } catch (error) {
                    if (error.response && error.response.status !== 204) {
                        console.error("친구 추천 로딩 실패:", error);
                    }
                    setRecommendations([]);
                }
            } else if (currentUser) {
                try {
                    const response = await apiClient.get(`/ilchons/relationship?currentUserId=${currentUser.id}&targetUserId=${userId}`);
                    setRelationship(response.data.degree);
                } catch (error) {
                    console.error("촌수 계산 실패:", error);
                    setRelationship(null);
                }
            }
            setLoadingUI(false);
        };
        fetchUIData();
    }, [userId, currentUser, isOwner]);

    useEffect(() => {
        if (audioRef.current) audioRef.current.volume = volume;
    }, [volume]);

    useEffect(() => {
        const audio = audioRef.current;
        if (!audio || !currentTrack) return;

        if (loadedSrcRef.current !== currentTrack.src) {
            loadedSrcRef.current = currentTrack.src;
            audio.src = currentTrack.src;
            audio.load();
        }

        if (isPlaying) {
            audio.play().catch(e => {
                if (e.name !== 'AbortError') {
                    console.error("음악 재생 오류:", e);
                    setIsPlaying(false);
                }
            });
        } else {
            audio.pause();
        }
    }, [isPlaying, currentTrack]);

    useEffect(() => {
        const handleInteraction = () => {
            setHasInteracted(true);
            document.removeEventListener('click', handleInteraction);
        };
        document.addEventListener('click', handleInteraction);
        return () => document.removeEventListener('click', handleInteraction);
    }, []);

    useEffect(() => {
        if (hasInteracted && playlist.length > 0 && !isPlaying) {
            const audio = audioRef.current;
            if (audio && audio.currentTime === 0) {
                setIsPlaying(true);
            }
        }
    }, [hasInteracted, playlist]);

    useEffect(() => {
        const marqueeElement = marqueeRef.current;
        if (!marqueeElement || !currentTrack) return;

        if (!isPlaying) {
            const style = window.getComputedStyle(marqueeElement);
            const matrix = new DOMMatrixReadOnly(style.transform);
            marqueeElement.style.transform = `translateX(${matrix.m41}px)`;
            marqueeElement.style.transition = 'none';
            return;
        }
        
        const animationFrameId = requestAnimationFrame(() => {
            if (!marqueeRef.current) return;
            const titleWidth = marqueeElement.offsetWidth;
            const speed = 40;
            const endPosition = -titleWidth;
            const style = window.getComputedStyle(marqueeElement);
            const matrix = new DOMMatrixReadOnly(style.transform);
            const currentPosition = matrix.m41;
            const remainingDistance = Math.abs(endPosition - currentPosition);
            const duration = remainingDistance / speed;

            marqueeElement.style.transition = `transform ${duration}s linear`;
            marqueeElement.style.transform = `translateX(${endPosition}px)`;
        });

        const loopAnimation = () => {
            const containerWidth = marqueeElement.parentElement.offsetWidth;
            const titleWidth = marqueeElement.offsetWidth;
            const speed = 40;
            const endPosition = -titleWidth;

            marqueeElement.style.transition = 'none';
            marqueeElement.style.transform = `translateX(${containerWidth}px)`;

            setTimeout(() => {
                const totalDistance = containerWidth + titleWidth;
                const totalDuration = totalDistance / speed;
                marqueeElement.style.transition = `transform ${totalDuration}s linear`;
                marqueeElement.style.transform = `translateX(${endPosition}px)`;
            }, 50);
        };

        const handleTransitionEnd = (event) => {
            if (event.propertyName === 'transform') loopAnimation();
        };

        marqueeElement.addEventListener('transitionend', handleTransitionEnd);

        return () => {
            cancelAnimationFrame(animationFrameId);
            marqueeElement.removeEventListener('transitionend', handleTransitionEnd);
        };
    }, [currentTrack, isPlaying]);

    const handlePlayPause = () => { if (playlist.length > 0) setIsPlaying(!isPlaying); };

    const handleNextTrack = () => {
        if (playlist.length > 0) {
            setCurrentTrackIndex(p => (p + 1) % playlist.length);
            if (!isPlaying) setIsPlaying(true);
        }
    };

    const handlePrevTrack = () => {
        if (playlist.length > 0) {
            setCurrentTrackIndex(p => (p - 1 + playlist.length) % playlist.length);
            if (!isPlaying) setIsPlaying(true);
        }
    };

    const handleEnded = () => handleNextTrack();

    
    const handleStop = () => {
        if (audioRef.current) {
            setIsPlaying(false);
            audioRef.current.currentTime = 0;
            
            setMarqueeKey(prevKey => prevKey + 1);
        }
    };

    const handleTrackSelect = (index) => {
        setCurrentTrackIndex(index);
        setIsPlaying(true);
        setIsPlaylistVisible(false);
    };

    const formatRelationship = (degree) => {
        if (!degree || degree > 4) return '모르는';
        const relations = { 1: '일촌', 2: '이촌', 3: '삼촌', 4: '사촌' };
        return relations[degree] || '모르는';
    };

    return (
        <aside className="bgm-player-aside">
            <section className="bgm-player">
                <div className="recommend-friend">
                    <div className="tab-menu">
                        <button className={`tab-btn ${activeTab === 'recommend' ? 'active' : ''}`} onClick={() => setActiveTab('recommend')}>친구 추천</button>
                        <button className={`tab-btn ${activeTab === 'fan' ? 'active' : ''}`} onClick={() => setActiveTab('fan')}>팬</button>
                    </div>
                    <div className="tab-content">
                        {loadingUI ? <p>로딩 중...</p> :
                            activeTab === 'recommend' ? (
                                isOwner ? (
                                    <div className="recommend-content">
                                        {recommendations.length > 0 ? (
                                            recommendations.map(rec => (
                                                <div key={rec.id || rec.loginId} className="recommend-item">
                                                    <Link to={`/${rec.loginId}`} className='img-link'>
                                                        <img src={rec.imageUrl} alt={`${rec.name}님의 프로필 사진`} className="minimi-img" />
                                                    </Link>
                                                    <div className="recommend-info">
                                                        <Link to={`/${rec.loginId}`} className="name-link">{rec.name}</Link>
                                                        <span>서로 아는 일촌 <strong>{rec.mutualIlchons}</strong>명</span>
                                                    </div>
                                                </div>
                                            ))
                                        ) : <p>추천할 친구가 없습니다.</p>}
                                    </div>
                                ) : (
                                    <div className="relationship-content">
                                        {currentUser ? (
                                            formatRelationship(relationship) !== '모르는' ? (
                                                <p><strong>{ownerName}</strong>님과 나는 <strong className="relationship-tag">{formatRelationship(relationship)}</strong></p>
                                            ) : (
                                                <p><strong>{ownerName}</strong>님과 아는 사이가 아니에요.</p>
                                            )
                                        ) : (<div className="login-prompt"><p>우리는 <Link to={'/login'} className="relationship-tag">몇촌</Link> 일까요?</p></div>)}
                                    </div>
                                )
                            ) : (<div className="fan-content"><p><strong>{fanCount}</strong> 명의 팬이 있습니다.</p></div>)}
                    </div>
                </div>

                <div className="player-wrap">
                    <audio
                        ref={audioRef}
                        src={currentTrack ? currentTrack.src : null}
                        onEnded={handleEnded}
                        aria-hidden="true"
                    />
                    <div className="track-info">
                        <span
                            key={`${currentTrackIndex}-${marqueeKey}`}
                            className="track-title"
                            ref={marqueeRef}
                        >
                            {currentTrack ? `${currentTrack.title} - ${currentTrack.artist}` : 'BGM 없음'}
                        </span>
                    </div>
                    <div className="controls">
                        <button onClick={handlePlayPause} aria-label={isPlaying ? "일시정지" : "재생"}>{isPlaying ? <PauseIcon /> : <PlayIcon />}</button>
                        <button onClick={handleStop} aria-label="정지"><StopIcon /></button>
                        <button onClick={handlePrevTrack} aria-label="이전 곡"><PrevIcon /></button>
                        <button onClick={handleNextTrack} aria-label="다음 곡"><NextIcon /></button>
                        <div className="volume-control">
                            <VolumeIcon className="volume-icon" />
                            <input type="range" min="0" max="1" step="0.01" value={volume} onChange={(e) => setVolume(parseFloat(e.target.value))} aria-label="볼륨 조절" />
                        </div>
                        <button className="list-btn" onClick={() => setIsPlaylistVisible(!isPlaylistVisible)}>LIST</button>
                    </div>
                    {isPlaylistVisible && playlist.length > 0 && (
                        <ul className="playlist-dropdown">
                            {playlist.map((track, index) => (
                                <li
                                    key={track.id || index}
                                    className={index === currentTrackIndex ? 'active' : ''}
                                    onClick={() => handleTrackSelect(index)}
                                >
                                    {track.title} - {track.artist}
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </section>
        </aside>
    );
}

export default BgmPlayer;