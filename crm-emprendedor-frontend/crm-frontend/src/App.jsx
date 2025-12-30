import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Layout from './components/Layout';
import Login from './pages/Login';
import Clientes from './pages/Clientes';
import Dashboard from './pages/Dashboard';
import Ventas from './pages/Ventas';
import Perfil from './pages/Perfil';
import Contenido from './pages/Contenidos';
import CategoriasContenido from './components/CategoriasContenido';
import Registro from './pages/Registro';
/**
 * Componente de Ruta Protegida
 * Si el usuario no está logueado, lo manda al login.
 */
const ProtectedRoute = ({ children }) => {
  const { user } = useAuth();
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* --- RUTAS PÚBLICAS (Sin protección) --- */}
          <Route path="/login" element={<Login />} />
          {/* MOVIDO AQUÍ AFUERA: Ahora es accesible sin estar logueado */}
          <Route path="/registro" element={<Registro />} />
          
          {/* --- RUTAS PRIVADAS (Protegidas) --- */}
          <Route 
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/clientes" element={<Clientes />} />
            <Route path="/contenido" element={<Contenido />} />
            <Route path="/ventas" element={<Ventas />} />
            <Route path="/perfil" element={<Perfil />} />
            <Route path="/categoria" element={<CategoriasContenido />} />
            {/* Se eliminó de aquí adentro */}
          </Route>

          {/* Redirección por defecto */}
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
