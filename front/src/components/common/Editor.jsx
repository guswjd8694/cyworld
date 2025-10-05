import React, { useState, useEffect } from 'react';
import { SimpleEditor } from '@/components/tiptap-templates/simple/simple-editor';

function Editor({ onSave, onCancel, initialData = {}, boardType }) {

    const createInitialContent = () => {
        let combinedContent = initialData.content || '';
        if (boardType === 'PHOTO' && initialData.images && initialData.images.length > 0) {
            const imagesHtml = initialData.images.map(url => `<img src="${url}">`).join('');
            combinedContent = imagesHtml + combinedContent;
        }
        return combinedContent;
    };

    const [content, setContent] = useState(createInitialContent());
    
    const [isPublic, setIsPublic] = useState(initialData.isPublic ?? true);
    const [title, setTitle] = useState(initialData.title || '');
    const [weather, setWeather] = useState(initialData.weather || 'SUNNY');
    const [mood, setMood] = useState(initialData.mood || 'HAPPY');

    const weatherOptions = { 'SUNNY': '☀️', 'PARTLY_CLOUDY': '⛅️', 'CLOUDY': '☁️', 'RAIN': '☔️', 'SNOW': '☃️' };
    const moodOptions = { 'HAPPY': '^ㅅ^ 해피', 'SAD': 'T_T 슬퍼', 'ANGRY': '-_-^ 아오', 'LOVE': '사랑해 S2' };

    useEffect(() => {
        setContent(createInitialContent());
        setIsPublic(initialData.isPublic ?? true);
        setTitle(initialData.title || '');
        setWeather(initialData.weather || 'SUNNY');
        setMood(initialData.mood || 'HAPPY');
    }, [initialData.boardId]);

    const handleSave = () => {
    const pureText = content.replace(/<[^>]*>/g, '').trim();
    if (boardType === 'PHOTO' && !content.includes('<img')) {
        alert('사진첩 게시글에는 이미지가 반드시 포함되어야 합니다.');
        return;
    }
    if (boardType !== 'PHOTO' && !pureText && !content.includes('<img')) {
        alert('내용을 입력해주세요.');
        return;
    }


    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = content;
    const allImages = tempDiv.querySelectorAll('img');
    const imageUrls = Array.from(allImages).map(img => img.src);

    const contentWithoutImages = content.replace(/<img[^>]*>/g, "");

    const postData = {
        content: contentWithoutImages,
        isPublic,
        imageUrls,
    };

    if (boardType === 'DIARY') {
        postData.weather = weather;
        postData.mood = mood;
    } else if (boardType === 'PHOTO') {
        if (!title.trim()) {
            alert('제목을 입력해주세요.');
            return;
        }
        postData.title = title;
    }

    if (initialData.boardId) {
        postData.boardId = initialData.boardId;
    }

    onSave(postData);
};

    return (
        <div className="common-editor-container"> 
            <header className="editor-header">
                {boardType === 'DIARY' && (
                    <>
                        <div className="weather-selection">
                            {Object.entries(weatherOptions).map(([key, icon]) => ( <button key={key} className={`weather-btn ${weather === key ? 'active' : ''}`} onClick={() => setWeather(key)} aria-label={key}>{icon}</button> ))}
                        </div>
                        <div className="mood-selection">
                            <label>기분
                                <select value={mood} onChange={(e) => setMood(e.target.value)}>
                                    {Object.entries(moodOptions).map(([key, value]) => ( <option key={key} value={key}>{value}</option> ))}
                                </select>
                            </label>
                        </div>
                    </>
                )}
                {boardType === 'PHOTO' && (
                    <div className="title-input-group">
                        <label htmlFor="editor-title" className="sr-only">제목</label>
                        <input id="editor-title" type="text" value={title} onChange={(e) => setTitle(e.target.value)} placeholder="제목을 입력하세요" required />
                    </div>
                )}
            </header>
            
            <div className="editor-wrapper">
                <SimpleEditor
                    content={content}
                    onUpdate={(newContent) => setContent(newContent)}
                />
            </div>

            <footer className="editor-footer">
                <div className="privacy-setting">
                    <label><input type="radio" name="privacy" checked={isPublic} onChange={() => setIsPublic(true)} />전체공개</label>
                    <label><input type="radio" name="privacy" checked={!isPublic} onChange={() => setIsPublic(false)} />비공개</label>
                </div>
                <div className="form-actions">
                    <button className="cancel-btn" onClick={onCancel}>취소</button>
                    <button className="save-btn" onClick={handleSave}>저장</button>
                </div>
            </footer>
        </div>
    );
}

export default Editor;