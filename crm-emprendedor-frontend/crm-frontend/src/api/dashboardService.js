import api from './axiosConfig';

export const getStats = async () => {
    try {
        // CORRECCIÓN: La ruta debe ser /dashboard, no /stats/summary
        const response = await api.get('/dashboard'); 
        return response.data;
    } catch (error) {
        console.error("Error en dashboardService:", error);
        // Mock por si el backend está caído, para que la UI no se rompa
        return {
            totalClientes: 0,
            ventasMes: 0,
            ingresos: 0,
            proyectosActivos: 0
        };
    }
};