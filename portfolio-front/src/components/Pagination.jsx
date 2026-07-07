export default function Pagination({
  pageNo,
  totalPageCnt,
  paging,
  startNumOfCurrentPagingBlock,
  endNumOfCurrentPagingBlock,
}) {
  const pageNumbers = [];
  for (
    let i = startNumOfCurrentPagingBlock;
    i <= endNumOfCurrentPagingBlock;
    i++
  ) {
    pageNumbers.push(i);
  }

  return (
    <div className="flex justify-center gap-2 mt-6">
      <button
        onClick={() => paging(pageNo - 1)}
        disabled={pageNo === 1}
        className="px-3 py-2 rounded-md border border-gray-300 text-sm font-medium transition disabled:opacity-50 disabled:cursor-not-allowed"
      >
        ◀
      </button>
      {pageNumbers.map((num) => (
        <button
          key={num}
          value={num}
          onClick={(e) => paging(Number(e.target.value))}
          className={`px-4 py-2 rounded-md border text-sm font-medium transition
            ${
              pageNo === num
                ? 'bg-blue-600 text-white border-blue-600'
                : 'bg-white text-gray-700 border-gray-300 hover:bg-blue-50'
            }
          `}
        >
          {num}
        </button>
      ))}
      <button
        onClick={() => paging(pageNo + 1)}
        disabled={pageNo === totalPageCnt}
        className="px-3 py-2 rounded-md border border-gray-300 text-sm font-medium transition disabled:opacity-50 disabled:cursor-not-allowed"
      >
        ▶
      </button>
    </div>
  );
}
