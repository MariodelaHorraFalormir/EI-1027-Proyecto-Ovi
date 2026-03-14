
-- =========================================
-- ENUMS
-- =========================================

CREATE TYPE disponibilidad_enum AS ENUM ('Disponible', 'No Disponible');
CREATE TYPE status_enum AS ENUM ('En espera', 'Aceptada', 'Rechazada');

-- =========================================
-- TABLA PERSONA
-- =========================================

CREATE TABLE persona (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    mail            VARCHAR(255) NOT NULL,
    nombre          VARCHAR(100) NOT NULL,
    apellido_1      VARCHAR(100) NOT NULL,
    apellido_2      VARCHAR(100),
    telefono        VARCHAR(20),
    fecha_alta      DATE NOT NULL,
    fecha_baja      DATE,
    CONSTRAINT pk_persona PRIMARY KEY (id),
    CONSTRAINT uq_persona_mail UNIQUE (mail)
);

-- =========================================
-- SUBTIPOS DE PERSONA
-- =========================================

CREATE TABLE admin_ovi (
    id INTEGER,
    CONSTRAINT pk_admin_ovi PRIMARY KEY (id),
    CONSTRAINT fk_admin_ovi_persona
        FOREIGN KEY (id)
        REFERENCES persona(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE ovi_user (
    id INTEGER,
    CONSTRAINT pk_ovi_user PRIMARY KEY (id),
    CONSTRAINT fk_ovi_user_persona
        FOREIGN KEY (id)
        REFERENCES persona(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE pat_pati (
    id               INTEGER,
    disponibilidad   disponibilidad_enum NOT NULL,
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
    id                  INTEGER GENERATED ALWAYS AS IDENTITY,
    status              status_enum NOT NULL,
    fecha_creacion      DATE NOT NULL,
    fecha_resolucion    DATE,
    ovi_user_id         INTEGER NOT NULL,
    CONSTRAINT pk_pa_request PRIMARY KEY (id),
    CONSTRAINT fk_pa_request_ovi_user
        FOREIGN KEY (ovi_user_id)
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
    fecha_fin       DATE,
    pa_request_id   INTEGER NOT NULL,
    url_documento   VARCHAR(500) NOT NULL,
    CONSTRAINT pk_contrato PRIMARY KEY (id),
    CONSTRAINT uq_contrato_pa_request UNIQUE (pa_request_id),
    CONSTRAINT uq_contrato_url_documento UNIQUE (url_documento),
    CONSTRAINT fk_contrato_pa_request
        FOREIGN KEY (pa_request_id)
        REFERENCES pa_request(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- =========================================
-- CONVERSACION
-- =========================================

CREATE TABLE conversacion (
    id_conversacion INTEGER GENERATED ALWAYS AS IDENTITY,
    pa_request      INTEGER NOT NULL,
    ovi_user        INTEGER NOT NULL,
    pat_pati        INTEGER NOT NULL,
    CONSTRAINT pk_conversacion PRIMARY KEY (id_conversacion),
    CONSTRAINT uq_conversacion UNIQUE (pa_request, ovi_user, pat_pati),
    CONSTRAINT fk_conversacion_pa_request
        FOREIGN KEY (pa_request)
        REFERENCES pa_request(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_conversacion_ovi_user
        FOREIGN KEY (ovi_user)
        REFERENCES ovi_user(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_conversacion_pat_pati
        FOREIGN KEY (pat_pati)
        REFERENCES pat_pati(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- =========================================
-- MENSAJE
-- =========================================

CREATE TABLE mensaje (
    id              INTEGER GENERATED ALWAYS AS IDENTITY,
    conversacion    INTEGER NOT NULL,
    emisor          INTEGER NOT NULL,
    contenido       TEXT NOT NULL,
    fecha_hora      TIMESTAMP NOT NULL,
    CONSTRAINT pk_mensaje PRIMARY KEY (id),
    CONSTRAINT fk_mensaje_conversacion
        FOREIGN KEY (conversacion)
        REFERENCES conversacion(id_conversacion)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_mensaje_emisor
        FOREIGN KEY (emisor)
        REFERENCES persona(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);