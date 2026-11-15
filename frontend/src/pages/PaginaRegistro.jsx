import { useState } from 'react';
import { Link } from 'react-router-dom';
import Mensaje from '../components/Mensaje';
import { registrarUsuario } from '../services/api';
import { obtenerMensajeError } from '../utils/mensajes';

// Valores de un formulario vacío.
const VACIO = { usuario: '', contrasena: '', confirmacion: '' };

/**
 * PaginaRegistro (ruta /registro) - HU-01 registrar usuario.
 * Envía POST /api/auth/registro. La API guarda la contraseña cifrada (BCrypt) y rechaza
 * contraseñas de menos de 8 caracteres (400) y usuarios repetidos (409).
 */
function PaginaRegistro() {
  const [valores, setValores] = useState(VACIO);
  const [errores, setErrores] = useState({});
  const [mensaje, setMensaje] = useState({ tipo: 'exito', texto: '' });
  const [enviando, setEnviando] = useState(false);

  const manejarCambio = (evento) => {
    const { name, value } = evento.target;
    setValores({ ...valores, [name]: value });
  };

  // Mismas reglas que la API, más la confirmación de la contraseña.
  const validar = () => {
    const nuevos = {};
    if (!valores.usuario.trim()) nuevos.usuario = 'El usuario es obligatorio';
    if (valores.contrasena.length < 8) nuevos.contrasena = 'La contraseña debe tener mínimo 8 caracteres';
    if (valores.confirmacion !== valores.contrasena) nuevos.confirmacion = 'Las contraseñas no coinciden';
    setErrores(nuevos);
    return Object.keys(nuevos).length === 0;
  };

  const manejarEnvio = async (evento) => {
    evento.preventDefault();
    setMensaje({ tipo: 'exito', texto: '' });
    if (!validar()) return;
    setEnviando(true);
    try {
      await registrarUsuario({ usuario: valores.usuario.trim(), contrasena: valores.contrasena });
      setMensaje({ tipo: 'exito', texto: 'Usuario registrado correctamente. Ya puedes iniciar sesión.' });
      setValores(VACIO);
    } catch (error) {
      setMensaje({ tipo: 'error', texto: obtenerMensajeError(error) });
    } finally {
      setEnviando(false);
    }
  };

  return (
    <section className="row justify-content-center">
      <div className="col-md-6 col-lg-5">
        <h2 className="h3 mb-3">Crear cuenta</h2>
        <Mensaje tipo={mensaje.tipo} texto={mensaje.texto} />
        <form onSubmit={manejarEnvio} noValidate className="card card-body">
          <div className="mb-3">
            <label htmlFor="usuario" className="form-label fw-semibold">Usuario</label>
            <input id="usuario" name="usuario" className={`form-control ${errores.usuario ? 'is-invalid' : ''}`}
              value={valores.usuario} onChange={manejarCambio} />
            {errores.usuario && <div className="invalid-feedback">{errores.usuario}</div>}
          </div>
          <div className="mb-3">
            <label htmlFor="contrasena" className="form-label fw-semibold">Contraseña</label>
            <input id="contrasena" name="contrasena" type="password" autoComplete="new-password"
              className={`form-control ${errores.contrasena ? 'is-invalid' : ''}`} value={valores.contrasena} onChange={manejarCambio} />
            {errores.contrasena && <div className="invalid-feedback">{errores.contrasena}</div>}
          </div>
          <div className="mb-3">
            <label htmlFor="confirmacion" className="form-label fw-semibold">Confirmar contraseña</label>
            <input id="confirmacion" name="confirmacion" type="password" autoComplete="new-password"
              className={`form-control ${errores.confirmacion ? 'is-invalid' : ''}`} value={valores.confirmacion} onChange={manejarCambio} />
            {errores.confirmacion && <div className="invalid-feedback">{errores.confirmacion}</div>}
          </div>
          <button type="submit" className="btn btn-success" disabled={enviando}>
            {enviando ? 'Registrando...' : 'Registrarme'}
          </button>
        </form>
        <p className="mt-3 text-muted">¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link></p>
      </div>
    </section>
  );
}

export default PaginaRegistro;
