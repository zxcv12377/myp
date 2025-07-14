import "../../assets/css/dribbleButton.css";

export default function DribbleButton({ content, type, onClick }) {
  return (
    <button className="drible-btn" type={type} onClick={onClick}>
      <span>{content}</span>
    </button>
  );
}
