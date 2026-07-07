import { useRef, useState } from 'react';
import SearchFacility from '../components/SearchFacility';
import DayCheck from '../components/DayCheck';
import { useNavigate } from 'react-router';
import LectureImage from '../components/LectureImage';
import { apiUrl } from '../lib/api';

export default function LectureWritePage() {
  const playerName = localStorage.getItem('username');
  const mainName = localStorage.getItem('mainName');
  const subName = localStorage.getItem('subName');
  const [lectureTitle, setLectureTitle] = useState('');
  const [lectureContent, setLectureContent] = useState('');
  const [minPeople, setMinPeople] = useState(1);
  const [maxPeople, setMaxPeople] = useState('');
  const navigate = useNavigate();
  const [facilityName, setFacilityName] = useState('');
  const [coordsX, setCoordsX] = useState('');
  const [coordsY, setCoordsY] = useState('');
  const contentRef = useRef(null);
  const contentError = '을 확인해주세요.';
  const [error, setError] = useState('');
  const token = localStorage.getItem('token');
  const [imageList, setImageList] = useState({ images: [], mainIndex: 0 });

  const peopleCntChange = (id, value) => {
    if (id === 'minPeople') {
      if (maxPeople && Number(value) > Number(maxPeople)) {
        alert('최소인원 최대인원보다 많을 수 없습니다.');
        setMinPeople(Number(maxPeople));
        return;
      }
      setMinPeople(Number(value));
    } else if (id === 'maxPeople') {
      if (minPeople && Number(value) < Number(minPeople)) {
        alert('최대인원은 최소인원보다 적을 수 없습니다.');
        setMaxPeople(Number(minPeople));
        return;
      }
      setMaxPeople(Number(value));
    }
  };
  const now = new Date();

  // helper function: Date → YYYY-MM-DDTHH:MM
  const formatDateTimeLocal = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${year}-${month}-${day}T${hours}:${minutes}`;
  };
  const [lectureStartTime, setLectureStartTime] = useState(
    formatDateTimeLocal(now)
  );
  const [lectureEndTime, setLectureEndTime] = useState('');

  const min = formatDateTimeLocal(now);

  const dateChange = (id, value) => {
    if (id === 'lectureStartTime') {
      if (lectureEndTime && value > lectureEndTime) {
        alert('시작 시간은 종료 시간 이후일 수 없습니다.');
        setLectureStartTime(lectureEndTime);
        return;
      }
      setLectureStartTime(value);
    } else if (id === 'lectureEndTime') {
      if (lectureStartTime && value < lectureStartTime) {
        alert('종료 시간은 시작 시간 이전일 수 없습니다.');
        setLectureEndTime(lectureStartTime);
        return;
      }
      setLectureEndTime(value);
    }
  };

  const saveLecture = async () => {
    if (!lectureTitle || lectureTitle.trim() === '') {
      alert('제목' + contentError);
      setTimeout(() => contentRef.current?.focus(), 0);
      return;
    }

    if (!facilityName || facilityName.trim() === '') {
      alert('시설명' + contentError);
      setTimeout(() => contentRef.current?.focus(), 0);
      return;
    }

    if (!lectureEndTime || lectureEndTime === '') {
      alert('종료시간' + contentError);
      setTimeout(() => contentRef.current?.focus(), 0);
      return;
    }

    if (!maxPeople) {
      alert('최대인원' + contentError);
      setTimeout(() => contentRef.current?.focus(), 0);
      return;
    }

    if (!token) {
      alert('로그인이 필요합니다.');
      return;
    }
    const lectureDTO = {
      username: playerName,
      subName,
      lectureName: lectureTitle,
      lectureContent,
      facilityName,
      coordsX,
      coordsY,
      lectureStartTime,
      lectureEndTime,
      maxPeople,
      minPeople,
      images: imageList.images,
      mainIndex: imageList.mainIndex,
    };
    console.log(lectureDTO);
    try {
      const response = await fetch(apiUrl('/lecture'), {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(lectureDTO),
      });

      if (!response.ok) {
        const msg = await response.text().catch(() => null);
        throw new Error(msg || '강좌를 등록할 수 없습니다.');
      }
      navigate('/');
    } catch (error) {
      setError(error.message);
    }
  };

  return (
    <div>
      <main className="max-w-3xl mx-auto bg-white shadow-md rounded-lg p-8 mt-10">
        <h1 className="text-2xl font-bold mb-6 text-gray-800">
          강좌 개설 페이지
        </h1>

        <section className="mb-8">
          <div className="flex gap-4 mb-4">
            <div className="flex-1">
              <input
                type="text"
                className="border border-gray-300 rounded-lg p-2 w-full focus:border-blue-500 focus:outline-none"
                // placeholder="강사 : "
                value={playerName}
                disabled
              />
            </div>
            <div className="flex-1">
              <input
                type="text"
                className="border border-gray-300 rounded-lg p-2 w-full focus:border-blue-500 focus:outline-none"
                // placeholder="종목 : "
                value={mainName + ' > ' + subName}
                disabled
              />
            </div>
          </div>

          <input
            type="text"
            className="w-full border border-gray-300 rounded-lg p-2 mb-4 focus:border-blue-500 focus:outline-none"
            placeholder="강의명을 입력하세요"
            ref={contentRef}
            value={lectureTitle}
            onChange={(e) => setLectureTitle(e.target.value)}
          />

          <textarea
            className="w-full border border-gray-300 rounded-lg p-2 focus:border-blue-500 focus:outline-none"
            placeholder="강의 내용을 입력하세요"
            value={lectureContent}
            onChange={(e) => setLectureContent(e.target.value)}
          />
        </section>

        {/* 강의 정보 */}
        <section className="mb-8">
          <h2 className="text-lg font-semibold mb-3 text-gray-700">
            강의 필수 정보
          </h2>
          <SearchFacility
            ref={contentRef}
            handleFacility={(item) => {
              setFacilityName(item.name);
              setCoordsY(item.lat);
              setCoordsX(item.lot);
            }}
          />
          <span>{facilityName}에서 수업하기 </span>
          <div className="grid grid-cols-2 gap-4">
            <DayCheck
              ref={contentRef}
              lectureStartTime={lectureStartTime}
              lectureEndTime={lectureEndTime}
              min={min}
              onChange={dateChange}
            />
            <span>최소인원</span>
            <input
              type="number"
              placeholder="최소 인원"
              className="border border-gray-300 rounded-lg p-2 focus:border-blue-500 focus:outline-none text-center"
              id="minPeople"
              value={minPeople}
              min={1}
              onChange={(e) => peopleCntChange('minPeople', e.target.value)}
            />
            <span>최대인원</span>
            <input
              type="number"
              placeholder="최대 인원"
              className="border border-gray-300 rounded-lg p-2 focus:border-blue-500 focus:outline-none text-center"
              id="maxPeople"
              value={maxPeople}
              min={1}
              ref={contentRef}
              onChange={(e) => peopleCntChange('maxPeople', e.target.value)}
            />
          </div>
        </section>

        {/* 파일 업로드 */}
        <LectureImage onChange={setImageList} />

        {/* 버튼 */}
        <div className="flex justify-end gap-4 mt-6">
          <button
            type="button"
            className="px-5 py-2 bg-gray-200 rounded-lg hover:bg-gray-300"
            onClick={() => {
              if (
                window.confirm(
                  '작성 중인 내용이 저장되지 않습니다. 목록으로 이동할까요?'
                )
              ) {
                navigate('/');
              }
            }}
          >
            취소
          </button>

          <button
            onClick={() => saveLecture()}
            className="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            등록
          </button>
        </div>
      </main>
    </div>
  );
}
