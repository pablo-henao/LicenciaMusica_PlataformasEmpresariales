import { BrowserRouter, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import { Layout } from "./components/layout/Layout";
import { ProtectedRoute } from "./components/ProtectedRoute";
import { HomePage } from "./pages/HomePage";
import { LoginPage } from "./pages/auth/LoginPage";
import { RegisterPage } from "./pages/auth/RegisterPage";
import { CatalogoPage } from "./pages/catalogo/CatalogoPage";
import { BeatDetailPage } from "./pages/catalogo/BeatDetailPage";
import { MisComprasPage } from "./pages/compras/MisComprasPage";
import { MisVentasPage } from "./pages/compras/MisVentasPage";
import { MisBeatsPage } from "./pages/beats/MisBeatsPage";
import { CrearBeatPage } from "./pages/beats/CrearBeatPage";
import { EditarBeatPage } from "./pages/beats/EditarBeatPage";
import { GestionarBeatPage } from "./pages/beats/GestionarBeatPage";
import { MisInvitacionesPage } from "./pages/creditos/MisInvitacionesPage";
import { NotificacionesPage } from "./pages/notificaciones/NotificacionesPage";
import { LicenciasPage } from "./pages/info/LicenciasPage";
import { SplitsPage } from "./pages/info/SplitsPage";
import { NosotrosPage } from "./pages/info/NosotrosPage";
import { AdminPage } from "./pages/admin/AdminPage";

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route element={<Layout />}>
            <Route index element={<HomePage />} />
            <Route path="licencias" element={<LicenciasPage />} />
            <Route path="splits" element={<SplitsPage />} />
            <Route path="nosotros" element={<NosotrosPage />} />
            <Route path="login" element={<LoginPage />} />
            <Route path="registro" element={<RegisterPage />} />
            <Route
              path="catalogo"
              element={
                <ProtectedRoute>
                  <CatalogoPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="mis-compras"
              element={
                <ProtectedRoute>
                  <MisComprasPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="mis-ventas"
              element={
                <ProtectedRoute rolesPermitidos={["PRODUCTOR", "ADMIN"]}>
                  <MisVentasPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="mis-invitaciones"
              element={
                <ProtectedRoute>
                  <MisInvitacionesPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="notificaciones"
              element={
                <ProtectedRoute>
                  <NotificacionesPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="admin"
              element={
                <ProtectedRoute rolesPermitidos={["ADMIN"]}>
                  <AdminPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="mis-beats"
              element={
                <ProtectedRoute rolesPermitidos={["PRODUCTOR"]}>
                  <MisBeatsPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="mis-beats/:id"
              element={
                <ProtectedRoute rolesPermitidos={["PRODUCTOR"]}>
                  <GestionarBeatPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="beats/nuevo"
              element={
                <ProtectedRoute rolesPermitidos={["PRODUCTOR"]}>
                  <CrearBeatPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="beats/:id/editar"
              element={
                <ProtectedRoute rolesPermitidos={["PRODUCTOR"]}>
                  <EditarBeatPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="beats/:id"
              element={
                <ProtectedRoute>
                  <BeatDetailPage />
                </ProtectedRoute>
              }
            />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
