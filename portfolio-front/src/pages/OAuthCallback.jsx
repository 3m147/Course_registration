import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

export default function OAuthCallback() {
  const navigate = useNavigate();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');
    const username = params.get('username');
    const role = params.get('role');
    const mainName = params.get('mainName');
    const subName = params.get('subName');
    if (token) {
      localStorage.setItem('token', token);
      localStorage.setItem('username', username);
      localStorage.setItem('role', role);
      localStorage.setItem('mainName', mainName);
      localStorage.setItem('subName', subName);

      navigate('/'); // 메인 페이지 이동
    } else {
      console.error('토큰이 없습니다.');
    }
  }, []);

  return (
    <div className="flex flex-col items-center justify-center min-h-screen">
      <p>로그인 처리 중...</p>
    </div>
  );
}
