export default function SidebarLeft() {
  return (
    <div className="flex justify-end bg-white border-gray-200 border-r-1">
      {/* 사이드바 */}
      <aside className="hidden lg:flex flex-col w-72 h-screen pt-10 pr-7 pl-7 space-y-6 sticky top-0">
        {/* 채널 */}
        <div>
          <h2 className="text-lg font-bold text-gray-400 mb-3 border-b-1 border-gray-200 pb-2">📌 채널</h2>
          <ul className="space-y-2 text-sm text-gray-400">
            <li className="hover:text-indigo-600 cursor-pointer font-medium">가</li>
            <li className="hover:text-indigo-600 cursor-pointer font-medium">나</li>
            <li className="hover:text-indigo-600 cursor-pointer font-medium">다</li>
            <li className="hover:text-indigo-600 cursor-pointer font-medium">라</li>
            <li className="hover:text-indigo-600 cursor-pointer font-medium">마</li>
            <li className="hover:text-indigo-600 cursor-pointer font-medium">바</li>
          </ul>
        </div>
      </aside>
    </div>
  );
}
