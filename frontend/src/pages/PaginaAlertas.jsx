import { useEffect, useState } from 'react';
import Mensaje from '../components/Mensaje';
import { listarAlertasStockMinimo, listarAlertasVencimiento } from '../services/api';
import { obtenerMensajeError } from '../utils/mensajes';
import { formatearCantidad } from '../utils/formato';

/**
 * PaginaAlertas (ruta /alertas) - HU-06 alertas de stock mínimo y de lotes por vencer.
 * Pide a la API los productos en nivel crítico y los lotes con stock que vencen en los próximos N días.
 */
function PaginaAlertas() {
  // Estado: alertas de cada tipo, días elegidos para el vencimiento, aviso y bandera de carga.
  const [stock, setStock] = useState([]);
  const [vencimiento, setVencimiento] = useState([]);
  const [dias, setDias] = useState('30');
  const [errorDias, setErrorDias] = useState('');
  const [mensaje, setMensaje] = useState({ tipo: 'exito', texto: '' });
  const [cargando, setCargando] = useState(true);

  // Pide las dos alertas a la API (GET /api/alertas/stock-minimo y GET /api/alertas/vencimiento?dias=).
  const cargar = async (diasConsulta) => {
    setCargando(true);
    try {
      const [listaStock, listaVencimiento] = await Promise.all([
        listarAlertasStockMinimo(), listarAlertasVencimiento(diasConsulta),
      ]);
      setStock(listaStock);
      setVencimiento(listaVencimiento);
      setMensaje({ tipo: 'exito', texto: '' });
    } catch (error) {
      setMensaje({ tipo: 'error', texto: obtenerMensajeError(error) });
    } finally {
      setCargando(false);
    }
  };

  // useEffect: carga las alertas al abrir la página (30 días por defecto, igual que la API).
  useEffect(() => {
    cargar(30);
  }, []);

  // Evento onSubmit del filtro de días: valida que sea un entero mayor que 0 (igual que la API).
  const consultarVencimiento = (evento) => {
    evento.preventDefault();
    const valor = Number(dias);
    if (!Number.isInteger(valor) || valor <= 0) {
      setErrorDias('Los días deben ser un número entero mayor a 0');
      return;
    }
    setErrorDias('');
    cargar(valor);
  };

  return (
    <section>
      <h2 className="h3 mb-3">Alertas</h2>
      <Mensaje tipo={mensaje.tipo} texto={mensaje.texto} />
      {cargando && <p>Cargando alertas...</p>}

      <h3 className="h5 mt-4">Productos bajo el stock mínimo</h3>
      {stock.length === 0 ? (
        <p className="text-muted">No hay productos en nivel crítico.</p>
      ) : (
        <div className="table-responsive">
          <table className="table table-striped align-middle">
            <thead className="table-warning">
              <tr><th>Producto</th><th className="text-end">Stock actual</th><th className="text-end">Stock mínimo</th><th>Aviso</th></tr>
            </thead>
            <tbody>
              {stock.map((a) => (
                <tr key={a.idProducto}>
                  <td>{a.nombreProducto}</td>
                  <td className="text-end">{formatearCantidad(a.stockActual)}</td>
                  <td className="text-end">{formatearCantidad(a.stockMinimo)}</td>
                  <td>{a.mensaje}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <h3 className="h5 mt-4">Lotes por vencer</h3>
      <form onSubmit={consultarVencimiento} noValidate className="row g-2 align-items-start mb-3">
        <div className="col-auto">
          <label htmlFor="dias" className="form-label fw-semibold">Vencen en los próximos (días)</label>
          <input id="dias" type="number" min="1" className={`form-control ${errorDias ? 'is-invalid' : ''}`}
            value={dias} onChange={(e) => setDias(e.target.value)} />
          {errorDias && <div className="invalid-feedback">{errorDias}</div>}
        </div>
        <div className="col-auto align-self-end" style={{ paddingBottom: errorDias ? '1.5rem' : 0 }}>
          <button type="submit" className="btn btn-success">Consultar</button>
        </div>
      </form>
      {vencimiento.length === 0 ? (
        <p className="text-muted">No hay lotes con stock por vencer en ese plazo.</p>
      ) : (
        <div className="table-responsive">
          <table className="table table-striped align-middle">
            <thead className="table-warning">
              <tr>
                <th>Producto</th><th>Bodega</th><th>Lote</th><th>Vence</th>
                <th className="text-end">Días</th><th className="text-end">Disponible</th>
              </tr>
            </thead>
            <tbody>
              {vencimiento.map((a) => (
                <tr key={`${a.idLote}-${a.idBodega}`}>
                  <td>{a.nombreProducto}</td>
                  <td>{a.nombreBodega}</td>
                  <td>#{a.idLote}</td>
                  <td>{a.fechaVencimiento}</td>
                  <td className="text-end">{a.diasRestantes}</td>
                  <td className="text-end">{formatearCantidad(a.cantidadDisponible)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

export default PaginaAlertas;
