import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import ProfileForm from '../components/ProfileForm';
import { apiUrl } from '../lib/api';

export default function ProfilePage({ readOnly }) {
  const [member, setMember] = useState({
    username: '',
    name: '',
    email: '',
    address: '',
    detailAddress: '',
    gender: '',
    role: '',
    sportName: '',
    fileName: '',
  });

  const [profilePreview, setProfilePreview] = useState(null);

  const username = localStorage.getItem('username');
  const navigate = useNavigate();

  useEffect(() => {
    if (!username) {
      alert('로그인이 필요합니다.');
      return;
    }

    const token = localStorage.getItem('token');
    const headers = token ? { Authorization: `Bearer ${token}` } : {};

    // 회원 정보 불러오기
    axios
      .get(apiUrl(`/member/${username}`), {
        headers,
        withCredentials: true,
      })
      .then((res) => {
        const data = res.data;

       
        if (data.roleSet && data.roleSet.length > 0) {
            const roleVal = data.roleSet[0];
            
            if (roleVal === 1 || roleVal === 'PLAYER') {
                data.role = 'PLAYER';
            } else {
                data.role = 'MEMBER';
            }
        } else {
             data.role = 'MEMBER';
        }

        if (data.address && data.address.includes('|')) {
          const [addr, detail] = data.address.split('|');
          data.address = addr;
          data.detailAddress = detail;
        }

        setMember(data);
      })
      .catch((err) => console.error('회원 정보 불러오기 실패:', err));

    // 프로필 이미지 불러오기
    axios
      .get(apiUrl(`/profile-img/${username}`), {
        headers,
        withCredentials: true,
      })
      .then((res) => {
        if (res.data && res.data.base64) {
          setProfilePreview(res.data.base64);
        } else if (res.data && res.data.imgName) {
          setProfilePreview(apiUrl(`/images/${res.data.imgName}`));
        }
      })
      .catch(() => {
        // 이미지가 없으면 기본 이미지 사용
        setProfilePreview(null);
      });
  }, [username]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setMember((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      const headers = {
        'Content-Type': 'application/json',
        ...(token && { Authorization: `Bearer ${token}` }),
      };

      const fullAddress = `${member.address}|${member.detailAddress || ''}`;

      const submitData = {
        ...member,
        address: fullAddress,
        detailAddress: '',
      };

      await axios.put(apiUrl(`/member/${username}`), submitData, {
        headers,
        withCredentials: true,
      });
    } catch (err) {
      console.error('수정 실패:', err);
      alert('수정실패: ' + err.message);
    }
  };

  const handleSuccess = () => {
    alert('회원정보가 수정되었습니다');
    if (member.name) localStorage.setItem('name', member.name);
    navigate('/');
  };

  const handlePasswordChange = () => {
    window.open(
      '/user/password-change',
      'PasswordChange',
      'width=500,height=600,scrollbars=yes,resizable=yes'
    );
  };

  const handleWithdraw = async () => {
    if (!window.confirm('정말로 회원탈퇴 하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const headers = token ? { Authorization: `Bearer ${token}` } : {};

      await axios.delete(apiUrl(`/member/${username}`), {
        headers,
        withCredentials: true,
      });

      alert('회원탈퇴가 완료되었습니다.');
      localStorage.clear();
      navigate('/');
    } catch (err) {
      console.error('회원탈퇴 실패:', err);
      alert('회원탈퇴 실패: ' + (err.response?.data || err.message));
    }
  };

  return (
    <div className="w-full max-w-4xl mx-auto py-12 px-4">
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-800 mb-2">
          {readOnly ? '회원 정보' : '프로필 수정'}
        </h1>
      </div>

      <ProfileForm
        member={member}
        onChange={handleChange}
        onSubmit={handleSubmit}
        onSuccess={handleSuccess}
        preview={profilePreview}
        setPreview={setProfilePreview}
        readOnly={readOnly}
        onWithdraw={handleWithdraw}
      />
    </div>
  );
}
