-- ============================================================
-- Lotes de demostracion para inventario_pae (nube)
-- Ejecutar UNA sola vez en MySQL Workbench, conexion "Railway PAE"
-- Requiere que ya existan los productos Arroz, Lenteja y Leche en polvo
-- ============================================================
USE inventario_pae;

INSERT INTO lote (fecha_fabricacion, fecha_vencimiento, cantidad_recibida, id_producto, id_proveedor)
VALUES
  ('2026-11-01', '2027-05-20', 500.00,
     (SELECT id_producto FROM producto WHERE nombre_producto = 'Arroz' LIMIT 1),
     (SELECT id_proveedor FROM proveedor WHERE nit = '900100200-1')),
  ('2026-11-01', '2027-05-20', 500.00,
     (SELECT id_producto FROM producto WHERE nombre_producto = 'Lenteja' LIMIT 1),
     (SELECT id_proveedor FROM proveedor WHERE nit = '900100200-1')),
  ('2026-10-15', '2026-11-27', 100.00,
     (SELECT id_producto FROM producto WHERE nombre_producto = 'Leche en polvo' LIMIT 1),
     (SELECT id_proveedor FROM proveedor WHERE nit = '900100200-1'));

-- Verificacion
SELECT l.id_lote, p.nombre_producto, l.fecha_vencimiento, l.cantidad_recibida
FROM lote l JOIN producto p ON p.id_producto = l.id_producto;
