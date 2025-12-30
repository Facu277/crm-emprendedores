import { useState, useEffect } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom'; // Añadido useNavigate
import { login as loginApi } from '../api/authService';
import { useAuth } from '../context/AuthContext';
import { LogIn, Loader2 } from 'lucide-react';
import { getProfile } from '../api/userService';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        if (location.state?.message) {
            setSuccessMessage(location.state.message);
        }
    }, [location]);

    const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); // Activa el spinner
    setError('');
    
    try {
        // 1. Autenticar
        const authResponse = await loginApi(username, password); 
        
        // 2. Guardar token para que getProfile tenga permisos
        localStorage.setItem('token', authResponse.access_token);
        
        // 3. Obtener el perfil real
        const profileData = await getProfile(); 
        
        // 4. Actualizar el contexto con los datos reales del perfil
        // Pasamos authResponse.access_token y profileData (que tiene nombre, foto, rol)
        login(authResponse.access_token, profileData);
        
        navigate('/dashboard');
    } catch (err) {
        setError('Credenciales incorrectas o error al cargar el perfil');
        localStorage.removeItem('token'); // Limpiamos por seguridad si falla el perfil
        console.error(err);
    } finally {
        setLoading(false); // Desactiva el spinner
    }
};

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100 px-4">
            <div className="max-w-md w-full bg-white rounded-2xl shadow-xl p-8">
                <div className="flex flex-col items-center mb-8">
                    <div className="bg-blue-600 p-3 rounded-full mb-4">
                        <LogIn className="text-white w-8 h-8" />
                    </div>
                    <h2 className="text-2xl font-bold text-gray-800">CRM Emprendedores</h2>
                    <p className="text-gray-500 text-sm">Ingresa a tu panel de control</p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-6">
                    {successMessage && (
                        <div className="bg-green-50 text-green-600 p-3 rounded-lg text-sm border border-green-100 font-medium">
                            {successMessage}
                        </div>
                    )}

                    {error && (
                        <div className="bg-red-50 text-red-500 p-3 rounded-lg text-sm border border-red-100 font-medium">
                            {error}
                        </div>
                    )}

                    <div>
                        <label className="block text-sm font-medium text-gray-700">Email (Usuario)</label>
                        <input
                            type="email"
                            className="mt-1 block w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Contraseña</label>
                        <input
                            type="password"
                            className="mt-1 block w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 rounded-lg transition-all shadow-lg active:scale-95 flex items-center justify-center gap-2 cursor-pointer disabled:opacity-70"
                    >
                        {loading ? <Loader2 className="animate-spin" /> : 'Iniciar Sesión'}
                    </button>
                    
                    <div className="text-center mt-4">
                        <p className="text-sm text-gray-500">
                            ¿Aún no tienes cuenta?{' '}
                            <Link to="/registro" className="text-blue-600 font-bold hover:underline cursor-pointer">
                                Regístrate aquí
                            </Link>
                        </p>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Login;