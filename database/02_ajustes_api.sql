-- ============================================================
-- Ajustes que necesita la API sobre la base de datos inventario_pae
-- Ejecutar UNA vez en MySQL Workbench, despues de 01_script_bd_proyecto.sql
-- (se puede volver a ejecutar sin problema: no duplica nada)
-- ============================================================
USE inventario_pae;

-- 1) Tabla de usuarios (HU-01 y HU-07): la contrasena se guarda cifrada con BCrypt
CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre_usuario VARCHAR(60) NOT NULL,
  contrasena VARCHAR(100) NOT NULL,
  rol VARCHAR(20) NOT NULL DEFAULT 'USUARIO',
  activo TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY uq_usuarios_nombre (nombre_usuario)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_spanish_ci;

-- 2) Stock minimo por producto (HU-06): se agrega solo si la columna no existe
SET @existe := (SELECT COUNT(*) FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = 'inventario_pae' AND TABLE_NAME = 'producto'
                  AND COLUMN_NAME = 'stock_minimo');
SET @sql := IF(@existe = 0,
               'ALTER TABLE producto ADD COLUMN stock_minimo DECIMAL(10,2) NOT NULL DEFAULT 0',
               'SELECT ''La columna stock_minimo ya existe'' AS aviso');
PREPARE instruccion FROM @sql;
EXECUTE instruccion;
DEALLOCATE PREPARE instruccion;
