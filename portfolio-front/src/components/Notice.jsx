import { useNavigate } from 'react-router';
import { useNotice } from '../context/NoticeContext';
import Pagination from './Pagination';
import { apiUrl } from '../lib/api';

export default function Notice() {
  const username = localStorage.getItem('username');
  const token = localStorage.getItem('token');
  const navigate = useNavigate();

  const {
    notices,
    refreshNotice,
    pageNo,
    totalPageCnt,
    paging,
    startNumOfCurrentPagingBlock,
    endNumOfCurrentPagingBlock,
  } = useNotice();

  async function readLecture(lectureId, noticeStatus) {
    try {
      await readNotice(lectureId);
      if (noticeStatus === 'NEW') {
        refreshNotice();
      }
      navigate(`/lecture/${lectureId}`);
    } catch (error) {
      alert(error.message);
    }
  }

  async function readNotice(lectureId) {
    const url = apiUrl(`/member/me/notice/${lectureId}`);
    const response = await fetch(url, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ username }),
    });
    if (!response.ok) throw new Error('알림 읽기에 실패했습니다.');
  }

  return (
    <>
      <div>
        {notices.length === 0 ? (
          <p className="text-gray-500">공지사항이 없습니다.</p>
        ) : (
          <ul className="space-y-4">
            {notices.map((notice, index) => (
              <li
                key={index}
                className="p-4  rounded-lg shadow-sm hover:shadow-md transition"
                onClick={() =>
                  readLecture(notice.lectureId, notice.noticeStatus)
                }
              >
                {notice.noticeStatus === 'NEW' && (
                  <span className="inline-block w-2 h-2 bg-red-500 rounded-full mt-2 mr-2"></span>
                )}
                <span className="font-semibold">
                  [{notice.lectureName}]강좌&nbsp;
                  {notice.noticeType === 'DELETED'
                    ? '삭제 알림'
                    : '수정 알림'}{' '}
                  -&nbsp;
                </span>
                <span>{notice.noticeContent}</span>
                <p className="text-gray-200 mt-2">{notice.content}</p>
                <p className="text-gray-400 text-sm mt-1">
                  {notice.noticeType === 'DELETED' ? '삭제일' : '수정일'}:{' '}
                  {new Date(notice.createdAt).toLocaleDateString()}
                </p>
              </li>
            ))}
          </ul>
        )}
        <Pagination
          pageNo={pageNo}
          totalPageCnt={totalPageCnt}
          paging={paging}
          startNumOfCurrentPagingBlock={startNumOfCurrentPagingBlock}
          endNumOfCurrentPagingBlock={endNumOfCurrentPagingBlock}
        />
      </div>
    </>
  );
}
