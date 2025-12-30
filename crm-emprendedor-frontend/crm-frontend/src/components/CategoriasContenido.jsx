import { useState, useEffect } from 'react';
import { getCategorias, createCategoria, deleteCategoria, updateCategoria } from '../api/categoriaService';
import { Tag, Plus, Trash2, Edit2, X, Check, Loader2, AlertCircle, CheckCircle2 } from 'lucide-react';

const CategoriasContenido = () => {
  const [categorias, setCategorias] = useState([]);
  const [nuevoNombre, setNuevoNombre] = useState('');
  const [loading, setLoading] = useState(true);
  const [btnLoading, setBtnLoading] = useState(false);
  
  // Estados para edición
  const [editandoId, setEditandoId] = useState(null);
  const [nombreEditado, setNombreEditado] = useState('');
  
  const [msg, setMsg] = useState({ type: '', text: '' });

  useEffect(() => { cargarCategorias(); }, []);

  const cargarCategorias = async () => {
    try {
      const data = await getCategorias();
      setCategorias(data);
    } catch (error) {
      setMsg({ type: 'error', text: 'Error al cargar datos.' });
    } finally { setLoading(false); }
  };

  const handleCrear = async (e) => {
    e.preventDefault();
    if (!nuevoNombre.trim()) return;
    setBtnLoading(true);
    try {
      await createCategoria(nuevoNombre);
      setNuevoNombre('');
      setMsg({ type: 'success', text: 'Categoría creada.' });
      cargarCategorias();
    } catch (error) {
      setMsg({ type: 'error', text: 'Error al crear (Verifica el Backend).' });
    } finally { setBtnLoading(false); }
  };

  const handleUpdate = async (id) => {
    if (!nombreEditado.trim()) return;
    try {
      await updateCategoria(id, nombreEditado);
      setEditandoId(null);
      setMsg({ type: 'success', text: 'Nombre actualizado.' });
      cargarCategorias();
    } catch (error) {
      setMsg({ type: 'error', text: 'Error al actualizar.' });
    }
  };

  if (loading) return <div className="p-10 text-center animate-pulse">Cargando...</div>;

  return (
  <div className="max-w-4xl mx-auto p-4 md:p-6 space-y-6">
    <h1 className="text-2xl font-bold flex items-center gap-2 text-gray-800">
      <Tag className="text-indigo-600" /> Categorías
    </h1>

    {msg.text && (
      <div className={`p-3 rounded-xl text-sm font-medium flex items-center gap-2 animate-in fade-in duration-300 ${
        msg.type === 'success' ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'
      }`}>
        {msg.type === 'success' ? <CheckCircle2 size={16}/> : <AlertCircle size={16}/>}
        {msg.text}
      </div>
    )}

    {/* Formulario Crear - Responsive: Columna en móvil, Fila en tablet/pc */}
    <form onSubmit={handleCrear} className="flex flex-col sm:flex-row gap-3 bg-white p-4 rounded-2xl shadow-sm border border-gray-100">
      <input 
        className="flex-1 p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 transition-all text-sm"
        value={nuevoNombre}
        onChange={(e) => setNuevoNombre(e.target.value)}
        placeholder="Nombre de la categoría..."
      />
      <button 
        type="submit"
        className="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-3 rounded-xl font-bold text-sm cursor-pointer active:scale-95 transition-all flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
        disabled={btnLoading || !nuevoNombre.trim()}
      >
        {btnLoading ? <Loader2 size={18} className="animate-spin" /> : <Plus size={18} />}
        <span>Agregar</span>
      </button>
    </form>

    {/* Listado - Grid adaptable */}
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-2 gap-4">
      {categorias.map((cat) => (
        <div key={cat.id} className="bg-white p-4 rounded-2xl border border-gray-100 flex items-center justify-between hover:shadow-md transition-all group">
          {editandoId === cat.id ? (
            <div className="flex items-center gap-2 w-full">
              <input 
                className="flex-1 p-2 bg-indigo-50 border-b-2 border-indigo-500 outline-none text-sm font-semibold rounded-t-md"
                value={nombreEditado}
                onChange={(e) => setNombreEditado(e.target.value)}
                autoFocus
              />
              <div className="flex gap-1">
                <button onClick={() => handleUpdate(cat.id)} className="p-2 text-green-600 cursor-pointer hover:bg-green-100 rounded-lg transition-colors"><Check size={20}/></button>
                <button onClick={() => setEditandoId(null)} className="p-2 text-red-400 cursor-pointer hover:bg-red-50 rounded-lg transition-colors"><X size={20}/></button>
              </div>
            </div>
          ) : (
            <>
              <span className="font-semibold text-gray-700 truncate mr-2">{cat.nombre}</span>
              <div className="flex gap-1 shrink-0">
                <button 
                  onClick={() => { setEditandoId(cat.id); setNombreEditado(cat.nombre); }}
                  className="p-2 text-blue-500 hover:bg-blue-50 rounded-lg cursor-pointer transition-colors sm:opacity-0 group-hover:opacity-100 focus:opacity-100"
                  title="Editar"
                >
                  <Edit2 size={16} />
                </button>
                <button 
                  onClick={() => { if(window.confirm('¿Eliminar?')) deleteCategoria(cat.id).then(cargarCategorias) }}
                  className="p-2 text-red-500 hover:bg-red-50 rounded-lg cursor-pointer transition-colors sm:opacity-0 group-hover:opacity-100 focus:opacity-100"
                  title="Eliminar"
                >
                  <Trash2 size={16} />
                </button>
              </div>
            </>
          )}
        </div>
      ))}
    </div>
  </div>
);
}

export default CategoriasContenido;