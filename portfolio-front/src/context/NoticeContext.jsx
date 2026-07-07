import { createContext, useContext, useState, useEffect } from 'react';
import { apiUrl } from '../lib/api';

export const NoticeContext = createContext(null);

export function NoticeProvider({ children }) {
  const [notices, setNotices] = useState([]);
  const [hasNewNotice, setHasNewNotice] = useState(false);
  const [pageNo, setPage] = useState(1);

  const token = localStorage.getItem('token');
  const username = localStorage.getItem('username');
  const [startNumOfCurrentPagingBlock, setStartNumOfCurrentPagingBlock] =
    useState(1);
  const [endNumOfCurrentPagingBlock, setEndNumOfCurrentPagingBlock] =
    useState(1);
  const [totalPageCnt, setTotalPageCnt] = useState(1);
  const [newNotice, setNewNotice] = useState(0);

  function paging(page) {
    setPage(page);
  }

  async function getNotices() {
    if (!username || !token) {
      setNotices([]);
      setHasNewNotice(false);
      return;
    }

    const url = apiUrl(`/member/me/notice/${username}/${pageNo}`);
    try {
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      });
      if (!response.ok) throw new Error('알림을 찾을 수 없습니다.');

      const data = await response.json();
      setNotices(data.dtoList || []);
      setTotalPageCnt(data.totalPageCnt);
      setStartNumOfCurrentPagingBlock(data.startNumOfCurrentPagingBlock);
      setEndNumOfCurrentPagingBlock(data.endNumOfCurrentPagingBlock);
      setNewNotice(data.newNotice);
    } catch (error) {
      console.error(error.message);
    }
  }

  const refreshNotice = () => {
    getNotices();
  };

  useEffect(() => {
    if (newNotice > 0) {
      setHasNewNotice(true);
    } else {
      setHasNewNotice(false);
    }
  }, [newNotice]);

  useEffect(() => {
    getNotices();

    const interval = setInterval(() => {
      getNotices();
    }, 30000);

    return () => clearInterval(interval);
  }, [username, token, pageNo]);

  const value = {
    notices,
    hasNewNotice,
    refreshNotice,
    clearNotice: () => {
      setNotices([]);
      setHasNewNotice(false);
    },
    paging,
    pageNo,
    startNumOfCurrentPagingBlock,
    endNumOfCurrentPagingBlock,
    totalPageCnt,
  };

  return (
    <NoticeContext.Provider value={value}>{children}</NoticeContext.Provider>
  );
}

export function useNotice() {
  return useContext(NoticeContext);
}
