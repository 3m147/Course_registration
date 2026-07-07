import axios from 'axios';
import { useEffect, useState } from 'react';
import { apiUrl } from '../lib/api';

function ReviewEdit({ reviewId, onSuccess, onCancel }) {
  const [form, setForm] = useState({
    rating: 0,
    content: '',
  });
  const [existingImages, setExistingImages] = useState([]);
  const [newImages, setNewImages] = useState([]);
  const [deletedImgIds, setDeletedImgIds] = useState([]);

  const MAX_IMAGES = 5;

  useEffect(() => {
    if (!reviewId) return;
    const token = localStorage.getItem('token');
    axios
      .get(apiUrl(`/reviews/detail/${reviewId}`), {
        headers: {
          ...(token && { Authorization: `Bearer ${token}` })
        },
        withCredentials: true
      })
      .then((res) => {
        setForm({
          rating: res.data.rating,
          content: res.data.content,
        });
        setExistingImages(res.data.reviewImages || []);
      })
      .catch(() => {
        alert('리뷰 정보를 불러오지 못했습니다');
      });
  }, [reviewId]);

  const handleStarClick = (value) => {
    setForm({ ...form, rating: value });
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleImageUpload = (e) => {
    const files = Array.from(e.target.files);
    const totalImages = existingImages.length + newImages.length + files.length;

    if (totalImages > MAX_IMAGES) {
      alert(`이미지는 최대 ${MAX_IMAGES}장까지 업로드할 수 있습니다.`);
      return;
    }

    const validFiles = files.filter((file) =>
      ['image/jpeg', 'image/png', 'image/gif'].includes(file.type)
    );

    if (validFiles.length !== files.length) {
      alert('JPG, PNG, GIF 파일만 업로드 가능합니다.');
    }

    setNewImages((prev) => [...prev, ...validFiles]);
  };

  const removeExistingImage = (imgId) => {
    setExistingImages(existingImages.filter((img) => img.lectureReviewImgId !== imgId));
    setDeletedImgIds((prev) => [...prev, imgId]);
  };

  const removeNewImage = (index) => {
    setNewImages(newImages.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (form.rating === 0) {
      alert('별점을 선택하세요!');
      return;
    }

    const formData = new FormData();
    formData.append('rating', form.rating);
    formData.append('content', form.content);
    
    newImages.forEach((file) => {
      formData.append('files', file);
    });

    deletedImgIds.forEach((id) => {
      formData.append('deletedImgIds', id);
    });

    try {
      const token = localStorage.getItem('token');
      await axios.patch(apiUrl(`/reviews/${reviewId}`), formData, {
        headers: { 
          'Content-Type': 'multipart/form-data',
          ...(token && { Authorization: `Bearer ${token}` })
        },
        withCredentials: true
      });

      alert('리뷰가 수정되었습니다!');
      if (onSuccess) onSuccess(); 
    } catch (err) {
      console.error(err);
      alert('리뷰 수정에 실패했습니다.');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="w-full bg-white rounded-xl">
      <div className="mb-4">
        <label className="block text-sm font-semibold text-gray-700 mb-1">별점</label>
        <div className="flex items-center">
          {Array.from({ length: 5 }, (_, index) => {
            const starValue = index + 1;
            return (
              <span
                key={starValue}
                onClick={() => handleStarClick(starValue)}
                className={`text-3xl cursor-pointer mr-1 transition-colors ${
                  starValue <= form.rating ? 'text-yellow-400' : 'text-gray-300'
                }`}
              >
                ★
              </span>
            );
          })}
        </div>
      </div>

      <div className="mb-4">
        <label className="block text-sm font-semibold text-gray-700 mb-1">내용</label>
        <textarea
          name="content"
          value={form.content}
          onChange={handleChange}
          placeholder="리뷰 내용을 입력하세요"
          required
          className="w-full h-32 p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none text-sm"
        ></textarea>
      </div>

      <div className="mb-6">
        <label className="block text-sm font-semibold text-gray-700 mb-2">
          사진 첨부 ({existingImages.length + newImages.length} / {MAX_IMAGES})
        </label>
        
        <div className="flex gap-3 flex-wrap">
          {/* 이미지 추가 버튼 */}
          {(existingImages.length + newImages.length) < MAX_IMAGES && (
            <label className="w-20 h-20 border-2 border-dashed border-gray-300 rounded-lg flex items-center justify-center cursor-pointer hover:bg-gray-50 hover:border-blue-400 transition-colors">
              <span className="text-2xl text-gray-400">+</span>
              <input
                type="file"
                accept="image/*"
                className="hidden"
                multiple
                onChange={handleImageUpload}
              />
            </label>
          )}

          {/* 기존 이미지 표시 */}
          {existingImages.map((img) => (
            <div key={img.lectureReviewImgId} className="relative w-20 h-20 group">
              <img
                src={`data:${img.contentType};base64,${img.base64}`}
                alt="existing"
                className="w-full h-full object-cover rounded-lg border border-gray-200"
              />
              <button
                type="button"
                onClick={() => removeExistingImage(img.lectureReviewImgId)}
                className="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs opacity-0 group-hover:opacity-100 transition-opacity shadow-sm"
              >
                ✕
              </button>
            </div>
          ))}

          {/* 새 이미지 미리보기 */}
          {newImages.map((file, index) => (
            <div key={`new-${index}`} className="relative w-20 h-20 group">
              <img
                src={URL.createObjectURL(file)}
                alt="preview"
                className="w-full h-full object-cover rounded-lg border border-gray-200"
              />
              <button
                type="button"
                onClick={() => removeNewImage(index)}
                className="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs opacity-0 group-hover:opacity-100 transition-opacity shadow-sm"
              >
                ✕
              </button>
            </div>
          ))}
        </div>
      </div>

      <div className="flex gap-2 justify-end pt-4 border-t border-gray-100">
        <button
          type="button"
          onClick={onCancel}
          className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition text-sm font-medium"
        >
          취소
        </button>
        <button
          type="submit"
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition text-sm font-medium"
        >
          수정 완료
        </button>
      </div>
    </form>
  );
}

export default ReviewEdit;
