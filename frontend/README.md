# Sistema de Inventario PAE — Front-end (React + Vite)

GA8-220501096-AA1 · SENA ADSO · Aprendiz: Oscar Fabian Estrada Arango · Ficha 3235898

Interfaz web del Sistema de Inventario del Programa de Alimentación Escolar. Consume la API REST de
Spring Boot de este mismo repositorio (paquete `co.edu.sena.pae`, base de datos MySQL `inventario_pae`).
Parte del front-end de la GA7-AA4-EV03, adaptado a la API final (GA7-AA5).

## Historias de usuario cubiertas

| Historia | Pantalla | Ruta |
|---|---|---|
| HU-01 Registrar usuario e iniciar sesión | Login y registro | `/login`, `/registro` |
| HU-02 Registrar producto | Productos | `/productos` |
| HU-08 Modificar producto | Productos | `/productos` |
| HU-03 Registrar entrada (suma stock) | Entradas | `/entradas` |
| HU-04 Registrar salida (valida y descuenta stock) | Salidas | `/salidas` |
| HU-05 Consultar inventario | Inventario | `/inventario` |
| HU-06 Alertas de stock mínimo y de vencimiento | Alertas | `/alertas` |
| HU-09 Reporte de stock por producto | Reportes | `/reportes` |
| HU-10 Exportar el reporte a CSV | Reportes | `/reportes` |

Quedan fuera del front-end las pantallas de administración de catálogos (municipios, bodegas, sedes,
empleados, proveedores y lotes): esos servicios existen en la API y se prueban con Postman.

## Requisitos

- Node.js 18 o superior.
- La API de Spring Boot en ejecución en `http://localhost:8080` con la base de datos `inventario_pae`.
- CORS: la API ya permite `http://localhost:5173` (propiedad `app.cors.origen` de `application.properties`).
  Para otro origen, por ejemplo la URL desplegada del front-end, se cambia esa propiedad.

## Cómo ejecutarlo

```bash
cd frontend
npm install
npm run dev        # abre http://localhost:5173
```

Si la API usa otra dirección, copiar `.env.example` como `.env` y cambiar `VITE_API_URL`.

Otros comandos:

| Comando | Para qué sirve |
|---|---|
| `npm test` | Ejecuta las pruebas unitarias (Vitest + React Testing Library) |
| `npm run build` | Compila para producción en la carpeta `dist/` |
| `npm run preview` | Sirve la carpeta `dist/` para revisar la versión compilada |

## Cómo se comunica con la API

- Todas las llamadas están en `src/services/api.js` (Axios); los componentes no usan Axios directamente.
- **Entradas y salidas** se envían en **una sola petición** (`POST /api/entradas` y `POST /api/salidas`)
  con la lista `detalles`. Si no hay stock suficiente, la API responde **409** «Stock insuficiente...» y la
  pantalla muestra el mensaje conservando la lista para corregirla.
- Los errores de la API (`{estado, error, mensaje, errores}`) se convierten en texto con
  `src/utils/mensajes.js`.
- **Limitación conocida:** el login valida usuario y contraseña, pero la API **no entrega token ni sesión**.
  La sesión del front-end solo decide qué pantallas se muestran; no protege los servicios de la API.

## Estructura

```
src/
├── main.jsx                 punto de entrada (React Router y sesión)
├── App.jsx                  componente raíz: menú, rutas y pie
├── context/AuthContext.jsx  sesión del usuario (sessionStorage)
├── components/              piezas reutilizables y formularios
├── pages/                   una página por pantalla (manejan el estado y llaman a la API)
├── services/api.js          TODAS las llamadas a la API REST (Axios)
├── utils/                   mensajes de error y formato de fechas, moneda y lotes
└── __tests__/               pruebas unitarias y de los flujos principales
```

## Componentes

| Tipo | Componentes |
|---|---|
| Generales | `App`, `Encabezado`, `PiePagina`, `RutaProtegida` |
| Acceso (HU-01) | `PaginaLogin`, `PaginaRegistro` |
| Productos (HU-02, HU-08) | `PaginaProductos`, `FormularioProducto`, `TablaProductos` |
| Entradas (HU-03) | `PaginaEntradas`, `FormularioEntrada`, `FormularioDetalleEntrada` |
| Salidas (HU-04) | `PaginaSalidas`, `FormularioSalida`, `FormularioDetalleSalida` |
| Inventario (HU-05) | `PaginaInventario`, `FiltroInventario`, `TablaExistencias` |
| Alertas (HU-06) | `PaginaAlertas` |
| Reportes (HU-09, HU-10) | `PaginaReportes` |
| Reutilizables | `SelectCatalogo`, `Mensaje` |

## Pruebas

`npm test` ejecuta 23 pruebas con la API simulada (no necesita servidor ni base de datos):

- `flujos.test.jsx` (14): login, registro, productos, entradas, salidas (con error 409), alertas, reportes y
  aviso cuando la API no responde.
- `utils.test.js` (9): mensajes de error y funciones de formato.
