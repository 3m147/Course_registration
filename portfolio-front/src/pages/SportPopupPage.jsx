import axios from 'axios';
import { useEffect, useState } from 'react';
import { apiUrl } from '../lib/api';

export default function SportPopupPage() {
  const [sports, setSports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    axios.get(apiUrl('/api/sports'))
      .then(res => {
        setSports(res.data);
        setLoading(false);
      })
      .catch(err => {
        console.error('Failed to fetch sports:', err);
        setError('종목 정보를 불러오는데 실패했습니다.');
        setLoading(false);
      });
  }, []);

  const handleSelect = (sport) => {
    if (window.opener) {
      window.opener.postMessage({ type: 'SPORT_SELECTED', sport }, window.location.origin);
      window.close();
    } else {
        alert("부모 창을 찾을 수 없습니다.");
    }
  };


  const searchResults = searchTerm 
    ? sports.filter(s => 
        s.mainName.includes(searchTerm) || 
        s.subName.includes(searchTerm)
      )
    : [];

  const categories = [...new Set(sports.map(s => s.mainName))];
  const filteredSports = selectedCategory 
    ? sports.filter(s => s.mainName === selectedCategory)
    : [];

  return (
    <div className="flex flex-col h-screen bg-white">
        <div className="p-4 border-b-2 border-black bg-white">
            <div className="relative flex items-center">
                <input 
                    type="text"
                    placeholder="예) 축구, 야구, 구기..."
                    className="w-full p-3 pr-10 border border-gray-300 focus:outline-none focus:border-blue-500 text-lg"
                    value={searchTerm}
                    onChange={(e) => {
                        setSearchTerm(e.target.value);
                        setSelectedCategory(null); 
                    }}
                    autoFocus
                />
               
            </div>
        </div>


        <div className="flex-1 overflow-y-auto bg-white p-4">
            {loading && <div className="text-center py-4">로딩 중...</div>}
            {error && <div className="text-center text-red-500 py-4">{error}</div>}
            
            {!loading && !error && (
                <>

                    {searchTerm && (
                        <div>
                            <h3 className="text-sm text-gray-500 mb-2 font-bold">검색 결과</h3>
                            {searchResults.length > 0 ? (
                                searchResults.map(sport => (
                                    <div 
                                        key={sport.sportId}
                                        className="p-3 border-b hover:bg-gray-50 cursor-pointer flex justify-between items-center"
                                        onClick={() => handleSelect(sport)}
                                    >
                                        <span className="font-medium text-lg">{sport.subName}</span>
                                        <span className="text-sm text-gray-500">{sport.mainName}</span>
                                    </div>
                                ))
                            ) : (
                                <p className="text-gray-500">검색 결과가 없습니다.</p>
                            )}
                        </div>
                    )}

                    {!searchTerm && (
                        <>
                            <div className="mb-2 flex items-center justify-between">
                                {selectedCategory ? (
                                    <button 
                                        onClick={() => setSelectedCategory(null)}
                                        className="text-blue-600 font-bold flex items-center hover:underline cursor-pointer"
                                    >
                                        &lt; 전체 카테고리
                                    </button>
                                ) : (
                                    <h3 className="text-sm text-gray-500 font-bold">카테고리 선택</h3>
                                )}
                            </div>

                            {!selectedCategory ? (
                                <div className="grid grid-cols-2 gap-2">
                                    {categories.map(category => (
                                        <div 
                                            key={category}
                                            className="p-4 border hover:border-blue-500 hover:bg-blue-50 cursor-pointer flex justify-center items-center bg-gray-50 text-gray-700 font-medium"
                                            onClick={() => setSelectedCategory(category)}
                                        >
                                            {category}
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <div>
                                    <h3 className="text-lg font-bold mb-3 border-b pb-2">{selectedCategory}</h3>
                                    {filteredSports.map(sport => (
                                        <div 
                                            key={sport.sportId}
                                            className="p-3 border-b hover:bg-gray-50 cursor-pointer"
                                            onClick={() => handleSelect(sport)}
                                        >
                                            {sport.subName}
                                        </div>
                                    ))}
                                </div>
                            )}
                        </>
                    )}
                </>
            )}
        </div>
    </div>
  );
}
