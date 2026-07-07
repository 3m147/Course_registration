import axios from 'axios';
import { useEffect, useState, useCallback } from 'react';
import ReviewCard from './ReviewCard';
import ReviewWrite from './ReviewWrite';
import { apiUrl } from '../lib/api';

export default function ReviewList({ lectureId }) {
  const [reviews, setReviews] = useState([]);
  const [page, setPage] = useState(1);

  const size = 10; // 한 페이지 10개 표시
  const totalPage = Math.ceil(reviews.length / size);

  // 페이징 5개씩
  const groupSize = 5;
  const currentGroup = Math.floor((page - 1) / groupSize);
  const start = currentGroup * groupSize + 1;
  const end = Math.min(start + groupSize - 1, totalPage);

  const currentReviews = reviews.slice((page - 1) * size, page * size);

  const fetchReviews = useCallback(() => {
    axios
      .get(apiUrl(`/reviews/lecture/${lectureId}`))
      .then((res) => {
        // 백엔드에서 필터링이 안 될 경우를 대비해 프론트엔드에서 한 번 더 필터링
        const filteredReviews = res.data.filter(
          (review) => review.lectureId === Number(lectureId)
        );
        setReviews(filteredReviews);
      })
      .catch(() => alert('리뷰 불러오기 실패'));
  }, [lectureId]);

  useEffect(() => {
    fetchReviews();
  }, [fetchReviews]);

  const handleDelete = async (reviewId) => {
    try {
      const token = localStorage.getItem('token');
      await axios.delete(apiUrl(`/reviews/${reviewId}`), {
        headers: {
          ...(token && { Authorization: `Bearer ${token}` })
        }
      });
      alert('리뷰가 삭제되었습니다.');
      fetchReviews();
    } catch (err) {
      alert('리뷰 삭제 실패');
    }
  };

  const [WriteModalOpen, setWriteModalOpen] = useState(false);

  const handleWriteSubmit = async (formData) => {
    try {
      const token = localStorage.getItem('token');
      const username = localStorage.getItem('username');
      formData.append('lectureId', lectureId);
      if (username) {
        formData.append('username', username);
      }

      await axios.post(apiUrl('/reviews'), formData, {
        headers: {
          ...(token && { Authorization: `Bearer ${token}` }),
        },
        withCredentials: true,
      });
      alert('리뷰가 등록되었습니다.');
      setWriteModalOpen(false);
      fetchReviews();
    } catch (err) {
      console.error(err);
      alert('리뷰 등록 실패');
    }
  };

  return (
    <div className="w-full mt-12 pt-10 border-t border-gray-100">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-lg font-semibold text-gray-900">
          수강생 후기 <span className="text-blue-600">{reviews.length}</span>
        </h2>
        {localStorage.getItem('token') && (
          <button
            onClick={() => setWriteModalOpen(true)}
            className="px-4 py-2 cursor-pointer bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition"
          >
            리뷰 작성
          </button>
        )}
      </div>

      <div className="flex flex-col w-full gap-4">
        {currentReviews.length > 0 ? (
          currentReviews.map((r) => (
            <ReviewCard
              key={r.reviewId}
              review={r}
              onDelete={handleDelete}
              onUpdate={fetchReviews}
            />
          ))
        ) : (
          <div className="text-center py-10 text-gray-500 bg-gray-50 rounded-xl border border-gray-100">
            아직 작성된 리뷰가 없습니다.
          </div>
        )}
      </div>

      <div className="flex justify-center gap-2 mt-10">
        {start > 1 && (
          <button
            onClick={() => setPage(start - 1)}
            className="px-3 py-2 cursor-pointer bg-gray-200 rounded-full hover:bg-gray-300"
          >
            ◀
          </button>
        )}

        {Array.from({ length: end - start + 1 }, (_, i) => start + i).map(
          (num) => (
            <button
              key={num}
              onClick={() => setPage(num)}
              className={`px-4 py-2 rounded-full cursor-pointer ${
                page === num
                  ? 'bg-orange-500 text-white'
                  : 'bg-gray-200 text-gray-600 hover:bg-gray-300'
              }`}
            >
              {num}
            </button>
          )
        )}

        {end < totalPage && (
          <button
            onClick={() => setPage(end + 1)}
            className="px-3 py-2 cursor-pointer bg-gray-200 rounded-full hover:bg-gray-300"
          >
            ▶
          </button>
        )}
      </div>

      {/* 리뷰 작성 모달 */}
      {WriteModalOpen && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto relative shadow-2xl">
            <button
              onClick={() => setWriteModalOpen(false)}
              className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 text-2xl"
            >
              ✕
            </button>
            <ReviewWrite onSubmit={handleWriteSubmit} />
          </div>
        </div>
      )}
    </div>
  );
}
