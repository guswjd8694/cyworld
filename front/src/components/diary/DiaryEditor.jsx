import React, { useState } from 'react';
import { CKEditor } from '@ckeditor/ckeditor5-react';
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';

function DiaryEditor({ onSave, onCancel }) {
    const [content, setContent] = useState('');
    const [weather, setWeather] = useState('SUNNY');
    const [mood, setMood] = useState('HAPPY');
    const [isPublic, setIsPublic] = useState(true);

    const weatherOptions = { 'SUNNY': '☀️', 'PARTLY_CLOUDY': '⛅️', 'CLOUDY': '☁️', 'RAIN': '☔️', 'SNOW': '☃️' };
    const moodOptions = { 'HAPPY': '^ㅅ^ 해피', 'SAD': 'T_T 슬퍼', 'ANGRY': '-_-^ 아오', 'LOVE': '사랑해 S2' };

    const handleSave = () => {
        const pureText = content.replace(/<[^>]*>/g, '').trim();
        if (!pureText) {
            alert('내용을 입력해주세요.');
            return;
        }
        onSave({ content, weather, mood, isPublic });
    };

    return (
        <div className="diary-editor-container">
            <header className="editor-header">
                <div className="weather-selection">
                    <span>날씨</span>
                    {Object.entries(weatherOptions).map(([key, icon]) => (
                        <button 
                            key={key} 
                            className={`weather-btn ${weather === key ? 'active' : ''}`}
                            onClick={() => setWeather(key)}
                            aria-label={key}
                        >
                            {icon}
                        </button>
                    ))}
                </div>
                <div className="mood-selection">
                    <label>기분
                        <select value={mood} onChange={(e) => setMood(e.target.value)}>
                            {Object.entries(moodOptions).map(([key, value]) => (
                                <option key={key} value={key}>{value}</option>
                            ))}
                        </select>
                    </label>
                </div>
            </header>
            
            <div className="ck-editor-wrapper">
                <CKEditor
                    editor={ ClassicEditor }
                    data={content}
                    onChange={ ( event, editor ) => {
                        const data = editor.getData();
                        setContent(data);
                    } }
                    config={{
                        toolbar: [ 'heading', '|', 'bold', 'italic', 'link', 'bulletedList', 'numberedList', 'blockQuote', 'insertTable', 'mediaEmbed', 'undo', 'redo' ]
                    }}
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

export default DiaryEditor;
