import { useEffect, useState, useMemo } from 'react';
import { Plus, Trash2, Edit, Eye, Calendar, ArrowUpDown, Filter, TrendingUp, Wallet } from 'lucide-react';
import { getVentas, deleteVenta } from '../api/ventaService';
import VentaForm from '../components/VentaForm';
import ConfirmModal from '../components/ConfirmModal';
import VentaDetalleModal from '../components/VentaDetalleModal';

const Ventas = () => {
  const [ventas, setVentas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filtroEstado, setFiltroEstado] = useState('TODOS');
  const [orden, setOrden] = useState({ campo: 'fecha', dir: 'desc' });

  // Estados para Modales
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [ventaAEditar, setVentaAEditar] = useState(null);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [idAEliminar, setIdAEliminar] = useState(null);
  const [ventaDetalle, setVentaDetalle] = useState(null);
  const [isDetalleOpen, setIsDetalleOpen] = useState(false);

  useEffect(() => { cargarVentas(); }, []);

  const cargarVentas = async () => {
    try {
      setLoading(true);
      const data = await getVentas();
      setVentas(Array.isArray(data) ? data : []);
    } catch (error) { console.error(error); } finally { setLoading(false); }
  };

  const manejarVerDetalle = (venta) => {
    setVentaDetalle(venta);
    setIsDetalleOpen(true);
  };

  // --- LÓGICA DE FILTRADO, ORDENAMIENTO Y ESTADÍSTICAS ---
  const { ventasProcesadas, statsFiltro } = useMemo(() => {
    let filtradas = [...ventas];
    
    // 1. Aplicar Filtro de Estado
    if (filtroEstado !== 'TODOS') {
      filtradas = filtradas.filter(v => v.estado === filtroEstado);
    }

    // 2. Calcular Estadísticas del grupo filtrado (Solo si no es TODOS)
    const stats = {
      cantidad: filtradas.length,
      montoTotal: filtradas.reduce((acc, curr) => acc + (curr.monto || 0), 0)
    };

    // 3. Aplicar Ordenamiento
    filtradas.sort((a, b) => {
      let valA = a[orden.campo];
      let valB = b[orden.campo];
      if (orden.campo === 'fecha') {
        valA = new Date(valA).getTime();
        valB = new Date(valB).getTime();
      }
      if (orden.dir === 'asc') return valA > valB ? 1 : -1;
      return valA < valB ? 1 : -1;
    });

    return { ventasProcesadas: filtradas, statsFiltro: stats };
  }, [ventas, filtroEstado, orden]);

  const toggleOrden = (campo) => {
    setOrden(prev => ({
      campo,
      dir: prev.campo === campo && prev.dir === 'desc' ? 'asc' : 'desc'
    }));
  };

  const getBadgeStyle = (e) => {
    switch (e) {
      case 'COMPLETADO': return 'bg-green-100 text-green-700 border-green-200';
      case 'PENDIENTE': return 'bg-amber-100 text-amber-700 border-amber-200';
      default: return 'bg-red-100 text-red-700 border-red-200';
    }
  };

  const formatearFechaCompleta = (f) => {
    const fecha = new Date(f);
    if (isNaN(fecha)) return 'Fecha no disponible';
    return new Intl.DateTimeFormat('es-AR', { 
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    }).format(fecha);
  };

  return (
    <div className="space-y-6">
      {/* HEADER */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Ventas</h1>
          <p className="text-gray-500 text-sm">Gestiona tus ingresos y cobros</p>
        </div>
        <button 
          onClick={() => { setVentaAEditar(null); setIsModalOpen(true); }}
          className="w-full sm:w-auto flex items-center justify-center space-x-2 bg-indigo-600 hover:bg-indigo-700 text-white px-5 py-2.5 rounded-xl shadow-lg cursor-pointer transition-all active:scale-95"
        >
          <Plus size={20} />
          <span className="font-semibold">Nueva Venta</span>
        </button>
      </div>

      {/* FILTROS Y ORDENAMIENTO */}
      <div className="flex flex-col md:flex-row gap-4 items-center justify-between bg-white p-4 rounded-2xl border border-gray-100 shadow-sm">
        <div className="flex items-center gap-2 overflow-x-auto w-full md:w-auto pb-2 md:pb-0">
          <Filter size={16} className="text-gray-400 shrink-0" />
          {['TODOS', 'COMPLETADO', 'PENDIENTE', 'CANCELADO'].map((e) => (
            <button
              key={e}
              onClick={() => setFiltroEstado(e)}
              className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all border cursor-pointer whitespace-nowrap ${
                filtroEstado === e ? 'bg-indigo-600 text-white border-indigo-600' : 'bg-gray-50 text-gray-500 border-gray-100 hover:bg-gray-100'
              }`}
            >
              {e}
            </button>
          ))}
        </div>

        <div className="flex items-center gap-2 w-full md:w-auto border-t md:border-t-0 pt-3 md:pt-0">
          <button 
            onClick={() => toggleOrden('fecha')}
            className={`flex-1 md:flex-none flex items-center justify-center gap-2 px-4 py-2 rounded-xl text-xs font-bold border transition-all cursor-pointer ${orden.campo === 'fecha' ? 'bg-indigo-50 border-indigo-200 text-indigo-700' : 'bg-white border-gray-200 text-gray-600 hover:bg-gray-50'}`}
          >
            <Calendar size={14} /> Fecha {orden.campo === 'fecha' && (orden.dir === 'desc' ? '↓' : '↑')}
          </button>
          <button 
            onClick={() => toggleOrden('monto')}
            className={`flex-1 md:flex-none flex items-center justify-center gap-2 px-4 py-2 rounded-xl text-xs font-bold border transition-all cursor-pointer ${orden.campo === 'monto' ? 'bg-indigo-50 border-indigo-200 text-indigo-700' : 'bg-white border-gray-200 text-gray-600 hover:bg-gray-50'}`}
          >
            <ArrowUpDown size={14} /> Monto {orden.campo === 'monto' && (orden.dir === 'desc' ? '↓' : '↑')}
          </button>
        </div>
      </div>

      {/* RESUMEN DE FILTRO (Solo si no es TODOS y no es CANCELADO) */}
      {filtroEstado !== 'TODOS' && filtroEstado !== 'CANCELADO' && (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 animate-in slide-in-from-top duration-300">
          <div className="bg-white p-4 rounded-2xl border border-gray-100 shadow-sm flex items-center gap-4">
            <div className={`p-3 rounded-xl ${filtroEstado === 'COMPLETADO' ? 'bg-green-100 text-green-600' : 'bg-amber-100 text-amber-600'}`}>
              <TrendingUp size={20} />
            </div>
            <div>
              <p className="text-gray-400 text-[10px] font-bold uppercase">Cant. Ventas {filtroEstado.toLowerCase()}</p>
              <p className="text-xl font-black text-gray-800">{statsFiltro.cantidad}</p>
            </div>
          </div>
          <div className="bg-white p-4 rounded-2xl border border-gray-100 shadow-sm flex items-center gap-4">
            <div className={`p-3 rounded-xl ${filtroEstado === 'COMPLETADO' ? 'bg-green-100 text-green-600' : 'bg-amber-100 text-amber-600'}`}>
              <Wallet size={20} />
            </div>
            <div>
              <p className="text-gray-400 text-[10px] font-bold uppercase">Monto Total {filtroEstado.toLowerCase()}</p>
              <p className="text-xl font-black text-gray-800">${statsFiltro.montoTotal.toLocaleString()}</p>
            </div>
          </div>
        </div>
      )}

      {/* TABLA */}
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead className="bg-gray-50/50 border-b border-gray-100">
              <tr>
                <th className="p-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Datos de Venta</th>
                <th className="hidden sm:table-cell p-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Monto</th>
                <th className="hidden sm:table-cell p-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Estado</th>
                <th className="p-4 text-xs font-bold text-gray-500 uppercase tracking-wider text-right">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-50">
              {ventasProcesadas.map((v) => (
                <tr key={v.id} className="hover:bg-gray-50/50 transition-colors group">
                  <td className="p-4">
                    <div className="flex items-center gap-4">
                      <div className="hidden xs:flex flex-col items-center justify-center bg-indigo-50 text-indigo-600 w-10 h-10 rounded-xl border border-indigo-100 shrink-0 group-hover:bg-indigo-600 group-hover:text-white transition-all">
                        <span className="text-[9px] font-bold uppercase leading-none">{new Date(v.fecha).toLocaleString('es-AR', { month: 'short' })}</span>
                        <span className="text-base font-black leading-none">{new Date(v.fecha).getDate()}</span>
                      </div>
                      <div className="flex flex-col min-w-0">
                        <span className="font-bold text-gray-800 text-sm truncate">
                          {v.cliente ? `${v.cliente.nombre} ${v.cliente.apellido}` : "Consumidor Final"}
                        </span>
                        <div className="flex items-center gap-1 text-gray-400 text-[11px]">
                          <Calendar size={12} />
                          <span>{formatearFechaCompleta(v.fecha)}</span>
                        </div>
                      </div>
                    </div>
                  </td>
                  <td className="hidden sm:table-cell p-4">
                    <span className="text-gray-900 font-bold text-sm">${v.monto?.toLocaleString()}</span>
                  </td>
                  <td className="hidden sm:table-cell p-4">
                    <span className={`px-2.5 py-1 rounded-full text-[10px] font-bold border ${getBadgeStyle(v.estado)}`}>
                      {v.estado}
                    </span>
                  </td>
                  <td className="p-4 text-right">
                    <div className="flex justify-end gap-1">
                      <button onClick={() => manejarVerDetalle(v)} className="p-2 text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors cursor-pointer" title="Ver detalle">
                        <Eye size={18} />
                      </button>
                      <button onClick={() => { setVentaAEditar(v); setIsModalOpen(true); }} className="p-2 text-blue-500 hover:bg-blue-50 rounded-lg transition-colors cursor-pointer" title="Editar">
                        <Edit size={18} />
                      </button>
                      <button onClick={() => { setIdAEliminar(v.id); setIsDeleteModalOpen(true); }} className="p-2 text-red-400 hover:bg-red-50 rounded-lg transition-colors cursor-pointer" title="Eliminar">
                        <Trash2 size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {ventasProcesadas.length === 0 && !loading && (
            <div className="p-12 text-center text-gray-400 italic text-sm">
              No se encontraron ventas con el filtro seleccionado.
            </div>
          )}
        </div>
      </div>

      {/* MODALES */}
      <VentaForm isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setVentaAEditar(null); }} onRefresh={cargarVentas} datosEdicion={ventaAEditar} />
      <ConfirmModal isOpen={isDeleteModalOpen} onClose={() => setIsDeleteModalOpen(false)} onConfirm={async () => { await deleteVenta(idAEliminar); cargarVentas(); setIsDeleteModalOpen(false); }} title="¿Eliminar venta?" message="Esta acción es irreversible." />
      <VentaDetalleModal isOpen={isDetalleOpen} onClose={() => setIsDetalleOpen(false)} venta={ventaDetalle} />
    </div>
  );
};

export default Ventas;