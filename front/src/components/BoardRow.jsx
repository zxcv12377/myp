import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";

const BoardRow = () => {
  const { id } = useParams();
  const [post, setPost] = useState(null);

  useEffect(() => {
    const getRow = async () => {
      try {
        const res = await axios.get(`http://localhost:8080/board/${id}`);
        setPost(res.data);
        if (!res) {
          console.error("존재하지 않은 게시물 입니다");
        }
      } catch (error) {
        console.error("게시글 에러", error);
      }
    };
    getRow();
  }, [id]);
  //  bg-gray-500
  if (!post) return <div className="p-8 text-gray-500">로딩 중...</div>;

  return (
    <div className="bg-white min-h-screen py-10 px-6">
      <div className="max-w-7xl mx-auto flex flex-col lg:flex-row gap-8">
        {/* 본문 */}
        <div className="flex-[3]">
          <header className="border-b border-gray-200 pb-5 mb-8">
            <h1 className="text-3xl font-bold text-indigo-600">{post.title}</h1>
            <p className="text-sm text-gray-500 mt-1">작성일: {new Date(post.createdDate).toLocaleString("ko-KR")}</p>
          </header>

          <main className="text-gray-800 text-[1.05rem] leading-relaxed whitespace-pre-line">{post.content}</main>

          {/* 댓글 */}
          <section className="mt-12 border-t pt-6">
            <h2 className="text-xl font-semibold text-gray-700 mb-4">💬 댓글</h2>
            <div className="text-gray-500">댓글 컴포넌트가 여기에 들어갑니다.</div>
          </section>
        </div>

        {/* 사이드바 */}
        <aside className="flex-[1] w-[300px] hidden lg:block">
          <div className="bg-white border border-gray-200 rounded-lg p-4 shadow-sm sticky top-10">
            <h2 className="text-lg font-semibold text-gray-800 mb-3">📌 최신 게시글</h2>
            <ul className="space-y-2 text-sm text-gray-700">
              <li className="hover:text-indigo-600 cursor-pointer">🔥 최신글 1</li>
              <li className="hover:text-indigo-600 cursor-pointer">🔥 최신글 2</li>
              <li className="hover:text-indigo-600 cursor-pointer">🔥 최신글 3</li>
            </ul>
          </div>
        </aside>
      </div>
    </div>
  );
};

export default BoardRow;
