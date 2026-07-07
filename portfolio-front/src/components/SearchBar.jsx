import { apiUrl } from '../lib/api';
// 검색바.

import axios from 'axios';
import { useContext, useRef, useState } from 'react';
import { useLectureList } from '../context/LectureListContext';

// 위치기반 검색
// 종목별 강좌 검색
// 날짜검색
// 선수별 검색

// 해야 하는것: 어딘가에 실려 있을 (LocalStorage / SessionStorage / HTTP Only Cookie) JWT를 헤더에 함께 담아서 전송하기.

export default function SearchBar() {
  const [open, setOpen] = useState(false);
  const { setPage } = useLectureList();

  function keyFilter(e) {
    if (e.code === 'Enter') {
      search(e.target.value);
    }
  }

  function search(q) {
    let qString = '?';
    if (q.length > 0) {
      qString += `q=${q}&`;
    }
    let x = GeolocationCoordinates.longitude;
    let y = GeolocationCoordinates.latitude;
    if (x instanceof Number && y instanceof Number) {
      qString += `x=${x}&y=${y}&`;
    }

    let rangeStart = startRef.current.value;
    let rangeEnd = endRef.current.value;
    qString += `start=${rangeStart}&`;
    qString += `end=${rangeEnd}&`;

    let playerName = playerNameRef.current.value;
    if (playerName !== null && playerName.length > 0) {
      qString += `playername=${playerName}`;
    }

    let title = titleRef.current.value;
    if (title !== null && title.length > 0) {
      qString += `title=${title}`;
    }

    let building = buildingRef.current.value;
    if (building !== null && building.length > 0) {
      qString += `loc=${building}`;
    }

    const header = {
      Authorization: localStorage.getItem('jwt'),
    };

    qString = qString.slice(0, -1);
    console.log(qString);
    const url = apiUrl(`/lecture${qString}`);
    axios.get(url, { headers: header }).then((data) => {
      console.log(data.data);
      setPage(data.data);
    });
  }

  let dateNow = new Date()
    .toLocaleDateString('ko-KR')
    .replaceAll('. ', '-')
    .replace('.', '');
  let dateEnd = new Date(Date.now() + 1000 * 60 * 60 * 24 * 30)
    .toLocaleDateString('ko-KR')
    .replaceAll('. ', '-')
    .replace('.', ''); // 지금으로부터 정확히 한달 뒤

  const startRef = useRef(dateNow);
  const endRef = useRef(dateEnd);
  const playerNameRef = useRef(null);
  const titleRef = useRef(null);
  const buildingRef = useRef(null);

  function checkDateRange() {
    let rangeStart = startRef.current.value;
    let rangeEnd = endRef.current.value;
    if (Date.parse(rangeStart) > Date.parse(rangeEnd)) {
      startRef.current.value = rangeEnd;
      endRef.current.value = rangeStart;
    }
  }

  return (
    <>
      <div className="relative inline-block">
        {/* 날짜검색 및 상세검색을 넣기 위한 드롭다운 */}
        <div
          className="inline-flex justify-center mx-1 rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900"
          onClick={() => setOpen(!open)}
        >
          상세 검색...
        </div>
        <div
          className={
            (open ? 'block' : 'hidden') +
            ' absolute border border-gray-300 rounded-lg p-2 bg-white m-2 w-xl right-10'
          }
          style={{ zIndex: 2000000000 }}
        >
          <div>
            <label>
              강좌 시작일
              <input
                ref={startRef}
                type="date"
                className="m-1 border border-gray-300 rounded-lg p-2 focus:border-blue-500 focus:outline-none"
                id="lectureStartTime"
                defaultValue={dateNow}
                onBlur={() => {
                  checkDateRange();
                }}
                onKeyUp={(e) => keyFilter(e)}
              />
              부터
            </label>
            <label>
              <input
                ref={endRef}
                type="date"
                className="border border-gray-300 rounded-lg p-2 focus:border-blue-500 focus:outline-none"
                id="lectureEndTime"
                defaultValue={dateEnd}
                onBlur={() => {
                  checkDateRange();
                }}
                onKeyUp={(e) => keyFilter(e)}
              />
              까지
            </label>
          </div>
          <div>
            <label>
              선수 이름
              <input
                ref={playerNameRef}
                className="border border-gray-300 rounded-lg p-2 focus:border-blue-500 focus:outline-none"
                type="text"
                onKeyUp={(e) => keyFilter(e)}
              />
            </label>
          </div>
          <div>
            <label>
              강좌 제목
              <input
                ref={titleRef}
                className="border border-gray-300 rounded-lg p-2 focus:border-blue-500 focus:outline-none"
                type="text"
                onKeyUp={(e) => keyFilter(e)}
              />
            </label>
          </div>
          <div>
            <label>
              건물 이름
              <input
                ref={buildingRef}
                className="border border-gray-300 rounded-lg p-2 focus:border-blue-500 focus:outline-none"
                type="text"
                onKeyUp={(e) => keyFilter(e)}
              />
            </label>
          </div>
          {/* 여기에 카카오맵을 붙인다고?????? */}
        </div>
        <input
          className="inline-flex justify-center mx-1 rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-xs inset-ring-1 inset-ring-gray-300 hover:bg-gray-50"
          type="text"
          placeholder="검색"
          onKeyUp={(e) => keyFilter(e)}
        ></input>
      </div>
    </>
  );
}
