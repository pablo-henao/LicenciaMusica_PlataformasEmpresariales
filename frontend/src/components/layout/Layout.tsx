import { Outlet } from "react-router-dom";
import { Navbar } from "./Navbar";

export function Layout() {
  return (
    <div className="flex min-h-screen flex-col">
      <div className="pt-2">
        <Navbar />
      </div>
      <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-6 sm:px-6">
        <Outlet />
      </main>
      <footer className="mx-auto w-full max-w-6xl px-4 pb-6 sm:px-6">
        <div className="rounded-2xl border border-neutral-200 bg-white px-5 py-4 text-xs text-neutral-500">
          <span className="font-semibold text-neutral-700">Licencia+</span> — beats con contrato y
          créditos repartidos. Trap · Reggaetón · Dembow · Bachata.
        </div>
      </footer>
    </div>
  );
}
