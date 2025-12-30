import { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Al cargar la app, recuperamos token y datos del usuario
        const token = localStorage.getItem('token');
        const storedUser = localStorage.getItem('userData');
        
        // CORRECCIÓN: Validamos que storedUser exista y no sea el texto "undefined"
        if (token && storedUser && storedUser !== "undefined") {
            try {
                setUser(JSON.parse(storedUser)); 
            } catch (error) {
                console.error("Error al parsear userData del localStorage:", error);
                // Si está corrupto, limpiamos para evitar que la app se bloquee
                localStorage.removeItem('userData');
                localStorage.removeItem('token');
            }
        }
        setLoading(false);
    }, []);

    const login = (token, userData) => {
        // SEGURIDAD: Solo guardamos si userData es un objeto válido
        if (!userData) {
            console.error("Error: Intentando hacer login con datos de usuario nulos o undefined");
            return;
        }

        localStorage.setItem('token', token);
        localStorage.setItem('userData', JSON.stringify(userData)); 
        setUser(userData);
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('userData');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);