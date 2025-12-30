import { useEffect } from 'react';

export const useTitle = (title) => {
  useEffect(() => {
    document.title = `Emprende - ${title}`;
    
    // Opcional: Al salir de la pantalla, vuelve al título original
    return () => document.title = 'Emprende';
  }, [title]);
};