import { Link, useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import axios from "axios";
import GlitchButton from "../UI/GlitchButton";
import SearchButton from "../UI/SearchButton";

export default function Navbar() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const navigate = useNavigate();
  const { pathname } = useLocation();

  useEffect(() => {
    // const checkAuth = () =>
    // checkAuth();
    // window.addEventListener("storage", checkAuth);
    // return () => window.removeEventListener("storage", checkAuth);
    setIsLoggedIn(!!localStorage.getItem("accessToken"));
  }, [pathname]);

  const handleLogout = async () => {
    try {
      await axios.post(
        "/auth/logout",
        { refreshToken: localStorage.getItem("refreshToken") },
        { withCredentials: false }
      );
    } catch (e) {
      console.warn("로그아웃 요청 실패:", e);
    } finally {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      setIsLoggedIn(false);
      navigate("/login", { replace: true });
    }
  };

  const handleLogin = () => {
    navigate("/login");
  };
  const handleRegister = () => {
    navigate("/register");
  };

  return (
    // #4f39f6 indigo-600 컬러코드
    <nav className="bg-indigo-600 shadow-md">
      <div className="container mx-auto px-4 py-3 flex justify-between items-center">
        {/* 로고 */}
        <Link to="/" className="text-2xl font-bold text-gray-200 hover:text-gray-300">
          📝 게시판
        </Link>
        {/* 데스크탑 메뉴 */}
        <div>
          <form className="flex items-center space-x-2">
            <input
              type="text"
              placeholder="검색어를 입력하세요"
              className="w-48 sm:w-64 md:w-80 lg:w-96 xl:w-112 px-2 border border-gray-400 bg-gray-100 rounded h-10"
            />
            <SearchButton content="검색" type="submit"></SearchButton>
          </form>
        </div>
        <div className="md:flex space-x-6">
          {isLoggedIn ? (
            <div style={{ textAlign: "center" }}>
              <GlitchButton methods={handleLogout} color="btn-logout">
                로그아웃
              </GlitchButton>
            </div>
          ) : (
            <>
              {/* <div style={{ textAlign: "center" }}>
                <GlitchButton methods={handleLogout}>로그인</GlitchButton>
            </div> */}
              <div style={{ textAlign: "center" }}>
                <GlitchButton methods={handleLogin} color="btn-login">
                  로그인
                </GlitchButton>
              </div>
              <div style={{ textAlign: "center" }}>
                <GlitchButton methods={handleRegister} color="btn-register">
                  회원가입
                </GlitchButton>
              </div>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
