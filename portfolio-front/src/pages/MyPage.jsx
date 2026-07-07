import { useState } from 'react';
import { useLocation } from 'react-router-dom';
import Notice from '../components/Notice';
import { useNotice } from '../context/NoticeContext';
import ProfilePage from './ProfilePage';
import MyLectureList from '../components/MyLectureList';

export default function MyPage() {
  const location = useLocation();
  const [tab, setTab] = useState(location.state?.tab || 'notice');
  const { hasNewNotice } = useNotice();
  const hasNewProfile = false;

  return (
    <div className="max-w-5xl mx-auto mt-8 bg-white  rounded-lg shadow-sm" >
      <div className="px-6 pt-4">
        <nav className="flex gap-6">
          <button
            onClick={() => setTab('profile')}
            className={
              tab === 'profile'
                ? 'text-blue-500 border-b-2 border-blue-500 flex items-center gap-2 cursor-pointer'
                : 'text-gray-500 flex items-center gap-2 cursor-pointer' 
            }
          >
            회원정보
            {hasNewProfile && (
              <span className="w-2 h-2 bg-red-500 rounded-full"></span>
            )}
          </button>

          <button
            onClick={() => setTab('notice')}
            className={
              tab === 'notice'
                ? 'text-blue-500 border-b-2 border-blue-500 flex items-center gap-2 cursor-pointer'
                : 'text-gray-500 flex items-center gap-2 cursor-pointer'
            }
          >
            알림
            {hasNewNotice && (
              <span className="w-2 h-2 bg-red-500 rounded-full"></span>
            )}
          </button>

          <button
            onClick={() => setTab('lectures')}
            className={
              tab === 'lectures'
                ? 'text-blue-500 border-b-2 border-blue-500 flex items-center gap-2 cursor-pointer'
                : 'text-gray-500 flex items-center gap-2 cursor-pointer'
            }
          >
            내 강좌 관리
          </button>
        </nav>
      </div>
      <div className="p-6">{tab === 'profile' && <ProfilePage readOnly={true} />}</div>
      <div className="p-6">{tab === 'notice' && <Notice />}</div>
      <div className="p-6">{tab === 'lectures' && <MyLectureList />}</div>
    </div>
  );
}
