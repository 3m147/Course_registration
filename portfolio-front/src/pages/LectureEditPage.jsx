import { useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router';
import LectureImage from '../components/LectureImage';
import { apiUrl } from '../lib/api';

export default function LectureEditPage() {
  const { state } = useLocation();
  const lecture = state.lecture;
  const token = localStorage.getItem('token');

  const playerName = localStorage.getItem('username');
  const mainName = localStorage.getItem('mainName');
  const subName = localStorage.getItem('subName');

  const [lectureTitle, setLectureTitle] = useState(lecture.lectureName);
  const [lectureContent, setLectureContent] = useState(lecture.lectureContent);
  const facilityName = lecture.facilityName;
  const coordsX = lecture.coordsX;
  const coordsY = lecture.coordsY;
  const lectureStartTime = lecture.lectureStartTime;
  const lectureEndTime = lecture.lectureEndTime;
  const [minPeople, setMinPeople] = useState(lecture.minPeople);
  const [maxPeople, setMaxPeople] = useState(lecture.maxPeople);
  const lectureId = lecture.lectureId;
  const initialImages = (lecture.lecImgList || []).map(
    (img) => `data:${img.contentType};base64,${img.base64}`
  );

  const [imageList, setImageList] = useState(() => ({
    images: initialImages,
    mainIndex: 0,
  }));

  const navigate = useNavigate();

  const contentRef = useRef(null);
  const contentError = '을 확인해주세요.';
  const [error, setError] = useState('');

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
      lectureId,
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
    console.log('보내는거', lectureDTO);
    try {
      const response = await fetch(
        apiUrl(`/lecture/${lecture.lectureId}`),
        {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(lectureDTO),
        }
      );

      if (!response.ok) {
        const msg = await response.text().catch(() => null);
        throw new Error(msg || '강좌를 등록할 수 없습니다.');
      }
      navigate(`/lecture/${lecture.lectureId}`);
      window.location.reload();
    } catch (error) {
      setError(error.message);
    }
  };

  // async function sendLecture(lectureItem) {
  //   console.log(lectureItem);
  //   const url = apiUrl(`/lecture/${lectureItem.lectureId}`);
  //   try {
  //     const response = await fetch(url, {
  //       method: 'PUT',
  //       headers: { 'Content-Type': 'application/json' },
  //       body: JSON.stringify(lectureItem),
  //     });
  //     if (!response.ok) throw new Error('강좌를 수정할 수 없습니다.');
  //     navigate(`/lecture/{lectureItem.letureId}`);
  //   } catch (error) {
  //     setError(error.message);
  //   }
  // }

  return (
    <div>
      <main className="max-w-3xl mx-auto bg-white shadow-md rounded-lg p-8 mt-10">
        <h1 className="text-2xl font-bold text-gray-800">강좌 수정 페이지</h1>
        <p className="text-xs mb-6 mt-2 text-red-400">
          제목, 내용, 최소인원, 최대인원, 이미지 외 수정은 불가합니다.
          관리자에게 문의하세요{' '}
        </p>

        <section className="mb-8">
          <div className="flex gap-4 mb-4">
            <div className="flex-1">
              <input
                type="text"
                className="border border-gray-300 rounded-lg p-2 w-full text-gray-600"
                value={playerName}
                disabled
              />
            </div>
            <div className="flex-1">
              <input
                type="text"
                className="border border-gray-300 rounded-lg p-2 w-full text-gray-600"
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
          <div className="relative w-full mb-5">
            <input
              type="text"
              value={lecture.facilityName}
              className="w-full border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 text-gray-600"
              disabled
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <input
              type="datetime-local"
              value={lecture.lectureStartTime}
              className="border border-gray-300 rounded-lg p-2 text-gray-600"
              disabled
            />
            <input
              type="datetime-local"
              value={lecture.lectureEndTime}
              className="border border-gray-300 rounded-lg p-2 text-gray-600"
              disabled
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
        <LectureImage onChange={setImageList} initialImages={initialImages} />

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
            onClick={() =>
              saveLecture(
                lectureId,
                playerName,
                subName,
                lectureTitle,
                lectureContent,
                facilityName,
                lectureStartTime,
                lectureEndTime,
                maxPeople,
                minPeople,
                coordsX,
                coordsY
              )
            }
            className="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            저장
          </button>
        </div>
      </main>
    </div>
  );
}
