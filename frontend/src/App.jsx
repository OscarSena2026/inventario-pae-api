import { Navigate, Route, Routes } from 'react-router-dom';
import Encabezado from './components/Encabezado';
import PiePagina from './components/PiePagina';
import RutaProtegida from './components/RutaProtegida';
import PaginaLogin from './pages/PaginaLogin';
import PaginaRegistro from './pages/PaginaRegistro';
import PaginaProductos from './pages/PaginaProductos';
import PaginaEntradas from './pages/PaginaEntradas';
import PaginaSalidas from './pages/PaginaSalidas';
import PaginaInventario from './pages/PaginaInventario';
import PaginaAlertas from './pages/PaginaAlertas';
import PaginaReportes from './pages/PaginaReportes';
import { useAuth } from './context/AuthContext';

// Opciones del menú (solo se muestran con la sesión iniciada): una por cada pantalla del sistema.
const ENLACES = [
  { ruta: '/inventario', texto: 'Inventario' },
  { ruta: '/productos', texto: 'Productos' },
  { ruta: '/entradas', texto: 'Entradas' },
  { ruta: '/salidas', texto: 'Salidas' },
  { ruta: '/alertas', texto: 'Alertas' },
  { ruta: '/reportes', texto: 'Reportes' },
];

// Envuelve una pantalla para que solo se vea con la sesión iniciada.
const protegida = (pantalla) => <RutaProtegida>{pantalla}</RutaProtegida>;

/**
 * App (componente raíz)
 * Define las rutas con React Router y ubica el encabezado, la página activa y el pie de página.
 */
function App() {
  const { usuario, salir } = useAuth();
  return (
    <>
      <Encabezado enlaces={usuario ? ENLACES : []} usuario={usuario} alSalir={salir} />
      <main className="container">
        <Routes>
          {/* La página de inicio es la consulta del inventario (HU-05) */}
          <Route path="/" element={<Navigate to="/inventario" replace />} />
          <Route path="/login" element={<PaginaLogin />} />
          <Route path="/registro" element={<PaginaRegistro />} />
          <Route path="/inventario" element={protegida(<PaginaInventario />)} />
          <Route path="/productos" element={protegida(<PaginaProductos />)} />
          <Route path="/entradas" element={protegida(<PaginaEntradas />)} />
          <Route path="/salidas" element={protegida(<PaginaSalidas />)} />
          <Route path="/alertas" element={protegida(<PaginaAlertas />)} />
          <Route path="/reportes" element={protegida(<PaginaReportes />)} />
          <Route path="*" element={<p className="text-muted">La página que buscas no existe.</p>} />
        </Routes>
      </main>
      <PiePagina version="2.0.0" />
    </>
  );
}

export default App;
