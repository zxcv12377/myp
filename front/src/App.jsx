import "./App.css";
import { Route, Routes } from "react-router-dom";
import BoardCreate from "./components/BoardCreate";
import BoardList from "./components/BoardList";
import BoardRow from "./components/BoardRow";

function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<BoardList />} />
        <Route path="/create" element={<BoardCreate />} />
        <Route path="/board/:id" element={<BoardRow />} />
      </Routes>
    </>
  );
}

export default App;
