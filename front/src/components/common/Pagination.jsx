import React from 'react';

// 이 컴포넌트는 현재 페이지, 전체 페이지 수, 페이지 변경 함수를 props로 받습니다.
function Pagination({ currentPage, totalPages, onPageChange }) {
    // 페이지 번호 목록을 생성하는 헬퍼 함수
    const getPageNumbers = () => {
        const pageNumbers = [];
        for (let i = 0; i < totalPages; i++) {
            pageNumbers.push(i);
        }
        return pageNumbers;
    };

    return (
        <nav className="pagination" aria-label="페이지 네비게이션">
            <ul>
                {/* [추가] 처음으로 가는 버튼 */}
                <li>
                    <a href="#"
                        className={currentPage === 0 ? 'disabled' : 'first'}
                        onClick={(e) => {
                            e.preventDefault();
                            if (currentPage > 0) onPageChange(0);
                        }}
                    >
                        &lt;&lt;
                    </a>
                </li>
                {/* '이전' 버튼 */}
                <li>
                    <a href="#" 
                        className={currentPage === 0 ? 'disabled' : 'prev'}
                        onClick={(e) => {
                            e.preventDefault();
                            if (currentPage > 0) onPageChange(currentPage - 1);
                        }}
                    >
                        &lt;
                    </a>
                </li>

                {/* 페이지 번호 목록 */}
                {getPageNumbers().map(pageNumber => (
                    <li key={pageNumber}>
                        <a href="#"
                            className={currentPage === pageNumber ? 'active' : ''}
                            onClick={(e) => {
                                e.preventDefault();
                                onPageChange(pageNumber);
                            }}
                        >
                            {pageNumber + 1}
                        </a>
                    </li>
                ))}

                {/* '다음' 버튼 */}
                <li>
                    <a href="#"
                        className={currentPage === totalPages - 1 ? 'disabled' : 'next'}
                        onClick={(e) => {
                            e.preventDefault();
                            if (currentPage < totalPages - 1) onPageChange(currentPage + 1);
                        }}
                    >
                        &gt;
                    </a>
                </li>
                {/* [추가] 마지막으로 가는 버튼 */}
                <li>
                    <a href="#"
                        className={currentPage === totalPages - 1 ? 'disabled' : 'last'}
                        onClick={(e) => {
                            e.preventDefault();
                            if (currentPage < totalPages - 1) onPageChange(totalPages - 1);
                        }}
                    >
                        &gt;&gt;
                    </a>
                </li>
            </ul>
        </nav>
    );
}

export default Pagination;