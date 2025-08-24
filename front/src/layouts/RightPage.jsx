import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../contexts/AuthContext';

function RightPageLayout({ children, owner, title, onTitleUpdate, onIlchonClick, onIlchonRequestCheckClick, ilchonStatus, receivedRequestCount }) {
    const { currentUser } = useContext(AuthContext);
    const isOwner = currentUser && owner && currentUser.id === owner.id;

    const [isEditingTitle, setIsEditingTitle] = useState(false);
    const [newTitle, setNewTitle] = useState(title);

    useEffect(() => {
        setNewTitle(title);
    }, [title]);

    const handleSave = () => {
        onTitleUpdate(newTitle);
        setIsEditingTitle(false);
    };

    const showIlchonButton = ilchonStatus === 'NONE';

    return (
        <section className="section_right book_cover right_page">
            <div className="inner_page">
                <section className="title_area top_area">
                    <div className="title-wrapper">
                        {isEditingTitle ? (
                            <input 
                                type="text" 
                                className="title-edit-input"
                                value={newTitle}
                                onChange={(e) => setNewTitle(e.target.value)}
                            />
                        ) : (
                            <h3>{title}</h3>
                        )}
                        {isOwner && (
                            isEditingTitle ? (
                                <div className="title-edit-actions">
                                    <button onClick={handleSave}>SAVE</button>
                                    <button onClick={() => setIsEditingTitle(false)}>CANCLE</button>
                                </div>
                            ) : (
                                <button className="title-edit-btn" onClick={() => setIsEditingTitle(true)}>EDIT</button>
                            )
                        )}
                    </div>
                    <div className="flex-wrap">
                        <ul>
                            {isOwner ? (
                                <li>
                                    <button type="button" onClick={onIlchonRequestCheckClick}>
                                        일촌신청확인 {receivedRequestCount > 0 && `(${receivedRequestCount})`}
                                    </button>
                                </li>
                            ) : (
                                showIlchonButton && <li><button type="button" onClick={onIlchonClick}>+일촌 맺기</button></li>
                            )}
                            <li><button type="button">+팬되기</button></li>
                        </ul>
                        <a href="#">http://www.cyworld.com/{owner?.loginId}</a>
                    </div>
                </section>
                <section className="bottom_area">
                    {children}
                </section>
            </div>
        </section>
    );
}

export default RightPageLayout;
