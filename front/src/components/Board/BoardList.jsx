import axios from "axios";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const BoardList = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [posts, setPosts] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;
  const navigate = useNavigate();
  const ViewList = async (page, size) => {
    try {
      const res = await axios.get("/board/page", {
        params: { page, size },
      });
      const list = Array.isArray(res.data) ? res.data : res.data.content || [];
      setPosts(list);
    } catch (error) {
      console.error("게시글 불러오기 실패 : ", error);
    }
  };

  const pageHandler = (id) => {
    navigate(`/board/${id}`);
  };

  const boardCreateHandler = () => {
    if (isLoggedIn) {
      navigate("/board/create");
    } else {
      const message = "게시글을 작성하려면 로그인이 필요합니다.\n 로그인 하시겠습니까?";
      if (confirm(message)) {
        navigate("/login");
      }
    }
  };

  useEffect(() => {
    ViewList(currentPage, pageSize);
  }, [currentPage]);

  useEffect(() => {
    const checkAuth = () => setIsLoggedIn(!!localStorage.getItem("accessToken"));

    checkAuth();
    window.addEventListener("storage", checkAuth);
    return () => window.removeEventListener("storage", checkAuth);
  }, []);

  return (
    <div className="min-h-screen bg-white p-8">
      <h2 className="text-2xl font-bold mb-6 text-gray-800 border-b pb-2">📋 게시글 목록</h2>

      <div className="overflow-x-auto">
        <table className="min-w-full table-auto text-sm border-collapse">
          <thead>
            <tr className="bg-gray-100 text-gray-700 border-y border-gray-300">
              <th className="px-4 py-2 text-left w-20">번호</th>
              <th className="px-4 py-2 text-left">제목</th>
              <th className="px-4 py-2 text-center w-32">작성자</th>
              <th className="px-4 py-2 text-center w-32">작성일</th>
              <th className="px-4 py-2 text-center w-20">조회수</th>
              <th className="px-4 py-2 text-center w-20">추천</th>
            </tr>
          </thead>
          <tbody>
            {posts.length === 0 ? (
              <tr>
                <td colSpan="6" className="text-center py-6 text-gray-500">
                  게시글이 없습니다.
                </td>
              </tr>
            ) : (
              posts.map((item) => (
                <tr
                  key={item.id}
                  className="hover:bg-gray-50 border-b border-gray-200 cursor-pointer"
                  onClick={() => pageHandler(item.id)}
                >
                  <td className="px-4 py-2">{item.id}</td>
                  <td className="px-4 py-2 text-gray-700 hover:underline">{item.title}</td>
                  <td className="px-4 py-2 text-center">{item.nickname || "익명"}</td>
                  <td className="px-4 py-2 text-center">
                    {new Date(item.createdDate).toLocaleDateString("ko-KR", {
                      year: "2-digit",
                      month: "2-digit",
                      day: "2-digit",
                    })}
                  </td>
                  <td className="px-4 py-2 text-center">{item.viewCount || 0}</td>
                  <td className="px-4 py-2 text-center">{item.likesCount || 0}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
        <div className="flex justify-end mt-4">
          <button
            type="button"
            onClick={boardCreateHandler}
            className="px-4 py-2 text-gray-700 bg-gray-100 rounded cursor-not-allowed hover:bg-gray-200 transition-colors duration-200 font-bold"
          >
            게시글 작성
          </button>
        </div>
        <div className="flex justify-center mt-4 space-x-2">
          <button
            onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
            className="px-3 py-1 bg-gray-200 rounded"
          >
            이전
          </button>
          <span className="px-3 py-1">{currentPage}</span>
          <button onClick={() => setCurrentPage((prev) => prev + 1)} className="px-3 py-1 bg-gray-200 rounded">
            다음
          </button>
        </div>
      </div>
    </div>
  );
};

export default BoardList;
