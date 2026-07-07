export default function DayCheck({
  lectureStartTime,
  lectureEndTime,
  min,
  onChange,
  ref,
}) {
  return (
    <>
      <input
        type="datetime-local"
        className="border border-gray-300 rounded-lg p-2 focus:border-blue-500 focus:outline-none"
        id="lectureStartTime"
        value={lectureStartTime}
        min={min}
        onChange={(e) => onChange('lectureStartTime', e.target.value)}
      />
      <input
        ref={ref}
        type="datetime-local"
        className="border border-gray-300 rounded-lg p-2 focus:border-blue-500 focus:outline-none"
        id="lectureEndTime"
        value={lectureEndTime}
        min={min}
        onChange={(e) => onChange('lectureEndTime', e.target.value)}
      />
    </>
  );
}
