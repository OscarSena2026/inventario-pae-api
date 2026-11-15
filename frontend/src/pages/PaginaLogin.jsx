import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Mensaje from '../components/Mensaje';
import { iniciarSesion } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { obtenerMensajeError } from '../utils/mensajes';

/**
 * PaginaLogin (ruta /login) - HU-01 iniciar sesión.
 * Envía usuario y contraseña a POST /api/auth/login. Si la API responde 200, guarda la sesión y entra
 * al inventario; si responde 401 muestra el mensaje genérico de la API (no da pistas sobre cuál dato falló).
 */
function PaginaLogin() {
  // Estado: campos, errores de validación, aviso de la API y bandera de envío.
  const [valores, setValores] = useState({ usuario: '', contrasena: '' });
  const [errores, setErrores] = useState({});
  const [mensaje, setMensaje] = useState({ tipo: 'error', texto: '' });
  const [enviando, setEnviando] = useState(false);
  const { entrar } = useAuth();
  const navegar = useNavigate();

  // Evento onChange: guarda lo que el usuario escribe.
  const manejarCambio = (evento) => {
    const { name, value } = evento.target;
    setValores({ ...valores, [name]: value });
  };

  // Los dos campos son obligatorios en la API.
  const validar = () => {
    const nuevos = {};
    if (!valores.usuario.trim()) nuevos.usuario = 'El usuario es obligatorio';
    if (!valores.contrasena) nuevos.contrasena = 'La contraseña es obligatoria';
    setErrores(nuevos);
    return Object.keys(nuevos).length === 0;
  };

  // Evento onSubmit: valida, llama a la API y, si es correcto, abre la sesión.
  const manejarEnvio = async (evento) => {
    evento.preventDefault();
    setMensaje({ tipo: 'error', texto: '' });
    if (!validar()) return;
    setEnviando(true);
    try {
      await iniciarSesion(valores.usuario.trim(), valores.contrasena);
      entrar(valores.usuario.trim());
      navegar('/inventario', { replace: true });
    } catch (error) {
      setMensaje({ tipo: 'error', texto: obtenerMensajeError(error) });
    } finally {
      setEnviando(false);
    }
  };

  return (
    <section className="row justify-content-center">
      <div className="col-md-6 col-lg-5">
        <h2 className="h3 mb-3">Iniciar sesión</h2>
        <Mensaje tipo={mensaje.tipo} texto={mensaje.texto} />
        <form onSubmit={manejarEnvio} noValidate className="card card-body">
          <div className="mb-3">
            <label htmlFor="usuario" className="form-label fw-semibold">Usuario</label>
            <input id="usuario" name="usuario" autoComplete="username"
              className={`form-control ${errores.usuario ? 'is-invalid' : ''}`} value={valores.usuario} onChange={manejarCambio} />
            {errores.usuario && <div className="invalid-feedback">{errores.usuario}</div>}
          </div>
          <div className="mb-3">
            <label htmlFor="contrasena" className="form-label fw-semibold">Contraseña</label>
            <input id="contrasena" name="contrasena" type="password" autoComplete="current-password"
              className={`form-control ${errores.contrasena ? 'is-invalid' : ''}`} value={valores.contrasena} onChange={manejarCambio} />
            {errores.contrasena && <div className="invalid-feedback">{errores.contrasena}</div>}
          </div>
          <button type="submit" className="btn btn-success" disabled={enviando}>
            {enviando ? 'Ingresando...' : 'Ingresar'}
          </button>
        </form>
        <p className="mt-3 text-muted">¿No tienes cuenta? <Link to="/registro">Regístrate</Link></p>
      </div>
    </section>
  );
}

export default PaginaLogin;
