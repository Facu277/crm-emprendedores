import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getStats } from '../api/dashboardService';
import { useAuth } from '../context/AuthContext';
import { 
  Users, ShoppingBag, DollarSign, Calendar, 
  ArrowUpRight, Clock, User as UserIcon, LayoutGrid, PlusCircle 
} from 'lucide-react';
import VentaDetalleModal from '../components/VentaDetalleModal';

const Dashboard = () => {
  const navigate = useNavigate();
  const { user } = useAuth(); // Datos del usuario autenticado
  const [stats, setStats] = useState({
    totalClientes: 0,
    ventasEsteMes: 0,
    ingresosEsteMes: 0,
    contenidosProgramados: 0,
    ultimasVentas: []
  });
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [ventaSeleccionada, setVentaSeleccionada] = useState(null);

 
  // --- LÓGICA DE DATOS DEL EMPRENDEDOR ---
  // Accedemos a los datos a través de la relación user -> emprendedor
  // 1. Extraemos el objeto emprendedor del usuario
const emprendedor = user?.emprendedor;

const getRolStyles = (rol) => {
  const r = rol?.toUpperCase();
  if (r?.includes('ADMIN')) {
    return 'bg-red-50 text-red-600 border-red-100';
  }
  if (r?.includes('EMPRENDEDOR')) {
    return 'bg-emerald-50 text-emerald-600 border-emerald-100';
  }

  if (r?.includes('EMPLEADO')) {
    return 'bg-emerald-50 text-emerald-600 border-emerald-100';
  }
  // Color por defecto (Usuario estándar)
  return 'bg-blue-50 text-blue-600 border-blue-100';
};

const rolEstilo = getRolStyles(user?.rol);
const API_BASE = import.meta.env.VITE_API_URL.replace('/api/v1', '');

const fotoUrl = emprendedor?.fotoPerfil 
  ? `${API_BASE}/uploads/emprendedores/${emprendedor.fotoPerfil}` 
  : null;

  const nombreAMostrar = emprendedor?.nombre || user?.username || 'Emprendedor';
  const rubroAMostrar = emprendedor?.rubro || 'Panel de Gestión de Negocio';
  
  // Formatear el Rol (ej: ROLE_EMPRENDEDOR -> emprendedor)
  const nombreRol = typeof user?.rol === 'string' 
  ? user.rol.replace('ROLE_', '').toLowerCase() 
  : (user?.rol?.nombre || 'usuario').replace('ROLE_', '').toLowerCase();

  const abrirDetalleVenta = (venta) => {
    setVentaSeleccionada(venta);
    setIsModalOpen(true);
  };

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const data = await getStats();
        setStats({
          totalClientes: data.totalClientes || 0,
          ventasEsteMes: data.ventasEsteMes || 0,
          ingresosEsteMes: data.ingresosEsteMes || 0,
          contenidosProgramados: data.contenidosProgramados || 0,
          ultimasVentas: data.ultimasVentas || [] 
        });
      } catch (error) {
        console.error("Error al cargar dashboard:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] space-y-4">
        <div className="w-12 h-12 border-4 border-indigo-500 border-t-transparent rounded-full animate-spin"></div>
        <p className="text-gray-500 font-medium animate-pulse">Cargando métricas de tu negocio...</p>
      </div>
    );
  }

  const cards = [
    { title: 'Total Clientes', value: stats.totalClientes, icon: Users, color: 'bg-blue-500', trend: 'Base total' },
    { title: 'Ventas del Mes', value: stats.ventasEsteMes, icon: ShoppingBag, color: 'bg-green-500', trend: 'Nuevas' },
    { title: 'Ingresos Mensuales', value: `$${Number(stats.ingresosEsteMes).toLocaleString()}`, icon: DollarSign, color: 'bg-purple-500', trend: 'Bruto' },
    { title: 'Contenido Pendiente', value: stats.contenidosProgramados, icon: Calendar, color: 'bg-orange-500', trend: 'Marketing' },
  ];

  return (
    <div className="space-y-8 animate-in fade-in duration-500 pb-10">
      
      {/* HEADER CON PERFIL Y ACCIONES */}
<header className="flex flex-col lg:flex-row lg:items-center justify-between gap-6 bg-white p-4 md:p-6 rounded-2xl shadow-sm border border-gray-100 transition-all hover:shadow-md">
  
  {/* Sección de Perfil */}
  <div className="flex items-center gap-4">
    {/* Círculo de Foto - flex-shrink-0 evita que se aplaste */}
    <div className={`w-16 h-16 rounded-full border-2 p-0.5 flex-shrink-0 transition-transform hover:scale-105 overflow-hidden shadow-sm ${user?.rol?.includes('ADMIN') ? 'border-red-400' : 'border-emerald-500'}`}>
      {fotoUrl ? (
        <img src={fotoUrl} alt="Perfil" className="w-full h-full rounded-full object-cover" />
      ) : (
        <div className="w-full h-full rounded-full bg-gray-50 flex items-center justify-center text-gray-400">
          <UserIcon size={30} />
        </div>
      )}
    </div>
    
    {/* Textos de Bienvenida */}
    <div className="min-w-0"> {/* min-w-0 permite que el texto trunque si es muy largo */}
      <div className="flex flex-wrap items-center gap-2">
        <h1 className="text-xl md:text-2xl font-bold text-gray-800 tracking-tight truncate">
          ¡Hola, {user?.emprendedor?.nombre || user?.nombre || 'Usuario'}!
        </h1>
        {/* Badge Dinámico */}
        <span className={`text-[9px] md:text-[10px] font-black px-2.5 py-0.5 rounded-full uppercase tracking-widest border ${rolEstilo}`}>
          {typeof user?.rol === 'string' ? user.rol.replace('ROLE_', '') : 'USUARIO'}
        </span>
      </div>
      <p className="text-gray-400 text-xs md:text-sm font-medium flex items-center gap-1.5 mt-1">
        <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"></span>
        <span className="truncate">{user?.emprendedor?.rubro || 'Panel de Gestión'}</span>
      </p>
    </div>
  </div>

  {/* Botones de acción rápida - Se ponen en columna en móviles pequeños y fila en más grandes */}
  <div className="flex flex-row gap-3 w-full lg:w-auto">
    <button 
      onClick={() => navigate('/categoria')}
      className="flex-1 lg:flex-none cursor-pointer flex items-center justify-center gap-2 px-4 md:px-5 py-3 bg-white text-gray-600 rounded-xl text-xs md:text-sm font-bold hover:bg-gray-50 transition-all border border-gray-200 shadow-sm active:scale-95"
    >
      <LayoutGrid size={18} className="text-gray-400" />
      <span className="whitespace-nowrap">Categorías</span>
    </button>
    
    <button 
      onClick={() => navigate('/ventas')}
      className="flex-1 cursor-pointer lg:flex-none flex items-center justify-center gap-2 px-4 md:px-5 py-3 bg-gray-900 text-white rounded-xl text-xs md:text-sm font-bold hover:bg-black transition-all shadow-lg active:scale-95"
    >
      <PlusCircle size={18} className="text-emerald-400" />
      <span className="whitespace-nowrap">Venta</span>
    </button>
  </div>
</header>

      {/* Grid de Tarjetas de Métricas */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {cards.map((card, index) => {
          const Icon = card.icon;
          return (
            <div key={index} className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 hover:shadow-md transition-all cursor-default group">
              <div className="flex items-center justify-between mb-4">
                <div className={`${card.color} p-3 rounded-xl text-white shadow-lg transform transition-transform group-hover:scale-110`}>
                  <Icon size={24} />
                </div>
                <span className="text-green-600 text-[10px] font-bold bg-green-50 px-2 py-1 rounded-full uppercase tracking-tighter">
                  {card.trend}
                </span>
              </div>
              <h3 className="text-gray-400 text-xs font-bold uppercase tracking-wider">{card.title}</h3>
              <p className="text-2xl font-black text-gray-800 mt-1">{card.value}</p>
            </div>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Tabla de Últimas Ventas */}
        <div className="lg:col-span-2 bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          <div className="p-6 border-b border-gray-50 flex justify-between items-center">
            <h2 className="font-bold text-gray-800 flex items-center gap-2">
              <Clock size={18} className="text-blue-500" />
              Actividad Reciente
            </h2>
            <button 
              onClick={() => navigate('/ventas')} 
              className="text-xs text-indigo-600 font-bold hover:underline"
            >
              Ver reporte histórico
            </button>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead className="bg-gray-50 text-gray-400 text-[10px] uppercase font-bold">
                <tr>
                  <th className="px-6 py-3 tracking-widest">Cliente</th>
                  <th className="px-6 py-3 tracking-widest">Monto</th>
                  <th className="px-6 py-3 text-right tracking-widest">Fecha</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {stats.ultimasVentas.length > 0 ? (
                  stats.ultimasVentas.map((venta, i) => (
                    <tr 
                      key={venta.id || i} 
                      onClick={() => abrirDetalleVenta(venta)} 
                      className="hover:bg-blue-50/40 transition-colors cursor-pointer group"
                    >
                      <td className="px-6 py-4">
                        <p className="text-sm font-bold text-gray-700 group-hover:text-indigo-600 transition-colors">
                          {venta.cliente ? `${venta.cliente.nombre} ${venta.cliente.apellido || ''}` : 'Venta Directa'}
                        </p>
                        <p className="text-[10px] text-gray-400 uppercase tracking-tighter">{venta.metodoPago}</p>
                      </td>
                      <td className="px-6 py-4 text-sm font-black text-gray-800">
                        ${Number(venta.monto || 0).toLocaleString()}
                      </td>
                      <td className="px-6 py-4 text-right">
                        <div className="flex justify-end items-center gap-2">
                          <span className="text-xs text-gray-400 font-medium">
                            {venta.fecha ? new Date(venta.fecha).toLocaleDateString() : 'Reciente'}
                          </span>
                          <ArrowUpRight size={14} className="text-gray-300 group-hover:text-indigo-500" />
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="3" className="px-6 py-12 text-center text-gray-400 italic text-sm">
                      No hay movimientos registrados este mes.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Card de Marketing / Agenda */}
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex flex-col hover:shadow-md transition-all">
          <h2 className="font-bold text-gray-800 mb-6 flex items-center gap-2">
            <Calendar size={18} className="text-orange-500" />
            Marketing & Agenda
          </h2>
          <div className="flex-1 flex flex-col justify-center items-center text-center space-y-4">
            <div className="w-20 h-20 bg-orange-50 rounded-full flex items-center justify-center text-orange-500 shadow-inner">
              <span className="text-3xl font-black">{stats.contenidosProgramados}</span>
            </div>
            <div>
              <p className="font-bold text-gray-700">Contenidos</p>
              <p className="text-xs text-gray-400 px-4 leading-tight">Publicaciones planeadas para los próximos días.</p>
            </div>
            
            <button 
              onClick={() => navigate('/contenido')}
              className="w-full py-3 bg-gray-900 hover:bg-black text-white rounded-xl text-xs font-bold transition-all uppercase tracking-widest cursor-pointer shadow-lg active:scale-95"
            >
              Gestionar Agenda
            </button>
          </div>
        </div>
      </div>

      {isModalOpen && (
        <VentaDetalleModal 
          isOpen={isModalOpen} 
          onClose={() => setIsModalOpen(false)} 
          venta={ventaSeleccionada} 
        />
      )}
    </div>
  );
};

export default Dashboard;