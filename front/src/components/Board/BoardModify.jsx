import axios from "axios";
import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

const BoardModify = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [post, setPost] = useState(null);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [file, setFile] = useState(null);

  useEffect(() => {
    (async () => {
      try {
        const res = await axios.get(`/board/${id}`);
        setPost(res.data);
        setTitle(res.data.title);
        setContent(res.data.content);
      } catch (e) {
        console.error(e);
      }
    })();
  }, [id]);

  const handleChange = (e) => {
    if (e.target.name === "image") setFile(e.target.files[0]);
    else if (e.target.name === "title") setTitle(e.target.value);
    else setContent(e.target.value);
  };

  const updateRow = async (e) => {
    e.preventDefault();
    const data = new FormData();
    data.append("title", title);
    data.append("content", content);
    // 선택된 새 파일이 있으면 추가
    if (file) data.append("image", file);

    try {
      const res = await axios.put(`/board/modify/${id}`, data);
      navigate(`/board/${res.data.id}`);
    } catch (err) {
      console.error(err);
    }
  };

  if (!post) return null;

  return (
    <div className="min-h-screen flex items-center justify-center p-10 bg-white">
      <form onSubmit={updateRow} className="w-full max-w-2xl space-y-6">
        <h2 className="text-3xl font-semibold text-center">게시글 수정</h2>

        <div>
          <label className="block text-gray-600">제목</label>
          <input
            name="title"
            value={title}
            onChange={handleChange}
            className="w-full border rounded p-3 focus:ring-2 focus:ring-black"
          />
        </div>

        <div>
          <label className="block text-gray-600">내용</label>
          <textarea
            name="content"
            rows="10"
            value={content}
            onChange={handleChange}
            className="w-full border rounded p-3 focus:ring-2 focus:ring-black"
          />
        </div>

        {/* 기존 이미지 미리보기 */}
        {post.imagePath && (
          <div>
            <p className="text-sm text-gray-600">현재 이미지:</p>
            <img
              src={post.imagePath.startsWith("http") ? post.imagePath : `http://localhost:8080${post.imagePath}`}
              alt="현재 첨부 이미지"
              className="max-w-xs max-h-48 mb-4"
            />
          </div>
        )}

        {/* 새 이미지 선택 */}
        <div>
          <input id="image" type="file" name="image" accept="image/*" onChange={handleChange} className="hidden" />
          <label
            htmlFor="image"
            className="inline-block bg-black text-white px-4 py-2 rounded cursor-pointer hover:bg-gray-800"
          >
            새 이미지 선택
          </label>
          {file && <span className="ml-2 text-sm text-gray-600">{file.name}</span>}
        </div>

        <div className="text-right">
          <button className="bg-black text-white px-6 py-3 rounded hover:bg-gray-800">수정 완료</button>
        </div>
      </form>
    </div>
  );
};

export default BoardModify;
