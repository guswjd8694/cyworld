import React from 'react';

function RightPageLayout({ children, owner, onIlchonClick, ilchonStatus }) {
    
    const showIlchonButton = ilchonStatus === 'NONE';

    return (
        <section className="section_right book_cover right_page">
            <h2 className="sr_only">오른쪽 페이지</h2>
            <div className="inner_page">
                <section className="title_area top_area" aria-label="메인 상단 영역">
                    <h3>사이좋은 사람들, 싸이월드</h3>
                    <div className="flex-wrap">
                        <ul>
                            {showIlchonButton && (
                                <li><button type="button" onClick={onIlchonClick}>+일촌 맺기</button></li>
                            )}
                            <li><button type="button">+팬되기</button></li>
                        </ul>
                        <a href="#">http://www.cyworld.com/{owner?.loginId}</a>
                    </div>
                </section>
                <section className="bottom_area" aria-label="메인 하단 영역">
                    {children}
                </section>
            </div>
        </section>
    );
}

export default RightPageLayout;
