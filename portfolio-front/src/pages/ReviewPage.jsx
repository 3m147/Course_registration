import ReviewWrite from '../components/ReviewWrite';
import ReviewList from '../components/ReviewList';
import { useState } from 'react';

function ReviewPage({ username, lectureId }) {
  const [reload, setReload] = useState(false);

  const handleReload = () => {
    setReload(!reload);
  };
  return (
    <div>
      <ReviewWrite
        username={username}
        lectureId={lectureId}
        onSuccess={handleReload}
      />

      <ReviewList lectureId={lectureId} key={reload} />
    </div>
  );
}

export default ReviewPage;
