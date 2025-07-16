import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, useParams } from "react-router-dom";
import CommentList from "../comment/CommentList";
import "../../assets/css/buttonstylish.css";

const BoardRow = () => {
  const navigate = useNavigate();

  const { id } = useParams();
  const [post, setPost] = useState(null);
  // const [likes, setLikes] = useState();
  const [detail, setDetail] = useState();

  useEffect(() => {
    const getRow = async () => {
      try {
        const res = await axios.get(`/board/${id}`);
        setPost(res.data);
        console.log(res.data);
        if (!res) {
          console.error("존재하지 않은 게시물 입니다");
        }
      } catch (error) {
        console.error("게시글 에러", error);
      }
    };
    userDetails();
    getRow();
  }, [id]);

  const userDetails = async () => {
    try {
      const res = await axios.get(`/board/detail/${id}`);
      setDetail(res.data);
    } catch (error) {
      console.log("유저 불러오는 중 오류", error);
    }
  };

  const deleteRow = async () => {
    try {
      alert("게시글을 삭제하시겠습니까?");
      await axios.delete(`/board/${id}`);
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

  const likeHandler = async () => {
    try {
      const { data: newCount } = await axios.put(`/board/${id}/likes`);
      setPost((prev) => ({
        ...prev,
        likesCount: newCount,
      }));
    } catch (error) {
      console.log("좋아요 에러", error);
    }
  };

  if (!post) return <div className="p-8 text-gray-500">로딩 중...</div>;

  return (
    <div className="bg-white min-h-screen py-10 px-6">
      <div className="max-w-7xl mx-auto flex flex-col lg:flex-row gap-8">
        {/* 본문 */}
        <div className="flex-[3]">
          <header className="border-b border-gray-200 pb-5 mb-8">
            <h1 className="text-3xl font-bold text-indigo-900">{post.title}</h1>
            <p className="text-sm text-gray-500 mt-1">작성자: {post.nickname}</p>
            <p className="text-sm text-gray-500 mt-1">작성일: {new Date(post.createdDate).toLocaleString("ko-KR")}</p>
          </header>
          <div className="h-[300px] overflow-y-auto">
            <main className="text-gray-800 text-[1.05rem] leading-relaxed whitespace-pre-line">{post.content}</main>
          </div>

          <div className="flex justify-between items-center w-full mt-4">
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

            <div className="flex space-x-2">
              <button type="button" onClick={likeHandler} className="button button--sacnite button--round-l">
                <div className="text-sm">추천</div>
                <div className="text-sm">{post.likesCount || 0}</div>
              </button>
            </div>

            {/* 오른쪽: 수정, 삭제 버튼 */}
            <div className="flex space-x-2">
              <button
                type="button"
                onClick={moveModifyHandler}
                className="px-2 py-0.5 bg-yellow-100 text-yellow-800 rounded hover:bg-yellow-200 transition mr-2"
              >
                수정
              </button>
              <button
                type="button"
                className="px-2 py-0.5 bg-red-100 text-red-700 rounded hover:bg-red-200 transition"
                onClick={deleteRow}
              >
                삭제
              </button>
            </div>
          </div>
          <div>{/* 프로필 정보 */}</div>

          {/* 댓글 */}
          <section className="mt-6 border-t pt-6">
            <h2 className="text-xl font-semibold text-gray-700 mb-4">💬 댓글</h2>
            <CommentList postId={id} />
          </section>
        </div>
      </div>
    </div>
  );
};

export default BoardRow;
