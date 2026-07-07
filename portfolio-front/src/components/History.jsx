import axios from 'axios';
import { useEffect, useState } from 'react';
import { apiUrl } from '../lib/api';

export default function History({ playerName }) {
  const [historyList, setHistoryList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  async function loadHistoryList() {
    try {
      const url = apiUrl(`/player/histories/${playerName}`);
      const resp = await axios.get(url);
      setHistoryList(resp.data);
    } catch (err) {
      console.error(err);
      setError('데이터를 불러오는 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadHistoryList();
  }, [playerName]);

  function formatDate(isoString) {
    if (!isoString) return '';
    return isoString.split('T')[0];
  }

  if (loading) return <p>로딩중</p>;
  if (error) return <p>{error}</p>;
  if (historyList.length === 0) return <p>이력이 없습니다.</p>;

  return (
    <>
      <ul className="list-disc pl-6">
        {historyList.map((history, idx) => (
          <li key={idx}>
            {history.content} ({formatDate(history.startDate)} ~{' '}
            {formatDate(history.endDate)})
          </li>
        ))}
      </ul>
    </>
  );
}
