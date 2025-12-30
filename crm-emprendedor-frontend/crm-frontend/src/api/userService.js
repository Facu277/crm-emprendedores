import api from './axiosConfig'; // Asegúrate de tener este archivo configurado

export const getProfile = async () => {
    // La URL debe coincidir con tu Backend
    const response = await api.get('/auth/me');
    return response.data;
};

export const updatePassword = async (passwords) => {
    // passwords debería ser un objeto { oldPassword, newPassword }
    const response = await api.post('/auth/change-password', passwords);
    return response.data;
};