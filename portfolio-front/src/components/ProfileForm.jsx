import { useDaumPostcodePopup } from 'react-daum-postcode';
import ProfileImg from './ProfileImg';
import { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { apiUrl } from '../lib/api';

function ProfileForm({ member, onChange, onSubmit, onSuccess, preview, setPreview, readOnly, onWithdraw }) {
  const open = useDaumPostcodePopup();
  const [profileImage, setProfileImage] = useState(null);
  const navigate = useNavigate();

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setProfileImage(file);
      setPreview(URL.createObjectURL(file));
    }
  };

  const handleDeleteImage = () => {
    setProfileImage(null);
    setPreview(null);
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

    onChange({ target: { name: 'address', value: fullAddress } });
  };

  const handlePostCode = () => {
    open({ onComplete: handleComplete });
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    
    try {
        await onSubmit(e); 

        if (profileImage) {
            const formData = new FormData();
            formData.append('file', profileImage);
            formData.append('username', member.username);

            const token = localStorage.getItem('token');
            await axios.post(apiUrl('/profile-img/upload'), formData, {
                headers: { 
                  ...(token && { Authorization: `Bearer ${token}` })
                },
                withCredentials: true
            });
        }

        if (onSuccess) {
          onSuccess();
        }

    } catch (error) {
        console.error("Form submission error", error);
    }
  };

  return (
    <form onSubmit={handleFormSubmit} className="w-full max-w-4xl mx-auto mt-8">
      <div className="border-t-2 border-gray-800">

        <div className="flex border-b border-gray-200">
          <div className="w-48 bg-gray-50 p-4 flex items-center justify-center font-bold text-gray-700 border-r border-gray-200">
            프로필사진
          </div>
          <div className="flex-1 p-6 bg-white">
            <ProfileImg 
                preview={preview} 
                onFileChange={handleFileChange} 
                onDelete={handleDeleteImage} 
                readOnly={readOnly}
            />
          </div>
        </div>

        <div className="flex border-b border-gray-200">
          <div className="w-48 bg-gray-50 p-4 flex items-center font-bold text-gray-700 border-r border-gray-200">
            아이디
          </div>
          <div className="flex-1 p-4 bg-white flex items-center">
            <input
              name="username"
              value={member.username || ''}
              readOnly
              className="w-full max-w-md p-2 border border-gray-300 bg-gray-100 text-gray-500 rounded-sm focus:outline-none"
            />
          </div>
        </div>

        {!readOnly && !member.fromSocial && (
        <div className="flex border-b border-gray-200">
          <div className="w-48 bg-gray-50 p-4 flex items-center font-bold text-gray-700 border-r border-gray-200">
            비밀번호
          </div>
          <div className="flex-1 p-4 bg-white flex items-center">
            <button
              type="button"
              onClick={() => window.open('/user/password-change', 'passwordChange', 'width=500,height=600')}
              className="px-3 py-1.5 border border-gray-300 rounded text-sm text-gray-600 hover:bg-gray-50 transition-colors cursor-pointer"
            >
              비밀번호 변경
            </button>
          </div>
        </div>
        )}


        <div className="flex border-b border-gray-200">
          <div className="w-48 bg-gray-50 p-4 flex items-center font-bold text-gray-700 border-r border-gray-200">
            별명(이름)
          </div>
          <div className="flex-1 p-4 bg-white flex items-center">
            <input
              name="name"
              value={member.name || ''}
              onChange={onChange}
              readOnly={readOnly}
              placeholder="이름을 입력하세요"
              className={`w-full max-w-md p-2 border border-gray-300 rounded-sm focus:outline-none focus:outline-none ${readOnly ? 'bg-gray-100 text-gray-500' : ''}`}
            />
          </div>
        </div>

 
        <div className="flex border-b border-gray-200">
          <div className="w-48 bg-gray-50 p-4 flex items-center font-bold text-gray-700 border-r border-gray-200">
            이메일
          </div>
          <div className="flex-1 p-4 bg-white flex items-center">
            <input
              name="email"
              value={member.email || ''}
              onChange={onChange}
              readOnly={readOnly}
              placeholder="이메일"
              className={`w-full max-w-md p-2 border border-gray-300 rounded-sm focus:outline-none focus:outline-none ${readOnly ? 'bg-gray-100 text-gray-500' : ''}`}
            />
          </div>
        </div>

        <div className="flex border-b border-gray-200">
          <div className="w-48 bg-gray-50 p-4 flex items-center font-bold text-gray-700 border-r border-gray-200">
            주소
          </div>
          <div className="flex-1 p-4 bg-white flex items-center gap-2">
            <input
              name="address"
              value={member.address || ''}
              readOnly
              placeholder="주소"
              className={`flex-1 max-w-md p-2 border border-gray-300 bg-gray-50 rounded-sm focus:outline-none ${readOnly ? 'bg-gray-100 text-gray-500' : ''}`}
            />
            {!readOnly && (
              <button 
                  type="button" 
                  onClick={handlePostCode}
                  className="px-3 py-2 cursor-pointer border border-gray-300 bg-white text-sm text-gray-700 hover:bg-gray-50"
              >
                주소찾기
              </button>
            )}
          </div>
        </div>

        <div className="flex border-b border-gray-200">
          <div className="w-48 bg-gray-50 p-4 flex items-center font-bold text-gray-700 border-r border-gray-200">
            상세주소
          </div>
          <div className="flex-1 p-4 bg-white flex items-center">
            <input
              type="text"
              name="detailAddress"
              value={member.detailAddress || ""}
              onChange={onChange}
              readOnly={readOnly}
              className={`w-full max-w-md p-2 border border-gray-300 rounded-sm focus:outline-none focus:outline-none ${readOnly ? 'bg-gray-100 text-gray-500' : ''}`}
              placeholder="상세주소를 입력하세요"
            />
          </div>
        </div>

        <div className="flex border-b border-gray-200">
          <div className="w-48 bg-gray-50 p-4 flex items-center font-bold text-gray-700 border-r border-gray-200">
            성별
          </div>
          <div className="flex-1 p-4 bg-white flex items-center">
            <select 
                name="gender" 
                value={member.gender || 'M'} 
                onChange={onChange}
                disabled={readOnly}
                className={`p-2 border border-gray-300 rounded-sm focus:outline-none ${readOnly ? 'bg-gray-100 text-gray-500' : ''}`}
            >
              <option value="M">남성</option>
              <option value="F">여성</option>
            </select>
          </div>
        </div>

        <div className="flex border-b border-gray-200">
          <div className="w-48 bg-gray-50 p-4 flex items-center font-bold text-gray-700 border-r border-gray-200">
            회원 유형
          </div>
          <div className="flex-1 p-4 bg-white flex items-center">
            <select 
                name="role" 
                value={member.role || 'MEMBER'} 
                disabled
                className="p-2 border border-gray-300 bg-gray-100 text-gray-500 rounded-sm focus:outline-none"
            >
              <option value="MEMBER">일반회원</option>
              <option value="PLAYER">선수회원</option>
            </select>
          </div>
        </div>
      </div>

      <div className="mt-4 flex items-center justify-between text-sm text-gray-500">
        <div className="flex items-center gap-2">
          <span className="w-1 h-1 bg-gray-400 rounded-full"></span>
          <span>회원탈퇴 후 동일 아이디로 재가입이 불가합니다.</span>
        </div>
        <button
          type="button"
          onClick={onWithdraw}
          className="px-3 py-1 border border-gray-300 rounded bg-white text-gray-600 hover:bg-gray-50 text-xs cursor-pointer"
        >
          회원탈퇴 &gt;
        </button>
      </div>

      {!readOnly ? (
        <div className="flex justify-center gap-2 mt-8">
          <button 
              type="submit"
              className="px-8 py-3 cursor-pointer bg-white border border-gray-300 text-gray-700 font-medium hover:bg-gray-50"
          >
              적용
          </button>
          <button 
              type="button"
              onClick={() => navigate('/user/my', { state: { tab: 'profile' } })}
              className="px-8 py-3 cursor-pointer bg-white border border-gray-300 text-gray-700 font-medium hover:bg-gray-50"
          >
              취소
          </button>
        </div>
      ) : (
        <div className="flex justify-center mt-8">
          <button 
              type="button"
              onClick={() => navigate('/profile')}
              className="px-8 py-3 cursor-pointer bg-blue-500 text-white font-medium hover:bg-blue-600 rounded-sm"
          >
              수정하기
          </button>
        </div>
      )}
    </form>
  );
}
export default ProfileForm;
