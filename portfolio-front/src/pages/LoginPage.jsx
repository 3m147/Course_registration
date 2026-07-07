import { useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import googleBtn from '../assets/images/web_neutral_sq_SI@3x.png';
import { apiUrl } from '../lib/api';

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [userPwd, setuserPwd] = useState('');
  const contentError = '를 확인해주세요.';
  const idRef = useRef();
  const pwdRef = useRef();
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const postLogin = (username, userPwd) => {
    if (!username || username.trim() === '') {
      alert('아이디' + contentError);
      setTimeout(() => {
        idRef.current?.focus();
      }, 0);
      return;
    }

    if (!userPwd || userPwd.trim() === '') {
      alert('비밀번호' + contentError);
      setTimeout(() => {
        pwdRef.current?.focus();
      }, 0);
      return;
    }
    const userInfo = {
      username: username,
      userPwd: userPwd,
    };
    console.log('userInfo', userInfo);
    sendLogin(userInfo);
  };

  async function sendLogin(userInfo) {
    console.log('userInfo2', userInfo);
    const url = apiUrl(`/auth/login`);
    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userInfo),
      });
      if (response.status === 401) {
        alert('아이디 또는 비밀번호가 올바르지 않습니다.');
        return;
      }
      if (!response.ok) throw new Error('로그인 중 오류가 발생했습니다.');
      const data = await response.json();
      console.log(data);
      localStorage.setItem('token', data.token);
      localStorage.setItem('username', data.username);
      localStorage.setItem('roles', data.roles);
      localStorage.setItem('mainName', data.mainName);
      localStorage.setItem('subName', data.subName);
      navigate('/');
    } catch (error) {
      setError(error.message);
      alert(error.message);
    }
  }

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50">
      <h1 className="text-3xl font-bold mb-8">Login</h1>

      <form className="flex flex-col gap-4 w-80 mb-6">
        <input
          type="text"
          placeholder="아이디를 입력하세요"
          className="p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          ref={idRef}
        />
        <input
          type="password"
          placeholder="비밀번호를 입력하세요"
          className="p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={userPwd}
          onChange={(e) => setuserPwd(e.target.value)}
          ref={pwdRef}
        />
        <button
          type="submit"
          className="bg-blue-500 text-white py-3 rounded-lg hover:bg-blue-600 transition"
          onClick={(e) => {
            e.preventDefault();
            postLogin(username, userPwd);
          }}
        >
          로그인
        </button>
      </form>
      <button
        onClick={() => {
          window.location.href =
            apiUrl('/oauth2/authorization/google');
        }}
        className="w-80 flex justify-center items-center bg-[#F2F2F2] py-1 rounded-lg"
      >
        <img src={googleBtn} alt="google login" style={{ width: '180px' }} />
      </button>
    </div>
  );
}
