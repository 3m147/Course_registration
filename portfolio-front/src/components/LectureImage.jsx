import { useState } from 'react';

export default function LectureImage({
  onChange,
  maxWidth = 800,
  maxHeight = 800,
  initialImages = [],
}) {
  const [previews, setPreviews] = useState(initialImages);
  const [base64Images, setBase64Images] = useState(initialImages);
  const [mainIndex, setMainIndex] = useState(0);

  const resizeAndConvertToBase64 = (file) => {
    return new Promise((resolve) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = (event) => {
        const img = new Image();
        img.src = event.target.result;

        img.onload = () => {
          let { width, height } = img;
          // 최대 크기 제한
          if (width > maxWidth) {
            height = (height * maxWidth) / width;
            width = maxWidth;
          }
          if (height > maxHeight) {
            width = (width * maxHeight) / height;
            height = maxHeight;
          }

          const canvas = document.createElement('canvas');
          canvas.width = width;
          canvas.height = height;
          const ctx = canvas.getContext('2d');
          ctx.drawImage(img, 0, 0, width, height);
          const base64 = canvas.toDataURL('image/jpeg', 0.8); // 압축 품질 0.8
          resolve(base64);
        };
      };
    });
  };

  const handleFileChange = async (e) => {
    const files = Array.from(e.target.files);

    const newPreviews = files.map((file) => URL.createObjectURL(file));
    setPreviews((prev) => [...prev, ...newPreviews]);

    // Base64 변환
    const newBase64s = await Promise.all(
      files.map((f) => resizeAndConvertToBase64(f))
    );

    const updatedBase64Images = [...base64Images, ...newBase64s];
    setBase64Images(updatedBase64Images);

    onChange({ images: updatedBase64Images, mainIndex });
  };

  const handleDelete = (index) => {
    const updatedPreviews = previews.filter((_, i) => i !== index);
    const updatedBase64s = base64Images.filter((_, i) => i !== index);

    setPreviews(updatedPreviews);
    setBase64Images(updatedBase64s);

    let newMainIndex = mainIndex;
    if (mainIndex === index) newMainIndex = 0;
    else if (mainIndex > index) newMainIndex -= 1;
    setMainIndex(newMainIndex);

    onChange({ images: updatedBase64s, mainIndex: newMainIndex });
  };

  return (
    <div className="w-full">
      <label className="block w-full border border-gray-300 rounded-lg p-3 mb-4 cursor-pointer bg-gray-50 text-center text-gray-600 hover:bg-gray-100">
        이미지 업로드 (여러 장 가능)
        <input
          type="file"
          className="hidden"
          multiple
          accept="image/*"
          onChange={handleFileChange}
          value={undefined}
        />
      </label>

      {previews.length > 0 && (
        <div className="grid grid-cols-2 gap-4">
          {previews.map((src, index) => (
            <div
              key={index}
              className="relative border rounded-lg overflow-hidden"
            >
              <img
                src={src}
                alt=""
                className="w-full h-40 object-cover bg-gray-200"
              />

              <button
                type="button"
                onClick={() => handleDelete(index)}
                className="absolute top-2 right-2 bg-black/60 text-white text-xs px-2 py-1 rounded"
              >
                X
              </button>

              <div className="absolute bottom-2 left-2 bg-white/80 px-2 py-1 rounded text-xs flex items-center gap-1">
                <input
                  type="radio"
                  name="mainImg"
                  checked={mainIndex === index}
                  onChange={() => {
                    setMainIndex(index);
                    onChange({ images: base64Images, mainIndex: index });
                  }}
                />
                메인
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
