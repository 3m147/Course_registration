import axios from 'axios';
import { useEffect, useState } from 'react';
import LectureItem from '../components/LectureItem';
import { apiUrl } from '../lib/api';

// 여기서 컨텍스트로 강좌를 빼옴?
export default function LectureListPage() {
  const [page, setPage] = useState(null);

  const url = apiUrl(`/lecture`);
  useEffect(() => {
    // 로딩하자마자 서버에 접속
    axios.get(url).then((resp) => {
      setPage(resp.data);
    });
  }, []);

  if (page === null) {
    // 아직 로딩 안됨
    return <p>로딩중입니다...</p>;
  } else {
    return (
      <div className="max-w-6xl mx-auto px-4 py-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {page.dtoList.map((lecture, idx) => (
            <LectureItem lectureInfo={lecture} key={idx} />
          ))}
        </div>
      </div>
    );
  }
}
