import { useState } from 'react';
import { apiUrl } from '../lib/api';

export default function EnrollList() {
  const [list, setList] = useState(null);

  const url = apiUrl(`/enroll/member`);
  const header = {
    Authorization: localStorage.getItem('token'),
  };
  useEffect(() => {
    // 로딩하자마자 서버에 접속
    axios.get(url, header).then((resp) => {
      console.log(resp.data);
      setPage(resp.data);
    });
  }, []);

  if (list === null) {
    // 아직 로딩 안됨
    return <p>로딩중입니다...</p>;
  } else {
    return (
      <>
        {list.dtoList.map((lecture, idx) => {
          return <LectureItem lectureInfo={lecture} key={idx} />;
        })}
      </>
    );
  }
}
