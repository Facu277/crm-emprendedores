import axios from 'axios';

// La URL base de tu Backend de Spring Boot
const API_URL = import.meta.env.VITE_API_URL;

export const login = async (username, password) => {
    try {
        const response = await axios.post(`${API_URL}/auth/authenticate`, {
            username,
            password
        });
        
        // Si el login es exitoso, Spring devuelve un objeto con access_token
        return response.data;
    } catch (error) {
        // Capturamos el error para que el componente Login pueda mostrarlo
        if (error.response) {
            // El servidor respondió con un error (403, 401, etc)
            throw error.response.data;
        } else {
            // El servidor ni siquiera respondió (está apagado o no hay internet)
            throw new Error('No se pudo conectar con el servidor');
        }
    }
};

export const logout = () => {
    localStorage.removeItem('token');
};