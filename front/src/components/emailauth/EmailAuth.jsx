import axios from "axios";
import { useEffect, useRef, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";

export default function EmailAuth() {
  const [message, setMessage] = useState("이메일 인증 중...");
  const hasFetched = useRef(false); // 한 번만 실행용 플래그
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    // 이미 한 번 실행했다면 무시
    if (hasFetched.current) return;
    hasFetched.current = true;

    const token = searchParams.get("token");
    if (!token) {
      setMessage("잘못된 접근입니다.");
      return;
    }

    axios
      .get(`http://localhost:8080/auth/verify?token=${token}`)
      .then((res) => {
        // 서버가 성공 메시지를 스트링으로 던지면 그대로 쓰셔도 되고
        setMessage("이메일 인증 성공! 로그인 페이지로 이동합니다...");
        // 인증이 끝난 즉시 로그인으로 이동
        // navigate("/login", { replace: true });
        setTimeout(() => {
          if (window.opener) {
            // 부모 창의 URL을 /login 으로 바꿔줌
            window.opener.location.href = "/login";
            // 팝업 닫기
            window.close();
          } else {
            // 팝업이 아닐 땐 그냥 현재 창 내비게이트
            window.location.href = "/login";
          }
        }, 2000);
      })
      .catch((err) => {
        // err.response.data 가 문자열인지 객체인지 체크
        const serverMsg = typeof err.response?.data === "string" ? err.response.data : err.response?.data?.message;
        setMessage(serverMsg || "이메일 인증 실패 또는 만료됨");
      });
  }, [navigate, searchParams]);

  return <div>{message}</div>;
}
