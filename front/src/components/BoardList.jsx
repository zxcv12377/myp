import axios from "axios";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const BoardList = () => {
  const [posts, setPosts] = useState([]);
  const navigate = useNavigate();
  const ViewList = async () => {
    try {
      const res = await axios.get("http://localhost:8080/board/");
      console.log(res.data);
      setPosts(res.data);
    } catch (error) {
      console.error("게시글 불러오기 실패 : ", error);
    }
  };

  const pageHandler = (id) => {
    navigate(`/board/${id}`);
  };

  useEffect(() => {
    ViewList();
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
              posts.map((item, index) => (
                <tr
                  key={item.id}
                  className="hover:bg-gray-50 border-b border-gray-200 cursor-pointer"
                  onClick={() => pageHandler(item.id)}
                >
                  <td className="px-4 py-2">{posts.length - index}</td>
                  <td className="px-4 py-2 text-blue-700 hover:underline">{item.title}</td>
                  <td className="px-4 py-2 text-center">{item.writer || "익명"}</td>
                  <td className="px-4 py-2 text-center">
                    {new Date(item.createdDate).toLocaleDateString("ko-KR", {
                      year: "2-digit",
                      month: "2-digit",
                      day: "2-digit",
                    })}
                  </td>
                  <td className="px-4 py-2 text-center">{item.views || 0}</td>
                  <td className="px-4 py-2 text-center">{item.likes || 0}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default BoardList;
