import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { UserPlus, Camera, Loader2 } from 'lucide-react';
import { registrarEmprendedor } from '../api/emprendedorService';

const Registro = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [fotoPreview, setFotoPreview] = useState(null);
    const [fotoFile, setFotoFile] = useState(null);
    const [error, setError] = useState('');
    
    const [formData, setFormData] = useState({
        nombre: '', apellido: '', email: '', password: '',
        telefono: '', rubro: '', pais: '', descripcion: ''
    });

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setFotoFile(file);
            setFotoPreview(URL.createObjectURL(file));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            await registrarEmprendedor(formData, fotoFile);
            // Redirigimos al login pasando un estado para mostrar el mensaje de éxito
            navigate('/login', { 
                state: { message: '¡Cuenta creada con éxito! Por favor, inicia sesión.' } 
            });
        } catch (err) {
            setError(err.response?.data?.message || 'Error al registrar el emprendedor');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100 py-12 px-4">
            <div className="max-w-2xl w-full bg-white rounded-2xl shadow-xl p-8">
                <div className="text-center mb-8">
                    <div className="inline-block bg-indigo-600 p-3 rounded-full mb-4">
                        <UserPlus className="text-white w-8 h-8" />
                    </div>
                    <h2 className="text-3xl font-bold text-gray-800">Únete como Emprendedor</h2>
                    <p className="text-gray-500">Crea tu perfil y comienza a gestionar tu negocio</p>
                </div>

                <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {error && (
                        <div className="md:col-span-2 bg-red-50 text-red-600 p-3 rounded-lg border border-red-100 text-sm">
                            {error}
                        </div>
                    )}
                    
                    {/* Foto de Perfil con Preview */}
                    <div className="md:col-span-2 flex flex-col items-center mb-4">
                        <div className="relative w-32 h-32">
                            <div className="w-full h-full rounded-full bg-gray-200 overflow-hidden border-4 border-white shadow-md">
                                {fotoPreview ? (
                                    <img src={fotoPreview} alt="Preview" className="w-full h-full object-cover" />
                                ) : (
                                    <div className="flex items-center justify-center h-full text-gray-400 text-xs text-center px-2">
                                        Subir Foto
                                    </div>
                                )}
                            </div>
                            <label className="absolute bottom-0 right-0 bg-indigo-600 p-2 rounded-full text-white cursor-pointer hover:bg-indigo-700 transition-colors shadow-lg">
                                <Camera size={20} />
                                <input type="file" className="hidden" onChange={handleFileChange} accept="image/*" />
                            </label>
                        </div>
                    </div>

                    <div className="space-y-1">
                        <label className="text-sm font-medium text-gray-700">Nombre</label>
                        <input type="text" required className="w-full p-3 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none" 
                            onChange={e => setFormData({...formData, nombre: e.target.value})} />
                    </div>

                    <div className="space-y-1">
                        <label className="text-sm font-medium text-gray-700">Apellido</label>
                        <input type="text" required className="w-full p-3 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none" 
                            onChange={e => setFormData({...formData, apellido: e.target.value})} />
                    </div>

                    <div className="space-y-1">
                        <label className="text-sm font-medium text-gray-700">Email Académico/Profesional</label>
                        <input type="email" required className="w-full p-3 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none" 
                            onChange={e => setFormData({...formData, email: e.target.value})} />
                    </div>

                    <div className="space-y-1">
                        <label className="text-sm font-medium text-gray-700">Contraseña</label>
                        <input type="password" required className="w-full p-3 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none" 
                            onChange={e => setFormData({...formData, password: e.target.value})} />
                    </div>

                    <div className="space-y-1">
                        <label className="text-sm font-medium text-gray-700">Rubro del Negocio</label>
                        <input type="text" placeholder="Ej: Gastronomía, Tech..." className="w-full p-3 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none" 
                            onChange={e => setFormData({...formData, rubro: e.target.value})} />
                    </div>

                    <div className="space-y-1">
                        <label className="text-sm font-medium text-gray-700">País</label>
                        <input type="text" className="w-full p-3 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none" 
                            onChange={e => setFormData({...formData, pais: e.target.value})} />
                    </div>

                    <div className="md:col-span-2 space-y-1">
                        <label className="text-sm font-medium text-gray-700">Descripción Breve</label>
                        <textarea className="w-full p-3 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none h-24" 
                            onChange={e => setFormData({...formData, descripcion: e.target.value})} />
                    </div>

                    <button 
                        type="submit" 
                        disabled={loading}
                        className="md:col-span-2 bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-3 rounded-lg transition-all flex items-center justify-center gap-2 disabled:opacity-70"
                    >
                        {loading ? <Loader2 className="animate-spin" /> : 'Crear Cuenta'}
                    </button>

                    <p className="md:col-span-2 text-center text-sm text-gray-500">
                        ¿Ya eres parte? <Link to="/login" className="text-indigo-600 font-bold hover:underline">Inicia sesión</Link>
                    </p>
                </form>
            </div>
        </div>
    );
};

export default Registro;