import { Link, useNavigate, useLocation } from 'react-router';
import { useState, useEffect } from 'react';
import axios from 'axios';
import '../App.css';
import SearchBar from './SearchBar';
import { useNotice } from '../context/NoticeContext';
import { apiUrl } from '../lib/api';

export default function Header() {
  const navigate = useNavigate();
  const location = useLocation(); // 페이지 이동 시 리렌더링을 위해 필요

  const username = localStorage.getItem('username');
  const roles = localStorage.getItem('roles');
  const [name, setName] = useState(localStorage.getItem('name'));
  const [profileImage, setProfileImage] = useState(null);
  const { hasNewNotice, clearNotice } = useNotice();

  useEffect(() => {
    if (!username) {
      setProfileImage(null);
      setName(null);
      return;
    }

    // localStorage의 name이 변경되었을 수 있으므로 업데이트
    const storedName = localStorage.getItem('name');
    if (storedName) {
      setName(storedName);
    }
    
    const token = localStorage.getItem('token');
    const headers = token ? { Authorization: `Bearer ${token}` } : {};

    if (!storedName) {
      axios
        .get(apiUrl(`/member/${username}`), { headers })
        .then((res) => {
          if (res.data && res.data.name) {
            localStorage.setItem('name', res.data.name);
            setName(res.data.name);
          }
        })
        .catch((err) => {
          console.error('사용자 정보 조회 실패:', err);
          if (err.response && err.response.status === 401) {
            handleLogout();
          }
        });
    }

    // 프로필 이미지 가져오기
    axios
      .get(apiUrl(`/profile-img/${username}`), { headers })
      .then((res) => {
        if (res.data && res.data.base64) {
          setProfileImage(res.data.base64);
        } else if (res.data && res.data.imgName) {
          setProfileImage(apiUrl(`/images/${res.data.imgName}`));
        }
      })
      .catch(() => setProfileImage(null));
  }, [username, location]); // location dependency added to re-run on navigation

  const handleLogout = () => {
    localStorage.clear();
    clearNotice();
    navigate('/');
    window.location.reload();
  };

  const handleMyPage = () => {
    navigate('/user/my');
  };

  return (
    <header className="bg-white shadow-sm py-3 px-6">
      <nav className="flex items-center justify-between">
        <span
          onClick={() => {
            window.location.href = '/';
          }}
          className="cursor-pointer text-2xl font-bold text-blue-600"
        >
          CoachLink
        </span>
        <div className="flex gap-6 ms-auto me-5">
          <SearchBar />
        </div>
        <div className="flex gap-6">
          {roles && roles.includes('PLAYER') && (
            <button
              onClick={() => {
                const subName = localStorage.getItem('subName'); // 로그인 시 저장해둔 subName

                if (!subName || subName.trim() === '') {
                  alert('강좌 등록 전 종목을 먼저 설정해주세요.');
                  navigate('/user/my'); // 회원정보 수정 페이지로 이동
                  return;
                }

                // 종목이 있으면 정상 이동
                navigate('/lecture/write');
              }}
              className="bg-blue-500 p-3 text-white transition-colors rounded-2xl font-semibold cursor-pointer"
            >
              + 새 강좌 등록
            </button>
          )}

          {!username && (
            <>
              <Link
                to="/user/login"
                className="text-gray-700 hover:text-blue-500 transition-colors cursor-pointer"
              >
                로그인
              </Link>
              <Link
                to="/user/signup"
                className="text-gray-700 hover:text-blue-500 transition-colors cursor-pointer"
              >
                회원가입
              </Link>
            </>
          )}

          {username && (
            <div className="flex items-center gap-2">
              <span
                className="text-gray-700 cursor-pointer hover:text-blue-500 "
                onClick={handleMyPage}
              >
                {hasNewNotice && (
                  <span className="inline-block w-2 h-2 bg-red-500 rounded-full mt-2 mr-2"></span>
                )}
                {username}
              </span>
              <button
                onClick={handleLogout}
                className="text-gray-700 hover:text-blue-500 transition-colors cursor-pointer"
              >
                로그아웃
              </button>
            </div>
          )}
        </div>
      </nav>
    </header>
  );
}
