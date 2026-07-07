import { useState } from 'react';
import ReviewEdit from './ReviewEdit';

function formatRelativeDate(dateString) {
  const date = new Date(dateString);
  const now = new Date();

  const diff = now - date;
  const sec = Math.floor(diff / 1000);
  const min = Math.floor(sec / 60);
  const hour = Math.floor(min / 60);
  const day = Math.floor(hour / 24);
  const month = Math.floor(day / 30);
  const year = Math.floor(day / 365);

  if (sec < 60) return '방금 전';
  if (min < 60) return `${min}분 전`;
  if (hour < 24) return `${hour}시간 전`;
  if (day < 30) return `${day}일 전`;
  if (month < 12) return `${month}개월 전`;
  return `${year}년 전`;
}

function maskUsername(username) {
  if (!username) return '익명';
  if (username.length <= 2) return username;
  return username.substring(0, 2) + '*'.repeat(username.length - 2);
}

export default function ReviewCard({ review, onDelete, onUpdate }) {
  const [expanded, setExpanded] = useState(false);
  const [isEditing, setIsEditing] = useState(false);

  const currentUser = localStorage.getItem('username');

  // review.member.username 또는 review.username 둘 다 확인
  const reviewUsername = review.member?.username || review.username;
  
  const isOwner = currentUser === reviewUsername;

  const MAX = 120;
  const full = review.content || '';
  const long = full.length > MAX;
  const short = long ? full.substring(0, MAX) + '...' : full;
  const createdTime = review?.createdAt
    ? formatRelativeDate(review.createdAt)
    : '';
  
  const hasImages = review.reviewImages && review.reviewImages.length > 0;
  const showExpandButton = long || hasImages;

  const handleDelete = () => {
    if (window.confirm('정말로 삭제하시겠습니까?')) {
      onDelete(review.reviewId);
    }
  };

  const handleUpdateSuccess = () => {
    setIsEditing(false);
    if (onUpdate) onUpdate();
  };

  const handleImageClick = (img) => {
    const newWindow = window.open('', '_blank');
    if (newWindow) {
      newWindow.document.write(`
        <html>
          <head><title>Image View</title></head>
          <body style="margin:0; background:black; display:flex; align-items:center; justify-content:center; height:100vh;">
            <img src="data:${img.contentType};base64,${img.base64}" style="max-width:100%; max-height:100%;" />
          </body>
        </html>
      `);
      newWindow.document.close();
    }
  };

  if (isEditing) {
    return (
      <div className="w-full bg-white p-4 border rounded-xl shadow-sm">
        <ReviewEdit
          reviewId={review.reviewId}
          onSuccess={handleUpdateSuccess}
          onCancel={() => setIsEditing(false)}
        />
      </div>
    );
  }

  return (
    <div className="flex justify-between items-start bg-white px-6 py-5 border border-gray-100 rounded-xl w-full hover:border-blue-100 transition-colors shadow-sm">
      <div className="flex-1 pr-6 min-w-0">
        <div className="flex items-center gap-2 mb-2">
          <div className="text-yellow-400 text-sm">
            {'★'.repeat(review.rating)}
            {'☆'.repeat(5 - review.rating)}
          </div>
          <span className="text-xs text-gray-400">{createdTime}</span>
        </div>

        <p className="font-medium text-gray-900 mb-2 text-sm">
          {isOwner ? (reviewUsername || '익명') : maskUsername(reviewUsername)}
        </p>

        <p className="text-gray-600 text-sm leading-relaxed whitespace-pre-line break-words">
          {expanded ? full : short}
        </p>

        {showExpandButton && (
          <button
            onClick={() => setExpanded((p) => !p)}
            className="text-blue-500 text-xs mt-2 cursor-pointer hover:underline font-medium"
          >
            {expanded ? '접기 ▲' : '펼치기 ▼'}
          </button>
        )}

        {/* 펼쳐졌을 때 이미지 리스트 표시 */}
        {expanded && hasImages && (
          <div className="mt-4 flex flex-row flex-wrap gap-4">
            {review.reviewImages.map((img, idx) => (
              <img
                key={idx}
                src={`data:${img.contentType};base64,${img.base64}`}
                alt={`review-detail-${idx}`}
                className="w-[100px] h-[100px] object-cover rounded-lg border border-gray-200 cursor-pointer hover:opacity-90"
                onClick={() => handleImageClick(img)}
              />
            ))}
          </div>
        )}
      </div>

      <div className="flex flex-col items-end gap-3 flex-shrink-0 ml-4">
        {isOwner && (
          <div className="flex gap-2 text-xs">
            <button
              onClick={() => setIsEditing(true)}
              className="text-gray-400 hover:text-blue-600 transition cursor-pointer"
            >
              수정
            </button>
            <button
              onClick={handleDelete}
              className="text-gray-400 hover:text-red-500 transition cursor-pointer"
            >
              삭제
            </button>
          </div>
        )}

        {/* 접혀있을 때만 우측 썸네일 표시 */}
        {!expanded && hasImages && (
          <div className="relative cursor-pointer" onClick={() => setExpanded(true)}>
            <img
              src={`data:${review.reviewImages[0].contentType};base64,${review.reviewImages[0].base64}`}
              alt="review-thumbnail"
              className="w-20 h-20 object-cover rounded-lg border border-gray-200 hover:opacity-90 transition"
            />
            {review.reviewImages.length > 1 && (
              <div className="absolute inset-0 bg-black/40 flex items-center justify-center rounded-lg text-white text-xs font-medium">
                +{review.reviewImages.length - 1}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
