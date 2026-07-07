import './App.css';
import Header from './components/Header';
import { Route, Routes, useLocation } from 'react-router-dom';
import SignUpPage from './pages/SignUpPage';
import LoginPage from './pages/LoginPage';
import LectureWritePage from './pages/LectureWritePage';
import LectureDetailPage from './pages/LectureDetailPage';
import HomePage from './pages/HomePage';
import ProfilePage from './pages/ProfilePage';
import PasswordChange from './components/PasswordChange';
import { useState } from 'react';
import OAuthCallback from './pages/OAuthCallback';
import MyPage from './pages/MyPage';
import PlayerPage from './pages/PlayerPage';
import Notice from './components/Notice';
import { NoticeProvider } from './context/NoticeContext';
import { LectureListProvider } from './context/LectureListContext';

import SportPopupPage from './pages/SportPopupPage';

function App() {
  const [page, setPage] = useState(null);
  const location = useLocation();

  return (
      <LectureListProvider>
        <NoticeProvider>
          {location.pathname !== '/user/password-change' &&   location.pathname !== '/user/sport-popup' && <Header />}
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/notice" element={<Notice />} />
            <Route path="/user/my" element={<MyPage />} />
            <Route path="/player/:playerName" element={<PlayerPage />} />

          <Route path="/user/login" element={<LoginPage />} />
          <Route path="/oauth/callback" element={<OAuthCallback />} />
          <Route path="/user/signup" element={<SignUpPage />} />

            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/user/password-change" element={<PasswordChange />} />
            <Route path="/user/sport-popup" element={<SportPopupPage />} />

            <Route path="/lecture/write" element={<LectureWritePage />} />
            <Route path="/lecture/:lectureId" element={<LectureDetailPage />} />
          </Routes>
        </NoticeProvider>
      </LectureListProvider>
  );
}

export default App;
