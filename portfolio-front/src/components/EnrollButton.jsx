import { apiUrl } from '../lib/api';
// 수강신청 버튼
// 혹은, 이미 수강신청이 되어 있다면 수강취소로 변경한다
// 로그인한 유저의 정보 + lecture_enrollments 테이블의 정보를 사용해서 상태를 결정한다

import { useEffect, useState } from 'react';
import axios from 'axios';

export default function EnrollButton({ lectureId }) {
  const url = apiUrl(`/enroll/${lectureId}`);
  const [enrollStatus, setEnrollStatus] = useState('LOADING');

  useEffect(() => {
    // 로딩하자마자 서버에 접속, 번호를 가져온다.?

    getEnroll(url);
  }, []);

  const header = {
    Authorization: 'Bearer ' + localStorage.getItem('token'),
  };

  function enrollLecture(url) {
    setEnrollStatus('LOADING');
    axios
      .post(url, null, {
        headers: header,
      })
      .then(window.location.reload());
  }

  function withdrawLecture(url) {
    setEnrollStatus('LOADING');
    axios
      .delete(url, {
        headers: header,
      })
      .then(window.location.reload());
  }

  function getEnroll(url) {
    axios
      .get(url, {
        headers: header,
      })
      .then((resp) => {
        setEnrollStatus(resp.data.enrollStatus);
        console.log(resp.data.enrollStatus);
      });
  }

  // 만약 로그인한 유저의 id가 강좌신청 테이블에 없다면
  //   실시간 수강인원을 체크한다
  //   만약 남은 인원이 있다면
  //     수강신청 버튼을 보여준다
  //       그 버튼을 클릭하면 (POST) 강좌신청 테이블에 새로운 줄을 만들고 리로딩한다
  //   만약 남은 인원이 없다면
  //     회색처리한 "정원초과" 버튼을 보여준다
  // 만약 로그인한 유저의 id가 강좌신청 테이블에 있다면
  //   수강취소 버튼을 보여준다
  //     그 버튼을 클릭하면 (DELETE) 강좌신청 테이블에 있는 줄을 없애고 리로딩한다

  //   (OPEN, // 수강신청가능
  //     OVERCAPACITY, // 인원초과
  //     ENROLLED, // 수강취소가능
  //     ACTIVE, // 수강신청되어 있고 확정되어 취소 불가
  //     STARTED, // 시작되어 수강신청 불가
  //     FINISHED, // 종료되어 수강신청 불가
  //     CANCELLED); // 취소됨

  // 강좌 신청한 경우
  switch (enrollStatus) {
    case 'OPEN':
      return (
        <>
          <button
            className="bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600 transition"
            onClick={() => {
              enrollLecture(url);
            }}
          >
            수강신청
          </button>
        </>
      );
    case 'OVERCAPACITY':
      return (
        <>
          <button
            className="bg-gray-500 text-white py-2 px-4 rounded-lg transition"
            disabled
          >
            정원 초과
          </button>
        </>
      );
    case 'ENROLLED':
      return (
        <>
          <button
            className="bg-red-500 text-white py-2 px-4 rounded-lg hover:bg-red-600 transition"
            onClick={() => {
              withdrawLecture(url);
            }}
          >
            수강 취소
          </button>
        </>
      );
    case 'OWN_LECTURE':
      return (
        <>
          <button
            className="bg-gray-500 text-white py-2 px-4 rounded-lg transition"
            disabled
          >
            본인 강좌입니다.
          </button>
        </>
      );
    case 'ACTIVE_ENROLLED':
      return (
        <>
          <button
            className="bg-gray-500 text-white py-2 px-4 rounded-lg transition"
            disabled
          >
            수강신청 확정됨
          </button>
        </>
      );
    case 'ACTIVE_NOT_ENROLLED':
      return (
        <>
          <button
            className="bg-gray-500 text-white py-2 px-4 rounded-lg transition"
            disabled
          >
            확정된 강좌입니다.
          </button>
        </>
      );
    case 'STARTED':
      return (
        <>
          <button
            className="bg-gray-500 text-white py-2 px-4 rounded-lg transition"
            disabled
          >
            진행중인 강좌입니다.
          </button>
        </>
      );
    //   case 'FINISHED':
    //     return (
    //       <>
    //         <button
    //           className="bg-gray-500 text-white py-2 px-4 rounded-lg transition"
    //           disabled
    //         >
    //           종료된 강좌입니다.
    //         </button>
    //       </>
    //     );
    //   case 'CANCELLED':
    //     return (
    //       <>
    //         <button
    //           className="bg-gray-500 text-white py-2 px-4 rounded-lg transition"
    //           disabled
    //         >
    //           취소된 강좌입니다.
    //         </button>
    //       </>
    //     );
    //   case 'LOADING':
    //     return (
    //       <>
    //         <button
    //           className="bg-gray-500 text-white py-2 px-4 rounded-lg transition"
    //           disabled
    //         >
    //           불러오는 중...
    //         </button>
    //       </>
    //     );
  }
}
