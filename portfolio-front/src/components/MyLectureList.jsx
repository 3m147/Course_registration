import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router';
import { apiUrl } from '../lib/api';

const statusLabels = {
  OPN: '모집 중',
  ACT: '확정',
  CAN: '취소',
  END: '종료',
};

function formatDateTime(dateTime) {
  if (!dateTime) return '';

  const date = new Date(dateTime);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');

  return `${year}.${month}.${day} ${hours}:${minutes}`;
}

export default function MyLectureList() {
  const username = localStorage.getItem('username');
  const token = localStorage.getItem('token');
  const [lectures, setLectures] = useState([]);
  const [loading, setLoading] = useState(true);
  const [cancellingId, setCancellingId] = useState(null);
  const [deletingId, setDeletingId] = useState(null);
  const [error, setError] = useState('');

  const loadLectures = useCallback(async () => {
    if (!username) {
      setError('로그인이 필요합니다.');
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError('');

      const response = await fetch(
        apiUrl(`/player/lectures/${username}?page=1`)
      );

      if (!response.ok) {
        throw new Error('내 강좌 목록을 불러올 수 없습니다.');
      }

      const data = await response.json();
      setLectures(data.dtoList || []);
    } catch (loadError) {
      setError(loadError.message);
    } finally {
      setLoading(false);
    }
  }, [username]);

  async function cancelLecture(lecture) {
    if (!window.confirm(`"${lecture.lectureName}" 강좌를 취소할까요?`)) {
      return;
    }

    try {
      setCancellingId(lecture.lectureId);
      setError('');

      const response = await fetch(
        apiUrl(`/lecture/${lecture.lectureId}/cancel`),
        {
          method: 'PATCH',
          headers: token
            ? {
                Authorization: `Bearer ${token}`,
              }
            : {},
        }
      );

      if (!response.ok) {
        const message = await response.text().catch(() => '');
        throw new Error(message || '강좌를 취소할 수 없습니다.');
      }

      await loadLectures();
    } catch (cancelError) {
      setError(cancelError.message);
    } finally {
      setCancellingId(null);
    }
  }

  async function deleteLecture(lecture) {
    if (
      !window.confirm(
        `"${lecture.lectureName}" 강좌와 관련 데이터를 모두 삭제할까요?`
      )
    ) {
      return;
    }

    try {
      setDeletingId(lecture.lectureId);
      setError('');

      const response = await fetch(apiUrl(`/lecture/${lecture.lectureId}`), {
        method: 'DELETE',
        headers: token
          ? {
              Authorization: `Bearer ${token}`,
            }
          : {},
      });

      if (!response.ok) {
        const message = await response.text().catch(() => '');
        throw new Error(message || '강좌를 삭제할 수 없습니다.');
      }

      await loadLectures();
    } catch (deleteError) {
      setError(deleteError.message);
    } finally {
      setDeletingId(null);
    }
  }

  useEffect(() => {
    loadLectures();
  }, [loadLectures]);

  if (loading) {
    return <p className="text-sm text-gray-500">내 강좌를 불러오는 중입니다.</p>;
  }

  return (
    <section>
      {error && <p className="mb-4 text-sm text-red-500">{error}</p>}

      {lectures.length === 0 ? (
        <p className="text-sm text-gray-500">등록한 강좌가 없습니다.</p>
      ) : (
        <div className="overflow-hidden border border-gray-200 rounded-lg">
          <div className="grid grid-cols-[minmax(0,1fr)_150px_84px_196px] gap-3 border-b border-gray-200 bg-gray-50 px-4 py-3 text-xs font-semibold text-gray-500">
            <span>강좌</span>
            <span>시작 시간</span>
            <span>상태</span>
            <span className="text-right">관리</span>
          </div>

          {lectures.map((lecture) => (
            <div
              key={lecture.lectureId}
              className="grid grid-cols-[minmax(0,1fr)_150px_84px_196px] items-center gap-3 border-b border-gray-100 px-4 py-3 last:border-b-0"
            >
              <div className="min-w-0">
                <Link
                  to={`/lecture/${lecture.lectureId}`}
                  className="block truncate font-medium text-gray-900 hover:text-blue-600"
                >
                  {lecture.lectureName}
                </Link>
                <p className="truncate text-xs text-gray-500">
                  {lecture.facilityName}
                </p>
              </div>

              <span className="text-sm text-gray-600">
                {formatDateTime(lecture.lectureStartTime)}
              </span>

              <span className="text-sm text-gray-600">
                {statusLabels[lecture.lectureStatus] || lecture.lectureStatus}
              </span>

              <div className="flex justify-end gap-2">
                <Link
                  to={`/lecture/${lecture.lectureId}`}
                  className="rounded-md border border-gray-200 px-3 py-1.5 text-sm text-gray-700 hover:bg-gray-50"
                >
                  보기
                </Link>
                <button
                  type="button"
                  disabled={
                    lecture.lectureStatus === 'CAN' ||
                    lecture.lectureStatus === 'END' ||
                    cancellingId === lecture.lectureId ||
                    deletingId === lecture.lectureId
                  }
                  onClick={() => cancelLecture(lecture)}
                  className="rounded-md border border-orange-200 px-3 py-1.5 text-sm text-orange-700 hover:bg-orange-50 disabled:cursor-not-allowed disabled:border-gray-200 disabled:text-gray-400"
                >
                  {cancellingId === lecture.lectureId ? '취소 중' : '취소'}
                </button>
                <button
                  type="button"
                  disabled={
                    cancellingId === lecture.lectureId ||
                    deletingId === lecture.lectureId
                  }
                  onClick={() => deleteLecture(lecture)}
                  className="rounded-md bg-red-500 px-3 py-1.5 text-sm text-white hover:bg-red-600 disabled:cursor-not-allowed disabled:bg-gray-300"
                >
                  {deletingId === lecture.lectureId ? '삭제 중' : '삭제'}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
