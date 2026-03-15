-- =========================================
-- ENUMS
-- =========================================

CREATE TYPE sexo_enum AS ENUM ('Hombre', 'Mujer', 'Otro');

CREATE TYPE disponibilidad_enum AS ENUM ('Total', 'Parcial', 'No disponible');

CREATE TYPE status_pa_request_enum AS ENUM ('Pendiente', 'Aceptada', 'Rechazada');


-- =========================================
-- PERSONA
-- =========================================

CREATE TABLE persona (
    id                INTEGER GENERATED ALWAYS AS IDENTITY,
    mail              VARCHAR(255) NOT NULL,
    nombre            VARCHAR(100) NOT NULL,
    apellido_1        VARCHAR(100) NOT NULL,
    apellido_2        VARCHAR(100),
    telefono          VARCHAR(20) NOT NULL,
    fecha_alta        DATE NOT NULL,
    fecha_baja        DATE,
    fecha_nacimiento  DATE NOT NULL,
    sexo              sexo_enum NOT NULL,

    CONSTRAINT pk_persona PRIMARY KEY (id),
    CONSTRAINT uq_persona_mail UNIQUE (mail)
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

    CONSTRAINT pk_ovi_user PRIMARY KEY (id),
    CONSTRAINT fk_ovi_user_persona
        FOREIGN KEY (id)
        REFERENCES persona(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);


-- =========================================
-- PAT_PATI
-- =========================================

CREATE TABLE pat_pati (
    id              INTEGER NOT NULL,
    disponibilidad  disponibilidad_enum NOT NULL,

    CONSTRAINT pk_pat_pati PRIMARY KEY (id),
    CONSTRAINT fk_pat_pati_persona
        FOREIGN KEY (id)
        REFERENCES persona(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);


-- =========================================
-- PA_REQUEST
-- =========================================

CREATE TABLE pa_request (
    id                 INTEGER GENERATED ALWAYS AS IDENTITY,
    status             status_pa_request_enum NOT NULL,
    fecha_creacion     DATE NOT NULL,
    fecha_resolucion   DATE,
    ovi_user           INTEGER NOT NULL,

    CONSTRAINT pk_pa_request PRIMARY KEY (id),
    CONSTRAINT fk_pa_request_ovi_user
        FOREIGN KEY (ovi_user)
        REFERENCES ovi_user(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
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
        ON DELETE RESTRICT
);


-- =========================================
-- CONVERSACION
-- =========================================

CREATE TABLE conversacion (
    id          INTEGER GENERATED ALWAYS AS IDENTITY,
    pa_request  INTEGER NOT NULL,
    ovi_user    INTEGER NOT NULL,
    pat_pati    INTEGER NOT NULL,

    CONSTRAINT pk_conversacion PRIMARY KEY (id),
    CONSTRAINT uq_conversacion UNIQUE (pa_request, ovi_user, pat_pati),

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

    CONSTRAINT fk_conversacion_pat_pati
        FOREIGN KEY (pat_pati)
        REFERENCES pat_pati(id)
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
    contenido     TEXT NOT NULL,
    fecha_hora    TIMESTAMP NOT NULL,

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