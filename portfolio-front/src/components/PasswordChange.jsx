import axios from 'axios';
import { useState } from 'react';
import { apiUrl } from '../lib/api';

function PasswordChange() {
  const [passwords, setPasswords] = useState({
    oldPwd: '',
    newPwd: '',
    confirmPwd: '',
  });

  const username = localStorage.getItem('username');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setPasswords((prev) => ({ ...prev, [name]: value }));
  };

  const handleClose = () => {
    window.close();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (passwords.newPwd !== passwords.confirmPwd) {
      alert('비밀번호가 일치하지 않습니다');
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const res = await axios.put(
        apiUrl('/member/password-change'),
        {
          username,
          oldPwd: passwords.oldPwd,
          newPwd: passwords.newPwd,
          confirmPwd: passwords.confirmPwd,
        },
        {
          headers: { 
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
        }
      );
      alert(res.data || '비밀번호가 변경되었습니다');
      window.close();
    } catch (err) {
      alert('비밀번호 변경 실패: ' + (err.response?.data || '서버 오류'));
    }
  };

  return (
    <div className="h-screen bg-white p-6">
      <h3 className="text-xl font-bold mb-6 text-gray-800 border-b pb-4">비밀번호 변경</h3>
      <form onSubmit={handleSubmit} className="flex flex-col gap-6">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">현재 비밀번호</label>
          <input
            type="password"
            name="oldPwd"
            value={passwords.oldPwd}
            onChange={handleChange}
            required
            className="w-full p-3 border border-gray-300 rounded focus:outline-none focus:border-blue-500 transition-colors"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">새 비밀번호</label>
          <input
            type="password"
            name="newPwd"
            value={passwords.newPwd}
            onChange={handleChange}
            required
            className="w-full p-3 border border-gray-300 rounded focus:outline-none focus:border-blue-500 transition-colors"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">새 비밀번호 확인</label>
          <input
            type="password"
            name="confirmPwd"
            value={passwords.confirmPwd}
            onChange={handleChange}
            required
            className="w-full p-3 border border-gray-300 rounded focus:outline-none focus:border-blue-500 transition-colors"
          />
        </div>

        <div className="flex justify-end gap-3 mt-auto pt-4">
          <button
            type="button"
            onClick={handleClose}
            className="px-6 py-2.5 cursor-pointer text-gray-700 bg-gray-100 rounded hover:bg-gray-200 font-medium transition-colors"
          >
            취소
          </button>
          <button
            type="submit"
            className="px-6 py-2.5 cursor-pointer text-white bg-blue-500 rounded hover:bg-blue-600 font-medium transition-colors"
          >
            변경하기
          </button>
        </div>
      </form>
    </div>
  );
}

export default PasswordChange;
