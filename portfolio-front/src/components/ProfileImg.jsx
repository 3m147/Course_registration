import { useRef } from 'react';

function ProfileImg({ preview, onFileChange, onDelete, readOnly }) {
  const fileInputRef = useRef(null);

  const handleButtonClick = () => {
    fileInputRef.current.click();
  };

  return (
    <div className="flex flex-col items-start gap-4">
      <div className="w-32 h-32 rounded-full overflow-hidden bg-gray-200 border border-gray-300">
        {preview ? (
          <img
            src={preview}
            alt="프로필 미리보기"
            className="w-full h-full object-cover"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center text-gray-400">
  
          </div>
        )}
      </div>

      {!readOnly && (
        <div className="flex gap-2">
          <input
            type="file"
            accept="image/*"
            onChange={onFileChange}
            ref={fileInputRef}
            className="hidden"
          />
          <button
            type="button"
            onClick={handleButtonClick}
            className="px-3 py-1.5 cursor-pointer border border-gray-300 text-sm text-gray-700 bg-white hover:bg-gray-50 transition"
          >
            사진변경
          </button>
          <button
            type="button"
            onClick={onDelete}
            className="px-3 py-1.5 cursor-pointer border border-gray-300 text-sm text-gray-700 bg-white hover:bg-gray-50 transition"
          >
            삭제
          </button>
        </div>
      )}
    </div>
  );
}

export default ProfileImg;
