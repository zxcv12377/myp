import axios from "axios";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import DribbleButton from "./../UI/DribbleButton";

export default function LoginForm() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  // 인터셉터는 한 번만 등록
  useEffect(() => {
    const reqInterceptor = axios.interceptors.request.use((config) => {
      const token = localStorage.getItem("accessToken");
      if (token) config.headers.Authorization = `Bearer ${token}`;
      return config;
    });

    const resInterceptor = axios.interceptors.response.use(
      (res) => res,
      async (err) => {
        if (err.response?.status === 401) {
          // 1) 로컬스토리지에서 refreshToken 꺼내기
          const refreshToken = localStorage.getItem("refreshToken");
          // 2) 없으면 재발급 불가 → 로그인 페이지로 강제 리다이렉트
          if (!refreshToken) {
            window.location.href = "/login";
            return Promise.reject(err);
          }

          try {
            // 3) refreshToken 이 있을 때만 재발급 시도
            const { data } = await axios.post("/auth/refresh", { refreshToken });

            // 4) 새 토큰 저장하고, 원래 요청 retry
            localStorage.setItem("accessToken", data.accessToken);
            err.config.headers.Authorization = `Bearer ${data.accessToken}`;
            return axios(err.config);
          } catch (refreshErr) {
            // 5) 재발급 실패했으면 토큰들 삭제하고 로그인 페이지로
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            window.location.href = "/login";
            return Promise.reject(refreshErr);
          }
        }
        return Promise.reject(err);
      }
    );

    return () => {
      axios.interceptors.request.eject(reqInterceptor);
      axios.interceptors.response.eject(resInterceptor);
    };
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const { data } = await axios.post("/auth/login", { email, password });
      localStorage.setItem("accessToken", data.accessToken);
      localStorage.setItem("refreshToken", data.refreshToken);
      setMessage("로그인 성공!");
      // 필요하면 리다이렉트
      navigate("/");
    } catch (err) {
      setMessage("로그인 실패: " + (err.response?.data || err.message));
    }
  };

  return (
    <div className="max-w-sm mx-auto mt-10 p-6 bg-white shadow rounded">
      <h2 className="text-2xl font-semibold mb-4">로그인</h2>
      {message && <p className="mb-4 text-center text-red-500">{message}</p>}
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block mb-1">이메일</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            className="w-full p-2 border rounded"
          />
        </div>
        <div>
          <label className="block mb-1">비밀번호</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            className="w-full p-2 border rounded"
          />
        </div>
        <DribbleButton type={"submit"} content={"로그인"}></DribbleButton>
        {/* <button type="submit" className="w-full py-2 bg-blue-600 text-white rounded hover:bg-blue-700">
          로그인
        </button> */}
      </form>
    </div>
  );
}
