import { useState, useEffect } from 'react';
import { X, Type, FileText, Image as ImageIcon, Calendar, Tag, Activity } from 'lucide-react';
import { getCategoriasContenido, createContenido, updateContenido } from '../api/contenidoService';

const ContenidoForm = ({ isOpen, onClose, onRefresh, datosEdicion }) => {
  const [categorias, setCategorias] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);

  const [formData, setFormData] = useState({
    titulo: '',
    descripcion: '',
    estado: 'BORRADOR',
    fechaProgramada: '',
    categoriaContenidoId: ''
  });

  useEffect(() => {
    if (isOpen) {
      cargarCategorias();
      if (datosEdicion) {
        setFormData({
          titulo: datosEdicion.titulo || '',
          descripcion: datosEdicion.descripcion || '',
          estado: datosEdicion.estado || 'BORRADOR',
          // Cortamos la fecha para que el input datetime-local la acepte (YYYY-MM-DDTHH:mm)
          fechaProgramada: datosEdicion.fechaProgramada ? datosEdicion.fechaProgramada.slice(0, 16) : '',
          categoriaContenidoId: datosEdicion.categoriaContenidoId || ''
        });
        setPreviewUrl(datosEdicion.imagen); // Mostrar imagen actual si existe
      } else {
        setFormData({
          titulo: '',
          descripcion: '',
          estado: 'BORRADOR',
          fechaProgramada: '',
          categoriaContenidoId: ''
        });
        setPreviewUrl(null);
        setSelectedFile(null);
      }
    }
  }, [isOpen, datosEdicion]);

  const cargarCategorias = async () => {
    try {
      const data = await getCategoriasContenido();
      setCategorias(data);
    } catch (error) {
      console.error("Error cargando categorías:", error);
    }
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedFile(file);
      setPreviewUrl(URL.createObjectURL(file)); // Crear vista previa temporal
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
        const dataPayload = new FormData();

        // 1. Construir el objeto con los datos
        const dto = {
            titulo: formData.titulo,
            descripcion: formData.descripcion,
            estado: formData.estado,
            categoriaContenidoId: parseInt(formData.categoriaContenidoId),
            // Formato ISO: YYYY-MM-DDTHH:mm:ss
            fechaProgramada: formData.fechaProgramada ? `${formData.fechaProgramada}:00` : null
        };

        // 2. Agregar el DTO como STRING plano
        // El backend lo recibirá como @RequestPart("dto") String dtoStr
        dataPayload.append("dto", JSON.stringify(dto));

        // 3. Agregar la imagen SOLAMENTE UNA VEZ
        if (selectedFile) {
            dataPayload.append("imagen", selectedFile);
        }

        // 4. Ejecutar la llamada según corresponda
        if (datosEdicion) {
            await updateContenido(datosEdicion.id, dataPayload);
        } else {
            await createContenido(dataPayload);
        }

        onRefresh();
        onClose();
    } catch (error) {
        console.error("Error detallado:", error.response?.data);
        // Ahora el error 500 debería haber desaparecido
        alert("Error al guardar el contenido: " + (error.response?.data?.message || "Error interno del servidor"));
    } finally {
        setLoading(false);
    }
};

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-70 p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden animate-in zoom-in duration-200">
        <div className="flex justify-between items-center p-6 border-b bg-gray-50/50">
          <h2 className="text-xl font-bold text-gray-800">
            {datosEdicion ? 'Editar Contenido' : 'Nuevo Contenido'}
          </h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
            <X size={24} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4 max-h-[80vh] overflow-y-auto">
          {/* Título */}
          <div>
            <label className="text-xs font-bold text-gray-500 uppercase mb-1 flex items-center gap-2">
              <Type size={14} /> Título del Post
            </label>
            <input
              type="text" required
              className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none transition-all"
              value={formData.titulo}
              onChange={(e) => setFormData({ ...formData, titulo: e.target.value })}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            {/* Categoría */}
            <div>
              <label className="text-xs font-bold text-gray-500 uppercase mb-1 flex items-center gap-2">
                <Tag size={14} /> Categoría
              </label>
              <select
                required
                className="w-full px-4 py-2 border rounded-lg bg-white outline-none focus:ring-2 focus:ring-indigo-500"
                value={formData.categoriaContenidoId}
                onChange={(e) => setFormData({ ...formData, categoriaContenidoId: e.target.value })}
              >
                <option value="">Seleccionar...</option>
                {categorias.map(cat => <option key={cat.id} value={cat.id}>{cat.nombre}</option>)}
              </select>
            </div>
            {/* Estado */}
            <div>
              <label className="text-xs font-bold text-gray-500 uppercase mb-1 flex items-center gap-2">
                <Activity size={14} /> Estado
              </label>
              <select
                className="w-full px-4 py-2 border rounded-lg bg-white outline-none focus:ring-2 focus:ring-indigo-500"
                value={formData.estado}
                onChange={(e) => setFormData({ ...formData, estado: e.target.value })}
              >
                <option value="BORRADOR">Borrador</option>
                <option value="PROGRAMADO">Programado</option>
                <option value="PUBLICADO">Publicado</option>
              </select>
            </div>
          </div>

          {/* Fecha Programada */}
          <div>
            <label className="text-xs font-bold text-gray-500 uppercase mb-1 flex items-center gap-2">
              <Calendar size={14} /> Fecha y Hora de Publicación
            </label>
            <input
              type="datetime-local"
              className="w-full px-4 py-2 border rounded-lg outline-none focus:ring-2 focus:ring-indigo-500"
              value={formData.fechaProgramada}
              onChange={(e) => setFormData({ ...formData, fechaProgramada: e.target.value })}
            />
          </div>

          {/* Carga de Imagen */}
          <div>
            <label className="text-xs font-bold text-gray-500 uppercase mb-1 flex items-center gap-2">
              <ImageIcon size={14} /> Imagen Representativa
            </label>
            <div className="mt-1 flex items-center gap-4">
              {previewUrl && (
                <img src={previewUrl} alt="Preview" className="w-16 h-16 object-cover rounded-lg border" />
              )}
              <input
                type="file"
                accept="image/*"
                onChange={handleFileChange}
                className="text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-xs file:font-bold file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100"
              />
            </div>
          </div>

          {/* Descripción */}
          <div>
            <label className="text-xs font-bold text-gray-500 uppercase mb-1 flex items-center gap-2">
              <FileText size={14} /> Descripción / Cuerpo del contenido
            </label>
            <textarea
              rows="3"
              className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none transition-all resize-none"
              placeholder="¿De qué trata este post?"
              value={formData.descripcion}
              onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
            />
          </div>

          <div className="flex gap-3 pt-6 border-t border-gray-100">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-2.5 border border-gray-200 text-gray-600 rounded-xl font-semibold hover:bg-gray-50 transition-all"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 py-2.5 bg-indigo-600 text-white rounded-xl font-bold shadow-lg shadow-indigo-100 hover:bg-indigo-700 disabled:bg-indigo-300 transition-all"
            >
              {loading ? 'Subiendo...' : (datosEdicion ? 'Actualizar Contenido' : 'Guardar Contenido')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ContenidoForm;