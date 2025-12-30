import { useState, useEffect } from 'react';
import { X, DollarSign, Calendar, FileText, User, CreditCard, Activity } from 'lucide-react';
import { getClientes } from '../api/clienteService';
import { createVenta, updateVenta } from '../api/ventaService';

// Constantes para los selectores
const ESTADOS = ['PENDIENTE', 'COMPLETADO', 'CANCELADO'];
const METODOS_PAGO = [
  { id: 'EFECTIVO', label: 'Efectivo' },
  { id: 'TRANSFERENCIA', label: 'Transferencia' },
  { id: 'TARJETA_DEBITO', label: 'Tarjeta de Débito' },
  { id: 'TARJETA_CREDITO', label: 'Tarjeta de Crédito' },
  { id: 'MERCADO_PAGO', label: 'Mercado Pago' }
];

const VentaForm = ({ isOpen, onClose, onRefresh, datosEdicion }) => {
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    clienteId: '',
    monto: '',
    concepto: '',
    fecha: new Date().toISOString().split('T')[0],
    estado: 'COMPLETADO',
    metodoPago: 'EFECTIVO'
  });

  useEffect(() => {
    if (isOpen) {
      cargarClientes();
      if (datosEdicion) {
        const fechaFormateada = datosEdicion.fecha ? datosEdicion.fecha.split('T')[0] : '';
        setFormData({
          clienteId: datosEdicion.clienteId || '',
          monto: datosEdicion.monto || '',
          concepto: datosEdicion.descripcion || datosEdicion.concepto || '',
          fecha: fechaFormateada,
          estado: datosEdicion.estado || 'COMPLETADO',
          metodoPago: datosEdicion.metodoPago || 'EFECTIVO'
        });
      } else {
        setFormData({
          clienteId: '',
          monto: '',
          concepto: '',
          fecha: new Date().toISOString().split('T')[0],
          estado: 'COMPLETADO',
          metodoPago: 'EFECTIVO'
        });
      }
    }
  }, [isOpen, datosEdicion]);

  const cargarClientes = async () => {
    try {
      const data = await getClientes();
      setClientes(data);
    } catch (error) {
      console.error("Error cargando clientes:", error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      // Usamos el patrón que configuramos en el backend: yyyy-MM-dd'T'HH:mm:ss.SSS
      const fechaISO = `${formData.fecha}T12:00:00.000`;

      const dataParaEnviar = {
        clienteId: parseInt(formData.clienteId),
        monto: parseFloat(formData.monto),
        descripcion: formData.concepto, // Enviamos como descripcion para el backend
        concepto: formData.concepto,     // Mantenemos concepto por compatibilidad
        fecha: fechaISO,
        estado: formData.estado,
        metodoPago: formData.metodoPago
      };

      if (datosEdicion) {
        await updateVenta(datosEdicion.id, dataParaEnviar);
      } else {
        await createVenta(dataParaEnviar);
      }
      
      onRefresh();
      onClose();
    } catch (error) {
      console.error("Error en el servidor:", error.response?.data);
      alert("Error: " + (error.response?.data?.message || "Revisa los datos ingresados"));
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-70 p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden animate-in zoom-in duration-200">
        <div className="flex justify-between items-center p-6 border-b border-gray-100 bg-gray-50/50">
          <h2 className="text-xl font-bold text-gray-800">
            {datosEdicion ? 'Editar Venta' : 'Registrar Venta'}
          </h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
            <X size={24} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {/* Cliente */}
          <div>
            <label className="text-xs font-bold text-gray-500 uppercase mb-1 flex items-center gap-2">
              <User size={14} /> Cliente
            </label>
            <select
              required
              className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none bg-white transition-all text-sm"
              value={formData.clienteId}
              onChange={(e) => setFormData({...formData, clienteId: e.target.value})}
            >
              <option value="">Selecciona un cliente</option>
              {clientes.map(c => (
                <option key={c.id} value={c.id}>{c.nombre} {c.apellido}</option>
              ))}
            </select>
          </div>

          {/* Concepto / Descripción */}
          <div>
            <label className="text-xs font-bold text-gray-500 uppercase mb-1 flex items-center gap-2">
              <FileText size={14} /> Concepto / Descripción
            </label>
            <input
              type="text" required
              className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none transition-all text-sm"
              placeholder="Ej: Servicio de consultoría"
              value={formData.concepto}
              onChange={(e) => setFormData({...formData, concepto: e.target.value})}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            {/* Monto */}
            <div>
              <label className="text-xs font-bold text-gray-500 uppercase mb-1 flex items-center gap-2">
                <DollarSign size={14} /> Monto
              </label>
              <input
                type="number" step="0.01" required
                className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none transition-all text-sm"
                placeholder="0.00"
                value={formData.monto}
                onChange={(e) => setFormData({...formData, monto: e.target.value})}
              />
            </div>
            {/* Fecha */}
            <div>
              <label className="text-xs font-bold text-gray-500 uppercase mb-1 flex items-center gap-2">
                <Calendar size={14} /> Fecha
              </label>
              <input
                type="date" required
                className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none transition-all text-sm"
                value={formData.fecha}
                onChange={(e) => setFormData({...formData, fecha: e.target.value})}
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            {/* Estado */}
            <div>
              <label className="text-xs font-bold text-gray-500 uppercase mb-1 flex items-center gap-2">
                <Activity size={14} /> Estado
              </label>
              <select
                className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none bg-white transition-all text-sm font-medium"
                value={formData.estado}
                onChange={(e) => setFormData({...formData, estado: e.target.value})}
              >
                {ESTADOS.map(est => (
                  <option key={est} value={est}>{est}</option>
                ))}
              </select>
            </div>
            {/* Método de Pago */}
            <div>
              <label className="text-xs font-bold text-gray-500 uppercase mb-1 flex items-center gap-2">
                <CreditCard size={14} /> Pago
              </label>
              <select
                className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none bg-white transition-all text-sm font-medium"
                value={formData.metodoPago}
                onChange={(e) => setFormData({...formData, metodoPago: e.target.value})}
              >
                {METODOS_PAGO.map(met => (
                  <option key={met.id} value={met.id}>{met.label}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="flex space-x-3 pt-6 border-t border-gray-100">
            <button
              type="button" onClick={onClose}
              className="flex-1 px-4 py-2.5 border border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 font-semibold transition-colors text-sm"
            >
              Cancelar
            </button>
            <button
              type="submit" disabled={loading}
              className="flex-1 px-4 py-2.5 bg-indigo-600 text-white rounded-xl hover:bg-indigo-700 disabled:bg-indigo-300 font-bold shadow-lg shadow-indigo-100 transition-all text-sm"
            >
              {loading ? 'Cargando...' : (datosEdicion ? 'Actualizar' : 'Guardar Venta')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default VentaForm;