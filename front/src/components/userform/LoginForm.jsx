import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function LoginForm() {
  const [form, setForm] = useState({ email: "", password: "" });
  const [message, setMessage] = useState("");
  const navigete = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post("http://localhost:8080/auth/login", form);
      setMessage("인증 이메일을 발송했습니다. 이메일을 확인해주세요.");
      setForm({ email: "", password: "" });
      navigete("/");
    } catch (err) {
      setMessage(err.response?.data || "회원가입에 실패했습니다.");
    }
  };

  const moveLogin = () => {
    navigete("/register");
  };

  return (
    <div className="max-w-md mx-auto mt-8 p-6 bg-white border border-gray-200 rounded-lg shadow">
      <h2 className="text-2xl font-semibold mb-4 text-gray-900">회원가입</h2>
      {message && <div className="mb-4 text-center text-sm text-green-600">{message}</div>}
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label htmlFor="email" className="block text-gray-700 mb-1">
            이메일
          </label>
          <input
            type="email"
            id="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            required
            className="w-full p-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
        </div>
        <div>
          <label htmlFor="password" className="block text-gray-700 mb-1">
            비밀번호
          </label>
          <input
            type="password"
            id="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            required
            minLength={6}
            className="w-full p-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
        </div>
        <div className="flex">
          <button
            type="submit"
            className="w-full py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition mr-1"
          >
            로그인
          </button>
          <button
            onClick={moveLogin}
            className="w-full py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
          >
            회원가입
          </button>
        </div>
      </form>
    </div>
  );
}
