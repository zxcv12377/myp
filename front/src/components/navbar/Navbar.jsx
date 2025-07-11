import { Link, useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import axios from "axios";
import "../../assets/css/testButton.css";
// import TButton from "./../Button";

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

  return (
    <nav className="bg-indigo-600 shadow-md">
      <div className="container mx-auto px-4 py-3 flex justify-between items-center">
        {/* 로고 */}
        <Link to="/" className="text-2xl font-bold text-gray-200 hover:text-gray-300">
          📝 게시판
        </Link>
        {/* 데스크탑 메뉴 */}
        <div className="hidden md:flex space-x-6">
          {isLoggedIn ? (
            // <button onClick={handleLogout} className="btn btn-small btn-dark btn-glitch">
            //   로그아웃
            //   {/*  text-red-500 hover:text-red-400 focus:outline-none*/}
            // </button>
            <button onClick={handleLogout} className="btn btn-small btn-dark btn-glitch">
              로그아웃
              {/*  text-red-500 hover:text-red-400 focus:outline-none*/}
            </button>
          ) : (
            // <TButton onClick={handleLogout} variant="glitch" glitchActive={true} size="small">
            //   로그아웃
            //   {/*  text-red-500 hover:text-red-400 focus:outline-none*/}
            // </TButton>
            <>
              <Link to="/login" className="text-gray-700 hover:text-gray-900">
                로그인
              </Link>
              <Link to="/register" className="text-gray-700 hover:text-gray-900">
                회원가입
              </Link>
            </>
          )}
        </div>
        {/* 모바일 메뉴 버튼 */}
        <div className="md:hidden">
          <button
            type="button"
            className="text-gray-700 hover:text-gray-900 focus:outline-none"
            onClick={() => document.getElementById("mobile-menu").classList.toggle("hidden")}
          >
            <svg className="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 8h16M4 16h16" />
            </svg>
          </button>
        </div>
      </div>
      {/* 모바일 메뉴 */}
      <div id="mobile-menu" className="hidden md:hidden bg-white">
        {isLoggedIn ? (
          <button
            onClick={handleLogout}
            className="block w-full text-left px-4 py-2 text-gray-700 hover:bg-gray-100 focus:outline-none"
          >
            로그아웃
          </button>
        ) : (
          <>
            <Link to="/login" className="block px-4 py-2 text-gray-700 hover:bg-gray-100">
              로그인
            </Link>
            <Link to="/register" className="block px-4 py-2 text-gray-700 hover:bg-gray-100">
              회원가입
            </Link>
          </>
        )}
      </div>
    </nav>
  );
}
