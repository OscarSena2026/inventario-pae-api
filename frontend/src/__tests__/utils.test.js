/**
 * Pruebas unitarias de las funciones de apoyo (mensajes de error y formato).
 * No dibujan pantallas ni usan la API: prueban cada función por separado.
 */
import { describe, it, expect } from 'vitest';
import { obtenerMensajeError } from '../utils/mensajes';
import { etiquetaExistencia, etiquetaLote, fechaHoy, formatearCantidad } from '../utils/formato';

describe('obtenerMensajeError', () => {
  it('avisa que no hay conexión cuando la petición no tuvo respuesta', () => {
    expect(obtenerMensajeError(new Error('Network Error'))).toMatch(/No se pudo conectar con la API/);
  });

  it('devuelve el texto plano que responde el login', () => {
    expect(obtenerMensajeError({ response: { status: 401, data: 'Error en la autenticación' } })).toBe('Error en la autenticación');
  });

  it('une los mensajes de validación por campo (400)', () => {
    const error = { response: { status: 400, data: { estado: 400, mensaje: 'Datos inválidos', errores: { nombreProducto: 'El nombre es obligatorio', categoria: 'La categoría es obligatoria' } } } };
    expect(obtenerMensajeError(error)).toBe('El nombre es obligatorio. La categoría es obligatoria');
  });

  it('devuelve el mensaje de una regla de negocio o conflicto (409)', () => {
    const error = { response: { status: 409, data: { estado: 409, error: 'Conflict', mensaje: 'Stock insuficiente' } } };
    expect(obtenerMensajeError(error)).toBe('Stock insuficiente');
  });

  it('usa un texto genérico con el código cuando la respuesta no trae mensaje', () => {
    expect(obtenerMensajeError({ response: { status: 500, data: {} } })).toBe('Error 500: no se pudo completar la operación.');
  });
});

describe('formato', () => {
  it('fechaHoy devuelve AAAA-MM-DD', () => {
    expect(fechaHoy()).toMatch(/^\d{4}-\d{2}-\d{2}$/);
  });

  it('formatearCantidad usa separador de miles y máximo 2 decimales', () => {
    expect(formatearCantidad(1234.567)).toBe('1.234,57');
  });

  it('etiquetaLote arma el texto con producto, lote y vencimiento', () => {
    const lote = { idLote: 3, fechaVencimiento: '2027-01-01', producto: { nombreProducto: 'Arroz' } };
    expect(etiquetaLote(lote)).toBe('Arroz · Lote #3 · vence 2027-01-01');
    expect(etiquetaLote(lote, 'disponible 50')).toBe('Arroz · Lote #3 · vence 2027-01-01 · disponible 50');
  });

  it('etiquetaExistencia arma el texto desde la respuesta plana de la API', () => {
    const existencia = { idLote: 3, nombreProducto: 'Arroz', fechaVencimiento: '2027-01-01' };
    expect(etiquetaExistencia(existencia, 'disponible 50')).toBe('Arroz · Lote #3 · vence 2027-01-01 · disponible 50');
  });
});
