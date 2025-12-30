import axios from 'axios';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    // Eliminamos el Content-Type fijo de aquí para permitir MultipartFormData en otros métodos
});

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    (error) => {
        // 401: Token expirado o inválido
        // 403: A veces Spring devuelve 403 en lugar de 401 si el token falla
        if (error.response && (error.response.status === 401)) {
            localStorage.removeItem('token');
            // Solo redireccionamos si no estamos ya en el login para evitar bucles
            if (!window.location.pathname.includes('/login')) {
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);

export default api;