import { Link, useNavigate, useLocation, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { 
  LayoutDashboard, 
  Users, 
  ShoppingCart, 
  Image as ImageIcon, 
  LogOut,
  Menu,
  X,
  UserCircle // Icono para el perfil
} from 'lucide-react';
import { useState } from 'react';

const Layout = () => {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const menuItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'Clientes', path: '/clientes', icon: Users },
    { name: 'Ventas', path: '/ventas', icon: ShoppingCart },
    { name: 'Contenido', path: '/contenido', icon: ImageIcon },
  ];

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const closeMobileMenu = () => setIsMobileMenuOpen(false);

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col md:flex-row">
      
      {/* --- SIDEBAR DESKTOP --- */}
      <aside className="hidden md:flex flex-col w-64 bg-white border-r border-gray-200 sticky top-0 h-screen">
        <div className="p-6">
          <h1 className="text-xl font-bold text-blue-600">CRM Emprende</h1>
        </div>
        
        <nav className="flex-1 px-4 space-y-2">
          {menuItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.path;
            return (
              <Link
                key={item.name}
                to={item.path}
                className={`flex items-center space-x-3 p-3 rounded-lg transition-colors ${
                  isActive 
                    ? 'bg-blue-50 text-blue-600' 
                    : 'text-gray-600 hover:bg-gray-100'
                }`}
              >
                <Icon size={20} />
                <span className="font-medium">{item.name}</span>
              </Link>
            );
          })}
        </nav>

        {/* --- SECCIÓN DE PERFIL Y LOGOUT (DESKTOP) --- */}
        <div className="p-4 border-t border-gray-200 space-y-2">
          <Link
            to="/perfil"
            className={`flex items-center space-x-3 w-full p-3 rounded-lg transition-colors ${
              location.pathname === '/perfil' 
                ? 'bg-indigo-50 text-indigo-600' 
                : 'text-gray-600 hover:bg-gray-100'
            }`}
          >
            <UserCircle size={20} />
            <span className="font-medium">Mi Perfil</span>
          </Link>
          
          <button
            onClick={handleLogout}
            className="flex items-center space-x-3 w-full p-3 text-red-500 hover:bg-red-50 rounded-lg transition-colors"
          >
            <LogOut size={20} />
            <span className="font-medium">Cerrar Sesión</span>
          </button>
        </div>
      </aside>

      {/* --- HEADER MOBILE --- */}
      <header className="md:hidden bg-white border-b border-gray-200 p-4 flex justify-between items-center sticky top-0 z-50">
        <h1 className="text-lg font-bold text-blue-600">CRM Emprende</h1>
        <button 
          onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
          className="p-2 text-gray-600 hover:bg-gray-100 rounded-lg"
        >
          {isMobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </header>

      {/* --- MENÚ DESPLEGABLE MOBILE (OVERLAY) --- */}
      {isMobileMenuOpen && (
        <div className="md:hidden fixed inset-0 z-40 flex">
          <div className="fixed inset-0 bg-black/50" onClick={closeMobileMenu}></div>
          
          <nav className="relative bg-white w-72 h-full shadow-xl flex flex-col p-6 space-y-4 animate-in slide-in-from-left duration-300">
            <div className="flex justify-between items-center mb-4">
               <h2 className="text-xl font-bold text-blue-600">Menú</h2>
               <button onClick={closeMobileMenu}><X size={24} className="text-gray-400" /></button>
            </div>
            
            {menuItems.map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === item.path;
              return (
                <Link
                  key={item.name}
                  to={item.path}
                  onClick={closeMobileMenu}
                  className={`flex items-center space-x-3 p-4 rounded-xl font-semibold ${
                    isActive ? 'bg-blue-50 text-blue-600' : 'text-gray-600'
                  }`}
                >
                  <Icon size={22} />
                  <span>{item.name}</span>
                </Link>
              );
            })}

            {/* --- SECCIÓN INFERIOR (MOBILE) --- */}
            <div className="mt-auto border-t pt-4 space-y-2">
              <Link
                to="/perfil"
                onClick={closeMobileMenu}
                className={`flex items-center space-x-3 w-full p-4 rounded-xl font-bold ${
                  location.pathname === '/perfil' ? 'bg-indigo-50 text-indigo-600' : 'text-gray-600'
                }`}
              >
                <UserCircle size={22} />
                <span>Mi Perfil</span>
              </Link>
              
              <button
                onClick={handleLogout}
                className="flex items-center space-x-3 w-full p-4 text-red-500 font-bold hover:bg-red-50 rounded-xl transition-colors"
              >
                <LogOut size={22} />
                <span>Cerrar Sesión</span>
              </button>
            </div>
          </nav>
        </div>
      )}

      {/* --- AREA DE CONTENIDO --- */}
      <div className="flex-1 flex flex-col min-w-0">
        <main className="p-4 md:p-8">
          <div className="max-w-7xl mx-auto">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
};

export default Layout;