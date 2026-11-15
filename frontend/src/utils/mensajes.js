/**
 * Convierte un error de Axios en un texto claro para el usuario.
 *
 * La API (ManejadorErrores) responde los errores con un JSON así:
 *   { estado: 409, error: "Conflict", mensaje: "Stock insuficiente..." }                    -> regla de negocio
 *   { estado: 400, error: "Bad Request", mensaje: "Datos inválidos", errores: { campo: "texto" } } -> validación
 * Los servicios de autenticación responden con texto plano (por ejemplo, «Error en la autenticación»).
 *
 * @param {Error} error Error recibido en el catch de una petición Axios.
 * @returns {string} Mensaje listo para mostrar en pantalla.
 */
export function obtenerMensajeError(error) {
  // Sin respuesta: la API está apagada, la red falló o el navegador bloqueó la petición por CORS.
  if (!error.response) {
    return 'No se pudo conectar con la API. Verifica que el back-end esté en ejecución (puerto 8080) y que CORS esté habilitado.';
  }

  const datos = error.response.data;

  // Respuesta de texto plano (login y registro).
  if (typeof datos === 'string' && datos.trim()) {
    return datos;
  }
  // Validación de campos: «errores» es un objeto { campo: mensaje }.
  if (datos && datos.errores && typeof datos.errores === 'object') {
    const detalle = Object.values(datos.errores).join('. ');
    if (detalle) return detalle;
  }
  // Regla de negocio, duplicado, no encontrado o stock insuficiente: «mensaje» trae el texto.
  if (datos && typeof datos.mensaje === 'string' && datos.mensaje) {
    return datos.mensaje;
  }
  return `Error ${error.response.status}: no se pudo completar la operación.`;
}
