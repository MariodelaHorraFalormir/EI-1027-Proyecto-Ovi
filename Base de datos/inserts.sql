-- =========================================
-- INSERTS PARA LA BASE DE DATOS
-- =========================================

-- =========================================
-- PERSONA
-- =========================================
INSERT INTO persona (
    id, mail, nombre, apellidos, direccion, pais, telefono,
    fecha_alta, fecha_baja, fecha_nacimiento, genero
)
OVERRIDING SYSTEM VALUE
VALUES
(1, 'admin1@ovi.es', 'Ana', 'Martinez Lopez', 'Calle Mayor 10', 'España', '600111111', '2026-01-01', NULL, '1990-05-10', 'Mujer'),
(2, 'admin2@ovi.es', 'Pablo', 'Ortega Vidal', 'Avenida Castellon 25', 'España', '600111112', '2026-01-02', NULL, '1988-08-21', 'Hombre'),

(3, 'mario.user@ovi.es', 'Mario', 'De la Horra Falomir', 'Calle San Roque 14', 'España', '600222222', '2026-01-05', NULL, '2002-03-14', 'Hombre'),
(4, 'laura.user@ovi.es', 'Laura', 'Navarro Sanchez', 'Plaza del Real 8', 'Chile', '600333333', '2026-01-08', NULL, '2001-07-22', 'Mujer'),
(5, 'joel.user@ovi.es', 'Joel', 'Gil', 'Calle Colon 32', 'España', '600333334', '2026-01-10', NULL, '2000-11-02', 'Hombre'),

(6, 'carlos.patpati@ovi.es', 'Carlos', 'Ruiz Perez', 'Calle Fadrell 7', 'España', '600444444', '2026-01-03', NULL, '1995-09-18', 'Hombre'),
(7, 'lucia.patpati@ovi.es', 'Lucia', 'Fernandez Diaz', 'Avenida del Mar 18', 'Francia', '600555555', '2026-01-06', NULL, '1997-12-01', 'Mujer'),
(8, 'sofia.patpati@ovi.es', 'Sofia', 'Moreno Gil', 'Calle Herrero 11', ' Italia', '600666666', '2026-01-09', NULL, '1999-04-27', 'Mujer'),
(9, 'alex.patpati@ovi.es', 'Alex', 'Torres', 'Ronda Mijares 21', 'España', '600666667', '2026-01-11', NULL, '1998-02-13', 'Otro');



-- =========================================
-- ADMIN_OVI
-- =========================================

INSERT INTO admin_ovi (id)
VALUES
(1),
(2);


-- =========================================
-- OVI_USER
-- =========================================

INSERT INTO ovi_user (id)
VALUES
(3),
(4),
(5);


-- =========================================
-- PAT_PATI
-- =========================================

INSERT INTO pat_pati (id, disponibilidad)
VALUES
(6, 'Total'),
(7, 'Parcial'),
(8, 'No disponible'),
(9, 'Total');


-- =========================================
-- PA_REQUEST
-- =========================================

INSERT INTO pa_request (id, status, fecha_creacion, fecha_resolucion, ovi_user)
OVERRIDING SYSTEM VALUE
VALUES
(1, 'En activo' , '2026-02-01', NULL, 3),
(2, 'Finalizada', '2026-02-03', '2026-02-05', 3),
(3, 'Finalizada', '2026-02-04', '2026-02-06', 4),
(4, 'Finalizada', '2026-02-10', '2026-02-12', 4),
(5, 'En espera', '2026-02-14', NULL, 5),
(6, 'En activo', '2026-02-15', '2026-02-16', 5);


-- =========================================
-- CONTRATO
-- =========================================

INSERT INTO contrato (id, fecha_inicio, fecha_fin, pa_request, url_documento)
OVERRIDING SYSTEM VALUE
VALUES
(1, '2026-02-06', '2026-03-06', 2, 'https://documentos.ovi/contratos/contrato_001.pdf'),
(2, '2026-02-13', '2026-03-13', 4, 'https://documentos.ovi/contratos/contrato_002.pdf'),
(3, '2026-02-17', '2026-03-17', 6, 'https://documentos.ovi/contratos/contrato_003.pdf');


-- =========================================
-- CONVERSACION
-- (pa_request, ovi_user, pat_pati) UNIQUE
-- =========================================

INSERT INTO conversacion (id, pa_request, ovi_user, pat_pati)
OVERRIDING SYSTEM VALUE
VALUES
(1, 1, 3, 6),
(2, 2, 3, 9),
(3, 3, 4, 7),
(4, 4, 4, 6),
(5, 5, 5, 8),
(6, 6, 5, 9);


-- =========================================
-- MENSAJE
-- emisor referencia a persona(id)
-- =========================================

INSERT INTO mensaje (id, conversacion, emisor, contenido, fecha_hora)
OVERRIDING SYSTEM VALUE
VALUES
(1, 1, 3, 'Hola, me gustaria saber si tienes disponibilidad este verano.', '2026-02-01 10:15:00'),
(2, 1, 6, 'Hola, si, actualmente tengo disponibilidad total.', '2026-02-01 10:20:00'),

(3, 2, 3, 'Perfecto, he visto que la solicitud ha sido aceptada.', '2026-02-05 09:00:00'),
(4, 2, 9, 'Genial, podemos concretar los detalles del contrato primero por aqui.', '2026-02-05 09:05:00'),
(5, 2, 3, 'De acuerdo, revisare el contrato hoy mismo.', '2026-02-05 09:10:00'),

(6, 3, 4, 'Hola, queria preguntar tu disponibilidad.', '2026-02-04 12:00:00'),
(7, 3, 7, 'Lo siento, no voy a poder aceptarla en este momento.', '2026-02-06 08:45:00'),

(8, 4, 4, 'Buenos dias, gracias por aceptar la solicitud.', '2026-02-12 11:00:00'),
(9, 4, 6, 'Encantado, revisa el contrato cuando puedas.', '2026-02-12 11:05:00'),

(10, 5, 5, 'Hola, me interesa hablar sobre la disponibilidad.', '2026-02-14 17:30:00'),
(11, 5, 8, 'Ahora mismo no tengo disponibilidad, pero podria cambiar pronto.', '2026-02-14 17:40:00'),

(12, 6, 5, 'Gracias por aceptar la solicitud.', '2026-02-16 09:00:00'),
(13, 6, 9, 'Perfecto, quedo atento a lo que necesites.', '2026-02-16 09:03:00');
-- =========================================
-- CANDIDATURAS
-- =========================================
INSERT INTO candidaturas (
    id, pa_request, pat_pati, puntuacion
)
OVERRIDING SYSTEM VALUE
VALUES
(1, 1, 4, 85),
(2, 1, 5, 72),
(3, 2, 5, 90),
(4, 3, 4, 95);