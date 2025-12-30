import { X, Receipt, Calendar, User, CreditCard, Tag } from 'lucide-react';

const VentaDetalleModal = ({ isOpen, onClose, venta }) => {
  if (!isOpen || !venta) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
      <div className="bg-white rounded-3xl w-full max-w-md overflow-hidden shadow-2xl animate-in zoom-in duration-200">
        <div className="p-6 border-b border-gray-100 flex justify-between items-center bg-gray-50/50">
          <div className="flex items-center gap-2">
            <div className="p-2 bg-blue-100 text-blue-600 rounded-lg">
              <Receipt size={20} />
            </div>
            <h2 className="font-bold text-gray-800">Detalle de Venta</h2>
          </div>
          <button onClick={onClose} className="p-2 hover:bg-gray-200 rounded-full transition-colors cursor-pointer">
            <X size={20} className="text-gray-500" />
          </button>
        </div>

        <div className="p-6 space-y-6">
          <div className="text-center pb-4 border-b border-dashed border-gray-200">
            <p className="text-sm text-gray-400 uppercase font-bold tracking-widest">Monto Total</p>
            <p className="text-4xl font-black text-gray-800">${Number(venta.monto).toLocaleString()}</p>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-1">
              <label className="text-[10px] font-bold text-gray-400 uppercase flex items-center gap-1">
                <User size={12} /> Cliente
              </label>
              <p className="text-sm font-semibold text-gray-700">
                {venta.cliente?.nombre} {venta.cliente?.apellido}
              </p>
            </div>
            <div className="space-y-1 text-right">
              <label className="text-[10px] font-bold text-gray-400 uppercase flex items-center gap-1 justify-end">
                <Calendar size={12} /> Fecha
              </label>
              <p className="text-sm font-semibold text-gray-700">
                {new Date(venta.fecha).toLocaleDateString()}
              </p>
            </div>
            <div className="space-y-1">
              <label className="text-[10px] font-bold text-gray-400 uppercase flex items-center gap-1">
                <CreditCard size={12} /> Pago
              </label>
              <p className="text-sm font-semibold text-gray-700">{venta.metodoPago || 'No especificado'}</p>
            </div>
            <div className="space-y-1 text-right">
              <label className="text-[10px] font-bold text-gray-400 uppercase flex items-center gap-1 justify-end">
                <Tag size={12} /> Estado
              </label>
              <span className="inline-block px-2 py-0.5 bg-green-100 text-green-700 rounded text-[10px] font-bold">
                {venta.estado || 'COMPLETADO'}
              </span>
            </div>
          </div>

          {venta.descripcion && (
            <div className="bg-gray-50 p-4 rounded-xl border border-gray-100">
              <p className="text-[10px] font-bold text-gray-400 uppercase mb-1">Notas de la venta</p>
              <p className="text-sm text-gray-600 italic">"{venta.descripcion}"</p>
            </div>
          )}
        </div>

        <div className="p-4 bg-gray-50 border-t border-gray-100">
          <button 
            onClick={onClose}
            className="w-full py-3 bg-gray-800 text-white rounded-xl font-bold text-sm hover:bg-black transition-all cursor-pointer shadow-lg"
          >
            Entendido
          </button>
        </div>
      </div>
    </div>
  );
};

export default VentaDetalleModal;