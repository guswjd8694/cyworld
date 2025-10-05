import React, { useState } from 'react';

function CommentForm({ onSubmit, isDisabled = false }) {
    const [content, setContent] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!content.trim() || isSubmitting || isDisabled) return;

        setIsSubmitting(true);
        try {
            await onSubmit(content);
            setContent(''); 
        } catch (error) {
            console.error("댓글 작성 실패:", error);
            alert("댓글 작성에 실패했습니다.");
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <form className={`comment-form ${isDisabled ? 'disabled' : ''}`} onSubmit={handleSubmit}>
            <textarea
                value={content}
                onChange={(e) => setContent(e.target.value)}
                aria-label="댓글 입력창"
                placeholder={isDisabled ? "로그인 후 댓글을 작성할 수 있습니다." : ""}
                disabled={isDisabled}
                required
            />
            <button type="submit" disabled={isSubmitting || isDisabled}>
                {isSubmitting ? '등록 중...' : '확인'}
            </button>
        </form>
    );
}

export default CommentForm;