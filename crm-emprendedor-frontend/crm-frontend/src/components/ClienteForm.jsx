import { useState, useEffect } from 'react'; // Agregado useEffect aquí
import { X } from 'lucide-react';
import api from '../api/axiosConfig';

const ClienteForm = ({ isOpen, onClose, onRefresh, datosEdicion }) => {
  const [formData, setFormData] = useState({ 
    nombre: '', apellido: '', email: '', telefono: '', etiqueta: 'NUEVO' 
  });
  const [loading, setLoading] = useState(false);

  // Efecto para cargar datos si vamos a editar
  useEffect(() => {
    if (datosEdicion) {
      setFormData({
        nombre: datosEdicion.nombre || '',
        apellido: datosEdicion.apellido || '',
        email: datosEdicion.email || '',
        telefono: datosEdicion.telefono || '',
        etiqueta: datosEdicion.etiqueta || 'NUEVO'
      });
    } else {
      setFormData({ nombre: '', apellido: '', email: '', telefono: '', etiqueta: 'NUEVO' });
    }
  }, [datosEdicion, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      if (datosEdicion) {
        // PUT para editar
        await api.put(`/clientes/${datosEdicion.id}`, formData);
      } else {
        // POST para crear
        await api.post('/clientes', formData);
      }
      onRefresh();
      onClose(); 
    } catch (error) {
      console.error("Error en la operación:", error);
      alert("Error al procesar la solicitud");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden animate-in fade-in zoom-in duration-200">
        <div className="flex justify-between items-center p-6 border-b border-gray-100">
          <h2 className="text-xl font-bold text-gray-800">
            {datosEdicion ? 'Editar Cliente' : 'Nuevo Cliente'}
          </h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
            <X size={24} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Nombre</label>
              <input
                type="text" required
                className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                value={formData.nombre}
                onChange={(e) => setFormData({...formData, nombre: e.target.value})}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Apellido</label>
              <input
                type="text" required
                className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                value={formData.apellido}
                onChange={(e) => setFormData({...formData, apellido: e.target.value})}
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input
              type="email" required
              className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
              value={formData.email}
              onChange={(e) => setFormData({...formData, email: e.target.value})}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Teléfono</label>
            <input
              type="text"
              className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
              value={formData.telefono}
              onChange={(e) => setFormData({...formData, telefono: e.target.value})}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Etiqueta</label>
            <select 
              className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none bg-white"
              value={formData.etiqueta}
              onChange={(e) => setFormData({...formData, etiqueta: e.target.value})}
            >
              <option value="NUEVO">Nuevo</option>
              <option value="POTENCIAL">Potencial</option>
              <option value="VIP">VIP</option>
            </select>
          </div>

          <div className="flex space-x-3 pt-4">
            <button
              type="button" onClick={onClose}
              className="flex-1 px-4 py-2 border border-gray-200 text-gray-600 rounded-lg hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              type="submit" disabled={loading}
              className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-blue-300 font-bold"
            >
              {loading ? 'Procesando...' : (datosEdicion ? 'Guardar Cambios' : 'Crear Cliente')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ClienteForm;