import { useEffect, useState } from 'react';
import FormularioEntrada from '../components/FormularioEntrada';
import FormularioDetalleEntrada from '../components/FormularioDetalleEntrada';
import Mensaje from '../components/Mensaje';
import {
  crearEntrada, listarBodegas, listarEmpleados, listarLotes, listarProveedores,
} from '../services/api';
import { obtenerMensajeError } from '../utils/mensajes';
import { etiquetaLote, formatearCantidad, formatearMoneda } from '../utils/formato';

/**
 * PaginaEntradas (ruta /entradas) - HU-03 registrar entrada de productos.
 * Coordina el registro en dos pasos de pantalla: primero los datos generales y luego la lista de productos.
 * Al final se envía UNA sola petición (POST /api/entradas) con la entrada y sus «detalles»; la API suma
 * el stock de cada lote a la bodega.
 */
function PaginaEntradas() {
  // Estado: catálogos para las listas, datos generales (null = aún no), productos agregados y aviso.
  const [catalogos, setCatalogos] = useState({ bodegas: [], proveedores: [], empleados: [], lotes: [] });
  const [cabecera, setCabecera] = useState(null);
  const [detalles, setDetalles] = useState([]);
  const [mensaje, setMensaje] = useState({ tipo: 'exito', texto: '' });
  const [enviando, setEnviando] = useState(false);

  // useEffect: al abrir la página se piden a la API los datos de las listas desplegables.
  useEffect(() => {
    const cargarCatalogos = async () => {
      try {
        const [bodegas, proveedores, empleados, lotes] = await Promise.all([
          listarBodegas(), listarProveedores(), listarEmpleados(), listarLotes(),
        ]);
        setCatalogos({ bodegas, proveedores, empleados, lotes });
      } catch (error) {
        setMensaje({ tipo: 'error', texto: obtenerMensajeError(error) });
      }
    };
    cargarCatalogos();
  }, []);

  // Paso 1: guarda los datos generales en pantalla (todavía no se envía nada a la API).
  const guardarCabecera = async (datos) => {
    const bodega = catalogos.bodegas.find((b) => b.idBodega === Number(datos.idBodega));
    setCabecera({ ...datos, bodega: bodega ? bodega.nombreBodega : '' });
    setDetalles([]);
    setMensaje({ tipo: 'exito', texto: '' });
  };

  // Paso 2: agrega un producto a la lista; si el lote ya estaba, suma la cantidad.
  const agregarDetalle = (datos) => {
    const lote = catalogos.lotes.find((l) => l.idLote === datos.idLote);
    const existente = detalles.find((d) => d.idLote === datos.idLote && d.valorUnitario === datos.valorUnitario);
    if (existente) {
      setDetalles(detalles.map((d) => (d === existente ? { ...d, cantidad: d.cantidad + datos.cantidad } : d)));
    } else {
      setDetalles([...detalles, { ...datos, lote: lote ? etiquetaLote(lote) : `Lote #${datos.idLote}` }]);
    }
  };

  // Quita un producto de la lista antes de registrar.
  const quitarDetalle = (indice) => setDetalles(detalles.filter((_, i) => i !== indice));

  // Vuelve al paso 1 sin registrar nada.
  const reiniciar = () => {
    setCabecera(null);
    setDetalles([]);
  };

  // Envía la entrada completa (POST /api/entradas). Si la API rechaza, se muestra el error y se conserva la lista.
  const registrarEntrada = async () => {
    setEnviando(true);
    try {
      const creada = await crearEntrada({
        fechaEntrada: cabecera.fechaEntrada,
        numeroFactura: cabecera.numeroFactura.trim() || null,
        idBodega: Number(cabecera.idBodega),
        idProveedor: Number(cabecera.idProveedor),
        idEmpleado: Number(cabecera.idEmpleado),
        detalles: detalles.map(({ idLote, cantidad, valorUnitario }) => ({ idLote, cantidad, valorUnitario })),
      });
      reiniciar();
      setMensaje({ tipo: 'exito', texto: `Entrada #${creada.idEntrada} registrada. El stock de la bodega se actualizó.` });
    } catch (error) {
      setMensaje({ tipo: 'error', texto: obtenerMensajeError(error) });
    } finally {
      setEnviando(false);
    }
  };

  const opcionesLote = catalogos.lotes.map((l) => ({ valor: l.idLote, texto: etiquetaLote(l) }));
  const total = detalles.reduce((suma, d) => suma + d.cantidad * d.valorUnitario, 0);

  return (
    <section>
      <h2 className="h3 mb-3">Registrar entrada de productos</h2>
      <Mensaje tipo={mensaje.tipo} texto={mensaje.texto} />

      {!cabecera && <FormularioEntrada {...catalogos} alCrear={guardarCabecera} />}

      {cabecera && (
        <>
          <p className="text-muted">
            Entrada · {cabecera.fechaEntrada} · Bodega: <strong>{cabecera.bodega}</strong>
          </p>
          <FormularioDetalleEntrada lotes={opcionesLote} alAgregar={agregarDetalle} />

          {detalles.length > 0 && (
            <div className="table-responsive mb-3">
              <table className="table table-sm table-striped">
                <thead className="table-success">
                  <tr>
                    <th>Lote</th><th className="text-end">Cantidad</th><th className="text-end">Valor unitario</th>
                    <th className="text-end">Subtotal</th><th></th>
                  </tr>
                </thead>
                <tbody>
                  {detalles.map((d, indice) => (
                    <tr key={`${d.idLote}-${d.valorUnitario}`}>
                      <td>{d.lote}</td>
                      <td className="text-end">{formatearCantidad(d.cantidad)}</td>
                      <td className="text-end">{formatearMoneda(d.valorUnitario)}</td>
                      <td className="text-end">{formatearMoneda(d.cantidad * d.valorUnitario)}</td>
                      <td className="text-end">
                        <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => quitarDetalle(indice)}>Quitar</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
                <tfoot>
                  <tr><th colSpan="3" className="text-end">Total</th><th className="text-end">{formatearMoneda(total)}</th><th></th></tr>
                </tfoot>
              </table>
            </div>
          )}
          <div className="d-flex gap-2">
            <button type="button" className="btn btn-success" disabled={detalles.length === 0 || enviando} onClick={registrarEntrada}>
              {enviando ? 'Registrando...' : 'Registrar entrada'}
            </button>
            <button type="button" className="btn btn-outline-secondary" onClick={reiniciar}>Cancelar</button>
          </div>
        </>
      )}
    </section>
  );
}

export default PaginaEntradas;
