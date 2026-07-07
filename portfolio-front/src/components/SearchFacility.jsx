import { useState, useEffect } from 'react';
import useDebounce from '../hook/useDebounce';

export default function SearchFacility({ handleFacility, ref }) {
  const [facility, setFacility] = useState({
    name: '',
    addr: '',
    lat: '',
    lot: '',
  });
  const [filteredFacility, setFilteredFacility] = useState([]);
  const [isOpen, setIsOpen] = useState(false);

  // facility를 디바운스
  const debouncedValue = useDebounce(facility.name, 250);

  const handleInput = (e) => {
    setFacility({
      name: e.target.value,
      addr: '',
      lat: '', // Y
      lot: '', // X
    });
    setIsOpen(true);
  };

  const handleSelect = (item) => {
    setFacility({
      name: item.name,
      addr: item.addr,
      lat: item.lat, // Y
      lot: item.lot, // X
    });
    setFilteredFacility([]);
    setIsOpen(false);
    if (handleFacility) {
      handleFacility(item);
    }
  };

  // debouncedValue가 바뀔 때 필터링
  useEffect(() => {
    if (isOpen && debouncedValue && debouncedValue.length >= 2) {
      fetchFacility(debouncedValue);
    } else {
      setFilteredFacility([]);
    }
  }, [debouncedValue, isOpen]);

  async function fetchFacility(value) {
    const serviceKey =
      '976ceea673c423334335660cf66cd0be0f6970d7fd2d69c08e7d779aa83bf651';
    const url = `https://apis.data.go.kr/B551014/SRVC_SFMS_FACIL_INFO/TODZ_SFMS_FACIL_INFO?serviceKey=${serviceKey}&pageNo=1&numOfRows=10&resultType=json&faci_nm=${value}`;

    try {
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error('API 요청 실패');
      }
      const data = await response.json();

      const items = data?.response?.body?.items?.item || [];
      const facilityData = items.map((item) => ({
        name: item.faci_nm,
        addr: item.faci_road_addr,
        lat: item.faci_lat, // Y
        lot: item.faci_lot, // X
      }));
      // console.log(items);
      setFilteredFacility(facilityData);
    } catch (error) {
      console.error(error);
      setFilteredFacility([]);
    }
  }

  return (
    <div className="relative w-full mb-5">
      <input
        type="text"
        ref={ref}
        value={facility.name}
        onChange={handleInput}
        placeholder="장소를 입력하세요"
        className="w-full border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
      />
      {isOpen && filteredFacility.length > 0 && (
        <ul className="absolute w-full bg-white border border-gray-300 rounded-md mt-1 max-h-40 overflow-y-auto z-10">
          {filteredFacility.map((item, idx) => (
            <li
              key={idx}
              className="p-2 hover:bg-blue-100 cursor-pointer"
              onClick={() => handleSelect(item)}
            >
              <span>{item.name}</span>
              <span className="text-gray-400 text-sm ml-2">{item.addr}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
