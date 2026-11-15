import { useEffect, useState } from 'react';
import FormularioSalida from '../components/FormularioSalida';
import FormularioDetalleSalida from '../components/FormularioDetalleSalida';
import Mensaje from '../components/Mensaje';
import {
  crearSalida, listarBodegas, listarEmpleados, listarExistenciasPorBodega, listarSedes,
} from '../services/api';
import { obtenerMensajeError } from '../utils/mensajes';
import { etiquetaExistencia, formatearCantidad } from '../utils/formato';

/**
 * PaginaSalidas (ruta /salidas) - HU-04 registrar salida de productos.
 * Coordina el registro en dos pasos de pantalla: primero los datos generales y luego la lista de productos.
 * Al final se envía UNA sola petición (POST /api/salidas) con la salida y sus «detalles». La API verifica
 * el stock: si algún lote no alcanza, responde 409 «Stock insuficiente...» y no descuenta nada.
 */
function PaginaSalidas() {
  // Estado: catálogos, datos generales (null = aún no), existencias de la bodega, productos agregados y aviso.
  const [catalogos, setCatalogos] = useState({ bodegas: [], sedes: [], empleados: [] });
  const [cabecera, setCabecera] = useState(null);
  const [disponibles, setDisponibles] = useState([]);
  const [detalles, setDetalles] = useState([]);
  const [mensaje, setMensaje] = useState({ tipo: 'exito', texto: '' });
  const [enviando, setEnviando] = useState(false);

  // useEffect: al abrir la página se piden a la API los datos de las listas desplegables.
  useEffect(() => {
    const cargarCatalogos = async () => {
      try {
        const [bodegas, sedes, empleados] = await Promise.all([listarBodegas(), listarSedes(), listarEmpleados()]);
        setCatalogos({ bodegas, sedes, empleados });
      } catch (error) {
        setMensaje({ tipo: 'error', texto: obtenerMensajeError(error) });
      }
    };
    cargarCatalogos();
  }, []);

  // Paso 1: guarda los datos generales y pide el stock de la bodega elegida (todavía no se registra nada).
  const guardarCabecera = async (datos) => {
    try {
      setDisponibles(await listarExistenciasPorBodega(Number(datos.idBodega)));
      const bodega = catalogos.bodegas.find((b) => b.idBodega === Number(datos.idBodega));
      const sede = catalogos.sedes.find((s) => s.idSede === Number(datos.idSede));
      setCabecera({ ...datos, bodega: bodega ? bodega.nombreBodega : '', sede: sede ? sede.nombreSede : '' });
      setDetalles([]);
      setMensaje({ tipo: 'exito', texto: '' });
    } catch (error) {
      setMensaje({ tipo: 'error', texto: obtenerMensajeError(error) });
    }
  };

  // Paso 2: agrega un producto a la lista; si el lote ya estaba, suma la cantidad.
  const agregarDetalle = (datos) => {
    const existencia = disponibles.find((e) => e.idLote === datos.idLote);
    const existente = detalles.find((d) => d.idLote === datos.idLote);
    if (existente) {
      setDetalles(detalles.map((d) => (d === existente ? { ...d, cantidad: d.cantidad + datos.cantidad } : d)));
    } else {
      setDetalles([...detalles, { ...datos, lote: existencia ? etiquetaExistencia(existencia) : `Lote #${datos.idLote}` }]);
    }
  };

  // Quita un producto de la lista antes de registrar.
  const quitarDetalle = (indice) => setDetalles(detalles.filter((_, i) => i !== indice));

  // Vuelve al paso 1 sin registrar nada.
  const reiniciar = () => {
    setCabecera(null);
    setDetalles([]);
    setDisponibles([]);
  };

  // Envía la salida completa (POST /api/salidas). Si no hay stock (409), se muestra el mensaje y se conserva la lista.
  const registrarSalida = async () => {
    setEnviando(true);
    try {
      const creada = await crearSalida({
        fechaSalida: cabecera.fechaSalida,
        motivo: cabecera.motivo.trim() || null,
        idBodega: Number(cabecera.idBodega),
        idSede: Number(cabecera.idSede),
        idEmpleado: Number(cabecera.idEmpleado),
        detalles: detalles.map(({ idLote, cantidad }) => ({ idLote, cantidad })),
      });
      reiniciar();
      setMensaje({ tipo: 'exito', texto: `Salida #${creada.idSalida} registrada. El stock se descontó.` });
    } catch (error) {
      setMensaje({ tipo: 'error', texto: obtenerMensajeError(error) });
    } finally {
      setEnviando(false);
    }
  };

  // Lotes con existencia mayor que 0 en la bodega, listos para la lista desplegable.
  const opcionesLote = disponibles
    .filter((e) => Number(e.cantidadDisponible) > 0)
    .map((e) => ({ valor: e.idLote, texto: etiquetaExistencia(e, `disponible ${formatearCantidad(e.cantidadDisponible)}`) }));

  return (
    <section>
      <h2 className="h3 mb-3">Registrar salida de productos</h2>
      <Mensaje tipo={mensaje.tipo} texto={mensaje.texto} />

      {!cabecera && <FormularioSalida {...catalogos} alCrear={guardarCabecera} />}

      {cabecera && (
        <>
          <p className="text-muted">
            Salida · {cabecera.fechaSalida} · <strong>{cabecera.bodega}</strong> → <strong>{cabecera.sede}</strong>
          </p>
          <FormularioDetalleSalida lotes={opcionesLote} alAgregar={agregarDetalle} />

          {detalles.length > 0 && (
            <div className="table-responsive mb-3">
              <table className="table table-sm table-striped">
                <thead className="table-success">
                  <tr><th>Lote</th><th className="text-end">Cantidad a despachar</th><th></th></tr>
                </thead>
                <tbody>
                  {detalles.map((d, indice) => (
                    <tr key={d.idLote}>
                      <td>{d.lote}</td>
                      <td className="text-end">{formatearCantidad(d.cantidad)}</td>
                      <td className="text-end">
                        <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => quitarDetalle(indice)}>Quitar</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
          <div className="d-flex gap-2">
            <button type="button" className="btn btn-success" disabled={detalles.length === 0 || enviando} onClick={registrarSalida}>
              {enviando ? 'Registrando...' : 'Registrar salida'}
            </button>
            <button type="button" className="btn btn-outline-secondary" onClick={reiniciar}>Cancelar</button>
          </div>
        </>
      )}
    </section>
  );
}

export default PaginaSalidas;
