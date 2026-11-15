import { useEffect, useState } from 'react';
import Mensaje from '../components/Mensaje';
import { descargarReporteCsv, listarReporteStock } from '../services/api';
import { obtenerMensajeError } from '../utils/mensajes';
import { formatearCantidad } from '../utils/formato';

/**
 * PaginaReportes (ruta /reportes) - HU-09 reporte de stock por producto y HU-10 exportar a CSV.
 * Muestra el stock actual de cada producto frente a su mínimo y permite descargar el mismo reporte en CSV.
 */
function PaginaReportes() {
  // Estado: filas del reporte, aviso y bandera de carga.
  const [filas, setFilas] = useState([]);
  const [mensaje, setMensaje] = useState({ tipo: 'exito', texto: '' });
  const [cargando, setCargando] = useState(true);

  // useEffect: pide el reporte a la API al abrir la página (GET /api/reportes/stock-productos).
  useEffect(() => {
    const cargar = async () => {
      try {
        setFilas(await listarReporteStock());
      } catch (error) {
        setMensaje({ tipo: 'error', texto: obtenerMensajeError(error) });
      } finally {
        setCargando(false);
      }
    };
    cargar();
  }, []);

  // Evento onClick: baja el CSV de la API y lo entrega al navegador como un archivo descargable.
  const exportarCsv = async () => {
    try {
      const archivo = await descargarReporteCsv();
      const enlace = document.createElement('a');
      enlace.href = URL.createObjectURL(archivo);
      enlace.download = 'stock_productos.csv';
      enlace.click();
      URL.revokeObjectURL(enlace.href);
      setMensaje({ tipo: 'exito', texto: 'Reporte exportado: stock_productos.csv' });
    } catch (error) {
      setMensaje({ tipo: 'error', texto: obtenerMensajeError(error) });
    }
  };

  return (
    <section>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2 className="h3 mb-0">Reporte de stock por producto</h2>
        <button type="button" className="btn btn-sm btn-success" onClick={exportarCsv}>Exportar CSV</button>
      </div>
      <Mensaje tipo={mensaje.tipo} texto={mensaje.texto} />
      {cargando && <p>Cargando reporte...</p>}
      {!cargando && filas.length === 0 && <p className="text-muted">No hay productos para reportar.</p>}
      {filas.length > 0 && (
        <div className="table-responsive">
          <table className="table table-striped align-middle">
            <thead className="table-success">
              <tr>
                <th>Producto</th><th>Categoría</th><th>Unidad</th>
                <th className="text-end">Stock actual</th><th className="text-end">Stock mínimo</th><th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {filas.map((f) => (
                <tr key={f.idProducto}>
                  <td>{f.nombreProducto}</td>
                  <td>{f.categoria}</td>
                  <td>{f.unidadMedida}</td>
                  <td className="text-end">{formatearCantidad(f.stockActual)}</td>
                  <td className="text-end">{formatearCantidad(f.stockMinimo)}</td>
                  <td>{f.estado}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

export default PaginaReportes;
