CREATE TABLE pacientes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(80) NOT NULL,
    documento VARCHAR(20) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    sexo VARCHAR(20) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(120),
    direccion VARCHAR(200),
    tipo_sangre VARCHAR(10),
    alergias TEXT,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pacientes_documento (documento),
    KEY idx_pacientes_apellidos (apellidos),
    KEY idx_pacientes_nombres (nombres)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE medicos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(80) NOT NULL,
    especialidad VARCHAR(80) NOT NULL,
    colegiatura VARCHAR(30) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(120),
    horario VARCHAR(120),
    activo TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_medicos_colegiatura (colegiatura),
    KEY idx_medicos_especialidad (especialidad)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE citas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    paciente_id BIGINT NOT NULL,
    medico_id BIGINT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADA',
    observaciones TEXT,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    KEY idx_citas_fecha_hora (fecha_hora),
    KEY idx_citas_estado (estado),
    KEY idx_citas_medico_fecha (medico_id, fecha_hora),
    KEY idx_citas_paciente_fecha (paciente_id, fecha_hora),
    CONSTRAINT fk_citas_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_citas_medico FOREIGN KEY (medico_id) REFERENCES medicos (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE historiales (
    id BIGINT NOT NULL AUTO_INCREMENT,
    paciente_id BIGINT NOT NULL,
    medico_id BIGINT NOT NULL,
    cita_id BIGINT NULL,
    fecha_consulta DATETIME NOT NULL,
    motivo_consulta VARCHAR(255) NOT NULL,
    diagnostico TEXT NOT NULL,
    tratamiento TEXT,
    notas TEXT,
    peso DECIMAL(5, 2),
    talla DECIMAL(5, 2),
    presion_arterial VARCHAR(20),
    temperatura DECIMAL(4, 1),
    frecuencia_cardiaca INT,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_historiales_cita (cita_id),
    KEY idx_historiales_fecha (fecha_consulta),
    KEY idx_historiales_paciente (paciente_id),
    CONSTRAINT fk_historiales_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_historiales_medico FOREIGN KEY (medico_id) REFERENCES medicos (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_historiales_cita FOREIGN KEY (cita_id) REFERENCES citas (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE recetas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    paciente_id BIGINT NOT NULL,
    medico_id BIGINT NOT NULL,
    historial_id BIGINT NOT NULL,
    fecha_emision DATETIME NOT NULL,
    indicaciones_generales TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'EMITIDA',
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    KEY idx_recetas_fecha (fecha_emision),
    KEY idx_recetas_paciente (paciente_id),
    KEY idx_recetas_historial (historial_id),
    CONSTRAINT fk_recetas_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_recetas_medico FOREIGN KEY (medico_id) REFERENCES medicos (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_recetas_historial FOREIGN KEY (historial_id) REFERENCES historiales (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE receta_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    receta_id BIGINT NOT NULL,
    medicamento VARCHAR(120) NOT NULL,
    presentacion VARCHAR(80),
    dosis VARCHAR(80),
    frecuencia VARCHAR(80),
    duracion VARCHAR(60),
    cantidad VARCHAR(40),
    indicaciones VARCHAR(255),
    PRIMARY KEY (id),
    KEY idx_receta_items_receta (receta_id),
    CONSTRAINT fk_receta_items_receta FOREIGN KEY (receta_id) REFERENCES recetas (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
