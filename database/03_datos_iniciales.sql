-- ============================================================
-- Datos iniciales (catalogos) para la base inventario_pae en la nube
-- Ejecutar UNA sola vez en MySQL Workbench, conexion "Railway PAE",
-- despues de 01_script_bd_proyecto.sql y 02_ajustes_api.sql
-- Datos de ejemplo para demostracion (no son datos reales)
-- ============================================================
USE inventario_pae;

INSERT INTO municipio (nombre_municipio) VALUES
  ('Monteria'), ('Lorica'), ('Cerete');

INSERT INTO bodega (nombre_bodega, direccion, id_municipio) VALUES
  ('Bodega Central PAE Monteria', 'Calle 30 # 10-25',
     (SELECT id_municipio FROM municipio WHERE nombre_municipio = 'Monteria')),
  ('Bodega PAE Lorica', 'Carrera 12 # 5-40',
     (SELECT id_municipio FROM municipio WHERE nombre_municipio = 'Lorica'));

INSERT INTO sede (nombre_sede, direccion, id_municipio) VALUES
  ('Institucion Educativa Los Almendros', 'Barrio Los Almendros',
     (SELECT id_municipio FROM municipio WHERE nombre_municipio = 'Monteria')),
  ('Institucion Educativa San Jose', 'Zona rural vereda San Jose',
     (SELECT id_municipio FROM municipio WHERE nombre_municipio = 'Cerete'));

INSERT INTO empleado (nombres, cargo, telefono) VALUES
  ('Oscar Fabian Estrada Arango', 'Almacenista', '3000000001'),
  ('Maria Fernanda Lopez', 'Auxiliar de bodega', '3000000002');

INSERT INTO proveedor (nombre_proveedor, nit, telefono, direccion) VALUES
  ('Distribuidora Alimentos del Sinu', '900100200-1', '3000000010', 'Calle 25 # 3-10 Monteria'),
  ('Lacteos y Granos de Cordoba', '900300400-2', '3000000011', 'Carrera 8 # 12-30 Lorica');

-- Verificacion rapida
SELECT 'municipio' AS tabla, COUNT(*) AS filas FROM municipio
UNION ALL SELECT 'bodega', COUNT(*) FROM bodega
UNION ALL SELECT 'sede', COUNT(*) FROM sede
UNION ALL SELECT 'empleado', COUNT(*) FROM empleado
UNION ALL SELECT 'proveedor', COUNT(*) FROM proveedor;
