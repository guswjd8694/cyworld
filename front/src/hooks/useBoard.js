import { useState, useEffect, useCallback } from 'react';
import apiClient from '../api/axiosConfig';

export function useBoard(userId, boardType) {
    const [pageData, setPageData] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [refetchTrigger, setRefetchTrigger] = useState(0);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    const [isEditing, setIsEditing] = useState(false);
    const [editingPost, setEditingPost] = useState(null);

    const fetchData = useCallback(async (pageNum) => {
        if (!userId) return;
        setLoading(true);
        setError(null);
        try {
            const response = await apiClient.get(`/users/${userId}/boards`, {
                params: { 
                    type: boardType, 
                    page: pageNum,
                    size: 5,
                    sort: 'createdAt,desc' 
                }
            });
            setPageData(response.data);
        } catch (err) {
            console.error(`${boardType} 데이터를 불러오는 데 실패했습니다.`, err);
            setError("데이터를 불러오는 중 오류가 발생했습니다.");
        } finally {
            setLoading(false);
        }
    }, [userId, boardType]);

    useEffect(() => {
        fetchData(currentPage);
    }, [userId, currentPage, refetchTrigger, fetchData]);
    
    const triggerRefetch = () => {
        if (currentPage === 0) {
            setRefetchTrigger(prev => prev + 1);
        } else {
            setCurrentPage(0);
        }
    };

    const handleSavePost = async (postData) => {
        try {
            if (postData.boardId) {
                await apiClient.put(`/boards/${postData.boardId}`, postData);
            } else {
                await apiClient.post(`/users/${userId}/boards`, { ...postData, type: boardType });
            }
            setIsEditing(false);
            setEditingPost(null);
            triggerRefetch(); // 저장 후 새로고침
        } catch (err) {
            if (err.response?.status !== 401) {
                alert(err.response?.data?.message || '저장에 실패했습니다.');
            }
        }
    };

    const handleDelete = async (boardId) => {
        if (window.confirm('정말로 삭제하시겠습니까?')) {
            try {
                await apiClient.delete(`/boards/${boardId}`);
                triggerRefetch(); // 삭제 후 새로고침
            } catch (err) {
                if (err.response?.status !== 401) {
                    alert(err.response?.data?.message || '삭제에 실패했습니다.');
                }
            }
        }
    };

    const handleEditClick = (post) => {
        setEditingPost(post);
        setIsEditing(true);
    };
    
    const handleWriteClick = () => {
        setEditingPost(null);
        setIsEditing(true);
    };

    const handleCancelEdit = () => {
        setIsEditing(false);
        setEditingPost(null);
    };

    return {
        posts: pageData ? pageData.content : [],
        loading,
        error,
        isEditing,
        editingPost,
        currentPage,
        totalPages: pageData ? pageData.totalPages : 0,
        handlePageChange: setCurrentPage,
        handleSavePost,
        handleDelete,
        handleEditClick,
        handleWriteClick,
        handleCancelEdit
    };
}