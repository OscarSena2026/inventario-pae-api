/**
 * Capa de servicios: aquí se concentran TODAS las llamadas a la API REST de Spring Boot (GA7-AA5).
 * Los componentes no usan Axios directamente; llaman a estas funciones.
 */
import axios from 'axios';

// Dirección base de la API. Se puede cambiar con la variable VITE_API_URL (archivo .env).
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
});

// Todas las funciones devuelven directamente los datos (response.data).
const obtener = (ruta, opciones) => api.get(ruta, opciones).then((respuesta) => respuesta.data);
const enviar = (ruta, datos) => api.post(ruta, datos).then((respuesta) => respuesta.data);
const modificar = (ruta, datos) => api.put(ruta, datos).then((respuesta) => respuesta.data);

// ---------- Autenticación (HU-01). La API responde con texto plano: 200 / 201 / 401 / 409.
export const iniciarSesion = (usuario, contrasena) => enviar('/auth/login', { usuario, contrasena });
export const registrarUsuario = (datos) => enviar('/auth/registro', datos);

// ---------- Productos (HU-02 registrar, HU-08 modificar)
export const listarProductos = () => obtener('/productos');
export const crearProducto = (producto) => enviar('/productos', producto);
export const actualizarProducto = (id, producto) => modificar(`/productos/${id}`, producto);

// ---------- Catálogos que alimentan las listas desplegables
export const listarBodegas = () => obtener('/bodegas');
export const listarProveedores = () => obtener('/proveedores');
export const listarEmpleados = () => obtener('/empleados');
export const listarSedes = () => obtener('/sedes');
export const listarLotes = () => obtener('/lotes');

// ---------- Entradas (HU-03)
// Una sola petición: la entrada viaja con su lista "detalles" y la API suma el stock de cada lote.
export const crearEntrada = (entrada) => enviar('/entradas', entrada);

// ---------- Salidas (HU-04)
// Una sola petición: la salida viaja con su lista "detalles". Si algún lote no tiene stock, la API
// responde 409 («Stock insuficiente...») y no descuenta nada.
export const crearSalida = (salida) => enviar('/salidas', salida);

// ---------- Inventario (HU-05)
export const listarExistencias = () => obtener('/existencias');
export const listarExistenciasPorBodega = (idBodega) => obtener(`/existencias/bodega/${idBodega}`);

// ---------- Alertas (HU-06)
export const listarAlertasStockMinimo = () => obtener('/alertas/stock-minimo');
export const listarAlertasVencimiento = (dias) => obtener('/alertas/vencimiento', { params: { dias } });

// ---------- Reportes (HU-09 y HU-10)
export const listarReporteStock = () => obtener('/reportes/stock-productos');
// El CSV llega como archivo (blob) para poder descargarlo desde el navegador.
export const descargarReporteCsv = () => obtener('/reportes/stock-productos/csv', { responseType: 'blob' });
