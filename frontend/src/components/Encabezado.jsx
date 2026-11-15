import { NavLink } from 'react-router-dom';
import PropTypes from 'prop-types';

/**
 * Encabezado
 * Muestra el nombre del sistema, el menú para pasar entre pantallas y, si hay sesión, el usuario
 * con el botón «Cerrar sesión». Aparece en todas las páginas (se escribe una sola vez).
 *
 * @param {Array}    enlaces    Lista de { ruta, texto } del menú (vacía si no hay sesión).
 * @param {string}   [usuario]  Nombre del usuario con sesión iniciada.
 * @param {Function} [alSalir]  Se ejecuta al pulsar «Cerrar sesión».
 */
function Encabezado({ enlaces, usuario = null, alSalir = undefined }) {
  return (
    <nav className="navbar navbar-expand-lg navbar-dark barra-pae mb-4">
      <div className="container">
        <span className="navbar-brand fw-bold">Inventario PAE</span>
        <ul className="navbar-nav me-auto flex-wrap">
          {enlaces.map((enlace) => (
            <li className="nav-item" key={enlace.ruta}>
              {/* NavLink cambia de pantalla al hacer clic (onClick interno) sin recargar la página */}
              <NavLink
                to={enlace.ruta}
                className={({ isActive }) => `nav-link${isActive ? ' active fw-bold' : ''}`}
              >
                {enlace.texto}
              </NavLink>
            </li>
          ))}
        </ul>
        {usuario && (
          <div className="d-flex align-items-center gap-2 text-white">
            <span className="small">Usuario: <strong>{usuario}</strong></span>
            <button type="button" className="btn btn-sm btn-outline-light" onClick={alSalir}>Cerrar sesión</button>
          </div>
        )}
      </div>
    </nav>
  );
}

Encabezado.propTypes = {
  enlaces: PropTypes.arrayOf(
    PropTypes.shape({ ruta: PropTypes.string.isRequired, texto: PropTypes.string.isRequired })
  ).isRequired,
  usuario: PropTypes.string,
  alSalir: PropTypes.func,
};

export default Encabezado;
