import { useState } from 'react';
import PropTypes from 'prop-types';
import SelectCatalogo from './SelectCatalogo';

/**
 * FormularioDetalleEntrada (HU-03, paso 2)
 * Agrega a la lista de la entrada un lote con su cantidad y valor unitario.
 * No habla con la API: los productos se acumulan en la página y viajan juntos al registrar la entrada.
 * Valida que cantidad y valor unitario sean válidos, igual que el back-end (cantidad > 0, valor >= 0).
 *
 * @param {Array}    lotes     Opciones { valor, texto } de lotes (cada lote ya incluye su producto).
 * @param {Function} alAgregar Función que recibe { idLote, cantidad, valorUnitario }.
 */
function FormularioDetalleEntrada({ lotes, alAgregar }) {
  // Estado: valores de los campos y errores de validación.
  const [valores, setValores] = useState({ idLote: '', cantidad: '', valorUnitario: '' });
  const [errores, setErrores] = useState({});

  // Evento onChange: copia lo que el usuario escribe o elige al campo que se llama igual (name) en el estado.
  const manejarCambio = (evento) => {
    const { name, value } = evento.target;
    setValores({ ...valores, [name]: value });
  };

  // Validaciones del lado del cliente (la API vuelve a validar al registrar la entrada).
  const validar = () => {
    const nuevos = {};
    if (!valores.idLote) nuevos.idLote = 'Selecciona un lote';
    if (!(Number(valores.cantidad) > 0)) nuevos.cantidad = 'La cantidad debe ser mayor a 0';
    if (valores.valorUnitario === '' || Number(valores.valorUnitario) < 0) nuevos.valorUnitario = 'El valor unitario no puede ser negativo';
    setErrores(nuevos);
    return Object.keys(nuevos).length === 0;
  };

  // Evento onSubmit: valida, entrega el producto a la página y limpia los campos para agregar otro.
  const manejarEnvio = (evento) => {
    evento.preventDefault();
    if (!validar()) return;
    // Los campos del formulario son texto: se convierten a número antes de entregarlos.
    alAgregar({
      idLote: Number(valores.idLote),
      cantidad: Number(valores.cantidad),
      valorUnitario: Number(valores.valorUnitario),
    });
    setValores({ idLote: '', cantidad: '', valorUnitario: '' });
  };

  return (
    <form onSubmit={manejarEnvio} noValidate className="card card-body mb-4">
      <h5 className="card-title">2. Productos de la entrada</h5>
      <div className="row">
        <div className="col-md-6">
          <SelectCatalogo nombre="idLote" etiqueta="Lote (producto)" opciones={lotes} valor={valores.idLote}
            alCambiar={manejarCambio} error={errores.idLote} vacio="Selecciona..." />
        </div>
        <div className="col-md-3 mb-3">
          <label htmlFor="cantidad" className="form-label fw-semibold">Cantidad</label>
          <input id="cantidad" name="cantidad" type="number" min="0" step="0.01"
            className={`form-control ${errores.cantidad ? 'is-invalid' : ''}`} value={valores.cantidad} onChange={manejarCambio} />
          {errores.cantidad && <div className="invalid-feedback">{errores.cantidad}</div>}
        </div>
        <div className="col-md-3 mb-3">
          <label htmlFor="valorUnitario" className="form-label fw-semibold">Valor unitario ($)</label>
          <input id="valorUnitario" name="valorUnitario" type="number" min="0" step="0.01"
            className={`form-control ${errores.valorUnitario ? 'is-invalid' : ''}`} value={valores.valorUnitario} onChange={manejarCambio} />
          {errores.valorUnitario && <div className="invalid-feedback">{errores.valorUnitario}</div>}
        </div>
      </div>
      <div>
        <button type="submit" className="btn btn-outline-success">Agregar a la lista</button>
      </div>
    </form>
  );
}

FormularioDetalleEntrada.propTypes = {
  lotes: PropTypes.arrayOf(PropTypes.object).isRequired,
  alAgregar: PropTypes.func.isRequired,
};

export default FormularioDetalleEntrada;
