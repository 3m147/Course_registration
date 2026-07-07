import { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate, useParams } from 'react-router';
import LectureEditPage from './LectureEditPage';
import ReviewList from '../components/ReviewList';
import EnrollButton from '../components/EnrollButton';
import { apiUrl } from '../lib/api';

export default function LectureDetailPage() {
  const { lectureId } = useParams();
  const [lecture, setLecture] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const username = localStorage.getItem('username');
  const token = localStorage.getItem('token');
  const today = new Date();
  const [sevenDaysBefore, setSevenDaysBefore] = useState(null);
  const [threeDaysBefore, setThreeDaysBefore] = useState(null);
  const location = useLocation();
  const mode = location.state?.mode;
  const navigate = useNavigate();

  async function getLecture() {
    const url = apiUrl(`/lecture/${lectureId}`);
    try {
      const response = await fetch(url);
      if (!response.ok) throw new Error('강좌를 찾을 수 없습니다.');

      const data = await response.json();

      if (!data || data === 0) {
        setError('해당 강좌를 찾을 수 없습니다.');
        return;
      }
      console.log(data);
      setLecture(data || null);
    } catch (error) {
      setError(error.message);
    } finally {
      setLoading(true);
    }
  }

  function formatTime(dateTimeStr) {
    const date = new Date(dateTimeStr);

    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();

    const hours = date.getHours();
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${year}년 ${month}월 ${day}일 ${hours}시${minutes}분`;
  }

  function daysBefore(dateTimeStr) {
    const base = new Date(dateTimeStr);

    // 7일 전 23:59:59
    const seven = new Date(base);
    seven.setDate(seven.getDate() - 7);
    seven.setHours(23, 59, 59, 0);

    // 3일 전 23:59:59
    const three = new Date(base);
    three.setDate(three.getDate() - 3);
    three.setHours(23, 59, 59, 0);

    setSevenDaysBefore(seven);
    setThreeDaysBefore(three);
  }

  async function deleteLecture(lectureId) {
    const url = apiUrl(`/lecture/${lectureId}`);
    try {
      const response = await fetch(url, {
        method: 'DELETE',
        headers: token
          ? {
              Authorization: `Bearer ${token}`,
            }
          : {},
      });
      if (!response.ok) throw new Error('강좌를 삭제할 수 없습니다.');
      navigate('/');
      window.location.reload();
    } catch (error) {
      setError(error.message);
    }
  }

  function handleDelete() {
    const ok = confirm('강좌를 정말 삭제할까요?');
    if (!ok) return;
    deleteLecture(lecture.lectureId);
  }

  useEffect(() => {
    getLecture();
  }, [lectureId]);

  useEffect(() => {
    if (lecture?.lectureStartTime) {
      daysBefore(lecture.lectureStartTime);
    }
  }, [lecture]);

  if (!loading) {
    return <div>로딩중...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  if (mode === 'edit') {
    return <LectureEditPage lecture={lecture} />;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <main className="max-w-3xl mx-auto bg-white rounded-2xl border border-gray-200 p-8 mt-10">
        {/* 제목 */}
        <h1
          className={`text-3xl font-bold mb-2 ${
            lecture.lectureStatus === 'CAN' || lecture.lectureStatus === 'END'
              ? 'text-gray-600'
              : 'text-gray-900'
          }`}
        >
          {lecture.lectureName}
        </h1>

        <EnrollButton lectureId={lectureId} />
        
        {lecture.lectureStatus === 'CAN' && (
          <p className="text-sm text-red-500 mb-10">현재 취소된 강좌입니다.</p>
        )}

        {lecture.lectureStatus === 'END' && (
          <p className="text-sm text-gray-500 mb-10">현재 종료된 강좌입니다.</p>
        )}

        {/* 강사 / 종목 */}
        <section className="mb-10">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <p className="text-xs text-gray-500 mb-1">강사명</p>
              <Link
                to={`/player/${lecture.username}`}
                className="font-semibold"
              >
                <div className="w-full border border-gray-200 rounded-lg px-3 py-2 bg-gray-50 text-gray-800 hover:bg-blue-50 transition hover:border-blue-400">
                  {lecture.username}
                </div>
              </Link>
            </div>
            <div>
              <p className="text-xs text-gray-500 mb-1">종목</p>
              <div className="w-full border border-gray-200 rounded-lg px-3 py-2 bg-gray-50 text-gray-800">
                {lecture.mainName} &gt; {lecture.subName}
              </div>
            </div>
          </div>
        </section>

        {/* 강의 상세 정보 */}
        <section className="mb-10">
          <h2 className="text-lg font-semibold mb-4 text-blue-600">
            강의 상세 정보
          </h2>

          <div className="space-y-3">
            {/* 장소 */}
            <div className="flex items-center justify-between border border-blue-100 bg-blue-50 rounded-lg px-4 py-3">
              <span className="text-xs font-semibold text-blue-500">장소</span>
              <span className="text-gray-900 font-medium">
                {lecture.facilityName}
              </span>
            </div>

            {/* 일시 */}
            <div className="flex items-start justify-between border border-blue-100 bg-blue-50 rounded-lg px-4 py-3">
              <span className="text-xs font-semibold text-blue-500">일시</span>
              <span className="text-gray-900 font-medium text-right leading-tight">
                {formatTime(lecture.lectureStartTime)} -{' '}
                {formatTime(lecture.lectureEndTime)}
              </span>
            </div>

            {/* 모집 인원 */}
            <div className="flex items-center justify-between border border-blue-100 bg-blue-50 rounded-lg px-4 py-3">
              <span className="text-xs font-semibold text-blue-500">
                모집 인원
              </span>
              <span className="text-gray-900 font-medium">
                {lecture.minPeople}명 - {lecture.maxPeople}명
              </span>
            </div>
          </div>
        </section>

        {/* 강의 소개 */}
        <section className="mb-10">
          <h2 className="text-base font-semibold text-gray-800 mb-2">
            강의 소개
          </h2>
          <p className="text-sm text-gray-700 leading-relaxed">
            {lecture.lectureContent}
          </p>
        </section>

        {/* 이미지 영역 */}
        <div className="mb-10">
          {lecture.lecImgList && lecture.lecImgList.length > 0 ? (
            <div className="w-full px-4 py-4 flex flex-wrap gap-4">
              {lecture.lecImgList.map((img, i) => {
                const src = `data:${img.contentType};base64,${img.base64}`;
                return (
                  <img
                    key={img.lectureImgId ?? i}
                    src={src}
                    alt={img.originalName || `img-${i}`}
                    className="max-h-[500px] w-auto object-contain mx-auto"
                  />
                );
              })}
            </div>
          ) : (
            <div className="w-full px-4 py-8 text-center text-gray-400 text-sm"></div>
          )}
        </div>
        {/* 버튼 */}
        {lecture.username == username && lecture.lectureStatus === 'OPN' && (
          <div className="flex justify-end gap-3 mt-8">
            {threeDaysBefore && today < threeDaysBefore && (
              <button
                type="button"
                className="px-5 py-2 rounded-lg border border-blue-100 text-blue-600 bg-white hover:bg-blue-50 transition"
                onClick={() =>
                  navigate(`/lecture/${lecture.lectureId}`, {
                    state: {
                      mode: 'edit',
                      lecture: lecture,
                    },
                  })
                }
              >
                수정
              </button>
            )}
            {sevenDaysBefore && today < sevenDaysBefore && (
              <button
                className="px-5 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition"
                onClick={handleDelete}
              >
                삭제
              </button>
            )}
          </div>
        )}

        {/* 리뷰 리스트 */}
        <ReviewList lectureId={lectureId} />
      </main>
    </div>
  );
}
