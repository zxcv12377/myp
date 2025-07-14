import axios from "axios";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

const CommentItem = ({
  comment,
  boardId,
  refresh,
  depth = 0, // 깊이
  isLast = false, // 마지막 여부
}) => {
  const [reply, setReply] = useState("");
  const [showReplyForm, setShowReplyForm] = useState(false);
  const [editing, setEditing] = useState(false);
  const [newContent, setNewContent] = useState(comment.content);

  const handleModify = async () => {
    await axios.put(`/comment/modify/${comment.id}`, { content: newContent });
    setEditing(false);
    refresh();
  };
  const handleDelete = async () => {
    await axios.delete(`/comment/delete/${comment.id}`);
    refresh();
  };
  const submitReply = async () => {
    await axios.post(`/comment/create`, {
      boardId,
      content: reply,
      parentId: comment.id,
    });
    setReply("");
    setShowReplyForm(false);
    refresh();
  };

  // depth별로 너비를 줄여서 오른쪽으로 밀기 (16px 단위)
  const wrapperStyle =
    depth > 0
      ? {
          width: `calc(100% - ${depth * 16}px)`,
          marginLeft: "auto",
        }
      : { width: "100%" };

  return (
    <div className={`${!isLast ? "mb-4" : ""}`} style={wrapperStyle}>
      <div className="bg-white">
        {/* 헤더 */}
        <div className="flex justify-between items-center mb-2">
          <span className="text-gray-900 font-semibold">{comment.nickname || "익명"}</span>
          {!editing && (
            <span className="text-gray-500 text-sm">
              {new Date(comment.createdDate).toLocaleDateString("ko-KR", {
                year: "2-digit",
                month: "2-digit",
                day: "2-digit",
              })}
            </span>
          )}
        </div>

        {/* 콘텐츠 */}
        {editing ? (
          <textarea
            className="w-full p-2 border border-gray-300 rounded mb-3 text-gray-800"
            value={newContent}
            onChange={(e) => setNewContent(e.target.value)}
          />
        ) : (
          <p className="text-gray-700 mb-3 whitespace-pre-wrap">{comment.content}</p>
        )}

        {/* 버튼 그룹 (카드 우측 고정) */}
        <div className="flex justify-end space-x-2 text-sm">
          {editing ? (
            <>
              <button
                onClick={handleModify}
                className="px-3 py-1 bg-green-100 text-green-700 rounded hover:bg-green-200 transition"
              >
                완료
              </button>
              <button
                onClick={() => {
                  setEditing(false);
                  setNewContent(comment.content);
                }}
                className="px-3 py-1 bg-gray-200 text-gray-700 rounded hover:bg-gray-300 transition"
              >
                취소
              </button>
            </>
          ) : (
            <>
              <button
                onClick={() => setShowReplyForm((v) => !v)}
                className="px-2 py-1 bg-blue-100 text-blue-700 rounded hover:bg-blue-200 transition"
              >
                답글
              </button>
              <button
                onClick={() => setEditing(true)}
                className="px-2 py-1 bg-yellow-100 text-yellow-800 rounded hover:bg-yellow-200 transition"
              >
                수정
              </button>
              <button
                onClick={handleDelete}
                className="px-2 py-1 bg-red-100 text-red-700 rounded hover:bg-red-200 transition"
              >
                삭제
              </button>
            </>
          )}
        </div>

        {/* 대댓글 폼 */}
        {showReplyForm && (
          <div className="mt-3 space-y-2">
            <textarea
              className="w-full p-2 border border-gray-300 rounded text-gray-800"
              placeholder="대댓글을 입력하세요"
              value={reply}
              onChange={(e) => setReply(e.target.value)}
            />
            <button
              onClick={submitReply}
              className="px-4 py-2 bg-gray-800 text-white rounded hover:bg-gray-900 text-sm"
            >
              등록
            </button>
          </div>
        )}
      </div>

      {/* 재귀: 자식 댓글 */}
      {comment.children?.map((child, idx) => (
        <CommentItem
          key={child.id}
          comment={child}
          boardId={boardId}
          refresh={refresh}
          depth={depth + 1}
          isLast={idx === comment.children.length - 1}
        />
      ))}
    </div>
  );
};

const CommentList = () => {
  const { id: boardId } = useParams();

  const [posts, setPosts] = useState([]);
  const [content, setContent] = useState();

  const showList = async () => {
    try {
      const res = await axios.get(`/comment/board/${boardId}`);
      setPosts(res.data);
      console.log(res.data);
      console.log(res.data.createdDate);
    } catch (error) {
      console.error("댓글 불러오는 중 오류 : ", error);
    }
  };

  const createComment = async (e) => {
    e.preventDefault();
    try {
      await axios.post("/comment/create", { boardId, content, parentId: null });
      setContent("");
      showList();
    } catch (error) {
      console.error("댓글 쓰기 중 오류 : ", error);
    }
  };

  useEffect(() => {
    showList();
  }, [boardId]);

  return (
    <div className="space-y-4">
      {posts.length === 0 ? (
        <div className="text-gray-700 border-b pb-2">댓글이 존재하지 않습니다.</div>
      ) : (
        posts.map((comment, index) => (
          <CommentItem
            key={comment.id}
            comment={comment}
            boardId={boardId}
            refresh={showList}
            isLast={index === comment.children.length - 1}
          />
        ))
      )}
      <form onSubmit={createComment} className="mt-4">
        <textarea
          className="w-full p-2 border rounded"
          placeholder="댓글을 입력하세요"
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />
        <button className="mt-2 px-4 py-2 bg-black text-white rounded hover:bg-gray-800">댓글 작성</button>
      </form>
    </div>
  );
};

export default CommentList;
