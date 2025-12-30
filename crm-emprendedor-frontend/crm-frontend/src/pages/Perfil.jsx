import { useState, useEffect } from 'react';
import { getProfile, updatePassword } from '../api/userService';
import { User, Mail, ShieldCheck, Key, Save, AlertCircle, Briefcase, CheckCircle2 } from 'lucide-react';

const Perfil = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [passData, setPassData] = useState({ current: '', new: '', confirm: '' });
  const [msg, setMsg] = useState({ type: '', text: '' });

  useEffect(() => {
    getProfile()
      .then(data => {
        setUser(data);
      })
      .catch((err) => {
        console.error(err);
        setMsg({ type: 'error', text: 'No se pudo cargar la información del servidor.' });
      })
      .finally(() => setLoading(false));
  }, []);

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    setMsg({ type: '', text: '' });

    if (passData.new !== passData.confirm) {
      setMsg({ type: 'error', text: 'Las nuevas contraseñas no coinciden.' });
      return;
    }

    try {
      await updatePassword({ 
        oldPassword: passData.current, 
        newPassword: passData.new 
      });
      setMsg({ type: 'success', text: 'Contraseña actualizada correctamente.' });
      setPassData({ current: '', new: '', confirm: '' });
    } catch (error) {
      setMsg({ type: 'error', text: error.response?.data?.message || 'Error al cambiar la contraseña.' });
    }
  };

  if (loading) return <div className="p-10 text-center text-gray-500 animate-pulse">Cargando perfil...</div>;

  return (
    <div className="max-w-5xl mx-auto space-y-6 p-4 animate-in fade-in duration-500">
      <header>
        <h1 className="text-2xl font-bold text-gray-800">Perfil de Usuario</h1>
        <p className="text-gray-500 text-sm">Configuración de cuenta y negocio</p>
      </header>

      {msg.text && (
        <div className={`p-4 rounded-xl flex items-center gap-3 animate-in slide-in-from-top ${
          msg.type === 'success' ? 'bg-green-50 text-green-700 border border-green-100' : 'bg-red-50 text-red-700 border border-red-100'
        }`}>
          {msg.type === 'success' ? <CheckCircle2 size={18} /> : <AlertCircle size={18} />}
          <span className="text-sm font-medium">{msg.text}</span>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Lado Izquierdo: Card del Emprendedor */}
        <div className="lg:col-span-1">
          <div className="bg-white p-6 rounded-3xl shadow-sm border border-indigo-100 bg-linear-to-b from-indigo-50/30 to-white sticky top-6">
            <div className="w-16 h-16 bg-indigo-600 text-white rounded-2xl flex items-center justify-center shadow-lg transform -rotate-3 mb-4">
              <Briefcase size={32} />
            </div>
            <h2 className="text-xl font-black text-gray-800">
              {user?.emprendedor?.nombre} {user?.emprendedor?.apellido}
            </h2>
            <p className="text-sm text-indigo-600 font-bold mt-1 uppercase tracking-wider">
              {user?.emprendedor?.rubro || 'Sin Rubro'}
            </p>
            
            <div className="mt-6 pt-6 border-t border-indigo-100 space-y-4">
              <div>
                <p className="text-[10px] text-gray-400 uppercase font-black tracking-widest">Rol del Sistema</p>
                <div className="flex items-center gap-2 mt-1">
                  <ShieldCheck size={14} className="text-green-500" />
                  <span className="text-sm font-bold text-gray-700">{user?.rol}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Lado Derecho: Formularios */}
        <div className="lg:col-span-2 space-y-6">
          <section className="bg-white p-6 rounded-3xl shadow-sm border border-gray-100">
            <h3 className="font-bold text-gray-800 mb-6 flex items-center gap-2">
              <User size={18} className="text-blue-500" /> Información de Acceso
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-1">
                <label className="text-[10px] text-gray-400 uppercase font-black ml-1">Usuario</label>
                <div className="p-3 bg-gray-50 rounded-xl border border-gray-100 text-sm font-semibold text-gray-600">
                  {user?.nombre}
                </div>
              </div>
              <div className="space-y-1">
                <label className="text-[10px] text-gray-400 uppercase font-black ml-1">Email</label>
                <div className="p-3 bg-gray-50 rounded-xl border border-gray-100 text-sm font-semibold text-gray-600">
                  {user?.email}
                </div>
              </div>
            </div>
          </section>

          <section className="bg-white p-6 rounded-3xl shadow-sm border border-gray-100">
            <h3 className="font-bold text-gray-800 mb-6 flex items-center gap-2">
              <Key size={18} className="text-amber-500" /> Cambiar Contraseña
            </h3>
            <form onSubmit={handlePasswordChange} className="space-y-4">
              <input 
                type="password" 
                placeholder="Contraseña Actual"
                className="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 transition-all text-sm"
                value={passData.current}
                onChange={(e) => setPassData({...passData, current: e.target.value})}
                required
              />
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <input 
                  type="password" 
                  placeholder="Nueva Contraseña"
                  className="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 transition-all text-sm"
                  value={passData.new}
                  onChange={(e) => setPassData({...passData, new: e.target.value})}
                  required
                />
                <input 
                  type="password" 
                  placeholder="Confirmar Nueva"
                  className="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 transition-all text-sm"
                  value={passData.confirm}
                  onChange={(e) => setPassData({...passData, confirm: e.target.value})}
                  required
                />
              </div>
              <button 
                type="submit"
                className="flex items-center justify-center gap-2 bg-gray-800 text-white px-6 py-3 rounded-xl font-bold text-sm hover:bg-black transition-all shadow-lg active:scale-95"
              >
                <Save size={16} /> Guardar Cambios
              </button>
            </form>
          </section>
        </div>
      </div>
    </div>
  );
};

export default Perfil;