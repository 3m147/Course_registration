import axios from 'axios';
import { useContext, useEffect } from 'react';
import LectureItem from '../components/LectureItem';
import { useLectureList } from '../context/LectureListContext';
import Pagination from '../components/Pagination';
import { apiUrl } from '../lib/api';

export default function HomePage() {
  // const { page, setPage } = useContext(LectureListContext);
  const {
    page,
    pageNo,
    totalPageCnt,
    paging,
    startNumOfCurrentPagingBlock,
    endNumOfCurrentPagingBlock,
  } = useLectureList();

  // const url = apiUrl(`/lecture`);

  // useEffect(() => {
  //   // 로딩하자마자 서버에 접속
  //   axios.get(url).then((resp) => {
  //     setPage(resp.data);
  //   });
  // }, []);

  if (page == null || page == undefined) {
    // 아직 로딩 안됨
    return <p>로딩중입니다...</p>;
  } else {
    return (
      <div className="max-w-6xl mx-auto px-4 py-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-6">
          {page.dtoList.map((lecture, idx) => (
            <LectureItem lectureInfo={lecture} key={idx} />
          ))}
        </div>
        <Pagination
          pageNo={pageNo}
          totalPageCnt={totalPageCnt}
          paging={paging}
          startNumOfCurrentPagingBlock={startNumOfCurrentPagingBlock}
          endNumOfCurrentPagingBlock={endNumOfCurrentPagingBlock}
        />
      </div>
    );
  }
}
