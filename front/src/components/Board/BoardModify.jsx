import axios from "axios";
import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

const BoardModify = () => {
  const navigate = useNavigate();

  const { id } = useParams();
  const [post, setPost] = useState(null);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  useEffect(() => {
    const getRow = async () => {
      try {
        const res = await axios.get(`http://localhost:8080/board/${id}`);
        console.log(res);
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

  useEffect(() => {
    if (post) {
      setTitle(post.title);
      setContent(post.content);
    }
  }, [post]);
  const updateRow = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.put(`/board/modify/${id}`, { title, content });
      navigate(`/board/${res.data.id}`);
    } catch (error) {
      console.error("수정 에러 : ", error);
    }
  };
  return (
    <div className="min-h-screen bg-white flex items-center justify-center px-6 py-10">
      <form onSubmit={updateRow} className="w-full max-w-2xl space-y-6">
        <h2 className="text-3xl font-semibold text-gray-800 text-center">게시글 작성</h2>

        <div className="space-y-2">
          <label htmlFor="title" className="block text-sm font-medium text-gray-600">
            제목
          </label>
          <input
            id="title"
            name="title"
            type="text"
            placeholder="제목을 입력하세요"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full border border-gray-300 rounded-md p-3 focus:outline-none focus:ring-2 focus:ring-black transition"
          />
        </div>

        <div className="space-y-2">
          <label htmlFor="content" className="block text-sm font-medium text-gray-600">
            내용
          </label>
          <textarea
            id="content"
            name="content"
            rows="10"
            placeholder="내용을 입력하세요"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            className="w-full border border-gray-300 rounded-md p-3 focus:outline-none focus:ring-2 focus:ring-black transition resize-none"
          />
        </div>

        <div className="text-right">
          <button
            type="submit"
            className="bg-black text-white px-6 py-3 rounded-md font-medium hover:bg-gray-800 transition"
          >
            수정 완료
          </button>
        </div>
      </form>
    </div>
  );
};

export default BoardModify;
