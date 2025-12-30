import { useEffect, useState, useMemo } from 'react';
import { getClientes, deleteCliente } from '../api/clienteService';
import { UserPlus, Trash2, Edit, Search, ArrowUpDown, FileDown, FileText } from 'lucide-react';
import * as XLSX from 'xlsx';
import jsPDF from 'jspdf';
import 'jspdf-autotable';
import ClienteForm from '../components/ClienteForm';
import ConfirmModal from '../components/ConfirmModal';

const Clientes = () => {
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortOrder, setSortOrder] = useState('asc');

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [clienteAEditar, setClienteAEditar] = useState(null);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [idAEliminar, setIdAEliminar] = useState(null);

  useEffect(() => {
    cargarClientes();
  }, []);

  const cargarClientes = async () => {
    setLoading(true);
    try {
      const data = await getClientes();
      setClientes(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error("Error:", error);
    } finally {
      setLoading(false);
    }
  };

  // --- LÓGICA DE PROCESAMIENTO ---
  const clientesProcesados = useMemo(() => {
    let resultado = clientes.filter(c => 
      c.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.apellido?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.email?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    resultado.sort((a, b) => {
      const apA = (a.apellido || "").toLowerCase();
      const apB = (b.apellido || "").toLowerCase();
      return sortOrder === 'asc' ? apA.localeCompare(apB) : apB.localeCompare(apA);
    });

    return resultado;
  }, [clientes, searchTerm, sortOrder]);

  // --- EXPORTAR EXCEL ---
  const exportarExcel = () => {
    const data = clientesProcesados.map(c => ({
      Apellido: c.apellido?.toUpperCase(),
      Nombre: c.nombre,
      Email: c.email,
      Telefono: c.telefono || 'Sin registro',
      Etiqueta: c.etiqueta || 'NUEVO'
    }));
    const hoja = XLSX.utils.json_to_sheet(data);
    const libro = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(libro, hoja, "Clientes");
    XLSX.writeFile(libro, "Reporte_Clientes.xlsx");
  };

  // --- EXPORTAR PDF ---
  const exportarPDF = () => {
    const doc = new jsPDF();
    doc.text("Listado de Clientes", 14, 15);
    
    const tablaData = clientesProcesados.map(c => [
      c.apellido?.toUpperCase(),
      c.nombre,
      c.email,
      c.telefono || '-'
    ]);

    doc.autoTable({
      head: [['Apellido', 'Nombre', 'Email', 'Teléfono']],
      body: tablaData,
      startY: 20,
      theme: 'grid',
      headStyles: { fillColor: [37, 99, 235] } // Color azul profesional
    });

    doc.save("Reporte_Clientes.pdf");
  };

  const handleEditClick = (c) => { setClienteAEditar(c); setIsModalOpen(true); };
  const handleDeleteClick = (id) => { setIdAEliminar(id); setIsDeleteModalOpen(true); };

  const confirmarEliminacion = async () => {
    await deleteCliente(idAEliminar);
    cargarClientes();
    setIsDeleteModalOpen(false);
  };

  const getBadgeColor = (etiqueta) => {
  const mapping = {
    'VIP': 'bg-amber-100 text-amber-700 border-amber-200',
    'NUEVO': 'bg-green-100 text-green-700 border-green-200',
    'POTENCIAL': 'bg-blue-100 text-blue-700 border-blue-200',
    'RECURRENTE': 'bg-purple-100 text-purple-700 border-purple-200',
    'PERDIDO': 'bg-gray-100 text-gray-500 border-gray-200',
  };
  return mapping[etiqueta?.toUpperCase()] || 'bg-gray-50 text-gray-600 border-gray-100';
};

  return (
    <div className="space-y-6 p-4">
      {/* Cabecera Adaptada */}
      <div className="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Clientes</h1>
          <p className="text-gray-500 text-sm">Base de datos de clientes registrados</p>
        </div>
        
        <div className="flex flex-wrap gap-2 w-full lg:w-auto">
          <button onClick={exportarPDF} className="flex-1 lg:flex-none flex items-center justify-center space-x-2 bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg transition-all shadow-md font-medium text-sm">
            <FileText size={18} /> <span>PDF</span>
          </button>
          
          <button onClick={exportarExcel} className="flex-1 lg:flex-none flex items-center justify-center space-x-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg transition-all shadow-md font-medium text-sm">
            <FileDown size={18} /> <span>Excel</span>
          </button>

          <button onClick={() => { setClienteAEditar(null); setIsModalOpen(true); }} className="w-full lg:w-auto flex items-center justify-center space-x-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg transition-all shadow-md font-medium text-sm">
            <UserPlus size={18} /> <span>Nuevo Cliente</span>
          </button>
        </div>
      </div>

      {/* Buscador y Orden */}
      <div className="flex flex-col md:flex-row gap-3">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
          <input 
            type="text" 
            placeholder="Buscar por nombre, apellido o email..."
            className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition-all shadow-sm text-sm"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <button
          onClick={() => setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc')}
          className="flex items-center justify-center gap-2 px-6 py-3 bg-white border border-gray-200 rounded-xl text-gray-600 hover:bg-gray-50 transition-colors shadow-sm text-sm font-medium"
        >
          <ArrowUpDown size={18} className="text-blue-500" />
          <span>{sortOrder === 'asc' ? 'A-Z' : 'Z-A'}</span>
        </button>
      </div>

      {/* Tabla Profesional */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse min-w-150">
  <thead className="bg-gray-50 border-b border-gray-100">
    <tr>
      <th className="p-4 font-semibold text-gray-600 text-sm uppercase">Cliente</th>
      <th className="hidden md:table-cell p-4 font-semibold text-gray-600 text-sm uppercase">Email</th>
      <th className="hidden sm:table-cell p-4 font-semibold text-gray-600 text-sm uppercase">Teléfono</th>
      {/* NUEVA COLUMNA */}
      <th className="p-4 font-semibold text-gray-600 text-sm uppercase">Etiqueta</th>
      <th className="p-4 font-semibold text-gray-600 text-sm text-right uppercase">Acciones</th>
    </tr>
  </thead>
  <tbody className="divide-y divide-gray-50">
    {loading ? (
      <tr><td colSpan="5" className="p-10 text-center text-gray-400 italic">Cargando...</td></tr>
    ) : clientesProcesados.map((cliente) => (
      <tr key={cliente.id} className="hover:bg-gray-50 transition-colors">
        <td className="p-4">
          <div className="flex flex-col">
            <span className="text-gray-700 font-bold text-sm">
              {cliente.apellido?.toUpperCase()}, {cliente.nombre}
            </span>
            <span className="text-gray-400 text-[11px] md:hidden">{cliente.email}</span>
          </div>
        </td>
        <td className="hidden md:table-cell p-4 text-gray-600 text-sm">{cliente.email}</td>
        <td className="hidden sm:table-cell p-4 text-gray-600 text-sm">{cliente.telefono || '-'}</td>
        
        {/* RENDERIZADO DE LA ETIQUETA */}
        <td className="p-4">
          <span className={`px-2 py-1 rounded-full text-[10px] font-black border uppercase tracking-wider ${getBadgeColor(cliente.etiqueta)}`}>
            {cliente.etiqueta || 'S/E'}
          </span>
        </td>

        <td className="p-4 text-right">
          <div className="flex justify-end space-x-1">
            <button onClick={() => handleEditClick(cliente)} className="text-blue-500 hover:bg-blue-50 p-2 rounded-lg transition-colors">
              <Edit size={18} />
            </button>
            <button onClick={() => handleDeleteClick(cliente.id)} className="text-red-500 hover:bg-red-50 p-2 rounded-lg transition-colors">
              <Trash2 size={18} />
            </button>
          </div>
        </td>
      </tr>
    ))}
  </tbody>
</table>
        </div>
      </div>

      <ClienteForm isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setClienteAEditar(null); }} onRefresh={cargarClientes} datosEdicion={clienteAEditar} />
      <ConfirmModal isOpen={isDeleteModalOpen} onClose={() => setIsDeleteModalOpen(false)} onConfirm={confirmarEliminacion} title="¿Eliminar cliente?" message="Esta acción no se puede deshacer." />
    </div>
  );
};

export default Clientes;