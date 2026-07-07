import { Link, useNavigate } from 'react-router';
import defaultPng from '../assets/images/lectureDefault.png';

export default function LectureItem({ lectureInfo }) {
  let lectureName = '강좌 이름';
  let playerName = '선수 이름';
  const navigate = useNavigate();
  const imgObj = lectureInfo?.lecImgList?.[0];

  const lecMainImg = imgObj?.base64
    ? `data:${imgObj.contentType};base64,${imgObj.base64}`
    : defaultPng;

  if (lectureInfo !== undefined && lectureInfo !== null) {
    lectureName = lectureInfo.lectureName;
    playerName = lectureInfo.username;
  }
  console.log('lecture', lectureInfo);
  return (
    <>
      <div className="w-full rounded-lg overflow-hidden shadow-lg bg-white transition-transform duration-300 hover:scale-105">
        <Link to={`/lecture/${lectureInfo.lectureId}`} className="block h-full">
          <div className="w-full aspect-square overflow-hidden bg-gray-100 relative">
            {lectureInfo.lectureStatus === 'ACT' ||
              ('OPN' && (
                <div className="absolute top-2 left-2 flex gap-1">
                  {lectureInfo.mainName && lectureInfo.subName && (
                    <span className="bg-black/30 text-white text-xs font-semibold px-2 py-1 rounded-md">
                      {lectureInfo.mainName} &gt; {lectureInfo.subName}
                    </span>
                  )}
                </div>
              ))}
            {/* 이미지 */}
            <img
              alt="대표 이미지"
              className={`w-full h-full object-cover transition duration-300 ${
                lectureInfo.lectureStatus === 'CAN' ||
                lectureInfo.lectureStatus === 'END' ||
                lectureInfo.lectureStatus === 'ACT'
                  ? 'brightness-50'
                  : ''
              }`}
              src={lecMainImg}
            />

            {/* 오버레이 텍스트 */}
            {(lectureInfo.lectureStatus === 'CAN' ||
              lectureInfo.lectureStatus === 'END' ||
              lectureInfo.lectureStatus === 'ACT') && (
              <div className="absolute inset-0 flex items-center justify-center">
                <span
                  className="text-white text-5xl font-bold tracking-widest"
                  style={{
                    transform: 'rotate(-35deg)',
                    whiteSpace: 'nowrap',
                  }}
                >
                  {lectureInfo.lectureStatus === 'CAN'
                    ? 'CANCEL'
                    : lectureInfo.lectureStatus === 'END'
                      ? 'ENDED'
                      : 'ACTIVE'}
                </span>
              </div>
            )}
          </div>
          <div className="p-4">
            {' '}
            <div className="text-lg font-semibold text-gray-800">
              {' '}
              {lectureName}{' '}
              <span className="text-gray-700 text-xs">
                {lectureInfo.facilityName}
              </span>
            </div>
            <p className="text-sm text-gray-500 mt-2">
              <span
                className="text-gray-700 hover:underline cursor-pointer"
                onClick={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  navigate(`/player/${playerName}`);
                }}
              >
                {playerName}
              </span>
            </p>
            <div className="flex items-center justify-between mt-4">
              {' '}
              <button className="bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600 transition">
                {' '}
                상세보기{' '}
              </button>
            </div>
          </div>
        </Link>
      </div>
    </>
  );
  0;
}
