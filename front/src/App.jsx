import "./App.css";
import { Route, Routes } from "react-router-dom";
import BoardCreate from "./components/Board/BoardCreate";
import BoardList from "./components/Board/BoardList";
import BoardRow from "./components/Board/BoardRow";
import BoardModify from "./components/Board/BoardModify";
import CommentList from "./components/Comment/CommentList";
import RegisterForm from "./components/userform/RegisterForm";

function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<BoardList />} />
        <Route path="/register" element={<RegisterForm />} />
        <Route path="/login" element={<RegisterForm />} />
        <Route path="/logout" element={<RegisterForm />} />
        <Route path="/board/create" element={<BoardCreate />} />
        <Route path="/board/:id" element={<BoardRow />} />
        <Route path="/board/modify/:id" element={<BoardModify />} />
        <Route path="/comment" element={<CommentList />} />
      </Routes>
    </>
  );
}

export default App;
