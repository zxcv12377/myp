import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, useParams } from "react-router-dom";
import CommentList from "../comment/CommentList";

const BoardRow = () => {
  const navigate = useNavigate();

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

  const deleteRow = async () => {
    try {
      alert("게시글을 삭제하시겠습니까?");
      await axios.delete(`http://localhost:8080/board/${id}`);
      alert("게시글이 삭제 되었습니다.");
      navigate("/");
    } catch (error) {
      console.error("삭제 에러 : ", error);
    }
  };

  const moveModifyHandler = async () => {
    navigate(`/board/modify/${id}`);
  };

  const moveListHandler = () => {
    navigate("/");
  };

  if (!post) return <div className="p-8 text-gray-500">로딩 중...</div>;

  return (
    <div className="bg-white min-h-screen py-10 px-6">
      <div className="max-w-7xl mx-auto flex flex-col lg:flex-row gap-8">
        {/* 본문 */}
        <div className="flex-[3]">
          <header className="border-b border-gray-200 pb-5 mb-8">
            <h1 className="text-3xl font-bold text-indigo-900">{post.title}</h1>
            <p className="text-sm text-gray-500 mt-1">작성일: {new Date(post.createdDate).toLocaleString("ko-KR")}</p>
          </header>
          <div>
            <main className="text-gray-800 text-[1.05rem] leading-relaxed whitespace-pre-line">{post.content}</main>
          </div>

          <div className="flex justify-between mt-8">
            {/* 왼쪽: 목록 버튼 */}
            <div>
              <button
                type="button"
                onClick={moveListHandler}
                className="px-4 py-0.5 text-gray-700 bg-gray-100 rounded hover:bg-gray-200 transition-colors duration-200 font-bold mr-1.5"
              >
                목록
              </button>
            </div>

            {/* 오른쪽: 수정, 삭제 버튼 */}
            <div className="flex">
              <button
                type="button"
                onClick={moveModifyHandler}
                className="px-2 py-1 bg-yellow-100 text-yellow-800 rounded hover:bg-yellow-200 transition mr-2"
              >
                수정
              </button>
              <button
                type="button"
                className="px-2 py-1 bg-red-100 text-red-700 rounded hover:bg-red-200 transition"
                onClick={deleteRow}
              >
                삭제
              </button>
            </div>
          </div>

          {/* 댓글 */}
          <section className="mt-6 border-t pt-6">
            <h2 className="text-xl font-semibold text-gray-700 mb-4">💬 댓글</h2>
            <CommentList postId={id} />
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
