import { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';
import { apiUrl } from '../lib/api';

export const LectureListContext = createContext(null);

export function LectureListProvider({ children, playerName }) {
  const [page, setPage] = useState(null);
  const [pageNo, setPageNo] = useState(1);
  const [startNumOfCurrentPagingBlock, setStartNumOfCurrentPagingBlock] =
    useState(1);
  const [endNumOfCurrentPagingBlock, setEndNumOfCurrentPagingBlock] =
    useState(1);
  const [totalPageCnt, setTotalPageCnt] = useState(1);

  function paging(page) {
    setPageNo(page);
  }

  async function loadLectureList() {
    // const url = apiUrl(`/lecture?page=${pageNo}`);
    let url;
    if (playerName) {
      url = apiUrl(`/player/lectures/${playerName}?page=${pageNo}`);
    } else {
      url = apiUrl(`/lecture?page=${pageNo}`);
    }

    const resp = await axios.get(url);
    const data = resp.data;

    setPage(data);

    setTotalPageCnt(data.totalPageCnt);
    setStartNumOfCurrentPagingBlock(data.startNumOfCurrentPagingBlock);
    setEndNumOfCurrentPagingBlock(data.endNumOfCurrentPagingBlock);
  }

  useEffect(() => {
    loadLectureList();
  }, [pageNo, playerName]);

  return (
    <LectureListContext.Provider
      value={{
        page,
        setPage,
        paging,
        pageNo,
        totalPageCnt,
        startNumOfCurrentPagingBlock,
        endNumOfCurrentPagingBlock,
      }}
    >
      {children}
    </LectureListContext.Provider>
  );
}

// Hook
export function useLectureList() {
  return useContext(LectureListContext);
}
