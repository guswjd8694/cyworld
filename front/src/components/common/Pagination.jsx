import React from 'react';

function Pagination({ currentPage, totalPages, onPageChange }) {
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