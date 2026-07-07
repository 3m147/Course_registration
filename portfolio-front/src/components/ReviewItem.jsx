import { useState } from 'react';

export default function ReviewItem({ review }) {
  const [expanded, setExpanded] = useState(false);

  const MAX_LENGTH = 120;

  const fullText = review.content;

  const shortText =
    fullText.length > MAX_LENGTH
      ? fullText.substring(0, MAX_LENGTH) + '...'
      : fullText;

  return (
    <div className="p-4 bg-white border rounded-lg shadow-sm">
      <div className="flex items-center justify-between">
        <span className="font-semibold text-gray-800">
          {review.member?.username}
        </span>
        <span className="text-yellow-500">⭐ {review.rating}</span>
      </div>

      <div className="flex justify-between mt-3">
        <div className="flex-1">
          <p className="text-gray-700 whitespace-pre-line">
            {expanded ? fullText : shortText}
          </p>

          {fullText.length > MAX_LENGTH && (
            <button
              className="text-blue-600 mt-2 text-sm hover:underline cursor-pointer"
              onClick={() => setExpanded(!expanded)}
            >
              {expanded ? '접기 ▲' : '펼치기 ▼'}
            </button>
          )}
        </div>

        {review.reviewImages && review.reviewImages.length > 0 && (
          <div className="ml-4 flex gap-2">
            {review.reviewImages.map((img, idx) => (
              <img
                key={idx}
                src={`data:${img.contentType};base64,${img.base64}`}
                alt="review"
                className="w-24 h-24 object-cover rounded-lg border"
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
