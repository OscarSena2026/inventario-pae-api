import { useState } from 'react';
import PropTypes from 'prop-types';
import SelectCatalogo from './SelectCatalogo';

/**
 * FormularioDetalleSalida (HU-04, paso 2)
 * Agrega a la lista de la salida un lote con la cantidad a despachar.
 * No habla con la API: los productos se acumulan en la página y viajan juntos al registrar la salida.
 * El stock disponible NO se valida aquí: lo verifica la API al registrar la salida (regla de negocio)
 * y, si no alcanza, responde 409 «Stock insuficiente...». La página muestra ese mensaje y conserva la lista.
 *
 * @param {Array}    lotes     Opciones { valor, texto } de los lotes con existencia en la bodega.
 * @param {Function} alAgregar Función que recibe { idLote, cantidad }.
 */
function FormularioDetalleSalida({ lotes, alAgregar }) {
  // Estado: valores de los campos y errores de validación.
  const [valores, setValores] = useState({ idLote: '', cantidad: '' });
  const [errores, setErrores] = useState({});

  // Evento onChange: copia lo que el usuario escribe o elige al campo que se llama igual (name) en el estado.
  const manejarCambio = (evento) => {
    const { name, value } = evento.target;
    setValores({ ...valores, [name]: value });
  };

  // Validación del lado del cliente: hay que elegir un lote y la cantidad debe ser mayor que 0.
  const validar = () => {
    const nuevos = {};
    if (!valores.idLote) nuevos.idLote = 'Selecciona un lote';
    if (!(Number(valores.cantidad) > 0)) nuevos.cantidad = 'La cantidad debe ser mayor a 0';
    setErrores(nuevos);
    return Object.keys(nuevos).length === 0;
  };

  // Evento onSubmit: valida, entrega el producto a la página y limpia los campos para agregar otro.
  const manejarEnvio = (evento) => {
    evento.preventDefault();
    if (!validar()) return;
    alAgregar({ idLote: Number(valores.idLote), cantidad: Number(valores.cantidad) });
    setValores({ idLote: '', cantidad: '' });
  };

  return (
    <form onSubmit={manejarEnvio} noValidate className="card card-body mb-4">
      <h5 className="card-title">2. Productos de la salida</h5>
      {lotes.length === 0 && (
        <p className="text-muted">La bodega elegida no tiene existencias disponibles para despachar.</p>
      )}
      <div className="row">
        <div className="col-md-8">
          <SelectCatalogo nombre="idLote" etiqueta="Lote disponible" opciones={lotes} valor={valores.idLote}
            alCambiar={manejarCambio} error={errores.idLote} vacio="Selecciona..." />
        </div>
        <div className="col-md-4 mb-3">
          <label htmlFor="cantidad" className="form-label fw-semibold">Cantidad a despachar</label>
          <input id="cantidad" name="cantidad" type="number" min="0" step="0.01"
            className={`form-control ${errores.cantidad ? 'is-invalid' : ''}`} value={valores.cantidad} onChange={manejarCambio} />
          {errores.cantidad && <div className="invalid-feedback">{errores.cantidad}</div>}
        </div>
      </div>
      <div>
        <button type="submit" className="btn btn-outline-success">Agregar a la lista</button>
      </div>
    </form>
  );
}

FormularioDetalleSalida.propTypes = {
  lotes: PropTypes.arrayOf(PropTypes.object).isRequired,
  alAgregar: PropTypes.func.isRequired,
};

export default FormularioDetalleSalida;
