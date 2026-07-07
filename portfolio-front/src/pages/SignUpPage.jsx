import { apiUrl } from '../lib/api';

import axios from 'axios';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { useDaumPostcodePopup } from 'react-daum-postcode';

function SignUpPage() {
  const [formData, setFormData] = useState({
    username: '',
    name: '',
    userPwd: '',
    email: '',
    zipNo: '',
    address: '',
    detailAddress: '',
    gender: 'M',
    role: 'MEMBER',
    userStatus: 'Y',
    fromSocial: 0,
    sportId: null, // Added sportId
  });

  const [isIdChecked, setIsIdChecked] = useState(false);
  const [userPwdConfirm, setUserPwdConfirm] = useState('');
  const [selectedSportName, setSelectedSportName] = useState(''); // Display name
  const navigate = useNavigate();

  useEffect(() => {
    const handleMessage = (event) => {
      if (event.origin !== window.location.origin) return;
      if (event.data.type === 'SPORT_SELECTED') {
        const sport = event.data.sport;
        setFormData(prev => ({ ...prev, sportId: sport.sportId }));
        setSelectedSportName(`${sport.mainName} - ${sport.subName}`);
      }
    };
    window.addEventListener('message', handleMessage);
    return () => window.removeEventListener('message', handleMessage);
  }, []);

  const handleCheckId = async () => {
    if (!formData.username.trim()) {
      alert('아이디를 입력해주세요');
      return;
    }
    try {
      const response = await axios.get(apiUrl(`/member/checkId`), {
        params: { username: formData.username },
      });
      if (response.data) {
        setIsIdChecked(true);
        alert('사용 가능한 아이디입니다');
      } else {
        setIsIdChecked(false);
        alert('이미 사용 중인 아이디입니다');
      }
    } catch (error) {
      console.error(error);
      alert('중복 확인 중 오류가 발생했습니다');
    }
  };

  const open = useDaumPostcodePopup();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    
    if (name === 'username') {
      setIsIdChecked(false);
    }

    if (name === 'role' && value === 'PLAYER') {
      // No longer opening modal directly here, user will click button
    }
  };

  const openSportPopup = () => {
    const width = 500;
    const height = 600;
    const left = (window.screen.width - width) / 2;
    const top = (window.screen.height - height) / 2;
    window.open('/user/sport-popup', 'SportSelection', `width=${width},height=${height},left=${left},top=${top},scrollbars=yes,resizable=yes`);
  };

  const handleComplete = (data) => {
    let fullAddress = data.address;
    let extraAddress = '';

    if (data.addressType === 'R') {
      if (data.bname !== '') {
        extraAddress += data.bname;
      }
      if (data.buildingName !== '') {
        extraAddress += extraAddress !== '' ? `, ${data.buildingName}` : data.buildingName;
      }
      fullAddress += extraAddress !== '' ? ` (${extraAddress})` : '';
    }

    setFormData((prev) => ({
      ...prev,
      address: fullAddress,
      zipNo: data.zonecode,
      detailAddress: '',
    }));
  };

  const handlePostCode = () => {
    open({ onComplete: handleComplete });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!isIdChecked) {
      alert('아이디 중복 확인을 해주세요');
      return;
    }

    if (formData.userPwd !== userPwdConfirm) {
      alert('비밀번호가 일치하지 않습니다');
      return;
    }

    if (!formData.userPwd || formData.userPwd.trim() === '') {
      alert('비밀번호를 입력해주세요');
      return;
    }

    if (formData.role === 'PLAYER' && !formData.sportId) {
        alert('선수회원은 종목을 선택해야 합니다.');
        openSportPopup();
        return;
    }

    try {
      const fullAddress = formData.detailAddress 
        ? `${formData.address}|${formData.detailAddress}`
        : formData.address;

      const submitData = {
        ...formData,
        address: fullAddress,
        detailAddress: '',
      };

      // If role is not PLAYER, ensure sportId is null (though it should be handled by backend logic usually)
      if (submitData.role !== 'PLAYER') {
          submitData.sportId = null;
      }

      console.log('회원가입 데이터:', {
        ...submitData,
        userPwd: '***' // 비밀번호는 로그에 표시하지 않음
      });

      const response = await axios.post(
        apiUrl('/member/register'),
        submitData,
        {
          headers: { 'Content-Type': 'application/json' },
        }
      );

      console.log('회원가입 응답:', response.data);
      alert(response.data);
      navigate('/user/login');
    } catch (error) {
      console.error('회원가입 에러:', error);
      alert('회원가입 실패: ' + (error.response?.data || error.message || '서버 오류'));
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <form
        onSubmit={handleSubmit}
        className="flex flex-col gap-4 w-full max-w-md bg-white p-6 rounded-lg shadow"
      >

        <input
          name="name"
          placeholder="이름"
          onChange={handleChange}
          className="p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <input
          name="email"
          placeholder="이메일"
          onChange={handleChange}
          className="p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <div className="flex gap-2">
          <input
            name="username"
            placeholder="아이디"
            onChange={handleChange}
            className="flex-1 p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button
            type="button"
            onClick={handleCheckId}
            className="px-4 py-2 cursor-pointer bg-blue-500 text-white rounded-lg hover:bg-blue-600 whitespace-nowrap"
          >
            중복확인
          </button>
        </div>


        <input
          type="password"
          name="userPwd"
          placeholder="비밀번호"
          onChange={handleChange}
          className="p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />

        <input
          type="password"
          name="userPwdConfirm"
          placeholder="비밀번호 확인"
          value={userPwdConfirm}
          onChange={(e) => setUserPwdConfirm(e.target.value)}
          className="p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />

        <div className="flex gap-2">
          <input
            name="address"
            placeholder="주소"
            value={formData.address}
            readOnly
            className="flex-1 p-3 border border-gray-300 rounded-lg focus:outline-none"
          />
          <button
            type="button"
            onClick={handlePostCode}
            className="px-4 py-2 cursor-pointer bg-gray-700 text-white rounded-lg hover:bg-gray-800"
          >
            주소찾기
          </button>
        </div>

        <input
          name="detailAddress"
          placeholder="상세주소"
          value={formData.detailAddress}
          onChange={handleChange}
          className="p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />

        <select
          name="gender"
          onChange={handleChange}
          className="p-3 border border-gray-300 rounded-lg focus:outline-none"
        >
          <option value="M">남성</option>
          <option value="F">여성</option>
        </select>
        
        <div className="flex gap-2">
            <select
                name="role"
                onChange={handleChange}
                className="w-32 p-3 border border-gray-300 rounded-lg focus:outline-none shrink-0"
            >
                <option value="MEMBER">일반회원</option>
                <option value="PLAYER">선수회원</option>
            </select>
            
            {formData.role === 'PLAYER' && (
                <div className="flex-1 flex gap-2 min-w-0">
                    <input 
                        readOnly
                        placeholder="선택된 종목 없음"
                        value={selectedSportName}
                        className="flex-1 p-3 border border-gray-300 rounded-lg focus:outline-none bg-gray-50 text-gray-600 min-w-0 text-ellipsis"
                    />
                    <button
                        type="button"
                        onClick={openSportPopup}
                        className="px-4 py-2 bg-gray-700 text-white rounded-lg hover:bg-gray-800 whitespace-nowrap shrink-0 cursor-pointer"
                    >
                        종목 선택
                    </button>
                </div>
            )}
        </div>

        <button
          type="submit"
          className="bg-blue-500 text-white py-3 cursor-pointer rounded-lg hover:bg-blue-600 transition"
        >
          회원가입
        </button>
      </form>
    </div>
  );
}
export default SignUpPage;
