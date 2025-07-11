import "./App.css";
import { Route, Routes } from "react-router-dom";
import BoardCreate from "./components/board/BoardCreate";
import BoardList from "./components/board/BoardList";
import BoardRow from "./components/board/BoardRow";
import BoardModify from "./components/board/BoardModify";
import CommentList from "./components/comment/CommentList";
import RegisterForm from "./components/userform/RegisterForm";
import EmailAuth from "./components/emailauth/EmailAuth";
import LoginForm from "./components/userform/LoginForm";
import Navbar from "./components/navbar/Navbar";
import PageLayout from "./Layout/PageLayout";

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/register" element={<RegisterForm />} />
        <Route path="/login" element={<LoginForm />} />
        <Route element={<PageLayout />}>
          <Route path="/" element={<BoardList />} />
          <Route path="/board/create" element={<BoardCreate />} />
          <Route path="/board/:id" element={<BoardRow />} />
          <Route path="/board/modify/:id" element={<BoardModify />} />
          <Route path="/comment" element={<CommentList />} />
          <Route path="/email-verify/:shortId" element={<EmailAuth />} />
        </Route>
      </Routes>
    </>
  );
}

export default App;
