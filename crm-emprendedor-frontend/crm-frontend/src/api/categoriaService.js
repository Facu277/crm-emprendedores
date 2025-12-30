import axios from './axiosConfig'; // Asegúrate de tener configurado axios con el interceptor del token

const API_URL = '/categorias-contenido';

export const getCategorias = async () => {
  const response = await axios.get(API_URL);
  return response.data;
};

export const createCategoria = async (nombre) => {
  const response = await axios.post(API_URL, { nombre });
  return response.data;
};

export const updateCategoria = async (id, nombre) => {
  const response = await axios.put(`${API_URL}/${id}`, { nombre });
  return response.data;
};

export const deleteCategoria = async (id) => {
  await axios.delete(`${API_URL}/${id}`);
};