import api from './axiosConfig';


const API_URL = 'http://localhost:8080/api/v1/emprendedores';

export const registrarEmprendedor = async (datos, fotoFile) => {
    const formData = new FormData();

    // El backend espera el objeto "dto" como un Blob JSON
    const blob = new Blob([JSON.stringify(datos)], {
        type: 'application/json'
    });
    
    formData.append('dto', blob);

    // El backend espera el archivo con nombre "fotoPerfil"
    if (fotoFile) {
        formData.append('fotoPerfil', fotoFile);
    }

    const response = await api.post(API_URL, formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });

    return response.data;
};