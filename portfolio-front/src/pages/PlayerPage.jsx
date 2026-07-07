import { useState } from 'react';
import HomePage from './HomePage';
import { useParams } from 'react-router';
import { LectureListProvider } from '../context/LectureListContext';
import History from '../components/History';

export default function PlayerPage() {
  const [tab, setTab] = useState('histories');
  const { playerName } = useParams();

  return (
    <div className="max-w-5xl mx-auto mt-8 bg-white  rounded-lg shadow-sm">
      <div className="px-6 pt-4">
        <nav className="flex gap-6">
          <button
            onClick={() => setTab('histories')}
            className={
              tab === 'histories'
                ? 'text-blue-500 border-b-2 border-blue-500'
                : 'text-gray-500'
            }
          >
            선수이력
          </button>

          <button
            onClick={() => setTab('lecture')}
            className={
              tab === 'lecture'
                ? 'text-blue-500 border-b-2 border-blue-500 flex items-center gap-2'
                : 'text-gray-500 flex items-center gap-2'
            }
          >
            강의목록
          </button>
        </nav>
      </div>
      <div className="p-6">
        {tab === 'histories' && <History playerName={playerName} />}
        {tab === 'lecture' && (
          <LectureListProvider playerName={playerName}>
            <HomePage />
          </LectureListProvider>
        )}
      </div>
    </div>
  );
}
