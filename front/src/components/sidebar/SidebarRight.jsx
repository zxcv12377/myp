import axios from "axios";
import { useEffect, useState } from "react";

export default function SidebarRight() {
  const [boards, setBoards] = useState([]);

  const findRecentlyBoard = async () => {
    try {
      const res = await axios.get("/board/top5");
      setBoards(res.data);
    } catch (error) {
      console.log("최근 게시물 에러", error);
    }
  };

  useEffect(() => {
    findRecentlyBoard();
  }, []);

  return (
    <div className="flex justify-end bg-white border-gray-200 border-l-1">
      {/* 사이드바 */}
      <aside className="hidden lg:flex flex-col w-72 h-screen pt-10 pr-7 pl-7 space-y-6 sticky top-0">
        {/* 최신 게시글 */}
        <div className="bg-white border border-gray-200 rounded-lg p-4 shadow-sm">
          <h2 className="text-lg font-semibold text-gray-800 mb-3">📌 최근 게시글</h2>
          {boards.length === 0 ? (
            <div>최근 게시물이 없습니다...</div>
          ) : (
            boards.map((board, index) => (
              <ul key={index} className="space-y-2 text-sm text-gray-700">
                <li className="truncate hover:text-indigo-600 cursor-pointer mt-2 mb-2 " style={{ maxWidth: "165px" }}>
                  {index + 1}. {board.title}
                </li>
              </ul>
            ))
          )}
        </div>
        {/* 오늘의 게시글 */}
        <div className="bg-white border border-gray-200 rounded-lg p-4 shadow-sm">
          <h2 className="text-lg font-semibold text-gray-800 mb-3">📌 오늘의 게시글</h2>
          <ul className="space-y-2 text-sm text-gray-700">
            <li className="hover:text-indigo-600 cursor-pointer">🔥 최신글 1</li>
            <li className="hover:text-indigo-600 cursor-pointer">🔥 최신글 2</li>
            <li className="hover:text-indigo-600 cursor-pointer">🔥 최신글 3</li>
          </ul>
        </div>
      </aside>
    </div>
  );
}
