import React from 'react';

function Nav({ navigateTo, activeView }) {
    const menus = ['홈', '다이어리', '방명록'];

    return (
        <nav className="nav" aria-label="미니홈피 메뉴">
            <ul>
                {menus.map(menu => {
                    const viewName = 
                        menu === '홈' ? 'HOME' :
                        menu === '다이어리' ? 'DIARY' :
                        menu === '방명록' ? 'GUESTBOOK' : '';
                    
                    const isActive = activeView === viewName;

                    return (
                        <li 
                            key={menu} 
                            className={isActive ? "active" : ""}
                            onClick={() => navigateTo(viewName)}
                            style={{ cursor: 'pointer' }}
                        >
                            <a
                                href="#"
                                onClick={(e) => e.preventDefault()}
                            >
                                {menu}
                            </a>
                        </li>
                    );
                })}
            </ul>
        </nav>
    );
}

export default Nav;
