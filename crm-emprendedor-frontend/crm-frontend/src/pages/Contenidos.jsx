import { useEffect, useState } from 'react';
import { Plus, Trash2, Edit, Calendar, Tag, Image as ImageIcon, LayoutGrid, Filter, X } from 'lucide-react';
import { getContenidos, deleteContenido } from '../api/contenidoService';
import { getCategorias } from '../api/categoriaService'; // Importamos el servicio de categorías
import ContenidoForm from '../components/ContenidoForm';
import ConfirmModal from '../components/ConfirmModal';

const Contenidos = () => {
  const [contenidos, setContenidos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Estados para Filtros
  const [filtroCategoria, setFiltroCategoria] = useState('');
  const [filtroFecha, setFiltroFecha] = useState('');

  // Modales
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [contenidoAEditar, setContenidoAEditar] = useState(null);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [idAEliminar, setIdAEliminar] = useState(null);

  const BASE_URL = "http://localhost:8080";

  useEffect(() => {
    cargarDatosIniciales();
  }, []);

  const cargarDatosIniciales = async () => {
    setLoading(true);
    try {
      // Cargamos contenidos y categorías en paralelo
      const [dataContenidos, dataCategorias] = await Promise.all([
        getContenidos(),
        getCategorias()
      ]);
      setContenidos(Array.isArray(dataContenidos) ? dataContenidos : []);
      setCategorias(Array.isArray(dataCategorias) ? dataCategorias : []);
    } catch (error) {
      console.error("Error cargando datos:", error);
    } finally {
      setLoading(false);
    }
  };

  const cargarContenidos = async () => {
    try {
      const data = await getContenidos();
      setContenidos(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error("Error al refrescar contenidos:", error);
    }
  };

  // 1. Definimos la lista filtrada (Sin console.logs externos que rompan el código)
  const contenidosFiltrados = contenidos.filter(item => {
    // Filtro de Categoría
    const idItem = item.categoriaContenido?.id?.toString();
    const idFiltro = filtroCategoria?.toString();
    const coincideCat = !filtroCategoria || filtroCategoria === "" || idItem === idFiltro;

    // Filtro de Fecha
    let coincideFecha = true;
    if (filtroFecha !== '') {
      let fechaItemNormalizada = '';
      if (Array.isArray(item.fechaProgramada)) {
        const [y, m, d] = item.fechaProgramada;
        fechaItemNormalizada = `${y}-${m.toString().padStart(2, '0')}-${d.toString().padStart(2, '0')}`;
      } else if (typeof item.fechaProgramada === 'string') {
        fechaItemNormalizada = item.fechaProgramada.split('T')[0];
      }
      coincideFecha = fechaItemNormalizada === filtroFecha;
    }

    return coincideCat && coincideFecha;
  });

  // --- REVISA AQUÍ ABAJO (Línea 108 aprox) ---
  // Asegúrate de que NO tengas ningún console.log(item) o referencia a item 
  // antes de llegar al return (...) del componente.

  const getEstadoStyle = (estado) => {
    switch (estado) {
      case 'PUBLICADO': return 'bg-green-100 text-green-700 border-green-200';
      case 'PROGRAMADO': return 'bg-blue-100 text-blue-700 border-blue-200';
      case 'BORRADOR': return 'bg-gray-100 text-gray-700 border-gray-200';
      default: return 'bg-gray-100 text-gray-700';
    }
  };

  // Función de fecha corregida para evitar el formato numérico largo
  const formatearFecha = (fechaRaw) => {
  if (!fechaRaw) return "Sin fecha";

  // Si llega como array [2025, 12, 23, ...] lo convertimos
  if (Array.isArray(fechaRaw)) {
    const [y, m, d, hh, mm] = fechaRaw;
    return `${d.toString().padStart(2, '0')}/${m.toString().padStart(2, '0')}/${y} ${hh}:${mm}`;
  }

  // Si llega como String ISO
  try {
    const date = new Date(fechaRaw);
    return date.toLocaleString('es-AR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (e) {
    return "Error fecha";
  }
};

  const handleEliminar = async () => {
    try {
      await deleteContenido(idAEliminar);
      cargarContenidos();
      setIsDeleteModalOpen(false);
    } catch (error) {
      alert("Error al eliminar");
    }
  };

  

  return (
    <div className="space-y-6 p-4 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Planificador de Contenidos</h1>
          <p className="text-gray-500 text-sm">Gestiona tus publicaciones y redes sociales</p>
        </div>
        <button 
          onClick={() => { setContenidoAEditar(null); setIsModalOpen(true); }}
          className="flex items-center justify-center space-x-2 bg-indigo-600 hover:bg-indigo-700 text-white px-5 py-2.5 rounded-xl transition-all shadow-lg shadow-indigo-200 font-semibold cursor-pointer active:scale-95"
        >
          <Plus size={20} />
          <span>Crear Contenido</span>
        </button>
      </div>

      {/* Barra de Filtros */}
      <div className="bg-white p-4 rounded-2xl border border-gray-100 shadow-sm flex flex-col md:flex-row items-end gap-4">
        <div className="w-full md:w-64">
          <label className="block text-[10px] font-bold text-gray-400 uppercase mb-1.5 ml-1">Filtrar por Categoría</label>
          <div className="relative">
            <select 
              value={filtroCategoria}
              onChange={(e) => setFiltroCategoria(e.target.value)}
              className="w-full bg-gray-50 border border-gray-200 rounded-xl p-2.5 text-sm outline-none focus:ring-2 focus:ring-indigo-500 cursor-pointer appearance-none"
            >
              <option value="">Todas las categorías</option>
              {categorias.map(cat => (
                <option key={cat.id} value={cat.id}>{cat.nombre}</option>
              ))}
            </select>
            <Tag className="absolute right-3 top-3 text-gray-400 pointer-events-none" size={16} />
          </div>
        </div>

        <div className="w-full md:w-64">
          <label className="block text-[10px] font-bold text-gray-400 uppercase mb-1.5 ml-1">Filtrar por Fecha</label>
          <input 
            type="date"
            value={filtroFecha}
            onChange={(e) => setFiltroFecha(e.target.value)}
            className="w-full bg-gray-50 border border-gray-200 rounded-xl p-2.5 text-sm outline-none focus:ring-2 focus:ring-indigo-500 cursor-pointer"
          />
        </div>

        {(filtroCategoria || filtroFecha) && (
          <button 
            onClick={() => { setFiltroCategoria(''); setFiltroFecha(''); }}
            className="flex items-center gap-1 text-red-500 hover:text-red-600 text-sm font-medium p-2.5 transition-colors cursor-pointer"
          >
            <X size={16} />
            Limpiar
          </button>
        )}
      </div>

      {/* Grid de Contenidos */}
      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {[1, 2, 3].map(n => (
            <div key={n} className="bg-white border border-gray-100 h-80 rounded-2xl animate-pulse"></div>
          ))}
        </div>
      ) : contenidosFiltrados.length === 0 ? (
        <div className="bg-white border-2 border-dashed border-gray-200 rounded-3xl p-12 text-center">
          <div className="bg-gray-50 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
            <LayoutGrid className="text-gray-400" size={32} />
          </div>
          <h3 className="text-gray-600 font-medium">No se encontraron resultados</h3>
          <p className="text-gray-400 text-sm mb-6">Prueba ajustando los filtros o crea un nuevo contenido.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {contenidosFiltrados.map((item) => (
            <div key={item.id} className="bg-white rounded-2xl border border-gray-100 shadow-sm hover:shadow-md transition-all overflow-hidden group">
              <div className="relative h-48 bg-gray-200 overflow-hidden">
                {item.imagen ? (
                  <img 
                    src={`${BASE_URL}/uploads/contenidos/${item.imagen}`} 
                    alt={item.titulo}
                    className="w-full h-48 object-cover group-hover:scale-105 transition-transform duration-500"
                    onError={(e) => {
                      e.target.onerror = null; 
                      e.target.src = "https://placehold.co/400x200/e2e8f0/64748b?text=Imagen+no+disponible"; 
                    }}
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center bg-indigo-50 text-indigo-200">
                    <ImageIcon size={48} />
                  </div>
                )}
                <div className="absolute top-3 left-3">
                  <span className={`px-2.5 py-1 rounded-full text-[10px] font-bold border backdrop-blur-md ${getEstadoStyle(item.estado)}`}>
                    {item.estado}
                  </span>
                </div>
              </div>

              <div className="p-5">
                <div className="flex justify-between items-start mb-2">
                  <h3 className="font-bold text-gray-800 line-clamp-1">{item.titulo}</h3>
                  <div className="flex gap-1 opacity-100 lg:opacity-0 lg:group-hover:opacity-100 transition-opacity">
                    <button 
                      onClick={() => { setContenidoAEditar(item); setIsModalOpen(true); }}
                      className="p-1.5 text-blue-600 hover:bg-blue-50 rounded-lg cursor-pointer"
                    >
                      <Edit size={16} />
                    </button>
                    <button 
                      onClick={() => { setIdAEliminar(item.id); setIsDeleteModalOpen(true); }}
                      className="p-1.5 text-red-600 hover:bg-red-50 rounded-lg cursor-pointer"
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                </div>
                
                <p className="text-gray-500 text-sm line-clamp-2 mb-4 h-10">
                  {item.descripcion || "Sin descripción..."}
                </p>

                <div className="flex items-center justify-between pt-4 border-t border-gray-50 text-[11px] font-medium text-gray-400">
                  <div className="flex items-center gap-1.5">
                    <Calendar size={14} className="text-indigo-400" />
                    <span>{formatearFecha(item.fechaProgramada)}</span>
                  </div>
                  <div className="flex items-center gap-1.5">
                    <Tag size={14} className="text-gray-400" />
                    <span className="truncate max-w-25 bg-gray-50 px-2 py-0.5 rounded text-gray-600">
  {item.categoriaContenido?.nombre || 'Sin Categoría'}
</span>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Componentes Externos */}
      <ContenidoForm 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        onRefresh={cargarContenidos}
        datosEdicion={contenidoAEditar}
      />

      <ConfirmModal 
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onConfirm={handleEliminar}
        title="¿Eliminar este contenido?"
        message="Esta acción no se puede deshacer y borrará la imagen asociada."
      />
    </div>
  );
};

export default Contenidos;