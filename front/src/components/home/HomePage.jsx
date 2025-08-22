import React from 'react';
import Ilchonpyeong from './Ilchonpyeong';
import IlchonModal from './IlchonModal';

function HomePage({ userId, navigateTo, ilchonStatus }) {
    return (
        <div className="grid_container">
            <section aria-labelledby="recent-posts-title" className="recent_posts">
                <h2 id="recent-posts-title">최근 게시물</h2>
                <ul className="post_lists">
                    <li><article><a href="#"><span className="tag tag_gray">동영상</span><h3>맘에 드는 아이템은 소망상자에 넣어보세요</h3></a></article></li>
                    <li><article><a href="#"><span className="tag tag_pink">게시판</span><h3>내 개성은 미니미로</h3></a></article></li>
                    <li><article><a href="#"><span className="tag tag_green">갤러리</span><h3>스킨, 메뉴효과 예약 기능 좋아</h3></a></article></li>
                    <li><article><a href="#"><span className="tag tag_orange">사진첩</span><h3>더욱 편리해진 사용중 아이템</h3></a></article></li>
                </ul>
            </section>
            <section aria-labelledby="grid-links-title" className="links">
                <h2 id="grid-links-title" className="sr_only">바로가기 링크</h2>
                <ul className="grid-2x2" role="group">
                    <li><a href="#" className="grid-item"><strong>쥬크박스</strong><span className="post_count">1/881</span></a></li>
                    <li><a href="#" className="grid-item"><strong>사진첩</strong><span className="post_count">30/9128</span></a></li>
                    <li>
                        <a href="#" className="grid-item" onClick={(e) => { e.preventDefault(); navigateTo('GUESTBOOK'); }}>
                            <strong>방명록</strong>
                            <span className="post_count">28/234</span>
                        </a>
                    </li>
                    <li><a href="#" className="grid-item"><strong>즐겨찾기</strong><span className="post_count">0/5</span></a></li>
                </ul>
            </section>
            <section className="miniroom" aria-labelledby="miniroom">
                <h2 id="miniroom-title">미니룸</h2>
                <figure>
                    <img src="/imgs/miniroom_03.gif" alt="미니룸 이미지" />
                </figure>
            </section>
            
            <Ilchonpyeong userId={userId} ilchonStatus={ilchonStatus} />
        </div>
    );
}

export default HomePage;
