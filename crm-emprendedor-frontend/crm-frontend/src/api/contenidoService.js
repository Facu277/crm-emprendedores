import api from './axiosConfig';

export const getContenidos = async () => {
  const response = await api.get('/contenidos');
  return response.data;
};

export const createContenido = async (formData) => {
  const response = await api.post('/contenidos', formData, {
    headers: {
      // IMPORTANTE: NO definas 'Content-Type': 'application/json' aquí
      // Deja que el navegador ponga el Boundary automáticamente
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;
};

export const updateContenido = async (id, formDataPayload) => {
  const response = await api.put(`/contenidos/${id}`, formDataPayload);
  return response.data;
};

export const deleteContenido = async (id) => {
  await api.delete(`/contenidos/${id}`);
};

// Necesitaremos las categorías para el select del formulario
export const getCategoriasContenido = async () => {
  const response = await api.get('/categorias-contenido');
  return response.data;
};