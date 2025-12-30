import api from './axiosConfig';

export const getVentas = async () => {
    const response = await api.get('/ventas');
    return response.data;
};

export const createVenta = async (ventaData) => {
    const response = await api.post('/ventas', ventaData);
    return response.data;
};

// --- NUEVAS FUNCIONES ---
export const updateVenta = async (id, ventaData) => {
    const response = await api.put(`/ventas/${id}`, ventaData);
    return response.data;
};

export const deleteVenta = async (id) => {
    const response = await api.delete(`/ventas/${id}`);
    return response.data;
};