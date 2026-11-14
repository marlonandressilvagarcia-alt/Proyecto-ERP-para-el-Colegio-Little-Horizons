-- ============================================================================
-- Little Horizons ERP - Sistema de Gestión Educativa
-- Archivo: docs/database.sql
-- Motor: MySQL 8.0 o superior (REQUERIDO por CHECK constraints y utf8mb4)
-- Descripción: Script completo DDL (Estructura) y DML (Datos de prueba con >30 registros)
-- ============================================================================

DROP DATABASE IF EXISTS erp_little_horizons;
CREATE DATABASE erp_little_horizons CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE erp_little_horizons;

-- ============================================================================
-- SECCIÓN 1: DDL (DATA DEFINITION LANGUAGE)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. MÓDULO DE ESTUDIANTES Y ACUDIENTES
-- ----------------------------------------------------------------------------

-- Tabla de grados académicos
CREATE TABLE grados (
    id_grado INT AUTO_INCREMENT PRIMARY KEY,
    nombre_grado VARCHAR(50) NOT NULL UNIQUE COMMENT 'Ej: Preescolar, Primero, Segundo, ... Once',
    nivel_numerico INT NOT NULL UNIQUE COMMENT 'Nivel numérico para ordenamiento (0 para preescolar, 1-11 para grados)',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Catálogo de grados académicos del colegio';

-- Tabla de estudiantes
CREATE TABLE estudiantes (
    id_estudiante INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento ENUM('TI', 'CC', 'CE', 'Pasaporte') NOT NULL DEFAULT 'TI',
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    primer_nombre VARCHAR(100) NOT NULL,
    segundo_nombre VARCHAR(100),
    primer_apellido VARCHAR(100) NOT NULL,
    segundo_apellido VARCHAR(100),
    fecha_nacimiento DATE NOT NULL,
    genero ENUM('M', 'F', 'Otro') NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    correo_electronico VARCHAR(100),
    tipo_sangre VARCHAR(5),
    alergias TEXT,
    condiciones_medicas TEXT,
    fecha_ingreso DATE NOT NULL,
    estado ENUM('Activo', 'Graduado', 'Retirado', 'Suspendido') DEFAULT 'Activo',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_documento (tipo_documento, numero_documento),
    INDEX idx_estado (estado),
    INDEX idx_nombre (primer_apellido, segundo_apellido, primer_nombre)
) ENGINE=InnoDB COMMENT='Información completa de estudiantes';

-- Tabla de acudientes/tutores legales
CREATE TABLE acudientes (
    id_acudiente INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento ENUM('CC', 'CE', 'Pasaporte') NOT NULL DEFAULT 'CC',
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    primer_nombre VARCHAR(100) NOT NULL,
    segundo_nombre VARCHAR(100),
    primer_apellido VARCHAR(100) NOT NULL,
    segundo_apellido VARCHAR(100),
    parentesco VARCHAR(50) NOT NULL COMMENT 'Padre, Madre, Abuelo, Tío, Tutor Legal, etc.',
    telefono_principal VARCHAR(20) NOT NULL,
    telefono_secundario VARCHAR(20),
    correo_electronico VARCHAR(100),
    direccion VARCHAR(255),
    ocupacion VARCHAR(100),
    es_responsable_financiero BOOLEAN DEFAULT TRUE COMMENT 'Indica si es responsable de pagos',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_documento (tipo_documento, numero_documento),
    INDEX idx_nombre (primer_apellido, segundo_apellido)
) ENGINE=InnoDB COMMENT='Acudientes y tutores legales';

-- Relación estudiantes-acudientes (muchos a muchos)
CREATE TABLE estudiante_acudiente (
    id_estudiante INT NOT NULL,
    id_acudiente INT NOT NULL,
    es_principal BOOLEAN DEFAULT FALSE COMMENT 'Acudiente principal para comunicaciones',
    es_contacto_emergencia BOOLEAN DEFAULT TRUE COMMENT 'Disponible para emergencias',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_estudiante, id_acudiente),
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    FOREIGN KEY (id_acudiente) REFERENCES acudientes(id_acudiente) ON DELETE CASCADE,
    INDEX idx_estudiante (id_estudiante),
    INDEX idx_acudiente (id_acudiente)
) ENGINE=InnoDB COMMENT='Relación entre estudiantes y sus acudientes';

-- Tabla de matrículas (historial de grados por año)
CREATE TABLE matriculas (
    id_matricula INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_grado INT NOT NULL,
    año_academico INT NOT NULL COMMENT 'Año académico (ej: 2024)',
    estado_matricula ENUM('Activa', 'Completada', 'Retirada') DEFAULT 'Activa',
    fecha_matricula DATE NOT NULL,
    fecha_retiro DATE,
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    FOREIGN KEY (id_grado) REFERENCES grados(id_grado),
    UNIQUE KEY matricula_unica (id_estudiante, año_academico),
    INDEX idx_año_grado (año_academico, id_grado),
    INDEX idx_estado (estado_matricula)
) ENGINE=InnoDB COMMENT='Historial de matrículas de estudiantes por año y grado';

-- ----------------------------------------------------------------------------
-- 2. MÓDULO ACADÉMICO
-- ----------------------------------------------------------------------------

-- Tabla de materias/asignaturas
CREATE TABLE materias (
    id_materia INT AUTO_INCREMENT PRIMARY KEY,
    codigo_materia VARCHAR(20) NOT NULL UNIQUE COMMENT 'Código único de la materia (ej: MAT-5, ESP-3)',
    nombre_materia VARCHAR(100) NOT NULL,
    descripcion TEXT,
    id_grado INT NOT NULL COMMENT 'Grado al que pertenece la materia',
    horas_semanales INT DEFAULT 2 COMMENT 'Horas semanales asignadas',
    esta_activa BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_grado) REFERENCES grados(id_grado),
    INDEX idx_grado (id_grado),
    INDEX idx_activa (esta_activa),
    INDEX idx_codigo (codigo_materia)
) ENGINE=InnoDB COMMENT='Catálogo de materias por grado';

-- Tabla de profesores
CREATE TABLE profesores (
    id_profesor INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento ENUM('CC', 'CE', 'Pasaporte') NOT NULL DEFAULT 'CC',
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    primer_nombre VARCHAR(100) NOT NULL,
    segundo_nombre VARCHAR(100),
    primer_apellido VARCHAR(100) NOT NULL,
    segundo_apellido VARCHAR(100),
    correo_electronico VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    especializacion VARCHAR(100) COMMENT 'Área de especialización (Matemáticas, Español, etc.)',
    fecha_contratacion DATE NOT NULL,
    estado ENUM('Activo', 'Inactivo', 'Licencia') DEFAULT 'Activo',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_documento (tipo_documento, numero_documento),
    INDEX idx_estado (estado),
    INDEX idx_nombre (primer_apellido, segundo_apellido)
) ENGINE=InnoDB COMMENT='Información de profesores';

-- Asignación de materias a profesores
CREATE TABLE asignaciones_profesor (
    id_asignacion INT AUTO_INCREMENT PRIMARY KEY,
    id_profesor INT NOT NULL,
    id_materia INT NOT NULL,
    año_academico INT NOT NULL,
    seccion VARCHAR(10) COMMENT 'Sección o grupo (A, B, C)',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_profesor) REFERENCES profesores(id_profesor) ON DELETE CASCADE,
    FOREIGN KEY (id_materia) REFERENCES materias(id_materia) ON DELETE CASCADE,
    UNIQUE KEY asignacion_unica (id_profesor, id_materia, año_academico, seccion),
    INDEX idx_profesor_año (id_profesor, año_academico),
    INDEX idx_materia_año (id_materia, año_academico)
) ENGINE=InnoDB COMMENT='Asignación de profesores a materias por año académico';

-- Inscripción de estudiantes en materias
CREATE TABLE inscripciones_materia (
    id_inscripcion INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_materia INT NOT NULL,
    año_academico INT NOT NULL,
    periodo_academico ENUM('I', 'II', 'III', 'IV') NOT NULL COMMENT 'Período académico',
    seccion VARCHAR(10) COMMENT 'Sección o grupo asignado',
    fecha_inscripcion DATE NOT NULL,
    estado ENUM('Activa', 'Retirada', 'Completada') DEFAULT 'Activa',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    FOREIGN KEY (id_materia) REFERENCES materias(id_materia) ON DELETE CASCADE,
    UNIQUE KEY inscripcion_unica (id_estudiante, id_materia, año_academico, periodo_academico),
    INDEX idx_estudiante_año (id_estudiante, año_academico),
    INDEX idx_materia_periodo (id_materia, periodo_academico)
) ENGINE=InnoDB COMMENT='Inscripción de estudiantes en materias específicas';

-- Tabla de registros académicos (calificaciones/notas)
CREATE TABLE registros_academicos (
    id_registro INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_materia INT NOT NULL,
    id_profesor INT NOT NULL,
    año_academico INT NOT NULL,
    periodo_academico ENUM('I', 'II', 'III', 'IV', 'Final') NOT NULL,
    valor_nota DECIMAL(2,1) NOT NULL COMMENT 'Nota de 0.0 a 5.0 según estándar colombiano',
    observaciones TEXT,
    fecha_registro DATE NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    FOREIGN KEY (id_materia) REFERENCES materias(id_materia) ON DELETE CASCADE,
    FOREIGN KEY (id_profesor) REFERENCES profesores(id_profesor),
    CHECK (valor_nota >= 0.0 AND valor_nota <= 5.0),
    UNIQUE KEY registro_unico (id_estudiante, id_materia, año_academico, periodo_academico),
    INDEX idx_estudiante_año (id_estudiante, año_academico),
    INDEX idx_materia_periodo (id_materia, periodo_academico),
    INDEX idx_valor_nota (valor_nota)
) ENGINE=InnoDB COMMENT='Registro de calificaciones de estudiantes';

-- Tabla de asistencia
CREATE TABLE asistencias (
    id_asistencia INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_materia INT NOT NULL,
    fecha_asistencia DATE NOT NULL,
    estado_asistencia ENUM('Presente', 'Ausente', 'Tardanza', 'Excusa Médica') NOT NULL,
    observaciones TEXT,
    registrado_por INT COMMENT 'ID del profesor que registró la asistencia',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    FOREIGN KEY (id_materia) REFERENCES materias(id_materia) ON DELETE CASCADE,
    FOREIGN KEY (registrado_por) REFERENCES profesores(id_profesor),
    UNIQUE KEY asistencia_unica (id_estudiante, id_materia, fecha_asistencia),
    INDEX idx_estudiante_fecha (id_estudiante, fecha_asistencia),
    INDEX idx_materia_fecha (id_materia, fecha_asistencia),
    INDEX idx_estado (estado_asistencia)
) ENGINE=InnoDB COMMENT='Registro de asistencia diaria de estudiantes';

-- ----------------------------------------------------------------------------
-- 3. MÓDULO DE ACTIVIDADES EXTRACURRICULARES
-- ----------------------------------------------------------------------------

-- Tabla de clubes
CREATE TABLE clubes (
    id_club INT AUTO_INCREMENT PRIMARY KEY,
    nombre_club VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    id_profesor_supervisor INT COMMENT 'Profesor encargado del club',
    capacidad_maxima INT DEFAULT 30 COMMENT 'Cupo máximo de estudiantes',
    horario_reunion VARCHAR(100) COMMENT 'Días y horario de reunión (ej: Martes 3-5 PM)',
    esta_activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_profesor_supervisor) REFERENCES profesores(id_profesor),
    INDEX idx_activo (esta_activo),
    INDEX idx_nombre (nombre_club)
) ENGINE=InnoDB COMMENT='Catálogo de clubes extracurriculares';

-- Membresías en clubes
CREATE TABLE membresias_club (
    id_membresia INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_club INT NOT NULL,
    fecha_ingreso DATE NOT NULL,
    estado_membresia ENUM('Activa', 'Retirada') DEFAULT 'Activa',
    fecha_retiro DATE,
    motivo_retiro TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    FOREIGN KEY (id_club) REFERENCES clubes(id_club) ON DELETE CASCADE,
    INDEX idx_estudiante (id_estudiante),
    INDEX idx_club_estado (id_club, estado_membresia)
) ENGINE=InnoDB COMMENT='Membresías de estudiantes en clubes';

-- Tabla de eventos
CREATE TABLE eventos (
    id_evento INT AUTO_INCREMENT PRIMARY KEY,
    nombre_evento VARCHAR(150) NOT NULL,
    descripcion TEXT,
    tipo_evento ENUM('Académico', 'Cultural', 'Deportivo', 'Social', 'Científico') NOT NULL,
    fecha_evento DATE NOT NULL,
    lugar VARCHAR(150),
    tiene_competencia BOOLEAN DEFAULT FALSE COMMENT 'Indica si el evento tiene competencia con posiciones',
    id_organizador INT COMMENT 'Profesor organizador del evento',
    esta_activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_organizador) REFERENCES profesores(id_profesor),
    INDEX idx_fecha (fecha_evento),
    INDEX idx_tipo (tipo_evento),
    INDEX idx_activo (esta_activo)
) ENGINE=InnoDB COMMENT='Catálogo de eventos del colegio';

-- Participantes en eventos
CREATE TABLE participantes_evento (
    id_participacion INT AUTO_INCREMENT PRIMARY KEY,
    id_evento INT NOT NULL,
    id_estudiante INT NOT NULL,
    rol_participacion ENUM('Participante', 'Competidor', 'Organizador', 'Asistente') DEFAULT 'Participante',
    posicion_obtenida VARCHAR(20) COMMENT 'Posición obtenida si hay competencia (1er Lugar, 2do Lugar, 3er Lugar, Participante)',
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_evento) REFERENCES eventos(id_evento) ON DELETE CASCADE,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    UNIQUE KEY participacion_unica (id_evento, id_estudiante),
    INDEX idx_evento (id_evento),
    INDEX idx_estudiante (id_estudiante)
) ENGINE=InnoDB COMMENT='Registro de participación de estudiantes en eventos';

-- ----------------------------------------------------------------------------
-- 4. MÓDULO FINANCIERO
-- ----------------------------------------------------------------------------

-- Conceptos de pago (catálogo)
CREATE TABLE conceptos_pago (
    id_concepto INT AUTO_INCREMENT PRIMARY KEY,
    codigo_concepto VARCHAR(20) NOT NULL UNIQUE,
    nombre_concepto VARCHAR(100) NOT NULL,
    descripcion TEXT,
    monto_base DECIMAL(10,2) NOT NULL COMMENT 'Valor base del concepto en pesos colombianos',
    es_recurrente BOOLEAN DEFAULT FALSE COMMENT 'Indica si es un pago recurrente (ej: pensión mensual)',
    esta_activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_activo (esta_activo),
    INDEX idx_codigo (codigo_concepto)
) ENGINE=InnoDB COMMENT='Catálogo de conceptos de pago del colegio';

-- Tabla de pagos
CREATE TABLE pagos (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_acudiente INT COMMENT 'Acudiente que realizó el pago',
    id_concepto INT NOT NULL,
    monto_pagado DECIMAL(10,2) NOT NULL,
    fecha_pago DATE NOT NULL,
    metodo_pago ENUM('Efectivo', 'Transferencia', 'Tarjeta Débito', 'Tarjeta Crédito', 'Cheque') NOT NULL,
    numero_recibo VARCHAR(50) UNIQUE,
    año_academico INT COMMENT 'Año al que aplica el pago',
    periodo_academico ENUM('I', 'II', 'III', 'IV', 'Anual') COMMENT 'Período al que aplica el pago',
    notas TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    FOREIGN KEY (id_acudiente) REFERENCES acudientes(id_acudiente),
    FOREIGN KEY (id_concepto) REFERENCES conceptos_pago(id_concepto),
    INDEX idx_estudiante_fecha (id_estudiante, fecha_pago),
    INDEX idx_concepto (id_concepto),
    INDEX idx_fecha (fecha_pago),
    INDEX idx_recibo (numero_recibo)
) ENGINE=InnoDB COMMENT='Registro de todos los pagos realizados';

-- Tabla de facturas electrónicas (Formato TXT)
CREATE TABLE facturas (
    id_factura INT AUTO_INCREMENT PRIMARY KEY,
    numero_factura VARCHAR(50) NOT NULL UNIQUE COMMENT 'Número consecutivo de factura',
    id_pago INT NOT NULL COMMENT 'Pago asociado a esta factura',
    fecha_emision DATE NOT NULL,
    cufe VARCHAR(100) COMMENT 'Código Único de Factura Electrónica (simulado)',
    nombre_archivo_txt VARCHAR(255) COMMENT 'Nombre del archivo TXT generado',
    ruta_archivo VARCHAR(500) COMMENT 'Ruta completa donde se guardó el TXT',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pago) REFERENCES pagos(id_pago) ON DELETE CASCADE,
    INDEX idx_numero_factura (numero_factura),
    INDEX idx_fecha_emision (fecha_emision),
    INDEX idx_pago (id_pago)
) ENGINE=InnoDB COMMENT='Registro de facturas electrónicas generadas en formato TXT';

-- ----------------------------------------------------------------------------
-- 5. MÓDULO DE GESTIÓN DE RECURSOS
-- ----------------------------------------------------------------------------

-- Categorías de recursos
CREATE TABLE categorias_recurso (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Categorías de recursos (Libro, Equipo de Laboratorio, etc.)';

-- Tabla de recursos del colegio
CREATE TABLE recursos (
    id_recurso INT AUTO_INCREMENT PRIMARY KEY,
    codigo_recurso VARCHAR(50) NOT NULL UNIQUE COMMENT 'Código de inventario único',
    nombre_recurso VARCHAR(150) NOT NULL,
    id_categoria INT NOT NULL,
    descripcion TEXT,
    fecha_adquisicion DATE,
    ubicacion_fisica VARCHAR(100) COMMENT 'Ubicación física (Biblioteca, Laboratorio 1, etc.)',
    estado_recurso ENUM('Disponible', 'Prestado', 'En Mantenimiento', 'Dado de Baja') DEFAULT 'Disponible',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_categoria) REFERENCES categorias_recurso(id_categoria),
    INDEX idx_codigo (codigo_recurso),
    INDEX idx_estado (estado_recurso),
    INDEX idx_categoria (id_categoria),
    INDEX idx_nombre (nombre_recurso)
) ENGINE=InnoDB COMMENT='Inventario de recursos prestables del colegio';

-- Tabla de préstamos de recursos a ESTUDIANTES
CREATE TABLE prestamos_estudiante (
    id_prestamo INT AUTO_INCREMENT PRIMARY KEY,
    id_recurso INT NOT NULL,
    id_estudiante INT NOT NULL COMMENT 'Estudiante que toma prestado el recurso',
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion_esperada DATE NOT NULL,
    fecha_devolucion_real DATE,
    estado_prestamo ENUM('Activo', 'Devuelto', 'Vencido') DEFAULT 'Activo',
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_recurso) REFERENCES recursos(id_recurso) ON DELETE CASCADE,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    INDEX idx_recurso_estado (id_recurso, estado_prestamo),
    INDEX idx_estudiante (id_estudiante),
    INDEX idx_estado (estado_prestamo),
    INDEX idx_fecha_devolucion (fecha_devolucion_esperada)
) ENGINE=InnoDB COMMENT='Registro de préstamos de recursos a estudiantes';

-- Tabla de préstamos de recursos a PROFESORES
CREATE TABLE prestamos_profesor (
    id_prestamo INT AUTO_INCREMENT PRIMARY KEY,
    id_recurso INT NOT NULL,
    id_profesor INT NOT NULL COMMENT 'Profesor que toma prestado el recurso',
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion_esperada DATE NOT NULL,
    fecha_devolucion_real DATE,
    estado_prestamo ENUM('Activo', 'Devuelto', 'Vencido') DEFAULT 'Activo',
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_recurso) REFERENCES recursos(id_recurso) ON DELETE CASCADE,
    FOREIGN KEY (id_profesor) REFERENCES profesores(id_profesor) ON DELETE CASCADE,
    INDEX idx_recurso_estado (id_recurso, estado_prestamo),
    INDEX idx_profesor (id_profesor),
    INDEX idx_estado (estado_prestamo),
    INDEX idx_fecha_devolucion (fecha_devolucion_esperada)
) ENGINE=InnoDB COMMENT='Registro de préstamos de recursos a profesores';


-- ============================================================================
-- SECCIÓN 2: DML (DATA MANIPULATION LANGUAGE)
-- Mínimo 30 registros en cada tabla principal y consistencia referencial
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. TABLA: grados (12 Registros - Preescolar a Once)
-- ----------------------------------------------------------------------------
INSERT INTO grados (id_grado, nombre_grado, nivel_numerico) VALUES
(1, 'Preescolar', 0),
(2, 'Primero', 1),
(3, 'Segundo', 2),
(4, 'Tercero', 3),
(5, 'Cuarto', 4),
(6, 'Quinto', 5),
(7, 'Sexto', 6),
(8, 'Séptimo', 7),
(9, 'Octavo', 8),
(10, 'Noveno', 9),
(11, 'Décimo', 10),
(12, 'Once', 11);

-- ----------------------------------------------------------------------------
-- 2. TABLA: estudiantes (35 Registros - Mínimo 30 requerido)
-- ----------------------------------------------------------------------------
INSERT INTO estudiantes (id_estudiante, tipo_documento, numero_documento, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, fecha_nacimiento, genero, direccion, telefono, correo_electronico, tipo_sangre, alergias, condiciones_medicas, fecha_ingreso, estado) VALUES
(1, 'TI', '1012345671', 'Santiago', 'Andrés', 'Gómez', 'Martínez', '2008-03-15', 'M', 'Calle 134 # 19-45, Bogotá', '3102345671', 'sgomez@littlehorizons.edu.co', 'O+', 'Ninguna', 'Ninguna', '2020-02-01', 'Activo'),
(2, 'TI', '1012345672', 'Valentina', 'Sofía', 'Rodríguez', 'Pérez', '2008-07-22', 'F', 'Carrera 15 # 106-32, Bogotá', '3112345672', 'vrodriguez@littlehorizons.edu.co', 'A+', 'Polen', 'Rinitis', '2020-02-01', 'Activo'),
(3, 'TI', '1012345673', 'Mateo', 'Alejandro', 'López', 'García', '2009-01-10', 'M', 'Calle 140 # 11-20, Bogotá', '3122345673', 'mlopez@littlehorizons.edu.co', 'B+', 'Ninguna', 'Ninguna', '2021-02-01', 'Activo'),
(4, 'TI', '1012345674', 'Isabella', 'María', 'Hernández', 'Torres', '2009-05-18', 'F', 'Carrera 7 # 127-10, Bogotá', '3132345674', 'ihernandez@littlehorizons.edu.co', 'O-', 'Penicilina', 'Asma leve', '2021-02-01', 'Activo'),
(5, 'TI', '1012345675', 'Samuel', 'David', 'González', 'Ramírez', '2010-09-05', 'M', 'Calle 150 # 16-50, Bogotá', '3142345675', 'sgonzalez@littlehorizons.edu.co', 'AB+', 'Maní', 'Ninguna', '2022-02-01', 'Activo'),
(6, 'TI', '1012345676', 'Mariana', 'Lucía', 'García', 'Vargas', '2010-11-30', 'F', 'Carrera 9 # 116-25, Bogotá', '3152345676', 'mgarcia@littlehorizons.edu.co', 'O+', 'Ninguna', 'Ninguna', '2022-02-01', 'Activo'),
(7, 'TI', '1012345677', 'Sebastián', 'Felipe', 'Martínez', 'Castro', '2011-04-12', 'M', 'Calle 116 # 15-40, Bogotá', '3162345677', 'smartinez@littlehorizons.edu.co', 'A-', 'Mariscos', 'Ninguna', '2022-02-01', 'Activo'),
(8, 'TI', '1012345678', 'Camila', 'Andrea', 'Pérez', 'Morales', '2011-08-25', 'F', 'Carrera 19 # 134-80, Bogotá', '3172345678', 'cperez@littlehorizons.edu.co', 'O+', 'Ninguna', 'Miopía', '2022-02-01', 'Activo'),
(9, 'TI', '1012345679', 'Nicolás', 'Esteban', 'Álvarez', 'Ortiz', '2012-02-14', 'M', 'Calle 122 # 18-35, Bogotá', '3182345679', 'nalvarez@littlehorizons.edu.co', 'B+', 'Ninguna', 'Ninguna', '2023-02-01', 'Activo'),
(10, 'TI', '1012345680', 'Sofía', 'Elena', 'Torres', 'Gutiérrez', '2012-06-03', 'F', 'Carrera 11 # 100-15, Bogotá', '3192345680', 'storres@littlehorizons.edu.co', 'A+', 'Ninguna', 'Ninguna', '2023-02-01', 'Activo'),
(11, 'TI', '1012345681', 'Alejandro', 'José', 'Ramírez', 'Herrera', '2013-10-19', 'M', 'Calle 127 # 7-60, Bogotá', '3202345681', 'aramirez@littlehorizons.edu.co', 'O+', 'Ninguna', 'Ninguna', '2023-02-01', 'Activo'),
(12, 'TI', '1012345682', 'Luciana', 'Victoria', 'Díaz', 'Medina', '2013-12-08', 'F', 'Carrera 13 # 119-42, Bogotá', '3212345682', 'ldiaz@littlehorizons.edu.co', 'O+', 'Polvo', 'Rinitis', '2023-02-01', 'Activo'),
(13, 'TI', '1012345683', 'Daniel', 'Eduardo', 'Castro', 'Silva', '2014-03-27', 'M', 'Calle 142 # 19A-12, Bogotá', '3222345683', 'dcastro@littlehorizons.edu.co', 'B-', 'Ninguna', 'Ninguna', '2024-02-01', 'Activo'),
(14, 'TI', '1012345684', 'Gabriela', 'Paola', 'Vargas', 'Rojas', '2014-07-14', 'F', 'Carrera 15 # 124-55, Bogotá', '3232345684', 'gvargas@littlehorizons.edu.co', 'A+', 'Ninguna', 'Ninguna', '2024-02-01', 'Activo'),
(15, 'TI', '1012345685', 'Emilio', 'Tomás', 'Morales', 'Jiménez', '2015-01-21', 'M', 'Calle 138 # 12-68, Bogotá', '3242345685', 'emorales@littlehorizons.edu.co', 'O+', 'Lactosa', 'Intolerancia lactosa', '2024-02-01', 'Activo'),
(16, 'TI', '1012345686', 'Salomé', 'Beatriz', 'Ortiz', 'Navarro', '2015-05-11', 'F', 'Carrera 18 # 140-30, Bogotá', '3252345686', 'sortiz@littlehorizons.edu.co', 'O+', 'Ninguna', 'Ninguna', '2024-02-01', 'Activo'),
(17, 'TI', '1012345687', 'Lucas', 'Gabriel', 'Gutiérrez', 'Mendoza', '2016-09-02', 'M', 'Calle 147 # 14-22, Bogotá', '3262345687', 'lgutierrez@littlehorizons.edu.co', 'A+', 'Ninguna', 'Ninguna', '2024-02-01', 'Activo'),
(18, 'TI', '1012345688', 'Juliana', 'Mercedes', 'Herrera', 'Cárdenas', '2016-11-17', 'F', 'Carrera 9 # 134-18, Bogotá', '3272345688', 'jherrera@littlehorizons.edu.co', 'O+', 'Abejas', 'Ninguna', '2024-02-01', 'Activo'),
(19, 'TI', '1012345689', 'Martín', 'Alonso', 'Medina', 'Paredes', '2017-02-28', 'M', 'Calle 152 # 11-45, Bogotá', '3282345689', 'mmedina@littlehorizons.edu.co', 'B+', 'Ninguna', 'Ninguna', '2024-02-01', 'Activo'),
(20, 'TI', '1012345690', 'Valeria', 'Inés', 'Silva', 'Suárez', '2017-08-09', 'F', 'Carrera 7 # 140-50, Bogotá', '3292345690', 'vsilva@littlehorizons.edu.co', 'O+', 'Ninguna', 'Ninguna', '2024-02-01', 'Activo'),
(21, 'TI', '1012345691', 'Joaquín', 'Ignacio', 'Rojas', 'Bermúdez', '2018-04-05', 'M', 'Calle 161 # 19-33, Bogotá', '3302345691', 'jrojas@littlehorizons.edu.co', 'A+', 'Ninguna', 'Ninguna', '2024-02-01', 'Activo'),
(22, 'TI', '1012345692', 'Antonia', 'Carolina', 'Jiménez', 'Acosta', '2018-10-12', 'F', 'Carrera 15 # 138-12, Bogotá', '3312345692', 'ajimenez@littlehorizons.edu.co', 'O-', 'Ninguna', 'Ninguna', '2024-02-01', 'Activo'),
(23, 'TI', '1012345693', 'Agustín', 'Julián', 'Navarro', 'Delgado', '2019-06-20', 'M', 'Calle 128 # 13-15, Bogotá', '3322345693', 'anavarro@littlehorizons.edu.co', 'O+', 'Ninguna', 'Ninguna', '2024-02-01', 'Activo'),
(24, 'TI', '1012345694', 'Elena', 'Patricia', 'Mendoza', 'Camacho', '2019-12-04', 'F', 'Carrera 11 # 120-40, Bogotá', '3332345694', 'emendoza@littlehorizons.edu.co', 'A+', 'Ninguna', 'Ninguna', '2024-02-01', 'Activo'),
(25, 'CC', '1012345695', 'David', 'Ricardo', 'Cárdenas', 'Valenzuela', '2006-05-14', 'M', 'Calle 106 # 14-60, Bogotá', '3342345695', 'dcardenas@littlehorizons.edu.co', 'O+', 'Ninguna', 'Ninguna', '2018-02-01', 'Activo'),
(26, 'CC', '1012345696', 'Paula', 'Daniela', 'Paredes', 'Montoya', '2006-09-28', 'F', 'Carrera 13 # 108-22, Bogotá', '3352345696', 'pparedes@littlehorizons.edu.co', 'B+', 'Ninguna', 'Ninguna', '2018-02-01', 'Activo'),
(27, 'TI', '1012345697', 'Simón', 'Arturo', 'Suárez', 'Barrios', '2007-01-16', 'M', 'Calle 118 # 15-70, Bogotá', '3362345697', 'ssuarez@littlehorizons.edu.co', 'A+', 'Gluten', 'Celiaquía leve', '2019-02-01', 'Activo'),
(28, 'TI', '1012345698', 'Catalina', 'María', 'Bermúdez', 'Caicedo', '2007-08-30', 'F', 'Carrera 9 # 122-10, Bogotá', '3372345698', 'cbermudez@littlehorizons.edu.co', 'O+', 'Ninguna', 'Ninguna', '2019-02-01', 'Activo'),
(29, 'TI', '1012345699', 'Benjamín', 'Mauricio', 'Acosta', 'Valencia', '2008-11-23', 'M', 'Calle 135 # 17-25, Bogotá', '3382345699', 'bacosta@littlehorizons.edu.co', 'O+', 'Ninguna', 'Ninguna', '2020-02-01', 'Activo'),
(30, 'TI', '1012345700', 'Renata', 'Isabel', 'Delgado', 'Salgado', '2008-04-18', 'F', 'Carrera 19 # 120-15, Bogotá', '3392345700', 'rdelgado@littlehorizons.edu.co', 'A-', 'Ninguna', 'Ninguna', '2020-02-01', 'Activo'),
(31, 'TI', '1012345701', 'Felipe', 'Santiago', 'Camacho', 'Palacios', '2009-03-09', 'M', 'Calle 144 # 13-30, Bogotá', '3402345701', 'fcamacho@littlehorizons.edu.co', 'O+', 'Ninguna', 'Ninguna', '2021-02-01', 'Activo'),
(32, 'TI', '1012345702', 'Sara', 'Valentina', 'Montoya', 'Escobar', '2010-06-27', 'F', 'Carrera 7 # 130-18, Bogotá', '3412345702', 'smontoya@littlehorizons.edu.co', 'B+', 'Ninguna', 'Ninguna', '2022-02-01', 'Activo'),
(33, 'TI', '1012345703', 'Manuel', 'Fernando', 'Barrios', 'Arango', '2011-09-14', 'M', 'Calle 155 # 14-55, Bogotá', '3422345703', 'mbarrios@littlehorizons.edu.co', 'O+', 'Ninguna', 'Ninguna', '2022-02-01', 'Activo'),
(34, 'TI', '1012345704', 'Clara', 'Eugenia', 'Caicedo', 'Márquez', '2012-12-05', 'F', 'Carrera 15 # 112-40, Bogotá', '3432345704', 'ccaicedo@littlehorizons.edu.co', 'A+', 'Ninguna', 'Ninguna', '2023-02-01', 'Activo'),
(35, 'TI', '1012345705', 'Pedro', 'Pablo', 'Valencia', 'Gaviria', '2013-05-19', 'M', 'Calle 125 # 18-42, Bogotá', '3442345705', 'pvalencia@littlehorizons.edu.co', 'O+', 'Ninguna', 'Ninguna', '2023-02-01', 'Activo');

-- ----------------------------------------------------------------------------
-- 3. TABLA: acudientes (32 Registros - Mínimo 30 requerido)
-- ----------------------------------------------------------------------------
INSERT INTO acudientes (id_acudiente, tipo_documento, numero_documento, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, parentesco, telefono_principal, telefono_secundario, correo_electronico, direccion, ocupacion, es_responsable_financiero) VALUES
(1, 'CC', '52123401', 'Carlos', 'Eduardo', 'Gómez', 'Trujillo', 'Padre', '3109012341', '6017412341', 'cgomez.trujillo@gmail.com', 'Calle 134 # 19-45, Bogotá', 'Ingeniero Civil', TRUE),
(2, 'CC', '52123402', 'Martha', 'Cecilia', 'Martínez', 'Duque', 'Madre', '3119012342', '6017412342', 'mmartinez.duque@gmail.com', 'Calle 134 # 19-45, Bogotá', 'Médica Pediatra', TRUE),
(3, 'CC', '52123403', 'Jorge', 'Enrique', 'Rodríguez', 'Peña', 'Padre', '3129012343', '6017412343', 'jrodriguez.pena@gmail.com', 'Carrera 15 # 106-32, Bogotá', 'Abogado Corporativo', TRUE),
(4, 'CC', '52123404', 'Patricia', 'Elena', 'Pérez', 'Cano', 'Madre', '3139012344', '6017412344', 'pperez.cano@gmail.com', 'Carrera 15 # 106-32, Bogotá', 'Contadora Pública', TRUE),
(5, 'CC', '52123405', 'Luis', 'Fernando', 'López', 'Rincón', 'Padre', '3149012345', '6017412345', 'lflopez@gmail.com', 'Calle 140 # 11-20, Bogotá', 'Arquitecto', TRUE),
(6, 'CC', '52123406', 'Gloria', 'Inés', 'Hernández', 'Molina', 'Madre', '3159012346', '6017412346', 'ghernandez.m@gmail.com', 'Carrera 7 # 127-10, Bogotá', 'Economista', TRUE),
(7, 'CC', '52123407', 'Andrés', 'Felipe', 'González', 'Pardo', 'Padre', '3169012347', '6017412347', 'afgonzalez@gmail.com', 'Calle 150 # 16-50, Bogotá', 'Consultor TI', TRUE),
(8, 'CC', '52123408', 'Diana', 'Marcela', 'García', 'Ochoa', 'Madre', '3179012348', '6017412348', 'dmgarcia@gmail.com', 'Carrera 9 # 116-25, Bogotá', 'Psicóloga', TRUE),
(9, 'CC', '52123409', 'Mauricio', 'Javier', 'Martínez', 'Soto', 'Padre', '3189012349', '6017412349', 'mmartinez.soto@gmail.com', 'Calle 116 # 15-40, Bogotá', 'Docente Universitario', TRUE),
(10, 'CC', '52123410', 'Adriana', 'Lucía', 'Pérez', 'Beltrán', 'Madre', '3199012350', '6017412350', 'aperez.beltran@gmail.com', 'Carrera 19 # 134-80, Bogotá', 'Odontóloga', TRUE),
(11, 'CC', '52123411', 'Gustavo', 'Adolfo', 'Álvarez', 'Castaño', 'Padre', '3209012351', '6017412351', 'galvarez.castano@gmail.com', 'Calle 122 # 18-35, Bogotá', 'Empresario', TRUE),
(12, 'CC', '52123412', 'Claudia', 'Patricia', 'Torres', 'León', 'Madre', '3219012352', '6017412352', 'ctorres.leon@gmail.com', 'Carrera 11 # 100-15, Bogotá', 'Diseñadora Industrial', TRUE),
(13, 'CC', '52123413', 'Héctor', 'Fabio', 'Ramírez', 'Londoño', 'Padre', '3229012353', '6017412353', 'hframirez@gmail.com', 'Calle 127 # 7-60, Bogotá', 'Administrador de Empresas', TRUE),
(14, 'CC', '52123414', 'Liliana', 'María', 'Díaz', 'Rendón', 'Madre', '3239012354', '6017412354', 'ldiaz.rendon@gmail.com', 'Carrera 13 # 119-42, Bogotá', 'Bacterióloga', TRUE),
(15, 'CC', '52123415', 'Álvaro', 'José', 'Castro', 'Henao', 'Padre', '3249012355', '6017412355', 'ajcastro@gmail.com', 'Calle 142 # 19A-12, Bogotá', 'Piloto Comercial', TRUE),
(16, 'CC', '52123416', 'Olga', 'Lucía', 'Vargas', 'Botero', 'Madre', '3259012356', '6017412356', 'ovargas.botero@gmail.com', 'Carrera 15 # 124-55, Bogotá', 'Comunicadora Social', TRUE),
(17, 'CC', '52123417', 'Rodrigo', 'Antonio', 'Morales', 'Villegas', 'Padre', '3269012357', '6017412357', 'rmorales.villegas@gmail.com', 'Calle 138 # 12-68, Bogotá', 'Ingeniero Mecánico', TRUE),
(18, 'CC', '52123418', 'Sandra', 'Milena', 'Ortiz', 'Zapata', 'Madre', '3279012358', '6017412358', 'sortiz.zapata@gmail.com', 'Carrera 18 # 140-30, Bogotá', 'Química Farmacéutica', TRUE),
(19, 'CC', '52123419', 'César', 'Augusto', 'Gutiérrez', 'Echeverri', 'Padre', '3289012359', '6017412359', 'cgutierrez.echeverri@gmail.com', 'Calle 147 # 14-22, Bogotá', 'Economista', TRUE),
(20, 'CC', '52123420', 'Esperanza', 'Beatriz', 'Herrera', 'Valencia', 'Madre', '3299012360', '6017412360', 'eherrera.valencia@gmail.com', 'Carrera 9 # 134-18, Bogotá', 'Nutricionista', TRUE),
(21, 'CC', '52123421', 'Guillermo', 'León', 'Medina', 'Cardona', 'Padre', '3309012361', '6017412361', 'gmedina.cardona@gmail.com', 'Calle 152 # 11-45, Bogotá', 'Veterinario', TRUE),
(22, 'CC', '52123422', 'Beatriz', 'Elena', 'Silva', 'Arango', 'Madre', '3319012362', '6017412362', 'bsilva.arango@gmail.com', 'Carrera 7 # 140-50, Bogotá', 'Traductora Oficial', TRUE),
(23, 'CC', '52123423', 'Fernando', 'Alberto', 'Rojas', 'Ospina', 'Padre', '3329012363', '6017412363', 'frojas.ospina@gmail.com', 'Calle 161 # 19-33, Bogotá', 'Sociólogo', TRUE),
(24, 'CC', '52123424', 'María', 'Fernanda', 'Jiménez', 'Tamayo', 'Madre', '3339012364', '6017412364', 'mfjimenez.tamayo@gmail.com', 'Carrera 15 # 138-12, Bogotá', 'Directora de Arte', TRUE),
(25, 'CC', '52123425', 'Jaime', 'Eduardo', 'Navarro', 'Quintero', 'Padre', '3349012365', '6017412365', 'jnavarro.quintero@gmail.com', 'Calle 128 # 13-15, Bogotá', 'Gerente de Proyectos', TRUE),
(26, 'CC', '52123426', 'Clara', 'Inés', 'Mendoza', 'Correa', 'Madre', '3359012366', '6017412366', 'cmendoza.correa@gmail.com', 'Carrera 11 # 120-40, Bogotá', 'Abogada Penalista', TRUE),
(27, 'CC', '52123427', 'Roberto', 'Carlos', 'Cárdenas', 'Gómez', 'Padre', '3369012367', '6017412367', 'rcardenas.g@gmail.com', 'Calle 106 # 14-60, Bogotá', 'Ingeniero Químico', TRUE),
(28, 'CC', '52123428', 'Sonia', 'Esperanza', 'Paredes', 'Vasco', 'Madre', '3379012368', '6017412368', 'sparedes.v@gmail.com', 'Carrera 13 # 108-22, Bogotá', 'Docente de Idiomas', TRUE),
(29, 'CC', '52123429', 'Hernando', 'José', 'Suárez', 'Betancur', 'Padre', '3389012369', '6017412369', 'hsuarez.b@gmail.com', 'Calle 118 # 15-70, Bogotá', 'Comerciante', TRUE),
(30, 'CC', '52123430', 'Amparo', 'del Carmen', 'Bermúdez', 'Jaramillo', 'Madre', '3399012370', '6017412370', 'abermudez.j@gmail.com', 'Carrera 9 # 122-10, Bogotá', 'Enfermera Jefe', TRUE),
(31, 'CC', '52123431', 'Bernardo', 'Arturo', 'Acosta', 'Restrepo', 'Padre', '3409012371', '6017412371', 'bacosta.restrepo@gmail.com', 'Calle 135 # 17-25, Bogotá', 'Ingeniero Agrónomo', TRUE),
(32, 'CC', '52123432', 'Luz', 'Marina', 'Delgado', 'Franco', 'Madre', '3419012372', '6017412372', 'ldelgado.franco@gmail.com', 'Carrera 19 # 120-15, Bogotá', 'Fisioterapeuta', TRUE);

-- ----------------------------------------------------------------------------
-- 4. TABLA: estudiante_acudiente (Relación Estudiante - Acudiente)
-- ----------------------------------------------------------------------------
INSERT INTO estudiante_acudiente (id_estudiante, id_acudiente, es_principal, es_contacto_emergencia) VALUES
(1, 1, TRUE, TRUE), (1, 2, FALSE, TRUE),
(2, 3, TRUE, TRUE), (2, 4, FALSE, TRUE),
(3, 5, TRUE, TRUE),
(4, 6, TRUE, TRUE),
(5, 7, TRUE, TRUE),
(6, 8, TRUE, TRUE),
(7, 9, TRUE, TRUE),
(8, 10, TRUE, TRUE),
(9, 11, TRUE, TRUE),
(10, 12, TRUE, TRUE),
(11, 13, TRUE, TRUE),
(12, 14, TRUE, TRUE),
(13, 15, TRUE, TRUE),
(14, 16, TRUE, TRUE),
(15, 17, TRUE, TRUE),
(16, 18, TRUE, TRUE),
(17, 19, TRUE, TRUE),
(18, 20, TRUE, TRUE),
(19, 21, TRUE, TRUE),
(20, 22, TRUE, TRUE),
(21, 23, TRUE, TRUE),
(22, 24, TRUE, TRUE),
(23, 25, TRUE, TRUE),
(24, 26, TRUE, TRUE),
(25, 27, TRUE, TRUE),
(26, 28, TRUE, TRUE),
(27, 29, TRUE, TRUE),
(28, 30, TRUE, TRUE),
(29, 31, TRUE, TRUE),
(30, 32, TRUE, TRUE),
(31, 1, FALSE, TRUE), -- Hermano o familiar de estudiante 1
(32, 3, FALSE, TRUE), -- Familiar de estudiante 2
(33, 5, FALSE, TRUE),
(34, 7, FALSE, TRUE),
(35, 9, FALSE, TRUE);

-- ----------------------------------------------------------------------------
-- 5. TABLA: matriculas (35 Registros para 2024)
-- ----------------------------------------------------------------------------
INSERT INTO matriculas (id_matricula, id_estudiante, id_grado, año_academico, estado_matricula, fecha_matricula, observaciones) VALUES
(1, 1, 11, 2024, 'Activa', '2024-01-20', 'Matrícula regular Grado Décimo'),
(2, 2, 11, 2024, 'Activa', '2024-01-20', 'Matrícula regular Grado Décimo'),
(3, 3, 10, 2024, 'Activa', '2024-01-21', 'Matrícula regular Grado Noveno'),
(4, 4, 10, 2024, 'Activa', '2024-01-21', 'Matrícula regular Grado Noveno'),
(5, 5, 9, 2024, 'Activa', '2024-01-22', 'Matrícula regular Grado Octavo'),
(6, 6, 9, 2024, 'Activa', '2024-01-22', 'Matrícula regular Grado Octavo'),
(7, 7, 8, 2024, 'Activa', '2024-01-22', 'Matrícula regular Grado Séptimo'),
(8, 8, 8, 2024, 'Activa', '2024-01-22', 'Matrícula regular Grado Séptimo'),
(9, 9, 7, 2024, 'Activa', '2024-01-23', 'Matrícula regular Grado Sexto'),
(10, 10, 7, 2024, 'Activa', '2024-01-23', 'Matrícula regular Grado Sexto'),
(11, 11, 6, 2024, 'Activa', '2024-01-23', 'Matrícula regular Grado Quinto'),
(12, 12, 6, 2024, 'Activa', '2024-01-23', 'Matrícula regular Grado Quinto'),
(13, 13, 5, 2024, 'Activa', '2024-01-24', 'Matrícula regular Grado Cuarto'),
(14, 14, 5, 2024, 'Activa', '2024-01-24', 'Matrícula regular Grado Cuarto'),
(15, 15, 4, 2024, 'Activa', '2024-01-24', 'Matrícula regular Grado Tercero'),
(16, 16, 4, 2024, 'Activa', '2024-01-24', 'Matrícula regular Grado Tercero'),
(17, 17, 3, 2024, 'Activa', '2024-01-25', 'Matrícula regular Grado Segundo'),
(18, 18, 3, 2024, 'Activa', '2024-01-25', 'Matrícula regular Grado Segundo'),
(19, 19, 2, 2024, 'Activa', '2024-01-25', 'Matrícula regular Grado Primero'),
(20, 20, 2, 2024, 'Activa', '2024-01-25', 'Matrícula regular Grado Primero'),
(21, 21, 1, 2024, 'Activa', '2024-01-26', 'Matrícula regular Preescolar'),
(22, 22, 1, 2024, 'Activa', '2024-01-26', 'Matrícula regular Preescolar'),
(23, 23, 1, 2024, 'Activa', '2024-01-26', 'Matrícula regular Preescolar'),
(24, 24, 1, 2024, 'Activa', '2024-01-26', 'Matrícula regular Preescolar'),
(25, 25, 12, 2024, 'Activa', '2024-01-19', 'Matrícula regular Grado Once'),
(26, 26, 12, 2024, 'Activa', '2024-01-19', 'Matrícula regular Grado Once'),
(27, 27, 11, 2024, 'Activa', '2024-01-20', 'Matrícula regular Grado Décimo'),
(28, 28, 11, 2024, 'Activa', '2024-01-20', 'Matrícula regular Grado Décimo'),
(29, 29, 10, 2024, 'Activa', '2024-01-21', 'Matrícula regular Grado Noveno'),
(30, 30, 10, 2024, 'Activa', '2024-01-21', 'Matrícula regular Grado Noveno'),
(31, 31, 9, 2024, 'Activa', '2024-01-22', 'Matrícula regular Grado Octavo'),
(32, 32, 8, 2024, 'Activa', '2024-01-22', 'Matrícula regular Grado Séptimo'),
(33, 33, 7, 2024, 'Activa', '2024-01-23', 'Matrícula regular Grado Sexto'),
(34, 34, 6, 2024, 'Activa', '2024-01-23', 'Matrícula regular Grado Quinto'),
(35, 35, 5, 2024, 'Activa', '2024-01-24', 'Matrícula regular Grado Cuarto');

-- ----------------------------------------------------------------------------
-- 6. TABLA: profesores (32 Registros - Mínimo 30 requerido)
-- ----------------------------------------------------------------------------
INSERT INTO profesores (id_profesor, tipo_documento, numero_documento, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, correo_electronico, telefono, especializacion, fecha_contratacion, estado) VALUES
(1, 'CC', '79101001', 'Alejandro', 'Darío', 'Morales', 'Castaño', 'amorales@littlehorizons.edu.co', '3104501001', 'Matemáticas y Cálculo', '2018-01-10', 'Activo'),
(2, 'CC', '79101002', 'Carolina', 'Inés', 'Restrepo', 'Vargas', 'crestrepo@littlehorizons.edu.co', '3114501002', 'Lengua Castellana y Literatura', '2018-01-10', 'Activo'),
(3, 'CC', '79101003', 'Felipe', 'Antonio', 'Guzmán', 'Soto', 'fguzman@littlehorizons.edu.co', '3124501003', 'Ciencias Naturales y Biología', '2019-01-15', 'Activo'),
(4, 'CC', '79101004', 'Marcela', 'Viviana', 'Ospina', 'Ríos', 'mospina@littlehorizons.edu.co', '3134501004', 'Química Orgánica e Inorgánica', '2019-01-15', 'Activo'),
(5, 'CC', '79101005', 'Julio', 'César', 'Pineda', 'Gómez', 'jpineda@littlehorizons.edu.co', '3144501005', 'Física Clásica y Moderna', '2019-07-01', 'Activo'),
(6, 'CC', '79101006', 'Tatiana', 'Marcela', 'Buitrago', 'Díaz', 'tbuitrago@littlehorizons.edu.co', '3154501006', 'Ciencias Sociales e Historia', '2020-01-10', 'Activo'),
(7, 'CC', '79101007', 'Andrés', 'Mauricio', 'Ceballos', 'Jaramillo', 'aceballos@littlehorizons.edu.co', '3164501007', 'Filosofía y Ética', '2020-01-10', 'Activo'),
(8, 'CC', '79101008', 'Andrea', 'Paola', 'Montes', 'Silva', 'amontes@littlehorizons.edu.co', '3174501008', 'Inglés y Literatura Anglófona', '2020-07-15', 'Activo'),
(9, 'CC', '79101009', 'Diego', 'Armando', 'Cañas', 'Ruiz', 'dcanas@littlehorizons.edu.co', '3184501009', 'Educación Física y Deportes', '2021-01-12', 'Activo'),
(10, 'CC', '79101010', 'Natalia', 'Sofía', 'Lozano', 'Hernández', 'nlozano@littlehorizons.edu.co', '3194501010', 'Artes Plásticas y Visuales', '2021-01-12', 'Activo'),
(11, 'CC', '79101011', 'Gabriel', 'Esteban', 'Patiño', 'Muñoz', 'gpatino@littlehorizons.edu.co', '3204501011', 'Música y Dirección Coral', '2021-07-01', 'Activo'),
(12, 'CC', '79101012', 'Adriana', 'Lucía', 'Mejía', 'Gaviria', 'amejia@littlehorizons.edu.co', '3214501012', 'Tecnología e Informática', '2021-07-01', 'Activo'),
(13, 'CC', '79101013', 'Germán', 'Alonso', 'Cardona', 'Gallego', 'gcardona@littlehorizons.edu.co', '3224501013', 'Robótica y Programación', '2022-01-10', 'Activo'),
(14, 'CC', '79101014', 'Verónica', 'Andrea', 'Valencia', 'Gutiérrez', 'vvalencia@littlehorizons.edu.co', '3234501014', 'Francés y Lenguas Extranjeras', '2022-01-10', 'Activo'),
(15, 'CC', '79101015', 'Mauricio', 'Andrés', 'Sarmiento', 'Pardo', 'msarmiento@littlehorizons.edu.co', '3244501015', 'Geografía y Democracia', '2022-07-01', 'Activo'),
(16, 'CC', '79101016', 'Luz', 'Stella', 'Correa', 'Berrío', 'lcorrea@littlehorizons.edu.co', '3254501016', 'Educación Preescolar e Inicial', '2022-07-01', 'Activo'),
(17, 'CC', '79101017', 'Gloria', 'Amparo', 'Hincapié', 'Bedoya', 'ghincapie@littlehorizons.edu.co', '3264501017', 'Pedagogía Infantil Grado 1-2', '2023-01-10', 'Activo'),
(18, 'CC', '79101018', 'Sandra', 'Patricia', 'Zapata', 'Ocampo', 'szapata@littlehorizons.edu.co', '3274501018', 'Pedagogía Infantil Grado 3-4', '2023-01-10', 'Activo'),
(19, 'CC', '79101019', 'Juan', 'Carlos', 'Chavarría', 'Rendón', 'jchavarria@littlehorizons.edu.co', '3284501019', 'Matemáticas Básicas Primaria', '2023-01-10', 'Activo'),
(20, 'CC', '79101020', 'Paola', 'Andrea', 'Giraldo', 'Toro', 'pgiraldo@littlehorizons.edu.co', '3294501020', 'Ciencias Sociales Primaria', '2023-07-01', 'Activo'),
(21, 'CC', '79101021', 'Rodrigo', 'Javier', 'Duque', 'Cano', 'rduque@littlehorizons.edu.co', '3304501021', 'Geometría y Estadística', '2023-07-01', 'Activo'),
(22, 'CC', '79101022', 'Mónica', 'Isabel', 'Vasco', 'Quintero', 'mvasco@littlehorizons.edu.co', '3314501022', 'Inglés Primaria', '2023-07-01', 'Activo'),
(23, 'CC', '79101023', 'Víctor', 'Hugo', 'Berrío', 'Posada', 'vberrio@littlehorizons.edu.co', '3324501023', 'Economía y Política 10-11', '2024-01-08', 'Activo'),
(24, 'CC', '79101024', 'Lina', 'Marcela', 'Alzate', 'Serna', 'lalzate@littlehorizons.edu.co', '3334501024', 'Danzas y Expresión Corporal', '2024-01-08', 'Activo'),
(25, 'CC', '79101025', 'Ricardo', 'León', 'Monsalve', 'Gómez', 'rmonsalve@littlehorizons.edu.co', '3344501025', 'Teatro y Artes Escénicas', '2024-01-08', 'Activo'),
(26, 'CC', '79101026', 'Yolanda', 'Cecilia', 'Puerta', 'Henao', 'ypuerta@littlehorizons.edu.co', '3354501026', 'Biología Celular y Genética', '2024-01-08', 'Activo'),
(27, 'CC', '79101027', 'Jaime', 'Alberto', 'Triviño', 'Castrillón', 'jtrivino@littlehorizons.edu.co', '3364501027', 'Química Ambiental', '2024-01-08', 'Activo'),
(28, 'CC', '79101028', 'Claudia', 'Helena', 'Osorio', 'Rojas', 'cosorio@littlehorizons.edu.co', '3374501028', 'Religión y Ética Universal', '2024-01-08', 'Activo'),
(29, 'CC', '79101029', 'Óscar', 'Eduardo', 'Arango', 'Marín', 'oarango@littlehorizons.edu.co', '3384501029', 'Natación y Acondicionamiento', '2024-01-08', 'Activo'),
(30, 'CC', '79101030', 'Eliana', 'Patricia', 'Zuluaga', 'Gil', 'ezuluaga@littlehorizons.edu.co', '3394501030', 'Ajedrez y Estrategia Cognitiva', '2024-01-08', 'Activo'),
(31, 'CC', '79101031', 'Fabio', 'Nelson', 'Tabares', 'Molina', 'ftabares@littlehorizons.edu.co', '3404501031', 'Astronomía y Ciencias del Espacio', '2024-01-08', 'Activo'),
(32, 'CC', '79101032', 'Helena', 'del Pilar', 'Cárdenas', 'Gómez', 'hcardenas@littlehorizons.edu.co', '3414501032', 'Diseño Gráfico y Multimedios', '2024-01-08', 'Activo');

-- ----------------------------------------------------------------------------
-- 7. TABLA: materias (35 Registros - Mínimo 30 requerido)
-- ----------------------------------------------------------------------------
INSERT INTO materias (id_materia, codigo_materia, nombre_materia, descripcion, id_grado, horas_semanales, esta_activa) VALUES
(1, 'MAT-PRE', 'Iniciación al Pensamiento Matemático', 'Desarrollo del razonamiento lógico en nivel inicial', 1, 3, TRUE),
(2, 'LIT-PRE', 'Dimension Comunicativa y Lectoescritura Inicial', 'Aproximación al lenguaje y grafomotricidad', 1, 4, TRUE),
(3, 'EXP-PRE', 'Expresión Artística y Corporal', 'Actividades de motricidad y arte infantil', 1, 3, TRUE),
(4, 'MAT-1', 'Matemáticas Grado Primero', 'Operaciones básicas de adición y sustracción', 2, 4, TRUE),
(5, 'ESP-1', 'Lengua Castellana Grado Primero', 'Consolidación de lectoescritura inicial', 2, 5, TRUE),
(6, 'ING-1', 'Inglés Básico Grado Primero', 'Vocabulario y comprensión auditiva básica', 2, 3, TRUE),
(7, 'MAT-2', 'Matemáticas Grado Segundo', 'Números hasta tres cifras y multiplicación inicial', 3, 4, TRUE),
(8, 'ESP-2', 'Lengua Castellana Grado Segundo', 'Comprensión lectora y redacción básica', 3, 4, TRUE),
(9, 'CIE-2', 'Ciencias Naturales Grado Segundo', 'Seres vivos y su entorno', 3, 3, TRUE),
(10, 'MAT-3', 'Matemáticas Grado Tercero', 'Tablas de multiplicar, división y nociones de fracciones', 4, 4, TRUE),
(11, 'ESP-3', 'Lengua Castellana Grado Tercero', 'Tipologías textuales y gramática básica', 4, 4, TRUE),
(12, 'SOC-3', 'Ciencias Sociales Grado Tercero', 'Comunidad, geografía local y símbolos patrios', 4, 3, TRUE),
(13, 'MAT-4', 'Matemáticas Grado Cuarto', 'Fracciones, decimales y geometría plana', 5, 4, TRUE),
(14, 'ESP-4', 'Lengua Castellana Grado Cuarto', 'Estructura oracional, comprensión inferencial', 5, 4, TRUE),
(15, 'ING-4', 'Inglés Intermedio Grado Cuarto', 'Estructuras verbales simples y conversación', 5, 3, TRUE),
(16, 'MAT-5', 'Matemáticas Grado Quinto', 'Resolución de problemas con operaciones combinadas', 6, 5, TRUE),
(17, 'ESP-5', 'Lengua Castellana Grado Quinto', 'Producción textual y análisis literario inicial', 6, 4, TRUE),
(18, 'CIE-5', 'Ciencias Naturales Grado Quinto', 'Sistemas del cuerpo humano y energía', 6, 4, TRUE),
(19, 'MAT-6', 'Aritmética Grado Sexto', 'Números enteros, racionales y teoría de números', 7, 4, TRUE),
(20, 'ESP-6', 'Lengua Castellana Grado Sexto', 'Géneros literarios: narrativo, lírico, dramático', 7, 4, TRUE),
(21, 'BIO-6', 'Biología Fundamental Grado Sexto', 'Estructura celular, taxonomía y reinos naturales', 7, 3, TRUE),
(22, 'MAT-7', 'Aritmética y Álgebra Grado Séptimo', 'Números reales, razones y proporciones', 8, 4, TRUE),
(23, 'SOC-7', 'Historia y Geografía Grado Séptimo', 'Edad Media, Renacimiento y geografía universal', 8, 3, TRUE),
(24, 'MAT-8', 'Álgebra Grado Octavo', 'Polinomios, productos notables y factorización', 9, 5, TRUE),
(25, 'ING-8', 'Inglés B1 Grado Octavo', 'Tiempos verbales continuos y compuestos', 9, 4, TRUE),
(26, 'MAT-9', 'Álgebra Lineal y Geometría Grado Noveno', 'Ecuaciones cuadráticas, trigonometría básica', 10, 5, TRUE),
(27, 'QUI-9', 'Introducción a la Química Grado Noveno', 'Tabla periódica, enlaces químicos y nomenclatura', 10, 4, TRUE),
(28, 'MAT-10', 'Trigonometría y Geometría Analítica 10', 'Funciones trigonométricas, secciones cónicas', 11, 5, TRUE),
(29, 'FIS-10', 'Física Mecánica Grado Décimo', 'Cinemática, leyes de Newton, trabajo y energía', 11, 4, TRUE),
(30, 'QUI-10', 'Química General Grado Décimo', 'Estequiometría, gases ideales y soluciones', 11, 4, TRUE),
(31, 'FIL-10', 'Filosofía Clásica Grado Décimo', 'Presocráticos, Platón, Aristóteles y Helenismo', 11, 3, TRUE),
(32, 'MAT-11', 'Cálculo Diferencial e Integral Grado Once', 'Límites, derivadas, integrales y aplicaciones', 12, 5, TRUE),
(33, 'FIS-11', 'Física Electromagnética y Ondas Grado Once', 'Termodinámica, óptica, electricidad y magnetismo', 12, 4, TRUE),
(34, 'ECO-11', 'Ciencias Económicas y Políticas Once', 'Micro y macroeconomía, sistema político colombiano', 12, 3, TRUE),
(35, 'ING-11', 'Inglés Avanzado B2/C1 Grado Once', 'Preparación pruebas Saber 11 y debate académico', 12, 4, TRUE);

-- ----------------------------------------------------------------------------
-- 8. TABLA: asignaciones_profesor (35 Asignaciones)
-- ----------------------------------------------------------------------------
INSERT INTO asignaciones_profesor (id_asignacion, id_profesor, id_materia, año_academico, seccion) VALUES
(1, 16, 1, 2024, 'A'),
(2, 16, 2, 2024, 'A'),
(3, 16, 3, 2024, 'A'),
(4, 17, 4, 2024, 'A'),
(5, 17, 5, 2024, 'A'),
(6, 22, 6, 2024, 'A'),
(7, 17, 7, 2024, 'A'),
(8, 17, 8, 2024, 'A'),
(9, 3, 9, 2024, 'A'),
(10, 18, 10, 2024, 'A'),
(11, 18, 11, 2024, 'A'),
(12, 20, 12, 2024, 'A'),
(13, 18, 13, 2024, 'A'),
(14, 18, 14, 2024, 'A'),
(15, 22, 15, 2024, 'A'),
(16, 19, 16, 2024, 'A'),
(17, 2, 17, 2024, 'A'),
(18, 3, 18, 2024, 'A'),
(19, 1, 19, 2024, 'A'),
(20, 2, 20, 2024, 'A'),
(21, 3, 21, 2024, 'A'),
(22, 1, 22, 2024, 'A'),
(23, 6, 23, 2024, 'A'),
(24, 1, 24, 2024, 'A'),
(25, 8, 25, 2024, 'A'),
(26, 1, 26, 2024, 'A'),
(27, 4, 27, 2024, 'A'),
(28, 1, 28, 2024, 'A'),
(29, 5, 29, 2024, 'A'),
(30, 4, 30, 2024, 'A'),
(31, 7, 31, 2024, 'A'),
(32, 1, 32, 2024, 'A'),
(33, 5, 33, 2024, 'A'),
(34, 23, 34, 2024, 'A'),
(35, 8, 35, 2024, 'A');

-- ----------------------------------------------------------------------------
-- 9. TABLA: inscripciones_materia (35 Registros)
-- ----------------------------------------------------------------------------
INSERT INTO inscripciones_materia (id_inscripcion, id_estudiante, id_materia, año_academico, periodo_academico, seccion, fecha_inscripcion, estado) VALUES
(1, 1, 28, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(2, 1, 29, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(3, 1, 30, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(4, 1, 31, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(5, 2, 28, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(6, 2, 29, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(7, 3, 26, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(8, 3, 27, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(9, 4, 26, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(10, 4, 27, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(11, 5, 24, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(12, 5, 25, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(13, 6, 24, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(14, 7, 22, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(15, 8, 22, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(16, 9, 19, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(17, 10, 19, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(18, 11, 16, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(19, 12, 16, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(20, 13, 13, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(21, 14, 13, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(22, 15, 10, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(23, 16, 10, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(24, 17, 7, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(25, 18, 7, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(26, 19, 4, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(27, 20, 4, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(28, 21, 1, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(29, 22, 1, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(30, 25, 32, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(31, 25, 33, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(32, 25, 34, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(33, 25, 35, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(34, 26, 32, 2024, 'I', 'A', '2024-02-01', 'Activa'),
(35, 26, 35, 2024, 'I', 'A', '2024-02-01', 'Activa');

-- ----------------------------------------------------------------------------
-- 10. TABLA: registros_academicos (35 Registros - Mínimo 30 requerido)
-- ----------------------------------------------------------------------------
INSERT INTO registros_academicos (id_registro, id_estudiante, id_materia, id_profesor, año_academico, periodo_academico, valor_nota, observaciones, fecha_registro) VALUES
(1, 1, 28, 1, 2024, 'I', 4.5, 'Excelente desempeño en trigonometría analítica', '2024-04-10'),
(2, 1, 29, 5, 2024, 'I', 4.2, 'Buen dominio de leyes de Newton y cinemática', '2024-04-10'),
(3, 1, 30, 4, 2024, 'I', 4.8, 'Destacado en laboratorio de estequiometría', '2024-04-11'),
(4, 1, 31, 7, 2024, 'I', 4.0, 'Participación activa en disertaciones filosóficas', '2024-04-11'),
(5, 2, 28, 1, 2024, 'I', 3.8, 'Cumple satisfactoriamente con los talleres', '2024-04-10'),
(6, 2, 29, 5, 2024, 'I', 3.9, 'Buen trabajo experimental en laboratorio', '2024-04-10'),
(7, 3, 26, 1, 2024, 'I', 4.1, 'Resolución precisa de sistemas de ecuaciones', '2024-04-12'),
(8, 3, 27, 4, 2024, 'I', 4.3, 'Comprensión sólida de la tabla periódica', '2024-04-12'),
(9, 4, 26, 1, 2024, 'I', 4.7, 'Alto nivel de razonamiento algebraico', '2024-04-12'),
(10, 4, 27, 4, 2024, 'I', 4.6, 'Excelente presentación en informes de laboratorio', '2024-04-12'),
(11, 5, 24, 1, 2024, 'I', 3.5, 'Requiere reforzar factorización de trinomios', '2024-04-13'),
(12, 5, 25, 8, 2024, 'I', 4.2, 'Buena fluidez en conversación y redacción', '2024-04-13'),
(13, 6, 24, 1, 2024, 'I', 4.4, 'Dominio completo de expresiones algebraicas', '2024-04-13'),
(14, 7, 22, 1, 2024, 'I', 3.7, 'Aplica adecuadamente proporcionalidad directa', '2024-04-14'),
(15, 8, 22, 1, 2024, 'I', 4.0, 'Participativa y dedicada en clase', '2024-04-14'),
(16, 9, 19, 1, 2024, 'I', 4.5, 'Gran habilidad en cálculo con números enteros', '2024-04-14'),
(17, 10, 19, 1, 2024, 'I', 4.3, 'Puntual en entrega de guías y talleres', '2024-04-14'),
(18, 11, 16, 19, 2024, 'I', 4.6, 'Liderazgo en resolución de situaciones problema', '2024-04-15'),
(19, 12, 16, 19, 2024, 'I', 4.1, 'Buen trabajo individual y colaborativo', '2024-04-15'),
(20, 13, 13, 18, 2024, 'I', 3.9, 'Comprende fracciones y operaciones básicas', '2024-04-15'),
(21, 14, 13, 18, 2024, 'I', 4.4, 'Excelente orden y precisión en operaciones', '2024-04-15'),
(22, 15, 10, 18, 2024, 'I', 4.8, 'Memorización y aplicación rápida de tablas', '2024-04-16'),
(23, 16, 10, 18, 2024, 'I', 4.5, 'Muy buen desempeño en razonamiento espacial', '2024-04-16'),
(24, 17, 7, 17, 2024, 'I', 4.2, 'Avanza favorablemente en cálculo mental', '2024-04-16'),
(25, 18, 7, 17, 2024, 'I', 4.0, 'Buena disposición hacia las matemáticas', '2024-04-16'),
(26, 19, 4, 17, 2024, 'I', 4.9, 'Destacado proceso en operaciones básicas', '2024-04-17'),
(27, 20, 4, 17, 2024, 'I', 4.6, 'Creativa y disciplinada en sus actividades', '2024-04-17'),
(28, 21, 1, 16, 2024, 'I', 5.0, 'Excelente reconocimiento de figuras y patrones', '2024-04-17'),
(29, 22, 1, 16, 2024, 'I', 4.8, 'Gran motivación y participación constante', '2024-04-17'),
(30, 25, 32, 1, 2024, 'I', 4.7, 'Alto rigor matemático en cálculo de derivadas', '2024-04-18'),
(31, 25, 33, 5, 2024, 'I', 4.6, 'Análisis sobresaliente en leyes de termodinámica', '2024-04-18'),
(32, 25, 34, 23, 2024, 'I', 4.8, 'Pensamiento crítico en análisis macroeconómico', '2024-04-18'),
(33, 25, 35, 8, 2024, 'I', 4.9, 'Nivel C1 certificado, excelencia académica', '2024-04-18'),
(34, 26, 32, 1, 2024, 'I', 4.2, 'Buen entendimiento de límites y continuidad', '2024-04-18'),
(35, 26, 35, 8, 2024, 'I', 4.5, 'Gran elocuencia en disertaciones en inglés', '2024-04-18');

-- ----------------------------------------------------------------------------
-- 11. TABLA: asistencias (35 Registros)
-- ----------------------------------------------------------------------------
INSERT INTO asistencias (id_asistencia, id_estudiante, id_materia, fecha_asistencia, estado_asistencia, observaciones, registrado_por) VALUES
(1, 1, 28, '2024-03-01', 'Presente', 'Puntual', 1),
(2, 2, 28, '2024-03-01', 'Presente', 'Puntual', 1),
(3, 1, 29, '2024-03-02', 'Presente', 'Participativo', 5),
(4, 2, 29, '2024-03-02', 'Tardanza', 'Llegada 10 min tarde por tráfico', 5),
(5, 3, 26, '2024-03-01', 'Presente', 'Puntual', 1),
(6, 4, 26, '2024-03-01', 'Presente', 'Puntual', 1),
(7, 3, 27, '2024-03-03', 'Ausente', 'Sin justificar aún', 4),
(8, 4, 27, '2024-03-03', 'Presente', 'Puntual', 4),
(9, 5, 24, '2024-03-04', 'Excusa Médica', 'Incapacidad médica radicada', 1),
(10, 6, 24, '2024-03-04', 'Presente', 'Puntual', 1),
(11, 7, 22, '2024-03-05', 'Presente', 'Puntual', 1),
(12, 8, 22, '2024-03-05', 'Presente', 'Puntual', 1),
(13, 9, 19, '2024-03-06', 'Presente', 'Puntual', 1),
(14, 10, 19, '2024-03-06', 'Presente', 'Puntual', 1),
(15, 11, 16, '2024-03-07', 'Presente', 'Puntual', 19),
(16, 12, 16, '2024-03-07', 'Presente', 'Puntual', 19),
(17, 13, 13, '2024-03-08', 'Presente', 'Puntual', 18),
(18, 14, 13, '2024-03-08', 'Presente', 'Puntual', 18),
(19, 15, 10, '2024-03-11', 'Presente', 'Puntual', 18),
(20, 16, 10, '2024-03-11', 'Presente', 'Puntual', 18),
(21, 17, 7, '2024-03-12', 'Presente', 'Puntual', 17),
(22, 18, 7, '2024-03-12', 'Presente', 'Puntual', 17),
(23, 19, 4, '2024-03-13', 'Presente', 'Puntual', 17),
(24, 20, 4, '2024-03-13', 'Presente', 'Puntual', 17),
(25, 21, 1, '2024-03-14', 'Presente', 'Puntual', 16),
(26, 22, 1, '2024-03-14', 'Presente', 'Puntual', 16),
(27, 25, 32, '2024-03-15', 'Presente', 'Puntual', 1),
(28, 26, 32, '2024-03-15', 'Presente', 'Puntual', 1),
(29, 25, 33, '2024-03-18', 'Presente', 'Puntual', 5),
(30, 25, 34, '2024-03-19', 'Presente', 'Puntual', 23),
(31, 25, 35, '2024-03-20', 'Presente', 'Puntual', 8),
(32, 26, 35, '2024-03-20', 'Presente', 'Puntual', 8),
(33, 1, 30, '2024-03-21', 'Presente', 'Puntual', 4),
(34, 1, 31, '2024-03-22', 'Presente', 'Puntual', 7),
(35, 5, 25, '2024-03-25', 'Presente', 'Puntual', 8);

-- ----------------------------------------------------------------------------
-- 12. TABLA: clubes (30 Registros - Mínimo 30 requerido)
-- ----------------------------------------------------------------------------
INSERT INTO clubes (id_club, nombre_club, descripcion, id_profesor_supervisor, capacidad_maxima, horario_reunion, esta_activo) VALUES
(1, 'Club de Robótica y Automatización', 'Construcción y programación de robots con Arduino y LEGO Mindstorms', 13, 25, 'Martes y Jueves 3:30 - 5:00 PM', TRUE),
(2, 'Club de Astronomía y Cosmología', 'Observación telescópica, astrofísica y cartografía celeste', 31, 30, 'Viernes 6:00 - 8:00 PM', TRUE),
(3, 'Club de Programación y Desarrollo Web', 'Desarrollo de software en Java, Python y tecnologías web', 12, 25, 'Lunes y Miércoles 3:30 - 5:00 PM', TRUE),
(4, 'Club de Debate y Modelo ONU', 'Técnicas de oratoria, diplomacia y resolución de conflictos', 7, 30, 'Martes 3:30 - 5:30 PM', TRUE),
(5, 'Club de Ajedrez y Estrategia', 'Entrenamiento táctico y torneos internos federados', 30, 35, 'Miércoles 3:30 - 5:00 PM', TRUE),
(6, 'Club de Ciencias y Biotecnología', 'Experimentos biológicos, cultivos y química aplicada', 3, 25, 'Jueves 3:30 - 5:00 PM', TRUE),
(7, 'Club de Matemáticas Olímpicas', 'Resolución de problemas de alta complejidad y olimpiadas', 1, 20, 'Lunes 3:30 - 5:00 PM', TRUE),
(8, 'Club de Artes Plásticas y Escultura', 'Pintura al óleo, acuarela, cerámica y escultura moderna', 10, 25, 'Miércoles 3:30 - 5:00 PM', TRUE),
(9, 'Club de Música y Ensamble Instrumental', 'Interpretación de guitarra, piano, percusión y vientos', 11, 30, 'Martes y Jueves 3:30 - 5:00 PM', TRUE),
(10, 'Club de Teatro y Expresión Escénica', 'Actuación, dramaturgia, improvisación y montaje de obras', 25, 30, 'Viernes 3:30 - 5:30 PM', TRUE),
(11, 'Club de Danza Contemporánea y Folclórica', 'Expresión corporal, ritmos colombianos y baile moderno', 24, 28, 'Lunes y Miércoles 3:30 - 5:00 PM', TRUE),
(12, 'Club de Periodismo Escolar y Radio', 'Redacción de periódicos, podcasts escolares y fotografía', 2, 25, 'Jueves 3:30 - 5:00 PM', TRUE),
(13, 'Club de Lectura y Creación Literaria', 'Tertulias literarias, poesía y narrativa creativa', 2, 20, 'Martes 3:30 - 5:00 PM', TRUE),
(14, 'Club de Fútbol Selección Masculina', 'Entrenamiento técnico, táctico y torneo intercolegiado', 9, 30, 'Lunes, Miércoles y Viernes 3:30 - 5:00 PM', TRUE),
(15, 'Club de Fútbol Selección Femenina', 'Entrenamiento deportivo y participación en copas escolares', 9, 30, 'Martes y Jueves 3:30 - 5:00 PM', TRUE),
(16, 'Club de Baloncesto y Táctica', 'Fundamentos de dribling, lanzamiento y juego en equipo', 9, 25, 'Martes y Jueves 3:30 - 5:00 PM', TRUE),
(17, 'Club de Voleibol Mixto', 'Voleo, remate, saques y preparación competitiva', 9, 25, 'Lunes y Miércoles 3:30 - 5:00 PM', TRUE),
(18, 'Club de Natación y Salvamento', 'Estilos libre, espalda, pecho, mariposa y condición física', 29, 25, 'Martes y Jueves 3:30 - 5:00 PM', TRUE),
(19, 'Club de Atletismo y Resistencia', 'Velocidad, fondo, relevos y salto largo', 29, 30, 'Miércoles y Viernes 3:30 - 5:00 PM', TRUE),
(20, 'Club de Gimnasia Rítmica y Acrobacia', 'Coordinación, flexibilidad y esquemas rítmicos', 24, 22, 'Lunes y Viernes 3:30 - 5:00 PM', TRUE),
(21, 'Club de Idiomas y Cultura Francesa', 'Conversación, gastronomía y literatura francófona', 14, 20, 'Miércoles 3:30 - 5:00 PM', TRUE),
(22, 'Club de Idioma Alemán', 'Iniciación lingüística y tradiciones germánicas', 8, 20, 'Viernes 3:30 - 5:00 PM', TRUE),
(23, 'Club de Ecología y Huerto Sostenible', 'Reciclaje, compostaje y cultivo hidropónico escolar', 26, 25, 'Jueves 3:30 - 5:00 PM', TRUE),
(24, 'Club de Emprendimiento y Finanzas', 'Creación de modelos de negocio sostenibles e inversión básica', 23, 25, 'Martes 3:30 - 5:00 PM', TRUE),
(25, 'Club de Fotografía Digital y Edición', 'Manejo de cámaras réflex, iluminación y retoque en Lightroom', 32, 22, 'Lunes 3:30 - 5:00 PM', TRUE),
(26, 'Club de Animación Digital y Videojuegos', 'Diseño de personajes 2D/3D y animación en software libre', 32, 20, 'Miércoles 3:30 - 5:00 PM', TRUE),
(27, 'Club de Cine y Producción Audiovisual', 'Cine foro, apreciación cinematográfica y cortometrajes', 25, 25, 'Viernes 3:30 - 6:00 PM', TRUE),
(28, 'Club de Filosofía y Pensamiento Crítico', 'Debate ético, bioética y análisis de sociedad moderna', 7, 20, 'Jueves 3:30 - 5:00 PM', TRUE),
(29, 'Club de Primeros Auxilios y Brigada', 'Atención prehospitalaria, simulacros y prevención', 4, 30, 'Martes 3:30 - 5:00 PM', TRUE),
(30, 'Club de Yoga y Mindfulness', 'Técnicas de respiración, concentración y bienestar emocional', 24, 25, 'Lunes y Miércoles 3:30 - 4:30 PM', TRUE);

-- ----------------------------------------------------------------------------
-- 13. TABLA: membresias_club (35 Registros)
-- ----------------------------------------------------------------------------
INSERT INTO membresias_club (id_membresia, id_estudiante, id_club, fecha_ingreso, estado_membresia) VALUES
(1, 1, 1, '2024-02-05', 'Activa'),
(2, 1, 5, '2024-02-05', 'Activa'),
(3, 2, 2, '2024-02-05', 'Activa'),
(4, 2, 13, '2024-02-05', 'Activa'),
(5, 3, 3, '2024-02-06', 'Activa'),
(6, 4, 4, '2024-02-06', 'Activa'),
(7, 5, 14, '2024-02-06', 'Activa'),
(8, 6, 8, '2024-02-07', 'Activa'),
(9, 7, 16, '2024-02-07', 'Activa'),
(10, 8, 11, '2024-02-07', 'Activa'),
(11, 9, 18, '2024-02-08', 'Activa'),
(12, 10, 10, '2024-02-08', 'Activa'),
(13, 11, 6, '2024-02-08', 'Activa'),
(14, 12, 12, '2024-02-09', 'Activa'),
(15, 13, 17, '2024-02-09', 'Activa'),
(16, 14, 15, '2024-02-09', 'Activa'),
(17, 15, 5, '2024-02-12', 'Activa'),
(18, 16, 8, '2024-02-12', 'Activa'),
(19, 17, 18, '2024-02-12', 'Activa'),
(20, 18, 11, '2024-02-13', 'Activa'),
(21, 19, 9, '2024-02-13', 'Activa'),
(22, 20, 10, '2024-02-13', 'Activa'),
(23, 21, 8, '2024-02-14', 'Activa'),
(24, 22, 11, '2024-02-14', 'Activa'),
(25, 23, 18, '2024-02-14', 'Activa'),
(26, 24, 8, '2024-02-15', 'Activa'),
(27, 25, 4, '2024-02-15', 'Activa'),
(28, 25, 24, '2024-02-15', 'Activa'),
(29, 26, 12, '2024-02-15', 'Activa'),
(30, 27, 1, '2024-02-16', 'Activa'),
(31, 28, 9, '2024-02-16', 'Activa'),
(32, 29, 14, '2024-02-16', 'Activa'),
(33, 30, 21, '2024-02-19', 'Activa'),
(34, 31, 23, '2024-02-19', 'Activa'),
(35, 32, 25, '2024-02-19', 'Activa');

-- ----------------------------------------------------------------------------
-- 14. TABLA: eventos (10 Eventos institucionales)
-- ----------------------------------------------------------------------------
INSERT INTO eventos (id_evento, nombre_evento, descripcion, tipo_evento, fecha_evento, lugar, tiene_competencia, id_organizador, esta_activo) VALUES
(1, 'Feria de Ciencias y Tecnología 2024', 'Presentación de proyectos científicos, experimentos y prototipos tecnológicos', 'Científico', '2024-05-17', 'Coliseo Principal', TRUE, 3, TRUE),
(2, 'Olimpiadas de Matemáticas Little Horizons', 'Competencia matemática intercolegiada por categorías', 'Académico', '2024-06-05', 'Auditorio Central', TRUE, 1, TRUE),
(3, 'Día de las Américas y la Interculturalidad', 'Muestras culturales, gastronómicas y danzas tradicionales', 'Cultural', '2024-04-12', 'Plaza Cívica', FALSE, 6, TRUE),
(4, 'Festival de las Artes y el Teatro', 'Obras de teatro, exposición de artes visuales y música', 'Cultural', '2024-08-23', 'Teatro Institucional', TRUE, 10, TRUE),
(5, 'Juegos Deportivos Intercursos 2024', 'Competencias de fútbol, baloncesto, voleibol y natación', 'Deportivo', '2024-09-18', 'Campos Deportivos', TRUE, 9, TRUE),
(6, 'Modelo de Naciones Unidas (LH-MUN)', 'Simulación diplomática de comités internacionales', 'Académico', '2024-10-10', 'Salas de Conferencias', TRUE, 7, TRUE),
(7, 'Jornada Ecológica y Siembra de Árboles', 'Actividad comunitaria de conservación ambiental y reciclaje', 'Social', '2024-04-22', 'Reserva Ecológica del Colegio', FALSE, 26, TRUE),
(8, 'Feria Empresarial y de Innovación', 'Exposición de planes de negocio de estudiantes de 10° y 11°', 'Académico', '2024-10-25', 'Coliseo Principal', TRUE, 23, TRUE),
(9, 'Noche de las Estrellas y Observación', 'Jornada nocturna de astronomía y campamento guiado', 'Científico', '2024-08-09', 'Cancha de Fútbol y Mirador', FALSE, 31, TRUE),
(10, 'Concierto de Gala de Navidad', 'Presentación del coro y la orquesta sinfónica escolar', 'Cultural', '2024-11-29', 'Auditorio Central', FALSE, 11, TRUE);

-- ----------------------------------------------------------------------------
-- 15. TABLA: participantes_evento (35 Participaciones)
-- ----------------------------------------------------------------------------
INSERT INTO participantes_evento (id_participacion, id_evento, id_estudiante, rol_participacion, posicion_obtenida, observaciones) VALUES
(1, 1, 1, 'Competidor', '1er Lugar', 'Proyecto: Brazo robótico con servomotores y visión artificial'),
(2, 1, 2, 'Competidor', '2do Lugar', 'Proyecto: Generador de biogás a partir de residuos orgánicos'),
(3, 1, 3, 'Competidor', '3er Lugar', 'Proyecto: Sistema IoT de monitoreo de calidad del aire'),
(4, 1, 4, 'Participante', 'Participante', 'Mención por creatividad en prototipo hidroeléctrico'),
(5, 2, 1, 'Competidor', '1er Lugar', 'Puntaje perfecto en fase final de cálculo'),
(6, 2, 3, 'Competidor', '2do Lugar', 'Excelente desempeño en álgebra analítica'),
(7, 2, 4, 'Competidor', '3er Lugar', 'Tercer lugar en categoría noveno grado'),
(8, 2, 5, 'Competidor', 'Participante', 'Finalista en fase eliminatoria'),
(9, 3, 6, 'Participante', 'Participante', 'Danza tradicional de la región andina'),
(10, 3, 7, 'Participante', 'Participante', 'Representación cultural de México'),
(11, 3, 8, 'Participante', 'Participante', 'Exposición gastronómica internacional'),
(12, 4, 10, 'Competidor', '1er Lugar', 'Mejor actriz principal en obra clásica'),
(13, 4, 16, 'Competidor', '2do Lugar', 'Primer puesto en escultura con material reciclado'),
(14, 4, 18, 'Participante', 'Participante', 'Coreografía destacada en danza moderna'),
(15, 5, 5, 'Competidor', '1er Lugar', 'Goleador del torneo de fútbol juvenil'),
(16, 5, 7, 'Competidor', '2do Lugar', 'Subcampeón en torneo de baloncesto'),
(17, 5, 9, 'Competidor', '1er Lugar', 'Medalla de oro en 100m estilo libre natación'),
(18, 5, 11, 'Competidor', '3er Lugar', 'Medalla de bronce en relevos 4x100m'),
(19, 5, 14, 'Competidor', '1er Lugar', 'Campeona torneo de voleibol femenino'),
(20, 6, 25, 'Competidor', '1er Lugar', 'Mejor delegado en comité de Seguridad ONU'),
(21, 6, 26, 'Competidor', '2do Lugar', 'Delegada sobresaliente en comité de Derechos Humanos'),
(22, 6, 2, 'Competidor', '3er Lugar', 'Mención de honor delegación de Francia'),
(23, 7, 12, 'Organizador', 'Participante', 'Líder estudiantil brigada ambiental'),
(24, 7, 13, 'Participante', 'Participante', 'Siembra de árboles nativos'),
(25, 7, 15, 'Participante', 'Participante', 'Gestión de reciclaje escolar'),
(26, 8, 25, 'Competidor', '1er Lugar', 'Emprendimiento: BioEnvases biodegradables'),
(27, 8, 26, 'Competidor', '2do Lugar', 'Emprendimiento: Plataforma de tutorías entre pares'),
(28, 8, 27, 'Competidor', '3er Lugar', 'Emprendimiento: Snacks saludables escolares'),
(29, 9, 2, 'Participante', 'Participante', 'Operadora de telescopio refractor'),
(30, 9, 17, 'Participante', 'Participante', 'Asistente campamento nocturno'),
(31, 9, 19, 'Participante', 'Participante', 'Asistente campamento nocturno'),
(32, 10, 11, 'Participante', 'Participante', 'Solista de violín en villancico universal'),
(33, 10, 20, 'Participante', 'Participante', 'Corista en ensamble vocal'),
(34, 10, 21, 'Participante', 'Participante', 'Percusionista en ensamble infantil'),
(35, 10, 28, 'Participante', 'Participante', 'Solista de flauta traversa');

-- ----------------------------------------------------------------------------
-- 16. TABLA: conceptos_pago (10 Conceptos)
-- ----------------------------------------------------------------------------
INSERT INTO conceptos_pago (id_concepto, codigo_concepto, nombre_concepto, descripcion, monto_base, es_recurrente, esta_activo) VALUES
(1, 'MAT', 'Matrícula Anual', 'Pago ordinario anual de matrícula académica', 650000.00, FALSE, TRUE),
(2, 'PEN', 'Pensión Mensual', 'Cuota mensual de pensión académica escolar', 450000.00, TRUE, TRUE),
(3, 'TRA', 'Transporte Escolar', 'Ruta escolar puerta a puerta mensual', 180000.00, TRUE, TRUE),
(4, 'ALI', 'Alimentación Escolar', 'Servicio mensual de almuerzo y refrigerio', 160000.00, TRUE, TRUE),
(5, 'UNI', 'Uniforme Institucional', 'Dotación de uniforme de diario y educación física', 280000.00, FALSE, TRUE),
(6, 'EXT', 'Club Extracurricular', 'Membresía mensual a clubes deportivos y artísticos', 70000.00, TRUE, TRUE),
(7, 'SAL', 'Salida Pedagógica', 'Transporte, entradas y seguro para salidas de campo', 60000.00, FALSE, TRUE),
(8, 'CER', 'Certificados y Constancias', 'Expedición de certificados académicos oficiales', 20000.00, FALSE, TRUE),
(9, 'LIB', 'Kit de Textos y Guías', 'Paquete de guías escolares y plataformas virtuales', 220000.00, FALSE, TRUE),
(10, 'SEH', 'Seguro Escolar de Accidentes', 'Póliza médica y de accidentes estudiantil anual', 55000.00, FALSE, TRUE);

-- ----------------------------------------------------------------------------
-- 17. TABLA: pagos (35 Registros - Mínimo 30 requerido)
-- ----------------------------------------------------------------------------
INSERT INTO pagos (id_pago, id_estudiante, id_acudiente, id_concepto, monto_pagado, fecha_pago, metodo_pago, numero_recibo, año_academico, periodo_academico, notas) VALUES
(1, 1, 1, 1, 650000.00, '2024-01-20', 'Transferencia', 'REC-2024-0001', 2024, 'Anual', 'Matrícula 2024 pagada a tiempo'),
(2, 1, 1, 2, 450000.00, '2024-02-05', 'Transferencia', 'REC-2024-0002', 2024, 'I', 'Pensión febrero 2024'),
(3, 1, 1, 2, 450000.00, '2024-03-05', 'Transferencia', 'REC-2024-0003', 2024, 'I', 'Pensión marzo 2024'),
(4, 2, 3, 1, 650000.00, '2024-01-20', 'Tarjeta Débito', 'REC-2024-0004', 2024, 'Anual', 'Matrícula 2024 estudiante Valentina'),
(5, 2, 3, 2, 450000.00, '2024-02-04', 'Tarjeta Débito', 'REC-2024-0005', 2024, 'I', 'Pensión febrero 2024'),
(6, 2, 3, 3, 180000.00, '2024-02-04', 'Tarjeta Débito', 'REC-2024-0006', 2024, 'I', 'Ruta escolar febrero 2024'),
(7, 3, 5, 1, 650000.00, '2024-01-21', 'Efectivo', 'REC-2024-0007', 2024, 'Anual', 'Pago en caja matrícula Mateo'),
(8, 3, 5, 2, 450000.00, '2024-02-06', 'Efectivo', 'REC-2024-0008', 2024, 'I', 'Pensión febrero 2024'),
(9, 4, 6, 1, 650000.00, '2024-01-21', 'Transferencia', 'REC-2024-0009', 2024, 'Anual', 'Matrícula 2024 Isabella'),
(10, 4, 6, 2, 450000.00, '2024-02-05', 'Transferencia', 'REC-2024-0010', 2024, 'I', 'Pensión febrero 2024'),
(11, 4, 6, 4, 160000.00, '2024-02-05', 'Transferencia', 'REC-2024-0011', 2024, 'I', 'Alimentación escolar febrero'),
(12, 5, 7, 1, 650000.00, '2024-01-22', 'Tarjeta Crédito', 'REC-2024-0012', 2024, 'Anual', 'Matrícula Samuel David'),
(13, 5, 7, 2, 450000.00, '2024-02-07', 'Tarjeta Crédito', 'REC-2024-0013', 2024, 'I', 'Pensión febrero 2024'),
(14, 6, 8, 1, 650000.00, '2024-01-22', 'Transferencia', 'REC-2024-0014', 2024, 'Anual', 'Matrícula Mariana'),
(15, 6, 8, 2, 450000.00, '2024-02-08', 'Transferencia', 'REC-2024-0015', 2024, 'I', 'Pensión febrero 2024'),
(16, 7, 9, 1, 650000.00, '2024-01-22', 'Efectivo', 'REC-2024-0016', 2024, 'Anual', 'Matrícula Sebastián Felipe'),
(17, 7, 9, 2, 450000.00, '2024-02-10', 'Efectivo', 'REC-2024-0017', 2024, 'I', 'Pensión febrero 2024'),
(18, 8, 10, 1, 650000.00, '2024-01-22', 'Transferencia', 'REC-2024-0018', 2024, 'Anual', 'Matrícula Camila'),
(19, 8, 10, 2, 450000.00, '2024-02-06', 'Transferencia', 'REC-2024-0019', 2024, 'I', 'Pensión febrero 2024'),
(20, 9, 11, 1, 650000.00, '2024-01-23', 'Tarjeta Débito', 'REC-2024-0020', 2024, 'Anual', 'Matrícula Nicolás'),
(21, 10, 12, 1, 650000.00, '2024-01-23', 'Transferencia', 'REC-2024-0021', 2024, 'Anual', 'Matrícula Sofía'),
(22, 11, 13, 1, 650000.00, '2024-01-23', 'Transferencia', 'REC-2024-0022', 2024, 'Anual', 'Matrícula Alejandro'),
(23, 12, 14, 1, 650000.00, '2024-01-23', 'Transferencia', 'REC-2024-0023', 2024, 'Anual', 'Matrícula Luciana'),
(24, 13, 15, 1, 650000.00, '2024-01-24', 'Efectivo', 'REC-2024-0024', 2024, 'Anual', 'Matrícula Daniel'),
(25, 14, 16, 1, 650000.00, '2024-01-24', 'Transferencia', 'REC-2024-0025', 2024, 'Anual', 'Matrícula Gabriela'),
(26, 15, 17, 1, 650000.00, '2024-01-24', 'Tarjeta Débito', 'REC-2024-0026', 2024, 'Anual', 'Matrícula Emilio'),
(27, 16, 18, 1, 650000.00, '2024-01-24', 'Transferencia', 'REC-2024-0027', 2024, 'Anual', 'Matrícula Salomé'),
(28, 17, 19, 1, 650000.00, '2024-01-25', 'Transferencia', 'REC-2024-0028', 2024, 'Anual', 'Matrícula Lucas'),
(29, 18, 20, 1, 650000.00, '2024-01-25', 'Transferencia', 'REC-2024-0029', 2024, 'Anual', 'Matrícula Juliana'),
(30, 19, 21, 1, 650000.00, '2024-01-25', 'Tarjeta Débito', 'REC-2024-0030', 2024, 'Anual', 'Matrícula Martín'),
(31, 20, 22, 1, 650000.00, '2024-01-25', 'Transferencia', 'REC-2024-0031', 2024, 'Anual', 'Matrícula Valeria'),
(32, 25, 27, 1, 650000.00, '2024-01-19', 'Transferencia', 'REC-2024-0032', 2024, 'Anual', 'Matrícula David Ricardo'),
(33, 25, 27, 2, 450000.00, '2024-02-05', 'Transferencia', 'REC-2024-0033', 2024, 'I', 'Pensión febrero David'),
(34, 26, 28, 1, 650000.00, '2024-01-19', 'Transferencia', 'REC-2024-0034', 2024, 'Anual', 'Matrícula Paula Daniela'),
(35, 26, 28, 2, 450000.00, '2024-02-05', 'Transferencia', 'REC-2024-0035', 2024, 'I', 'Pensión febrero Paula');

-- ----------------------------------------------------------------------------
-- 18. TABLA: facturas (Facturas Electrónicas generadas en formato TXT)
-- ----------------------------------------------------------------------------
INSERT INTO facturas (id_factura, numero_factura, id_pago, fecha_emision, cufe, nombre_archivo_txt, ruta_archivo) VALUES
(1, 'FAC-LH-000001', 1, '2024-01-20', 'a1b2c3d4e5f67890123456789abcdef0123456789abcdef0123456789abcdef0', 'factura_FAC-LH-000001.txt', 'invoices/factura_FAC-LH-000001.txt'),
(2, 'FAC-LH-000002', 2, '2024-02-05', 'b2c3d4e5f6a17890123456789abcdef0123456789abcdef0123456789abcdef0', 'factura_FAC-LH-000002.txt', 'invoices/factura_FAC-LH-000002.txt'),
(3, 'FAC-LH-000003', 3, '2024-03-05', 'c3d4e5f6a1b27890123456789abcdef0123456789abcdef0123456789abcdef0', 'factura_FAC-LH-000003.txt', 'invoices/factura_FAC-LH-000003.txt'),
(4, 'FAC-LH-000004', 4, '2024-01-20', 'd4e5f6a1b2c37890123456789abcdef0123456789abcdef0123456789abcdef0', 'factura_FAC-LH-000004.txt', 'invoices/factura_FAC-LH-000004.txt'),
(5, 'FAC-LH-000005', 5, '2024-02-04', 'e5f6a1b2c3d47890123456789abcdef0123456789abcdef0123456789abcdef0', 'factura_FAC-LH-000005.txt', 'invoices/factura_FAC-LH-000005.txt'),
(6, 'FAC-LH-000006', 6, '2024-02-04', 'f6a1b2c3d4e57890123456789abcdef0123456789abcdef0123456789abcdef0', 'factura_FAC-LH-000006.txt', 'invoices/factura_FAC-LH-000006.txt'),
(7, 'FAC-LH-000007', 7, '2024-01-21', '0123456789abcdefa1b2c3d4e5f67890123456789abcdef0123456789abcdef0', 'factura_FAC-LH-000007.txt', 'invoices/factura_FAC-LH-000007.txt'),
(8, 'FAC-LH-000008', 8, '2024-02-06', '123456789abcdef0a1b2c3d4e5f67890123456789abcdef0123456789abcdef0', 'factura_FAC-LH-000008.txt', 'invoices/factura_FAC-LH-000008.txt'),
(9, 'FAC-LH-000009', 9, '2024-01-21', '23456789abcdef01a1b2c3d4e5f67890123456789abcdef0123456789abcdef0', 'factura_FAC-LH-000009.txt', 'invoices/factura_FAC-LH-000009.txt'),
(10, 'FAC-LH-000010', 10, '2024-02-05', '3456789abcdef012a1b2c3d4e5f67890123456789abcdef0123456789abcdef0', 'factura_FAC-LH-000010.txt', 'invoices/factura_FAC-LH-000010.txt');

-- ----------------------------------------------------------------------------
-- 19. TABLA: categorias_recurso (5 Categorías)
-- ----------------------------------------------------------------------------
INSERT INTO categorias_recurso (id_categoria, nombre_categoria, descripcion) VALUES
(1, 'Libro', 'Libros de la biblioteca escolar, enciclopedias y literatura'),
(2, 'Equipo de Laboratorio', 'Equipos científicos (microscopios, balanzas, reactivos y mecheros)'),
(3, 'Equipo Deportivo', 'Balones, conos, redes, cronómetros y material de gimnasia'),
(4, 'Dispositivo Tecnológico', 'Tablets, calculadoras científicas, portátiles y proyectores'),
(5, 'Material Didáctico', 'Modelos anatómicos, mapas murales, fósiles y telescopios');

-- ----------------------------------------------------------------------------
-- 20. TABLA: recursos (35 Registros - Mínimo 30 requerido)
-- ----------------------------------------------------------------------------
INSERT INTO recursos (id_recurso, codigo_recurso, nombre_recurso, id_categoria, descripcion, fecha_adquisicion, ubicacion_fisica, estado_recurso) VALUES
(1, 'LIB-001', 'Cien Años de Soledad - Gabriel García Márquez', 1, 'Edición conmemorativa ilustrada', '2021-03-10', 'Biblioteca - Estante L1', 'Disponible'),
(2, 'LIB-002', 'Don Quijote de la Mancha - Miguel de Cervantes', 1, 'Edición RAE completa con notas al pie', '2021-03-10', 'Biblioteca - Estante L1', 'Disponible'),
(3, 'LIB-003', 'El Principito - Antoine de Saint-Exupéry', 1, 'Traducción clásica con acuarelas originales', '2021-03-10', 'Biblioteca - Estante L2', 'Disponible'),
(4, 'LIB-004', 'Cálculo de una Variable - James Stewart', 1, 'Octava edición, problemas resueltos', '2022-01-15', 'Biblioteca - Estante M3', 'Prestado'),
(5, 'LIB-005', 'Álgebra de Baldor con CD interactivo', 1, 'Nueva edición con ejercicios ilustrados', '2020-02-01', 'Biblioteca - Estante M1', 'Disponible'),
(6, 'LIB-006', 'Física Universitaria Vol 1 - Sears & Zemansky', 1, 'Mecánica y ondas para educación media', '2022-01-15', 'Biblioteca - Estante F2', 'Disponible'),
(7, 'LIB-007', 'Química: La Ciencia Central - Brown & LeMay', 1, 'Duodécima edición con laboratorio', '2022-01-15', 'Biblioteca - Estante Q1', 'Prestado'),
(8, 'LIB-008', 'Biología Celular y Molecular - Lodish', 1, 'Edición médica de consulta docente', '2022-06-20', 'Biblioteca - Estante B2', 'Disponible'),
(9, 'LIB-009', 'Historia Universal Ilustrada - Larousse', 1, 'Enciclopedia de civilizaciones antiguas', '2020-08-14', 'Biblioteca - Estante S3', 'Disponible'),
(10, 'LIB-010', 'Diccionario Oxford Advanced Learner', 1, 'Diccionario bilingüe inglés-español avanzado', '2021-09-05', 'Biblioteca - Estante I1', 'Disponible'),
(11, 'LAB-001', 'Microscopio Óptico Binocular Olympus CX23', 2, 'Aumentos 40x, 100x, 400x y 1000x inmersión', '2022-03-18', 'Laboratorio de Biología - Mesa 1', 'Prestado'),
(12, 'LAB-002', 'Microscopio Óptico Binocular Olympus CX23', 2, 'Aumentos 40x, 100x, 400x y 1000x inmersión', '2022-03-18', 'Laboratorio de Biología - Mesa 2', 'Disponible'),
(13, 'LAB-003', 'Balanza Analítica Digital Radwag AS 220.R2', 2, 'Precisión de 0.0001g con cabina cortavientos', '2022-05-10', 'Laboratorio de Química - Mesa Central', 'Disponible'),
(14, 'LAB-004', 'Kit de Destilación Fraccionada y Refrigerante', 2, 'Vidrio borosilicato 3.3 con matraz de 500ml', '2022-05-10', 'Laboratorio de Química - Armario B', 'Disponible'),
(15, 'LAB-005', 'Sensor de pH Digital Pasco con Interfaz USB', 2, 'Electrodo sumergible para titulación ácida', '2023-02-14', 'Laboratorio de Química - Gaveta 3', 'Disponible'),
(16, 'LAB-006', 'Kit de Cinemática y Riel de Aire Pasco', 2, 'Medición de velocidad y aceleración por fotocompuertas', '2023-02-14', 'Laboratorio de Física - Estante F1', 'Prestado'),
(17, 'LAB-007', 'Generador Van de Graaff Electrostático', 2, 'Esfera conductora de 25cm para cargas estáticas', '2021-11-20', 'Laboratorio de Física - Mesa 4', 'Disponible'),
(18, 'DEP-001', 'Balón de Fútbol Golty Profesional FIFA', 3, 'Balón No. 5 cosido a mano para grama natural', '2023-01-20', 'Almacén Deportivo - Canasta 1', 'Disponible'),
(19, 'DEP-002', 'Balón de Baloncesto Molten BG4500 Cuero', 3, 'Balón oficial tamaño No. 7 para coliseo', '2023-01-20', 'Almacén Deportivo - Canasta 2', 'Disponible'),
(20, 'DEP-003', 'Balón de Voleibol Mikasa V200W Oficial', 3, 'Microfibra aerodinámica de 18 paneles', '2023-01-20', 'Almacén Deportivo - Canasta 3', 'Disponible'),
(21, 'DEP-004', 'Set de 10 Vallas de Salto Graduables', 3, 'Altura ajustable de 60 a 106 cm para atletismo', '2023-04-12', 'Almacén Deportivo - Zona Vallas', 'Disponible'),
(22, 'DEP-005', 'Cronómetro Digital Profesional Casio HS-80TW', 3, 'Precisión 1/1000 seg con memoria de 100 vueltas', '2022-09-08', 'Almacén Deportivo - Vitrina 1', 'Prestado'),
(23, 'DEP-006', 'Set de 20 Conos de Entrenamiento de 30cm', 3, 'Material flexible color naranja reflectivo', '2023-01-20', 'Almacén Deportivo - Canasta 4', 'Disponible'),
(24, 'TEC-001', 'Tablet Samsung Galaxy Tab S7 FE 128GB', 4, 'Pantalla 12.4 pulgadas con S-Pen para diseño', '2023-03-15', 'Aula de Informática - Carrito A', 'Prestado'),
(25, 'TEC-002', 'Tablet Samsung Galaxy Tab S7 FE 128GB', 4, 'Pantalla 12.4 pulgadas con S-Pen para diseño', '2023-03-15', 'Aula de Informática - Carrito A', 'Disponible'),
(26, 'TEC-003', 'Calculadora Graficadora Texas Instruments TI-Nspire CX', 4, 'Pantalla a color, álgebra simbólica CAS', '2022-08-10', 'Biblioteca - Vitrina Electrónica', 'Prestado'),
(27, 'TEC-004', 'Calculadora Graficadora Texas Instruments TI-Nspire CX', 4, 'Pantalla a color, álgebra simbólica CAS', '2022-08-10', 'Biblioteca - Vitrina Electrónica', 'Disponible'),
(28, 'TEC-005', 'Videoproyector Epson PowerLite L200W Láser', 4, '4200 lúmenes WXGA con conexión inalámbrica', '2023-01-18', 'Audiovisuales - Estante Proyectores', 'Prestado'),
(29, 'TEC-006', 'Kit de Robótica LEGO Mindstorms EV3 Education', 4, 'Set principal de sensores táctiles, color y giroscopio', '2021-07-25', 'Taller de Robótica - Armario R1', 'Prestado'),
(30, 'TEC-007', 'Kit de Robótica LEGO Mindstorms EV3 Education', 4, 'Set principal con ladrillo inteligente EV3', '2021-07-25', 'Taller de Robótica - Armario R1', 'Disponible'),
(31, 'DID-001', 'Esqueleto Humano Articulado Escala Real 170cm', 5, 'Modelo anatómico con soporte rodante y nervios', '2020-05-15', 'Laboratorio de Biología - Esquina Sur', 'Disponible'),
(32, 'DID-002', 'Torso Humano Sexuado 28 Partes Desmontables', 5, 'Órganos internos detallados en resina médica', '2020-05-15', 'Laboratorio de Biología - Vitrina 2', 'Disponible'),
(33, 'DID-003', 'Telescopio Celestron Omni XLT 150 Newtoniano', 5, 'Apertura 150mm con montura ecuatorial CG-4', '2022-10-05', 'Observatorio Escolar - Sala Cielos', 'Prestado'),
(34, 'DID-004', 'Globo Terráqueo Físico-Político Iluminado 40cm', 5, 'Relieve tridimensional y luz LED interna', '2021-04-12', 'Biblioteca - Sala de Geografía', 'Disponible'),
(35, 'DID-005', 'Set de Sólidos Geométricos Transparentes 15 Pcs', 5, 'Figuras 3D con secciones transversales de color', '2021-04-12', 'Aula de Matemáticas - Módulo 2', 'Disponible');

-- ----------------------------------------------------------------------------
-- 21. TABLA: prestamos_estudiante (10 Préstamos)
-- ----------------------------------------------------------------------------
INSERT INTO prestamos_estudiante (id_prestamo, id_recurso, id_estudiante, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado_prestamo, observaciones) VALUES
(1, 4, 1, '2024-03-01', '2024-03-08', '2024-03-08', 'Devuelto', 'Libro devuelto en excelente estado'),
(2, 4, 25, '2024-04-01', '2024-04-15', NULL, 'Activo', 'Préstamo para preparación Olimpiadas'),
(3, 7, 2, '2024-04-05', '2024-04-12', NULL, 'Activo', 'Consulta para proyecto de feria de ciencias'),
(4, 24, 1, '2024-04-08', '2024-04-15', NULL, 'Activo', 'Tablet solicitada para simulación de robótica'),
(5, 26, 3, '2024-04-10', '2024-04-17', NULL, 'Activo', 'Calculadora graficadora para examen de cálculo'),
(6, 3, 5, '2024-02-10', '2024-02-17', '2024-02-16', 'Devuelto', 'Devolución oportuna sin daños'),
(7, 5, 7, '2024-03-10', '2024-03-17', '2024-03-17', 'Devuelto', 'Álgebra de Baldor devuelta completa'),
(8, 29, 27, '2024-04-02', '2024-04-09', NULL, 'Vencido', 'Kit de robótica pendiente de entrega'),
(9, 1, 9, '2024-02-20', '2024-02-27', '2024-02-26', 'Devuelto', 'Cien Años de Soledad entregado'),
(10, 2, 10, '2024-03-05', '2024-03-12', '2024-03-12', 'Devuelto', 'Don Quijote entregado a tiempo');

-- ----------------------------------------------------------------------------
-- 22. TABLA: prestamos_profesor (8 Préstamos)
-- ----------------------------------------------------------------------------
INSERT INTO prestamos_profesor (id_prestamo, id_recurso, id_profesor, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado_prestamo, observaciones) VALUES
(1, 11, 3, '2024-04-02', '2024-04-30', NULL, 'Activo', 'Microscopio para prácticas de biología grado 6° y 7°'),
(2, 16, 5, '2024-04-05', '2024-04-20', NULL, 'Activo', 'Riel de aire para experimentos de cinemática'),
(3, 22, 9, '2024-04-01', '2024-04-30', NULL, 'Activo', 'Cronómetro profesional para pruebas de atletismo'),
(4, 28, 7, '2024-04-10', '2024-04-11', '2024-04-11', 'Devuelto', 'Proyector usado en cine foro filosófico'),
(5, 33, 31, '2024-04-08', '2024-04-22', NULL, 'Activo', 'Telescopio para observación del club de astronomía'),
(6, 13, 4, '2024-03-01', '2024-03-15', '2024-03-15', 'Devuelto', 'Balanza analítica para preparación reactivos'),
(7, 28, 1, '2024-03-20', '2024-03-20', '2024-03-20', 'Devuelto', 'Proyector entregado sin novedades'),
(8, 14, 4, '2024-03-10', '2024-03-25', '2024-03-24', 'Devuelto', 'Kit de destilación devuelto limpio');

-- ============================================================================
-- VERIFICACIÓN DE REQUISITOS (Mínimo 30 registros en tablas principales)
-- ============================================================================
SELECT 'estudiantes' AS Tabla, COUNT(*) AS Total_Registros FROM estudiantes
UNION ALL
SELECT 'acudientes', COUNT(*) FROM acudientes
UNION ALL
SELECT 'profesores', COUNT(*) FROM profesores
UNION ALL
SELECT 'materias', COUNT(*) FROM materias
UNION ALL
SELECT 'registros_academicos', COUNT(*) FROM registros_academicos
UNION ALL
SELECT 'pagos', COUNT(*) FROM pagos
UNION ALL
SELECT 'clubes', COUNT(*) FROM clubes
UNION ALL
SELECT 'recursos', COUNT(*) FROM recursos;
