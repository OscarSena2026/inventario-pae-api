# API del Sistema de Inventario PAE

**Evidencia:** GA7-220501096-AA5-EV03 · Diseño y desarrollo de servicios web – proyecto
**Programa:** Análisis y Desarrollo de Software (SENA) · Ficha 3235898
**Aprendiz:** Oscar Fabian Estrada Arango

API REST hecha con **Spring Boot + MySQL** para el Sistema de Inventario del Programa de Alimentación Escolar (PAE).
Permite gestionar productos, lotes, proveedores, bodegas y sedes; registrar **entradas** y **salidas** de stock con
actualización automática de existencias; consultar el inventario, generar **alertas** (stock mínimo y vencimiento) y
**reportes**; y administrar usuarios. Los servicios salen de las historias de usuario, el diagrama de clases y la
base de datos `inventario_pae` del proyecto formativo. El registro e inicio de sesión de la EV01 se reutilizan (HU-01).

## Cómo ejecutarlo

1. Tener instalados **Java 17+**, **MySQL 8** y MySQL Workbench.
2. En MySQL Workbench ejecutar, en este orden:
   - `database/01_script_bd_proyecto.sql` (crea la base `inventario_pae` y sus tablas; **borra y recrea** las tablas).
   - `database/02_ajustes_api.sql` (crea la tabla `usuarios` y la columna `stock_minimo`).
3. Crear un usuario de MySQL para la API (reemplazar `TU_CONTRASENA` por una contraseña propia):
   ```sql
   CREATE USER IF NOT EXISTS 'auth_user'@'localhost' IDENTIFIED BY 'TU_CONTRASENA';
   GRANT ALL PRIVILEGES ON inventario_pae.* TO 'auth_user'@'localhost';
   FLUSH PRIVILEGES;
   ```
4. Definir la variable de entorno `DB_PASSWORD` con esa contraseña (la contraseña no se guarda en el repositorio).
5. Ejecutar la clase `InventarioPaeApplication` (botón Run) o `mvn spring-boot:run`.
6. Cuando la consola muestre `Started InventarioPaeApplication`, la API escucha en `http://localhost:8080`.

## Arquitectura en capas

```
src/main/java/co/edu/sena/pae/
├── controller/   endpoints REST: reciben la petición y responden con el código HTTP
├── service/      lógica de negocio y validaciones (reglas del PAE)
├── repository/   acceso a la base de datos (Spring Data JPA)
├── model/        entidades que representan las tablas de inventario_pae
├── dto/          datos de entrada y salida con sus validaciones
├── exception/    excepciones y manejador de errores (400, 404, 409)
├── config/       configuración de CORS para el front-end en React
└── InventarioPaeApplication.java
```

## Historias de usuario cubiertas

| HU | Historia | Servicios |
|---|---|---|
| HU-01 | Autenticarse | `POST /api/auth/registro`, `POST /api/auth/login` |
| HU-02 | Registrar productos | `POST /api/productos` (no permite datos vacíos ni stock mínimo negativo) |
| HU-03 | Registrar entradas de stock | `POST /api/entradas` (suma la existencia, cantidad mayor a cero) |
| HU-04 | Registrar salidas de stock | `POST /api/salidas` (bloquea con 409 si el stock es insuficiente) |
| HU-05 | Consultar inventario con filtros | `GET /api/productos?nombre=&categoria=`, `GET /api/existencias` |
| HU-06 | Alertar stock mínimo | `GET /api/alertas/stock-minimo`, `GET /api/alertas/vencimiento` |
| HU-07 | Gestionar usuarios | `GET /api/usuarios`, `PUT /api/usuarios/{id}/desactivar` |
| HU-08 | Modificar productos | `PUT /api/productos/{id}` |
| HU-09 | Generar reportes | `GET /api/reportes/stock-productos` |
| HU-10 | Exportar datos | `GET /api/reportes/stock-productos/csv` |

## Diseño del servicio: tabla de endpoints

URL base: `http://localhost:8080`

| Método | Ruta | Descripción | Éxito | Errores | HU |
|---|---|---|---|---|---|
| POST | `/api/auth/registro` | Registrar un usuario | 201 | 400 · 409 | HU-01 |
| POST | `/api/auth/login` | Iniciar sesión | 200 | 400 · 401 | HU-01 |
| GET | `/api/usuarios` | Listar usuarios (sin contraseñas) | 200 | — | HU-07 |
| PUT | `/api/usuarios/{id}/desactivar` | Desactivar un usuario | 200 | 404 | HU-07 |
| PUT | `/api/usuarios/{id}/activar` | Activar un usuario | 200 | 404 | HU-07 |
| GET | `/api/municipios` | Listar municipios | 200 | — | Soporte |
| GET | `/api/municipios/{id}` | Consultar municipio por id | 200 | 404 | Soporte |
| POST | `/api/municipios` | Crear municipio | 201 | 400 · 404 · 409 | Soporte |
| PUT | `/api/municipios/{id}` | Actualizar municipio | 200 | 400 · 404 · 409 | Soporte |
| DELETE | `/api/municipios/{id}` | Eliminar municipio | 204 | 404 · 409 | Soporte |
| GET | `/api/empleados` | Listar empleados | 200 | — | Soporte |
| GET | `/api/empleados/{id}` | Consultar empleado por id | 200 | 404 | Soporte |
| POST | `/api/empleados` | Crear empleado | 201 | 400 · 404 | Soporte |
| PUT | `/api/empleados/{id}` | Actualizar empleado | 200 | 400 · 404 | Soporte |
| DELETE | `/api/empleados/{id}` | Eliminar empleado | 204 | 404 · 409 | Soporte |
| GET | `/api/proveedores` | Listar proveedors | 200 | — | HU-03 |
| GET | `/api/proveedores/{id}` | Consultar proveedor por id | 200 | 404 | HU-03 |
| POST | `/api/proveedores` | Crear proveedor | 201 | 400 · 404 · 409 | HU-03 |
| PUT | `/api/proveedores/{id}` | Actualizar proveedor | 200 | 400 · 404 · 409 | HU-03 |
| DELETE | `/api/proveedores/{id}` | Eliminar proveedor | 204 | 404 · 409 | HU-03 |
| GET | `/api/bodegas` | Listar bodegas | 200 | — | HU-03/04 |
| GET | `/api/bodegas/{id}` | Consultar bodega por id | 200 | 404 | HU-03/04 |
| POST | `/api/bodegas` | Crear bodega | 201 | 400 · 404 | HU-03/04 |
| PUT | `/api/bodegas/{id}` | Actualizar bodega | 200 | 400 · 404 | HU-03/04 |
| DELETE | `/api/bodegas/{id}` | Eliminar bodega | 204 | 404 · 409 | HU-03/04 |
| GET | `/api/sedes` | Listar sedes | 200 | — | HU-04 |
| GET | `/api/sedes/{id}` | Consultar sede por id | 200 | 404 | HU-04 |
| POST | `/api/sedes` | Crear sede | 201 | 400 · 404 | HU-04 |
| PUT | `/api/sedes/{id}` | Actualizar sede | 200 | 400 · 404 | HU-04 |
| DELETE | `/api/sedes/{id}` | Eliminar sede | 204 | 404 · 409 | HU-04 |
| GET | `/api/productos` | Listar productos (filtros nombre y categoría) | 200 | — | HU-02/05/08 |
| GET | `/api/productos/{id}` | Consultar producto por id | 200 | 404 | HU-02/05/08 |
| POST | `/api/productos` | Crear producto | 201 | 400 · 404 | HU-02/05/08 |
| PUT | `/api/productos/{id}` | Actualizar producto | 200 | 400 · 404 | HU-02/05/08 |
| DELETE | `/api/productos/{id}` | Eliminar producto | 204 | 404 · 409 | HU-02/05/08 |
| GET | `/api/lotes` | Listar lotes | 200 | — | HU-03 |
| GET | `/api/lotes/{id}` | Consultar lote por id | 200 | 404 | HU-03 |
| POST | `/api/lotes` | Crear lote | 201 | 400 · 404 | HU-03 |
| PUT | `/api/lotes/{id}` | Actualizar lote | 200 | 400 · 404 | HU-03 |
| DELETE | `/api/lotes/{id}` | Eliminar lote | 204 | 404 · 409 | HU-03 |
| GET | `/api/existencias` | Listar existencias (stock por lote y bodega) | 200 | — | HU-05 |
| GET | `/api/existencias/bodega/{idBodega}` | Existencias de una bodega | 200 | 404 | HU-05 |
| GET | `/api/entradas` | Listar entradas | 200 | — | HU-03 |
| GET | `/api/entradas/{id}` | Consultar entrada por id | 200 | 404 | HU-03 |
| POST | `/api/entradas` | Registrar entrada de stock | 201 | 400 · 404 | HU-03 |
| GET | `/api/salidas` | Listar salidas | 200 | — | HU-04 |
| GET | `/api/salidas/{id}` | Consultar salida por id | 200 | 404 | HU-04 |
| POST | `/api/salidas` | Registrar salida de stock | 201 | 400 · 404 · 409 | HU-04 |
| GET | `/api/alertas/stock-minimo` | Productos en nivel crítico | 200 | — | HU-06 |
| GET | `/api/alertas/vencimiento?dias=30` | Lotes por vencer | 200 | 400 | HU-06 |
| GET | `/api/reportes/stock-productos` | Reporte de stock por producto | 200 | — | HU-09 |
| GET | `/api/reportes/stock-productos/csv` | Exportar el reporte a CSV | 200 | — | HU-10 |

## Formato de los errores

Los errores se responden en JSON con el código HTTP correspondiente:

```json
{
  "estado": 400,
  "error": "Bad Request",
  "mensaje": "Datos inválidos",
  "errores": { "nombreProducto": "El nombre del producto es obligatorio" }
}
```

| Código | Cuándo ocurre |
|---|---|
| 400 | Datos vacíos o con formato incorrecto, o se incumple una regla del negocio |
| 401 | Credenciales incorrectas en el inicio de sesión |
| 404 | El registro o una referencia (id) no existe |
| 409 | Dato duplicado, registro con dependencias o stock insuficiente |

## CORS

La API permite peticiones desde el front-end en React (`http://localhost:5173`, puerto por defecto de Vite).
Si el front-end usa otro puerto, se cambia `app.cors.origen` en `application.properties`.

## Estándares de código

Paquetes en minúscula, clases en PascalCase, métodos y variables en camelCase, y comentarios de propósito en cada clase.
La contraseña de los usuarios se guarda con BCrypt y el mensaje de error del login es siempre el mismo.

## Documentación de cada servicio

### Autenticación (HU-01)

#### Registrar usuario

- **Descripción:** Crea un usuario; la contraseña se guarda cifrada con BCrypt. Si no se envía el rol queda como USUARIO.
- **Método y URL:** `POST /api/auth/registro`
- **Body (JSON):**

```json
{
  "usuario": "ana.perez",
  "contrasena": "Clave123*",
  "rol": "USUARIO"
}
```

- **Respuestas:**
  - `201 Created` "Usuario registrado correctamente"
  - `400 Bad Request` usuario vacío, contraseña con menos de 8 caracteres o rol inválido
  - `409 Conflict` "El usuario ya existe"

#### Iniciar sesión

- **Descripción:** Valida usuario y contraseña. Un usuario desactivado no puede iniciar sesión.
- **Método y URL:** `POST /api/auth/login`
- **Body (JSON):**

```json
{
  "usuario": "ana.perez",
  "contrasena": "Clave123*"
}
```

- **Respuestas:**
  - `200 OK` "Autenticación satisfactoria"
  - `400 Bad Request` usuario o contraseña vacíos
  - `401 Unauthorized` "Error en la autenticación" (mismo mensaje si falla el usuario o la contraseña)

### Usuarios (HU-07)

#### Listar usuarios

- **Descripción:** Devuelve los usuarios con su rol y estado. Nunca incluye la contraseña.
- **Método y URL:** `GET /api/usuarios`
- **Respuestas:**
  - `200 OK` lista de usuarios

#### Desactivar usuario

- **Descripción:** Marca al usuario como inactivo; ya no podrá iniciar sesión.
- **Método y URL:** `PUT /api/usuarios/{id}/desactivar`
- **Respuestas:**
  - `200 OK` usuario actualizado
  - `404 Not Found` el usuario no existe

#### Activar usuario

- **Descripción:** Vuelve a habilitar a un usuario desactivado.
- **Método y URL:** `PUT /api/usuarios/{id}/activar`
- **Respuestas:**
  - `200 OK` usuario actualizado
  - `404 Not Found` el usuario no existe

### Municipios — recurso `/api/municipios`

Municipios donde operan las bodegas y las sedes educativas. Catalogo de apoyo para HU-03 y HU-04.

#### Listar municipios

- **Descripción:** Consulta municipios donde operan las bodegas y las sedes educativas.
- **Método y URL:** `GET /api/municipios`
- **Respuestas:**
  - `200 OK` Lista de registros.

#### Consultar municipio por id

- **Descripción:** Devuelve un municipio según su id.
- **Método y URL:** `GET /api/municipios/{id}`
- **Respuestas:**
  - `200 OK` el municipio
  - `404 Not Found` el municipio no existe

#### Crear municipio

- **Descripción:** Registra el municipio.
- **Método y URL:** `POST /api/municipios`
- **Body (JSON):**

```json
{
  "nombreMunicipio": "Montería"
}
```

- **Respuestas:**
  - `201 Created` el municipio creado
  - `400 Bad Request` datos inválidos: nombreMunicipio vacío, campos fuera de rango o con formato incorrecto
  - `409 Conflict` Ya existe un municipio con ese nombre

#### Actualizar municipio

- **Descripción:** Modifica los datos de el municipio.
- **Método y URL:** `PUT /api/municipios/{id}`
- **Body (JSON):**

```json
{
  "nombreMunicipio": "Montería"
}
```

- **Respuestas:**
  - `200 OK` el municipio actualizado
  - `400 Bad Request` datos inválidos
  - `404 Not Found` el municipio o una referencia no existe
  - `409 Conflict` Ya existe un municipio con ese nombre

#### Eliminar municipio

- **Descripción:** Elimina el municipio.
- **Método y URL:** `DELETE /api/municipios/{id}`
- **Respuestas:**
  - `204 No Content` eliminado
  - `404 Not Found` el municipio no existe
  - `409 Conflict` tiene registros relacionados y no puede eliminarse

### Empleados — recurso `/api/empleados`

Personas que registran las entradas y salidas de bodega. Catalogo de apoyo para HU-03 y HU-04.

#### Listar empleados

- **Descripción:** Consulta personas que registran las entradas y salidas de bodega.
- **Método y URL:** `GET /api/empleados`
- **Respuestas:**
  - `200 OK` Lista de registros.

#### Consultar empleado por id

- **Descripción:** Devuelve un empleado según su id.
- **Método y URL:** `GET /api/empleados/{id}`
- **Respuestas:**
  - `200 OK` el empleado
  - `404 Not Found` el empleado no existe

#### Crear empleado

- **Descripción:** Registra el empleado.
- **Método y URL:** `POST /api/empleados`
- **Body (JSON):**

```json
{
  "nombres": "Oscar Fabian Estrada Arango",
  "cargo": "Almacenista",
  "telefono": "3001234567"
}
```

- **Respuestas:**
  - `201 Created` el empleado creado
  - `400 Bad Request` datos inválidos: nombres vacío, campos fuera de rango o con formato incorrecto

#### Actualizar empleado

- **Descripción:** Modifica los datos de el empleado.
- **Método y URL:** `PUT /api/empleados/{id}`
- **Body (JSON):**

```json
{
  "nombres": "Oscar Fabian Estrada Arango",
  "cargo": "Almacenista",
  "telefono": "3001234567"
}
```

- **Respuestas:**
  - `200 OK` el empleado actualizado
  - `400 Bad Request` datos inválidos
  - `404 Not Found` el empleado o una referencia no existe

#### Eliminar empleado

- **Descripción:** Elimina el empleado.
- **Método y URL:** `DELETE /api/empleados/{id}`
- **Respuestas:**
  - `204 No Content` eliminado
  - `404 Not Found` el empleado no existe
  - `409 Conflict` tiene registros relacionados y no puede eliminarse

### Proveedors — recurso `/api/proveedores`

Empresas que suministran los insumos al programa PAE. Se asocian a lotes y entradas (HU-03).

#### Listar proveedors

- **Descripción:** Consulta empresas que suministran los insumos al programa pae.
- **Método y URL:** `GET /api/proveedores`
- **Respuestas:**
  - `200 OK` Lista de registros.

#### Consultar proveedor por id

- **Descripción:** Devuelve un proveedor según su id.
- **Método y URL:** `GET /api/proveedores/{id}`
- **Respuestas:**
  - `200 OK` el proveedor
  - `404 Not Found` el proveedor no existe

#### Crear proveedor

- **Descripción:** Registra el proveedor.
- **Método y URL:** `POST /api/proveedores`
- **Body (JSON):**

```json
{
  "nombreProveedor": "Distribuidora Alimentos del Sinú",
  "nit": "900123456-7",
  "telefono": "3109876543",
  "direccion": "Calle 30 # 12-45"
}
```

- **Respuestas:**
  - `201 Created` el proveedor creado
  - `400 Bad Request` datos inválidos: nombreProveedor vacío, nit vacío, campos fuera de rango o con formato incorrecto
  - `409 Conflict` Ya existe un proveedor con ese NIT

#### Actualizar proveedor

- **Descripción:** Modifica los datos de el proveedor.
- **Método y URL:** `PUT /api/proveedores/{id}`
- **Body (JSON):**

```json
{
  "nombreProveedor": "Distribuidora Alimentos del Sinú",
  "nit": "900123456-7",
  "telefono": "3109876543",
  "direccion": "Calle 30 # 12-45"
}
```

- **Respuestas:**
  - `200 OK` el proveedor actualizado
  - `400 Bad Request` datos inválidos
  - `404 Not Found` el proveedor o una referencia no existe
  - `409 Conflict` Ya existe un proveedor con ese NIT

#### Eliminar proveedor

- **Descripción:** Elimina el proveedor.
- **Método y URL:** `DELETE /api/proveedores/{id}`
- **Respuestas:**
  - `204 No Content` eliminado
  - `404 Not Found` el proveedor no existe
  - `409 Conflict` tiene registros relacionados y no puede eliminarse

### Bodegas — recurso `/api/bodegas`

Bodegas donde se almacenan los insumos. Cada bodega pertenece a un municipio (HU-03, HU-04, HU-05).

#### Listar bodegas

- **Descripción:** Consulta bodegas donde se almacenan los insumos.
- **Método y URL:** `GET /api/bodegas`
- **Respuestas:**
  - `200 OK` Lista de registros.

#### Consultar bodega por id

- **Descripción:** Devuelve un bodega según su id.
- **Método y URL:** `GET /api/bodegas/{id}`
- **Respuestas:**
  - `200 OK` el bodega
  - `404 Not Found` el bodega no existe

#### Crear bodega

- **Descripción:** Registra la bodega.
- **Método y URL:** `POST /api/bodegas`
- **Body (JSON):**

```json
{
  "nombreBodega": "Bodega Central",
  "direccion": "Carrera 5 # 20-10",
  "idMunicipio": 1
}
```

- **Respuestas:**
  - `201 Created` el bodega creado
  - `400 Bad Request` datos inválidos: nombreBodega vacío, campos fuera de rango o con formato incorrecto
  - `404 Not Found` alguna referencia (idMunicipio) no existe

#### Actualizar bodega

- **Descripción:** Modifica los datos de la bodega.
- **Método y URL:** `PUT /api/bodegas/{id}`
- **Body (JSON):**

```json
{
  "nombreBodega": "Bodega Central",
  "direccion": "Carrera 5 # 20-10",
  "idMunicipio": 1
}
```

- **Respuestas:**
  - `200 OK` el bodega actualizado
  - `400 Bad Request` datos inválidos
  - `404 Not Found` el bodega o una referencia no existe

#### Eliminar bodega

- **Descripción:** Elimina la bodega.
- **Método y URL:** `DELETE /api/bodegas/{id}`
- **Respuestas:**
  - `204 No Content` eliminado
  - `404 Not Found` el bodega no existe
  - `409 Conflict` tiene registros relacionados y no puede eliminarse

### Sedes — recurso `/api/sedes`

Sedes educativas beneficiarias del PAE que reciben los despachos (HU-04).

#### Listar sedes

- **Descripción:** Consulta sedes educativas beneficiarias del pae que reciben los despachos (hu-04).
- **Método y URL:** `GET /api/sedes`
- **Respuestas:**
  - `200 OK` Lista de registros.

#### Consultar sede por id

- **Descripción:** Devuelve un sede según su id.
- **Método y URL:** `GET /api/sedes/{id}`
- **Respuestas:**
  - `200 OK` el sede
  - `404 Not Found` el sede no existe

#### Crear sede

- **Descripción:** Registra la sede.
- **Método y URL:** `POST /api/sedes`
- **Body (JSON):**

```json
{
  "nombreSede": "Institución Educativa La Esperanza",
  "direccion": "Vereda El Palmar",
  "idMunicipio": 1
}
```

- **Respuestas:**
  - `201 Created` el sede creado
  - `400 Bad Request` datos inválidos: nombreSede vacío, campos fuera de rango o con formato incorrecto
  - `404 Not Found` alguna referencia (idMunicipio) no existe

#### Actualizar sede

- **Descripción:** Modifica los datos de la sede.
- **Método y URL:** `PUT /api/sedes/{id}`
- **Body (JSON):**

```json
{
  "nombreSede": "Institución Educativa La Esperanza",
  "direccion": "Vereda El Palmar",
  "idMunicipio": 1
}
```

- **Respuestas:**
  - `200 OK` el sede actualizado
  - `400 Bad Request` datos inválidos
  - `404 Not Found` el sede o una referencia no existe

#### Eliminar sede

- **Descripción:** Elimina la sede.
- **Método y URL:** `DELETE /api/sedes/{id}`
- **Respuestas:**
  - `204 No Content` eliminado
  - `404 Not Found` el sede no existe
  - `409 Conflict` tiene registros relacionados y no puede eliminarse

### Productos — recurso `/api/productos`

Insumos alimentarios del inventario PAE: registro (HU-02), consulta con filtros (HU-05) y modificacion (HU-08).

#### Listar productos

- **Descripción:** Consulta insumos alimentarios del inventario pae: registro (hu-02), consulta con filtros (hu-05) y modificacion (hu-08).
- **Método y URL:** `GET /api/productos?nombre=&categoria=`
- **Respuestas:**
  - `200 OK` Lista de productos. Filtros opcionales: `?nombre=arroz` y `?categoria=Cereal`.

#### Consultar producto por id

- **Descripción:** Devuelve un producto según su id.
- **Método y URL:** `GET /api/productos/{id}`
- **Respuestas:**
  - `200 OK` el producto
  - `404 Not Found` el producto no existe

#### Crear producto

- **Descripción:** Registra el producto.
- **Método y URL:** `POST /api/productos`
- **Body (JSON):**

```json
{
  "nombreProducto": "Arroz blanco",
  "categoria": "Cereal",
  "unidadMedida": "kg",
  "perecedero": "NO",
  "stockMinimo": 50
}
```

- **Respuestas:**
  - `201 Created` el producto creado
  - `400 Bad Request` datos inválidos: nombreProducto vacío, categoria vacío, campos fuera de rango o con formato incorrecto

#### Actualizar producto

- **Descripción:** Modifica los datos de el producto.
- **Método y URL:** `PUT /api/productos/{id}`
- **Body (JSON):**

```json
{
  "nombreProducto": "Arroz blanco",
  "categoria": "Cereal",
  "unidadMedida": "kg",
  "perecedero": "NO",
  "stockMinimo": 50
}
```

- **Respuestas:**
  - `200 OK` el producto actualizado
  - `400 Bad Request` datos inválidos
  - `404 Not Found` el producto o una referencia no existe

#### Eliminar producto

- **Descripción:** Elimina el producto.
- **Método y URL:** `DELETE /api/productos/{id}`
- **Respuestas:**
  - `204 No Content` eliminado
  - `404 Not Found` el producto no existe
  - `409 Conflict` tiene registros relacionados y no puede eliminarse

### Lotes — recurso `/api/lotes`

Lotes de producto recibidos de un proveedor, con su fecha de vencimiento (HU-03 y alertas de HU-06).

#### Listar lotes

- **Descripción:** Consulta lotes de producto recibidos de un proveedor, con su fecha de vencimiento (hu-03 y alertas de hu-06).
- **Método y URL:** `GET /api/lotes`
- **Respuestas:**
  - `200 OK` Lista de registros.

#### Consultar lote por id

- **Descripción:** Devuelve un lote según su id.
- **Método y URL:** `GET /api/lotes/{id}`
- **Respuestas:**
  - `200 OK` el lote
  - `404 Not Found` el lote no existe

#### Crear lote

- **Descripción:** Registra el lote.
- **Método y URL:** `POST /api/lotes`
- **Body (JSON):**

```json
{
  "fechaFabricacion": "2026-09-01",
  "fechaVencimiento": "2027-03-01",
  "cantidadRecibida": 200,
  "idProducto": 1,
  "idProveedor": 1
}
```

- **Respuestas:**
  - `201 Created` el lote creado
  - `400 Bad Request` datos inválidos, cantidad no positiva o fecha de vencimiento anterior a la de fabricación
  - `404 Not Found` alguna referencia (idProducto, idProveedor) no existe

#### Actualizar lote

- **Descripción:** Modifica los datos de el lote.
- **Método y URL:** `PUT /api/lotes/{id}`
- **Body (JSON):**

```json
{
  "fechaFabricacion": "2026-09-01",
  "fechaVencimiento": "2027-03-01",
  "cantidadRecibida": 200,
  "idProducto": 1,
  "idProveedor": 1
}
```

- **Respuestas:**
  - `200 OK` el lote actualizado
  - `400 Bad Request` datos inválidos
  - `404 Not Found` el lote o una referencia no existe

#### Eliminar lote

- **Descripción:** Elimina el lote.
- **Método y URL:** `DELETE /api/lotes/{id}`
- **Respuestas:**
  - `204 No Content` eliminado
  - `404 Not Found` el lote no existe
  - `409 Conflict` tiene registros relacionados y no puede eliminarse

### Existencias (HU-05)

Stock disponible por lote y bodega. Se actualiza solo con cada entrada y salida.

#### Listar existencias

- **Descripción:** Devuelve el stock de todos los lotes en todas las bodegas.
- **Método y URL:** `GET /api/existencias`
- **Respuestas:**
  - `200 OK` lista de existencias

#### Existencias de una bodega

- **Descripción:** Devuelve el stock de una bodega.
- **Método y URL:** `GET /api/existencias/bodega/{idBodega}`
- **Respuestas:**
  - `200 OK` lista de existencias
  - `404 Not Found` la bodega no existe

### Entradas de stock (HU-03)

Registra el ingreso de productos a una bodega. Suma la cantidad a la existencia y guarda el movimiento. Las entradas no se modifican ni se eliminan (trazabilidad).

#### Listar entradas

- **Descripción:** Devuelve todas las entradas con sus productos.
- **Método y URL:** `GET /api/entradas`
- **Respuestas:**
  - `200 OK` lista de entradas

#### Consultar entrada por id

- **Descripción:** Devuelve una entrada con su detalle.
- **Método y URL:** `GET /api/entradas/{id}`
- **Respuestas:**
  - `200 OK` la entrada
  - `404 Not Found` la entrada no existe

#### Registrar entrada de stock

- **Descripción:** Crea la entrada, guarda su detalle y suma las cantidades a la existencia de la bodega.
- **Método y URL:** `POST /api/entradas`
- **Body (JSON):**

```json
{
  "fechaEntrada": "2026-10-19",
  "numeroFactura": "FAC-1001",
  "idBodega": 1,
  "idProveedor": 1,
  "idEmpleado": 1,
  "detalles": [
    {
      "idLote": 1,
      "cantidad": 100,
      "valorUnitario": 3500
    }
  ]
}
```

- **Respuestas:**
  - `201 Created` la entrada registrada con su detalle
  - `400 Bad Request` fecha futura, cantidad menor o igual a cero, sin productos o lote repetido
  - `404 Not Found` bodega, proveedor, empleado o lote inexistente

### Salidas de stock (HU-04)

Registra el despacho de productos a una sede. Verifica que haya stock suficiente y descuenta la existencia. Las salidas no se modifican ni se eliminan (trazabilidad).

#### Listar salidas

- **Descripción:** Devuelve todas las salidas con sus productos.
- **Método y URL:** `GET /api/salidas`
- **Respuestas:**
  - `200 OK` lista de salidas

#### Consultar salida por id

- **Descripción:** Devuelve una salida con su detalle.
- **Método y URL:** `GET /api/salidas/{id}`
- **Respuestas:**
  - `200 OK` la salida
  - `404 Not Found` la salida no existe

#### Registrar salida de stock

- **Descripción:** Valida el stock, crea la salida y descuenta las cantidades de la existencia.
- **Método y URL:** `POST /api/salidas`
- **Body (JSON):**

```json
{
  "fechaSalida": "2026-10-19",
  "motivo": "Despacho semanal",
  "idBodega": 1,
  "idSede": 1,
  "idEmpleado": 1,
  "detalles": [
    {
      "idLote": 1,
      "cantidad": 40
    }
  ]
}
```

- **Respuestas:**
  - `201 Created` la salida registrada con su detalle
  - `400 Bad Request` fecha futura, cantidad menor o igual a cero, lote repetido o lote vencido
  - `404 Not Found` bodega, sede, empleado o lote inexistente
  - `409 Conflict` stock insuficiente: la cantidad supera lo disponible

### Alertas (HU-06)

#### Alerta de stock mínimo

- **Descripción:** Lista los productos cuyo stock total es menor o igual a su stock mínimo.
- **Método y URL:** `GET /api/alertas/stock-minimo`
- **Respuestas:**
  - `200 OK` lista de alertas (vacía si todos están sobre el mínimo)

#### Alerta de vencimiento

- **Descripción:** Lista los lotes con existencias que vencen dentro de los próximos días indicados (por defecto 30) o que ya vencieron.
- **Método y URL:** `GET /api/alertas/vencimiento?dias=30`
- **Respuestas:**
  - `200 OK` lista de alertas
  - `400 Bad Request` dias menor o igual a cero

### Reportes (HU-09 y HU-10)

#### Reporte de stock por producto

- **Descripción:** Muestra el stock actual de cada producto y su estado (NORMAL o CRITICO). Filtro opcional `?categoria=Cereal`.
- **Método y URL:** `GET /api/reportes/stock-productos`
- **Respuestas:**
  - `200 OK` lista con stock actual por producto

#### Exportar reporte a CSV

- **Descripción:** Genera el mismo reporte como archivo CSV con encabezados claros.
- **Método y URL:** `GET /api/reportes/stock-productos/csv`
- **Respuestas:**
  - `200 OK` archivo stock_productos.csv
