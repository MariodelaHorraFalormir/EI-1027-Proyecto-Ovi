-- =========================================
-- ENUMS
-- =========================================

CREATE TYPE genero_enum AS ENUM ('Hombre', 'Mujer', 'Otro');

CREATE TYPE disponibilidad_enum AS ENUM ('Total', 'Parcial', 'No disponible');

CREATE TYPE status_pa_request_enum AS ENUM ('En espera', 'En activo', 'Caducada', 'Finalizada');

CREATE TYPE diversidad_funcional_enum AS ENUM ('Visual', 'Auditiva', 'Motora', 'Cognitiva', 'Psicosocial', 'Organica', 'Otra');

CREATE TYPE categoria_solicitud_enum AS ENUM ('Rol', 'Proceso');

CREATE TYPE detalle_solicitud_enum AS ENUM ('Pap_pati', 'Ovi_user', 'Pa_request', 'Asistencia_tecnica', 'Otro');

CREATE TYPE estado_rol_enum AS ENUM ('Pendiente', 'Activo', 'Rechazado');

CREATE TYPE estado_solicitud_enum AS ENUM ('Pendiente','Aprobada','Rechazada');
-- =========================================
-- PERSONA
-- =========================================

CREATE TABLE persona (
    id                INTEGER GENERATED ALWAYS AS IDENTITY,
    mail              VARCHAR(255) NOT NULL,
    nombre            VARCHAR(100) NOT NULL,
    apellidos     VARCHAR(100) NOT NULL,
    direccion         VARCHAR(50) NOT NULL,
    pais              VARCHAR(50) NOT NULL,
    telefono          VARCHAR(20) NOT NULL,
    fecha_alta        DATE DEFAULT CURRENT_DATE NOT NULL,
    fecha_baja        DATE,
    fecha_nacimiento  DATE NOT NULL,
    contrasena         VARCHAR(255) NOT NULL,
    dni                VARCHAR(9) NOT NULL, 
    genero            genero_enum NOT NULL,

    CONSTRAINT pk_persona PRIMARY KEY (id),
    CONSTRAINT uq_persona_mail UNIQUE (mail),
    CONSTRAINT uq_persona_dni UNIQUE (dni),
    CONSTRAINT ck_persona_fecha_baja
    CHECK (fecha_baja IS NULL OR fecha_baja >= fecha_alta),
    CONSTRAINT ck_persona_fecha_nacimiento
    CHECK (fecha_nacimiento <= CURRENT_DATE),
    CONSTRAINT ck_persona_fecha_alta_nacimiento
    CHECK (fecha_alta >= fecha_nacimiento),
    CONSTRAINT ck_fechas_futura
    CHECK ( fecha_alta <= CURRENT_TIMESTAMP)
);


-- =========================================
-- ADMIN_OVI
-- =========================================

CREATE TABLE admin_ovi (
    id INTEGER NOT NULL,

    CONSTRAINT pk_admin_ovi PRIMARY KEY (id),
    CONSTRAINT fk_admin_ovi_persona
        FOREIGN KEY (id)
        REFERENCES persona(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);


-- =========================================
-- OVI_USER
-- =========================================

CREATE TABLE ovi_user (
    id INTEGER NOT NULL,
    grado_diversidad_funcional INTEGER NOT NULL ,
    grado_dependencia INTEGER ,
    estado estado_rol_enum NOT NULL DEFAULT 'Pendiente',
    url_proyecto_de_vida VARCHAR(500) ,
    centro_social_referencia VARCHAR(255) , 
    CONSTRAINT pk_ovi_user PRIMARY KEY (id),
    CONSTRAINT fk_ovi_user_persona
        FOREIGN KEY (id)
        REFERENCES persona(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT ck_grado_diversidad_funcional CHECK ((grado_diversidad_funcional between 0 and 100)),
    CONSTRAINT ck_grado_dependencia CHECK (grado_dependencia IS NULL OR (grado_dependencia between 1 and 3))

);

-- =========================================
-- DIVERSIDAD_FUNCIONAL
-- =========================================

CREATE TABLE diversidad_funcional (
    id INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL,
    ovi_user        INTEGER NOT NULL,
    tipo            diversidad_funcional_enum NOT NULL,
    observaciones   VARCHAR(255),

     
    CONSTRAINT pk_diversidad_funcional PRIMARY KEY (id),
    CONSTRAINT fk_diversidad_funcional_ovi_user
        FOREIGN KEY (ovi_user)
        REFERENCES ovi_user(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT uq_diversidad_funcional
        UNIQUE (ovi_user, tipo)


);


-- =========================================
-- PAP_PATI
-- =========================================

CREATE TABLE pap_pati (
    id              INTEGER NOT NULL,
    disponibilidad  disponibilidad_enum NOT NULL,
    fecha_inicio_disponible  DATE NOT NULL,
    fecha_fin_disponible     DATE,
    experiencia            INTEGER NOT NULL DEFAULT 0,
    vehiculo_propio             BOOLEAN NOT NULL DEFAULT FALSE,
    carnet_conducir             BOOLEAN NOT NULL DEFAULT FALSE,
    url_cv                      VARCHAR(500),
    descripcion_perfil          VARCHAR(500),
    centro_social_referencia    VARCHAR(255),
    estado estado_rol_enum NOT NULL DEFAULT 'Pendiente',
    
    
    CONSTRAINT pk_pap_pati PRIMARY KEY (id),
    CONSTRAINT fk_pap_pati_persona
        FOREIGN KEY (id)
        REFERENCES persona(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT ck_pap_pati_experiencia CHECK (experiencia >= 0),
    CONSTRAINT ck_pap_pati_fechas_disponibilidad CHECK (fecha_fin_disponible IS NULL OR fecha_fin_disponible >= fecha_inicio_disponible)
);

-- =========================================
-- ESPECIALIDAD
-- =========================================
CREATE TABLE especialidad (
    id INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL,
    pap_pati INTEGER NOT NULL,
    especialidad  diversidad_funcional_enum NOT NULL,

    CONSTRAINT pk_especialidad PRIMARY KEY (id),
    CONSTRAINT fk_especialidad_pap_pati
        FOREIGN KEY (pap_pati)
        REFERENCES pap_pati(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT uq_especialidad UNIQUE (pap_pati, especialidad)
);

-- =========================================
-- PA_REQUEST
-- =========================================

CREATE TABLE pa_request (
    id                 INTEGER GENERATED ALWAYS AS IDENTITY,
    status             status_pa_request_enum NOT NULL,
    fecha_creacion     DATE DEFAULT CURRENT_DATE  NOT NULL,
    fecha_resolucion   DATE,
    ovi_user           INTEGER NOT NULL,
    

    CONSTRAINT pk_pa_request PRIMARY KEY (id),
    CONSTRAINT fk_pa_request_ovi_user
        FOREIGN KEY (ovi_user)
        REFERENCES ovi_user(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT ck_pa_request_fecha_creacion_no_futura
    CHECK (fecha_creacion <= CURRENT_DATE),
   CONSTRAINT ck_pa_request_fechas_coherentes
    CHECK (fecha_resolucion IS NULL OR fecha_resolucion >= fecha_creacion)
    

);


-- =========================================
-- CONTRATO
-- =========================================

CREATE TABLE contrato (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    fecha_inicio    DATE NOT NULL,
    fecha_fin       DATE NOT NULL,
    pa_request      INTEGER NOT NULL,

    url_documento   VARCHAR(500) NOT NULL,

    CONSTRAINT pk_contrato PRIMARY KEY (id),
    CONSTRAINT uq_contrato_pa_request UNIQUE (pa_request),
    CONSTRAINT uq_contrato_url_documento UNIQUE (url_documento),
    CONSTRAINT fk_contrato_pa_request
        FOREIGN KEY (pa_request)
        REFERENCES pa_request(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT , 
        CONSTRAINT ck_contrato_fechas
         CHECK (fecha_fin >= fecha_inicio)
);


-- =========================================
-- CONVERSACION
-- =========================================

CREATE TABLE conversacion (
    id          INTEGER GENERATED ALWAYS AS IDENTITY,
    pa_request  INTEGER NOT NULL,
    ovi_user    INTEGER NOT NULL,
    pap_pati    INTEGER NOT NULL,

    CONSTRAINT pk_conversacion PRIMARY KEY (id),
    CONSTRAINT uq_conversacion UNIQUE (pa_request, ovi_user, pap_pati),

    CONSTRAINT fk_conversacion_pa_request
        FOREIGN KEY (pa_request)
        REFERENCES pa_request(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_conversacion_ovi_user
        FOREIGN KEY (ovi_user)
        REFERENCES ovi_user(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_conversacion_pap_pati
        FOREIGN KEY (pap_pati)
        REFERENCES pap_pati(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);


-- =========================================
-- MENSAJE
-- =========================================

CREATE TABLE mensaje (
    id            INTEGER GENERATED ALWAYS AS IDENTITY,
    conversacion  INTEGER NOT NULL,
    emisor        INTEGER NOT NULL,
    contenido TEXT NOT NULL CHECK (length(trim(contenido)) > 0),
    fecha_hora    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT pk_mensaje PRIMARY KEY (id),

    CONSTRAINT fk_mensaje_conversacion
        FOREIGN KEY (conversacion)
        REFERENCES conversacion(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_mensaje_emisor
        FOREIGN KEY (emisor)
        REFERENCES persona(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
        
);
-- =========================================
-- CANDIDATURAS
-- =========================================
CREATE TABLE candidaturas (
    id            INTEGER GENERATED ALWAYS AS IDENTITY,
    pa_request    INTEGER NOT NULL,
    pap_pati      INTEGER NOT NULL,
    puntuacion     INTEGER DEFAULT 0 NOT NULL,

 
    CONSTRAINT pk_candidaturas PRIMARY KEY (id),

    CONSTRAINT uq_candidaturas UNIQUE (pap_pati, pa_request),

    CONSTRAINT fk_candidaturas_pa_request
        FOREIGN KEY (pa_request)
        REFERENCES pa_request(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_candidaturas_pap_pati
        FOREIGN KEY (pap_pati)
        REFERENCES pap_pati(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
-- =========================================
-- SOLICITUD
-- =========================================

CREATE TABLE solicitud (
    id                    INTEGER GENERATED ALWAYS AS IDENTITY,
    persona_solicitante   INTEGER NOT NULL,
    categoria             categoria_solicitud_enum NOT NULL,
    detalle               detalle_solicitud_enum NOT NULL,
    estado                estado_solicitud_enum NOT NULL DEFAULT 'Pendiente',
    mensaje_solicitante   VARCHAR(500),
    motivo_resolucion     VARCHAR(500),
    tecnico_revisor       INTEGER,
    fecha_creacion        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion      TIMESTAMP,

    CONSTRAINT pk_solicitud PRIMARY KEY (id),

    CONSTRAINT fk_solicitud_persona
        FOREIGN KEY (persona_solicitante)
        REFERENCES persona(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT ck_solicitud_fecha_creacion_no_futura
    CHECK (fecha_creacion <= CURRENT_TIMESTAMP),

    CONSTRAINT fk_solicitud_tecnico
        FOREIGN KEY (tecnico_revisor)
        REFERENCES admin_ovi(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    --- La fecha de resolución debe ser posterior o igual a la fecha de creación y no puede ser una fecha futura.
    CONSTRAINT ck_solicitud_fechas
        CHECK (
            fecha_resolucion IS NULL
            OR fecha_resolucion >= fecha_creacion AND fecha_resolucion <= CURRENT_TIMESTAMP
        ),
    --- Si la solicitud está pendiente, no debe haber fecha de resolución. Si está aprobada o rechazada, debe haber una fecha de resolución y sea anterior o igual a la fecha actual.
    CONSTRAINT ck_solicitud_estado_fecha
        CHECK (
            (estado = 'Pendiente' AND fecha_resolucion IS NULL) 
             OR (estado IN ('Aprobada', 'Rechazada') 
             AND fecha_resolucion IS NOT NULL)
        ),
    --- Si la solicitud está rechazada, debe haber un motivo de resolución. Si está pendiente o aprobada, no es necesario.
    CONSTRAINT ck_solicitud_rechazo_motivo
        CHECK (
            estado <> 'Rechazada'OR motivo_resolucion IS NOT NULL
        ),
    --- Si la solicitud está vista, debe haber un técnico revisor asignado. Si está pendiente, no es necesario.
    CONSTRAINT ck_solicitud_revision_tecnico
        CHECK (estado IN ('Pendiente')OR tecnico_revisor IS NOT NULL),

    --- Validación para asegurar que el detalle corresponda con la categoría
    CONSTRAINT ck_solicitud_categoria_detalle
        CHECK ((categoria = 'Rol' AND detalle IN ('Pap_pati', 'Ovi_user'))OR(categoria = 'Proceso' AND detalle IN ('Pa_request', 'Asistencia_tecnica', 'Otro')))
);