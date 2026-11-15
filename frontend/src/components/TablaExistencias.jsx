import PropTypes from 'prop-types';
import { formatearCantidad } from '../utils/formato';

/**
 * TablaExistencias (HU-05)
 * Muestra bodega, producto, lote, vencimiento y cantidad disponible de cada existencia.
 * Solo muestra datos: no tiene estado propio.
 *
 * @param {Array} existencias Lista de existencias que entrega la API.
 */
function TablaExistencias({ existencias }) {
  if (existencias.length === 0) {
    return <p className="text-muted">No hay existencias para mostrar.</p>;
  }
  return (
    <div className="table-responsive">
      <table className="table table-striped table-hover align-middle">
        <thead className="table-success">
          <tr>
            <th>Bodega</th>
            <th>Producto</th>
            <th>Lote</th>
            <th>Vence</th>
            <th className="text-end">Disponible</th>
          </tr>
        </thead>
        <tbody>
          {/* Una fila por existencia: la «key» (idExistencia) ayuda a React a identificar cada fila */}
          {existencias.map((existencia) => (
            <tr key={existencia.idExistencia}>
              <td>{existencia.nombreBodega}</td>
              <td>{existencia.nombreProducto}</td>
              <td>#{existencia.idLote}</td>
              <td>{existencia.fechaVencimiento}</td>
              <td className="text-end">
                {/* Una cantidad en 0 se resalta para que el encargado la identifique rápido */}
                <span className={Number(existencia.cantidadDisponible) === 0 ? 'badge text-bg-danger' : ''}>
                  {formatearCantidad(existencia.cantidadDisponible)}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

TablaExistencias.propTypes = {
  existencias: PropTypes.arrayOf(PropTypes.object).isRequired,
};

export default TablaExistencias;
