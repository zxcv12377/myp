import "../../assets/css/nukaButton.css";

export default function SearchButton({ type, content }) {
  return (
    <button type={type} className="search-button search-button--nuka button--round-s button--text-thick ml-1">
      {content}
    </button>
  );
}
