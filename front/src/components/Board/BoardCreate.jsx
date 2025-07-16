import axios from "axios";
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const BoardCreate = () => {
  const navigate = useNavigate();

  const [form, setForm] = useState({ title: "", content: "" });
  const [file, setFile] = useState(null);

  const handleChange = (e) => {
    const { name, value, files } = e.target;
    if (name === "image") setFile(files[0]);
    else setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handlesSubmit = async (e) => {
    e.preventDefault();
    const data = new FormData();
    data.append("title", form.title);
    data.append("content", form.content);
    if (file) data.append("image", file);
    try {
      const res = await axios.post("/board/create", data);
      navigate(`/board/${res.data.id}`);
    } catch (error) {
      console.error("에러 : ", error);
    }
  };
  return (
    <div className="min-h-screen bg-white flex items-center justify-center px-6 py-10">
      <form onSubmit={handlesSubmit} className="w-full max-w-2xl space-y-6">
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
            value={form.title}
            onChange={handleChange}
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
            value={form.content}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded-md p-3 focus:outline-none focus:ring-2 focus:ring-black transition resize-none"
          />
        </div>
        <div>
          <input id="image" type="file" name="image" accept="image/*" onChange={handleChange} className="hidden" />
          <label
            htmlFor="image"
            className="inline-block bg-black text-white px-4 py-2 rounded-md cursor-pointer hover:bg-gray-800 transition"
          >
            이미지 첨부
          </label>
          {/* 선택된 파일 이름 표시 (선택 사항) */}
          {file && <span className="ml-2 text-sm text-gray-600">{file.name}</span>}
        </div>

        <div className="text-right">
          <button
            type="submit"
            className="bg-black text-white px-6 py-3 rounded-md font-medium hover:bg-gray-800 transition"
          >
            작성 완료
          </button>
        </div>
      </form>
    </div>
  );
};

export default BoardCreate;
