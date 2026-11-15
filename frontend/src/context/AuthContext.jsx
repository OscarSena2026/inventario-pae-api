import { createContext, useContext, useState } from 'react';
import PropTypes from 'prop-types';

const CLAVE = 'pae_usuario';
const AuthContext = createContext(null);

// Lee el usuario guardado en la sesión del navegador (se pierde al cerrar la pestaña).
function leerUsuario() {
  try {
    return sessionStorage.getItem(CLAVE);
  } catch {
    return null;
  }
}

/**
 * AuthProvider (HU-01)
 * Guarda quién inició sesión. Importante: la API NO entrega token ni sesión, así que esto solo
 * controla qué pantallas se muestran en el navegador; no es seguridad real del back-end.
 */
export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(leerUsuario);

  const entrar = (nombre) => {
    sessionStorage.setItem(CLAVE, nombre);
    setUsuario(nombre);
  };

  const salir = () => {
    sessionStorage.removeItem(CLAVE);
    setUsuario(null);
  };

  return <AuthContext.Provider value={{ usuario, entrar, salir }}>{children}</AuthContext.Provider>;
}

AuthProvider.propTypes = { children: PropTypes.node.isRequired };

// Hook para leer la sesión desde cualquier componente.
export function useAuth() {
  return useContext(AuthContext);
}
