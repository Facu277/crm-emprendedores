import api from './axiosConfig'; // El que tiene el interceptor del token

export const getClientes = async () => {
    try {
        const response = await api.get('/clientes');
        return response.data;
    } catch (error) {
        throw error.response?.data || "Error al obtener clientes";
    }
};

export const deleteCliente = async (id) => {
    await api.delete(`/clientes/${id}`);
};