import { useState } from 'react';

export default function ReviewWrite({ onSubmit }) {
  const [content, setContent] = useState('');
  const [rating, setRating] = useState(0);
  const [images, setImages] = useState([]);

  const MAX_LENGTH = 3000;
  const MAX_IMAGES = 5;

  // 내용 입력
  const handleContent = (e) => {
    if (e.target.value.length <= MAX_LENGTH) {
      setContent(e.target.value);
    }
  };

  // 별점 클릭
  const handleStarClick = (value) => {
    setRating(value);
  };

  // 이미지 업로드
  const handleImageUpload = (e) => {
    const files = Array.from(e.target.files);

    if (images.length + files.length > MAX_IMAGES) {
      alert('이미지는 최대 5장까지 업로드할 수 있습니다.');
      return;
    }

    const validFiles = files.filter((file) =>
      ['image/jpeg', 'image/png', 'image/gif'].includes(file.type)
    );

    if (validFiles.length !== files.length) {
      alert('JPG, PNG, GIF 파일만 업로드 가능합니다.');
    }

    setImages((prev) => [...prev, ...validFiles]);
  };

  // 이미지 삭제
  const removeImage = (index) => {
    setImages(images.filter((_, i) => i !== index));
  };

  // 등록 버튼 클릭
  const handleSubmit = (e) => {
    e.preventDefault();

    if (rating === 0) {
      alert('별점을 선택해주세요.');
      return;
    }

    if (content.length < 10) {
      alert('리뷰 내용을 10자 이상 입력해주세요.');
      return;
    }

    const formData = new FormData();
    formData.append('content', content);
    formData.append('rating', rating);
    images.forEach((img, idx) => formData.append('files', img));

    if (onSubmit) onSubmit(formData);
  };

  return (
    <div className="w-full max-w-2xl p-6">
      <h2 className="text-lg font-semibold mb-4">리뷰 작성</h2>

      <label className="text-sm font-semibold mb-1 block">
        별점
      </label>
      <div className="star-rating mb-4">
        {Array.from({ length: 5 }, (_, index) => {
          const starValue = index + 1;
          return (
            <span
              key={starValue}
              onClick={() => handleStarClick(starValue)}
              style={{
                cursor: 'pointer',
                fontSize: '30px',
                color: starValue <= rating ? '#FFD700' : '#CCCCCC',
                marginRight: '5px',
              }}
            >
              ★
            </span>
          );
        })}
      </div>

      <label className="text-sm font-semibold mb-1 block">
        리뷰 작성
      </label>

      <textarea
        value={content}
        onChange={handleContent}
        placeholder="내용을 10자 이상 입력해 주세요."
        className="w-full h-40 border rounded-lg p-3 focus:ring-2 focus:ring-blue-500 text-sm"
      />

      <div className="text-right text-gray-500 text-xs">
        {content.length} / 3000
      </div>

      <div className="mt-4">
        <label className="text-sm font-semibold block">
          사진 첨부 {images.length} / 5
        </label>

        <div className="flex gap-3 flex-wrap">
          {images.length < MAX_IMAGES && (
            <label className="w-24 h-24 border-2 border-dashed rounded-md flex items-center justify-center cursor-pointer hover:bg-gray-50">
              <span className="text-3xl text-gray-400">＋</span>
              <input
                type="file"
                accept="image/*"
                className="hidden"
                multiple
                onChange={handleImageUpload}
              />
            </label>
          )}

          {images.map((img, index) => (
            <div key={index} className="relative w-24 h-24">
              <img
                src={URL.createObjectURL(img)}
                className="w-full h-full object-cover rounded-lg"
                alt="preview"
              />
              <button
                className="absolute top-1 right-1 cursor-pointer bg-black/60 text-white text-xs px-1 rounded"
                onClick={() => removeImage(index)}
              >
                ✕
              </button>
            </div>
          ))}
        </div>
      </div>

      <button
        onClick={handleSubmit}
        className="w-full mt-6 py-3 cursor-pointer bg-indigo-400 text-white rounded-lg hover:bg-indigo-500 transition"
      >
        등록
      </button>
    </div>
  );
}
