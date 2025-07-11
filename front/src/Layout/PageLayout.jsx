import { Outlet } from "react-router-dom";
import SidebarRight from "../components/sidebar/SidebarRight";
import SidebarLeft from "../components/sidebar/SidebarLeft";

export default function PageLayout() {
  return (
    <div className="flex flex-col lg:flex-row  w-full max-w-7xl mx-auto">
      {/* 좌측 사이드바 (숨김 on small) */}
      <aside className="hidden lg:block w-64 flex-shrink-0">
        <SidebarLeft />
      </aside>

      {/* 메인 컨텐츠 영역 */}
      <main className="flex-1">
        <Outlet />
      </main>

      {/* 우측 사이드바 (숨김 on small) */}
      <aside className="hidden lg:block w-64 flex-shrink-0">
        <SidebarRight />
      </aside>
    </div>
  );
}
