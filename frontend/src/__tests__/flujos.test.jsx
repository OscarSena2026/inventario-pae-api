/**
 * Pruebas de los flujos principales del front-end con la API simulada (mock).
 * Se ejecutan con:  npm test
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import App from '../App';
import { AuthProvider } from '../context/AuthContext';
import * as api from '../services/api';

vi.mock('../services/api');

// Datos de ejemplo con la misma forma que devuelve la API.
const bodega = { idBodega: 1, nombreBodega: 'Bodega Central Montería' };
const proveedor = { idProveedor: 2, nombreProveedor: 'Distribuidora La Sabana' };
const empleado = { idEmpleado: 1, nombres: 'Laura Martínez', cargo: 'Bodeguera' };
const sede = { idSede: 1, nombreSede: 'IE San José' };
const producto = { idProducto: 1, nombreProducto: 'Arroz', categoria: 'Granos', unidadMedida: 'kg', perecedero: 'NO', stockMinimo: 100 };
const lote = { idLote: 1, fechaVencimiento: '2027-01-01', producto };
// GET /api/existencias devuelve objetos planos (no anidados).
const existencia = {
  idExistencia: 1, idBodega: 1, nombreBodega: 'Bodega Central Montería', idLote: 1, idProducto: 1,
  nombreProducto: 'Arroz', fechaVencimiento: '2027-01-01', cantidadDisponible: 50,
};

// Dibuja la aplicación completa en la ruta indicada (sin navegador real). Por defecto, con sesión iniciada.
const ir = (ruta, { sesion = true } = {}) => {
  if (sesion) sessionStorage.setItem('pae_usuario', 'oscar');
  return render(
    <MemoryRouter initialEntries={[ruta]} future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <AuthProvider><App /></AuthProvider>
    </MemoryRouter>
  );
};

// Antes de cada prueba se reinician los mocks y se simulan las respuestas de la API (sin servidor real).
beforeEach(() => {
  sessionStorage.clear();
  vi.resetAllMocks();
  api.listarBodegas.mockResolvedValue([bodega]);
  api.listarProveedores.mockResolvedValue([proveedor]);
  api.listarEmpleados.mockResolvedValue([empleado]);
  api.listarSedes.mockResolvedValue([sede]);
  api.listarLotes.mockResolvedValue([lote]);
  api.listarProductos.mockResolvedValue([producto]);
  api.listarExistencias.mockResolvedValue([existencia]);
  api.listarExistenciasPorBodega.mockResolvedValue([existencia]);
});

describe('HU-01: autenticación', () => {
  it('sin sesión envía a la pantalla de inicio de sesión', async () => {
    ir('/productos', { sesion: false });
    expect(await screen.findByRole('heading', { name: 'Iniciar sesión' })).toBeTruthy();
    expect(screen.queryByText('Inventario', { selector: 'a' })).toBeNull();
  });

  it('valida campos vacíos, inicia sesión y permite cerrarla', async () => {
    const u = userEvent.setup();
    ir('/login', { sesion: false });
    await u.click(screen.getByRole('button', { name: 'Ingresar' }));
    expect(screen.getByText('El usuario es obligatorio')).toBeTruthy();
    expect(api.iniciarSesion).not.toHaveBeenCalled();
    api.iniciarSesion.mockResolvedValue('Autenticación satisfactoria');
    await u.type(screen.getByLabelText('Usuario'), 'oscar');
    await u.type(screen.getByLabelText('Contraseña'), 'Clave1234');
    await u.click(screen.getByRole('button', { name: 'Ingresar' }));
    await waitFor(() => expect(api.iniciarSesion).toHaveBeenCalledWith('oscar', 'Clave1234'));
    expect(await screen.findByText('Inventario (existencias)')).toBeTruthy();
    await u.click(screen.getByRole('button', { name: 'Cerrar sesión' }));
    expect(await screen.findByRole('heading', { name: 'Iniciar sesión' })).toBeTruthy();
  });

  it('muestra el mensaje de la API cuando las credenciales son incorrectas (401)', async () => {
    const u = userEvent.setup();
    ir('/login', { sesion: false });
    api.iniciarSesion.mockRejectedValue({ response: { status: 401, data: 'Error en la autenticación' } });
    await u.type(screen.getByLabelText('Usuario'), 'oscar');
    await u.type(screen.getByLabelText('Contraseña'), 'mala');
    await u.click(screen.getByRole('button', { name: 'Ingresar' }));
    expect(await screen.findByText('Error en la autenticación')).toBeTruthy();
  });

  it('registro: valida la contraseña y registra al usuario', async () => {
    const u = userEvent.setup();
    ir('/registro', { sesion: false });
    await u.type(screen.getByLabelText('Usuario'), 'nuevo');
    await u.type(screen.getByLabelText('Contraseña'), 'corta');
    await u.type(screen.getByLabelText('Confirmar contraseña'), 'otra');
    await u.click(screen.getByRole('button', { name: 'Registrarme' }));
    expect(screen.getByText('La contraseña debe tener mínimo 8 caracteres')).toBeTruthy();
    expect(screen.getByText('Las contraseñas no coinciden')).toBeTruthy();
    expect(api.registrarUsuario).not.toHaveBeenCalled();
    api.registrarUsuario.mockResolvedValue('Usuario registrado correctamente');
    await u.clear(screen.getByLabelText('Contraseña')); await u.type(screen.getByLabelText('Contraseña'), 'Clave1234');
    await u.clear(screen.getByLabelText('Confirmar contraseña')); await u.type(screen.getByLabelText('Confirmar contraseña'), 'Clave1234');
    await u.click(screen.getByRole('button', { name: 'Registrarme' }));
    await waitFor(() => expect(api.registrarUsuario).toHaveBeenCalledWith({ usuario: 'nuevo', contrasena: 'Clave1234' }));
    expect(await screen.findByText(/Usuario registrado correctamente/)).toBeTruthy();
  });
});

describe('Sistema de Inventario PAE (front-end)', () => {
  it('HU-05: muestra el inventario y filtra por producto', async () => {
    const u = userEvent.setup();
    ir('/');
    expect(await screen.findByText('Bodega Central Montería', { selector: 'td' })).toBeTruthy();
    expect(screen.getByText('50')).toBeTruthy();
    await u.type(screen.getByPlaceholderText('Buscar por nombre...'), 'zzz');
    expect(screen.getByText('No hay existencias para mostrar.')).toBeTruthy();
  });

  it('HU-02: valida campos obligatorios y registra un producto', async () => {
    const u = userEvent.setup();
    ir('/productos');
    await screen.findByText('Arroz');
    await u.click(screen.getByRole('button', { name: 'Registrar producto' }));
    expect(screen.getByText('El nombre del producto es obligatorio')).toBeTruthy();
    expect(screen.getByText('La categoría es obligatoria')).toBeTruthy();
    expect(screen.getByText(/El stock mínimo es obligatorio/)).toBeTruthy();
    expect(api.crearProducto).not.toHaveBeenCalled();
    api.crearProducto.mockResolvedValue({ idProducto: 2 });
    await u.type(screen.getByLabelText('Nombre del producto'), 'Lentejas');
    await u.type(screen.getByLabelText('Categoría'), 'Granos');
    await u.type(screen.getByLabelText('Unidad de medida'), 'kg');
    await u.type(screen.getByLabelText('Stock mínimo'), '20');
    await u.click(screen.getByRole('button', { name: 'Registrar producto' }));
    await waitFor(() => expect(api.crearProducto).toHaveBeenCalledWith(
      { nombreProducto: 'Lentejas', categoria: 'Granos', unidadMedida: 'kg', perecedero: 'NO', stockMinimo: 20 }));
    expect(await screen.findByText('Producto registrado correctamente.')).toBeTruthy();
  });

  it('HU-02: muestra el error de producto duplicado que devuelve la API (409)', async () => {
    const u = userEvent.setup();
    ir('/productos');
    await screen.findByText('Arroz');
    api.crearProducto.mockRejectedValue({ response: { status: 409, data: { estado: 409, error: 'Conflict', mensaje: "Ya existe un producto registrado con el nombre 'Arroz'" } } });
    await u.type(screen.getByLabelText('Nombre del producto'), 'Arroz');
    await u.type(screen.getByLabelText('Categoría'), 'Granos');
    await u.type(screen.getByLabelText('Unidad de medida'), 'kg');
    await u.type(screen.getByLabelText('Stock mínimo'), '10');
    await u.click(screen.getByRole('button', { name: 'Registrar producto' }));
    expect(await screen.findByText(/Ya existe un producto registrado/)).toBeTruthy();
  });

  it('HU-08: modifica un producto', async () => {
    const u = userEvent.setup();
    window.scrollTo = vi.fn();
    ir('/productos');
    await screen.findByText('Arroz');
    api.actualizarProducto.mockResolvedValue({});
    await u.click(screen.getByRole('button', { name: 'Editar' }));
    expect(screen.getByText('Modificar producto')).toBeTruthy();
    const campo = screen.getByLabelText('Unidad de medida');
    await u.clear(campo); await u.type(campo, 'libra');
    await u.click(screen.getByRole('button', { name: 'Guardar cambios' }));
    await waitFor(() => expect(api.actualizarProducto).toHaveBeenCalledWith(1,
      { nombreProducto: 'Arroz', categoria: 'Granos', unidadMedida: 'libra', perecedero: 'NO', stockMinimo: 100 }));
  });

  it('HU-03: arma la lista de productos y registra la entrada en una sola petición', async () => {
    const u = userEvent.setup();
    ir('/entradas');
    await screen.findByRole('option', { name: 'Bodega Central Montería' });
    api.crearEntrada.mockResolvedValue({ idEntrada: 7 });
    await u.selectOptions(screen.getByLabelText('Bodega'), '1');
    await u.selectOptions(screen.getByLabelText('Proveedor'), '2');
    await u.selectOptions(screen.getByLabelText('Empleado responsable'), '1');
    await u.click(screen.getByRole('button', { name: 'Continuar' }));
    expect(await screen.findByText(/Productos de la entrada/)).toBeTruthy();
    expect(api.crearEntrada).not.toHaveBeenCalled(); // todavía no se envía nada a la API
    await u.selectOptions(screen.getByLabelText('Lote (producto)'), '1');
    await u.type(screen.getByLabelText('Cantidad'), '10');
    await u.type(screen.getByLabelText('Valor unitario ($)'), '2500');
    await u.click(screen.getByRole('button', { name: 'Agregar a la lista' }));
    expect((await screen.findAllByText(/25\.000/)).length).toBeGreaterThan(0);
    await u.click(screen.getByRole('button', { name: 'Registrar entrada' }));
    await waitFor(() => expect(api.crearEntrada).toHaveBeenCalledTimes(1));
    expect(api.crearEntrada).toHaveBeenCalledWith({
      fechaEntrada: expect.any(String), numeroFactura: null, idBodega: 1, idProveedor: 2, idEmpleado: 1,
      detalles: [{ idLote: 1, cantidad: 10, valorUnitario: 2500 }],
    });
    expect(await screen.findByText(/Entrada #7 registrada/)).toBeTruthy();
  });

  it('HU-04: muestra «Stock insuficiente» (409) y conserva la lista de productos', async () => {
    const u = userEvent.setup();
    ir('/salidas');
    await screen.findByRole('option', { name: 'IE San José' });
    await u.selectOptions(screen.getByLabelText('Bodega de origen'), '1');
    await u.selectOptions(screen.getByLabelText('Sede de destino'), '1');
    await u.selectOptions(screen.getByLabelText('Empleado responsable'), '1');
    await u.click(screen.getByRole('button', { name: 'Continuar' }));
    expect(await screen.findByText(/Productos de la salida/)).toBeTruthy();
    await u.selectOptions(screen.getByLabelText('Lote disponible'), '1');
    await u.type(screen.getByLabelText('Cantidad a despachar'), '999');
    await u.click(screen.getByRole('button', { name: 'Agregar a la lista' }));
    api.crearSalida.mockRejectedValue({ response: { status: 409, data: { estado: 409, error: 'Conflict', mensaje: 'Stock insuficiente: no hay suficiente cantidad disponible de este lote en la bodega para despachar 999' } } });
    await u.click(screen.getByRole('button', { name: 'Registrar salida' }));
    expect(await screen.findByText(/Stock insuficiente/)).toBeTruthy();
    // La lista sigue en pantalla para corregir la cantidad.
    expect(screen.getByRole('button', { name: 'Quitar' })).toBeTruthy();
    expect(api.crearSalida).toHaveBeenCalledWith(expect.objectContaining({
      idBodega: 1, idSede: 1, idEmpleado: 1, detalles: [{ idLote: 1, cantidad: 999 }],
    }));
  });

  it('HU-04: registra una salida correcta', async () => {
    const u = userEvent.setup();
    ir('/salidas');
    await screen.findByRole('option', { name: 'IE San José' });
    await u.selectOptions(screen.getByLabelText('Bodega de origen'), '1');
    await u.selectOptions(screen.getByLabelText('Sede de destino'), '1');
    await u.selectOptions(screen.getByLabelText('Empleado responsable'), '1');
    await u.click(screen.getByRole('button', { name: 'Continuar' }));
    await u.selectOptions(await screen.findByLabelText('Lote disponible'), '1');
    await u.type(screen.getByLabelText('Cantidad a despachar'), '10');
    await u.click(screen.getByRole('button', { name: 'Agregar a la lista' }));
    api.crearSalida.mockResolvedValue({ idSalida: 9 });
    await u.click(screen.getByRole('button', { name: 'Registrar salida' }));
    expect(await screen.findByText(/Salida #9 registrada/)).toBeTruthy();
  });

  it('HU-06: muestra las alertas y valida los días de vencimiento', async () => {
    const u = userEvent.setup();
    api.listarAlertasStockMinimo.mockResolvedValue([
      { idProducto: 1, nombreProducto: 'Arroz', stockActual: 50, stockMinimo: 100, mensaje: 'Stock por debajo del mínimo' }]);
    api.listarAlertasVencimiento.mockResolvedValue([
      { idLote: 1, idProducto: 1, nombreProducto: 'Arroz', idBodega: 1, nombreBodega: 'Bodega Central Montería',
        cantidadDisponible: 50, fechaVencimiento: '2027-01-01', diasRestantes: 54, mensaje: 'Lote por vencer' }]);
    ir('/alertas');
    expect(await screen.findByText('Stock por debajo del mínimo')).toBeTruthy();
    expect(screen.getByText('54')).toBeTruthy();
    expect(api.listarAlertasVencimiento).toHaveBeenCalledWith(30);
    await u.clear(screen.getByLabelText('Vencen en los próximos (días)'));
    await u.type(screen.getByLabelText('Vencen en los próximos (días)'), '0');
    await u.click(screen.getByRole('button', { name: 'Consultar' }));
    expect(screen.getByText('Los días deben ser un número entero mayor a 0')).toBeTruthy();
    await u.clear(screen.getByLabelText('Vencen en los próximos (días)'));
    await u.type(screen.getByLabelText('Vencen en los próximos (días)'), '60');
    await u.click(screen.getByRole('button', { name: 'Consultar' }));
    await waitFor(() => expect(api.listarAlertasVencimiento).toHaveBeenLastCalledWith(60));
  });

  it('HU-09 y HU-10: muestra el reporte de stock y exporta el CSV', async () => {
    const u = userEvent.setup();
    api.listarReporteStock.mockResolvedValue([
      { idProducto: 1, nombreProducto: 'Arroz', categoria: 'Granos', unidadMedida: 'kg', stockActual: 50, stockMinimo: 100, estado: 'CRITICO' }]);
    api.descargarReporteCsv.mockResolvedValue(new Blob(['producto;stock']));
    URL.createObjectURL = vi.fn(() => 'blob:prueba');
    URL.revokeObjectURL = vi.fn();
    const clic = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
    ir('/reportes');
    expect(await screen.findByText('CRITICO')).toBeTruthy();
    await u.click(screen.getByRole('button', { name: 'Exportar CSV' }));
    expect(await screen.findByText(/stock_productos\.csv/)).toBeTruthy();
    expect(clic).toHaveBeenCalled();
    clic.mockRestore();
  });

  it('muestra un aviso claro cuando la API no responde', async () => {
    api.listarProductos.mockRejectedValue(new Error('Network Error'));
    ir('/productos');
    expect(await screen.findByText(/No se pudo conectar con la API/)).toBeTruthy();
  });
});
