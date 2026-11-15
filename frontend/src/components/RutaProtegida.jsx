import { Navigate } from 'react-router-dom';
import PropTypes from 'prop-types';
import { useAuth } from '../context/AuthContext';

/**
 * RutaProtegida
 * Muestra la pantalla solo si hay un usuario con sesión iniciada; si no, lo envía al login.
 */
function RutaProtegida({ children }) {
  const { usuario } = useAuth();
  return usuario ? children : <Navigate to="/login" replace />;
}

RutaProtegida.propTypes = { children: PropTypes.node.isRequired };

export default RutaProtegida;
