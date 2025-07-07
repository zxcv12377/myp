import axios from "axios";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

const CommentItem = ({ comment, boardId, refresh, isLast }) => {
  const [reply, setReply] = useState("");
  const [showReplyForm, setShowReplyForm] = useState(false);
  const [editing, setEditing] = useState(false);
  const [newContent, setNewContent] = useState(comment.content);

  console.log(comment);
  const handleModify = async () => {
    try {
      await axios.put(`http://localhost:8080/comment/modify/${comment.id}`, {
        content: newContent,
      });
      setEditing(false);
      refresh();
    } catch (err) {
      console.error("댓글 수정 오류", err);
    }
  };

  const handleDelete = async () => {
    try {
      await axios.delete(`http://localhost:8080/comment/delete/${comment.id}`);
      refresh();
    } catch (err) {
      console.error("댓글 삭제 오류", err);
    }
  };

  const submitReply = async () => {
    try {
      await axios.post("http://localhost:8080/comment/create", {
        boardId,
        content: reply,
        parentId: comment.id,
      });
      setReply("");
      setShowReplyForm(false);
      refresh();
    } catch (error) {
      console.error("대댓글 작성 오류", error);
    }
  };

  return (
    <div className={`ml-2 mb-3 ${isLast ? "" : "border-b pb-2  mt-2 border-gray-300"}`}>
      <div className="text-gray-700 font-semibold">{comment.writer || "익명"}</div>

      {editing ? (
        <textarea
          value={newContent}
          onChange={(e) => setNewContent(e.target.value)}
          className="w-full p-1 border rounded mt-1"
        />
      ) : (
        <div className="text-gray-700 ml-2 mt-1">{comment.content}</div>
      )}

      <div className="flex justify-end mt-2 space-x-2">
        {editing ? (
          <>
            <button onClick={handleModify} className="px-3 py-1 bg-green-600 text-white rounded text-sm">
              수정 완료
            </button>
            <button
              onClick={() => {
                setEditing(false);
                setNewContent(comment.content); // 수정 취소 시 초기화
              }}
              className="px-3 py-1 bg-gray-300 text-black rounded text-sm"
            >
              취소
            </button>
          </>
        ) : (
          <>
            {new Date(comment.createdDate).toLocaleDateString("ko-KR", {
              year: "2-digit",
              month: "2-digit",
              day: "2-digit",
            })}
            <button
              onClick={() => setEditing(true)}
              className="px-4 py-0.5 text-green-600 bg-gray-100 rounded hover:bg-gray-200 transition-colors duration-200 font-bold"
            >
              수정
            </button>
            <button
              onClick={handleDelete}
              className="px-4 py-0.5 text-red-500 bg-gray-100 rounded hover:bg-gray-200 transition-colors duration-200 font-bold"
            >
              삭제
            </button>
          </>
        )}
      </div>

      {/* 대댓글은 기존처럼 계속 렌더링 */}
      {comment.children?.length > 0 &&
        comment.children.map((child) => (
          <CommentItem key={child.id} comment={child} boardId={boardId} refresh={refresh} />
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
      const res = await axios.get(`http://localhost:8080/comment/board/${boardId}`);
      setPosts(res.data);
      console.log(res.data.createdDate);
    } catch (error) {
      console.error("댓글 불러오는 중 오류 : ", error);
    }
  };

  const createComment = async (e) => {
    e.preventDefault();
    try {
      await axios.post("http://localhost:8080/comment/create", { boardId, content, parentId: null });
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
