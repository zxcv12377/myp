import { useState } from "react";
import reactLogo from "./assets/react.svg";
import viteLogo from "/vite.svg";
import "./App.css";
import { Route, Routes } from "react-router-dom";
import BoardCreate from "./components/BoardCreate";
import BoardList from "./components/BoardList";

function App() {
  const [count, setCount] = useState(0);

  return (
    <>
      <Routes>
        <Route path="/create" element={<BoardCreate />} />
        <Route path="/" element={<BoardList />} />
      </Routes>
    </>
  );
}

export default App;
