import React, { useState } from 'react';
// import '../styles/calendar.scss';

function Calendar({ selectedDate, onDateChange }) {
    const [displayDate, setDisplayDate] = useState(selectedDate);
    
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();

    const firstDayOfMonth = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const today = new Date();

    const goToPrevMonth = () => setCurrentDate(new Date(year, month - 1, 1));
    const goToNextMonth = () => setCurrentDate(new Date(year, month + 1, 1));

    const handleDateClick = (day) => {
        onDateChange(new Date(year, month, day));
    };

    const renderCalendarBody = () => {
        const allCells = [];
        for (let i = 0; i < firstDayOfMonth; i++) {
            allCells.push(<td key={`empty-${i}`} className="calendar-day empty"></td>);
        }
        for (let day = 1; day <= daysInMonth; day++) {
            const isToday = day === today.getDate() && month === today.getMonth() && year === today.getFullYear();
            const isSelected = day === selectedDate.getDate() && month === selectedDate.getMonth() && year === selectedDate.getFullYear();
            
            const dayClasses = `calendar-day ${isToday ? 'today' : ''} ${isSelected ? 'selected' : ''}`;

            allCells.push(
                <td key={day} className={dayClasses}>
                    {/* [핵심] 날짜를 키보드로 선택하고 스크린 리더가 읽을 수 있도록 <button>으로 감쌉니다. */}
                    <button onClick={() => handleDateClick(day)}>
                        {day}
                    </button>
                </td>
            );
        }

        const rows = [];
        let cells = [];
        allCells.forEach((cell, i) => {
            cells.push(cell);
            if ((i + 1) % 7 === 0 || i === allCells.length - 1) {
                rows.push(<tr key={i}>{cells}</tr>);
                cells = [];
            }
        });
        return <tbody>{rows}</tbody>;
    };

    return (
        <div className="calendar-container">
            <div className="calendar-header">
                <button onClick={goToPrevMonth} aria-label="이전 달로 이동">◀</button>
                <h2 aria-live="polite">{year}년 {month + 1}월</h2>
                <button onClick={goToNextMonth} aria-label="다음 달로 이동">▶</button>
            </div>
            <table className="calendar-table" role="grid">
                <thead>
                    <tr>
                        <th scope="col" abbr="Sunday">일</th>
                        <th scope="col" abbr="Monday">월</th>
                        <th scope="col" abbr="Tuesday">화</th>
                        <th scope="col" abbr="Wednesday">수</th>
                        <th scope="col" abbr="Thursday">목</th>
                        <th scope="col" abbr="Friday">금</th>
                        <th scope="col" abbr="Saturday">토</th>
                    </tr>
                </thead>
                {renderCalendarBody()}
            </table>
        </div>
    );
}

export default Calendar;
