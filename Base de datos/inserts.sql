-- =========================================================
-- INSERTS CORREGIDOS Y AMPLIADOS
-- Pensado para cargar la BD desde cero
-- =========================================================

-- =========================================================
-- PERSONA
-- =========================================================
INSERT INTO persona (
    id, mail, nombre, apellidos, direccion, pais, telefono,
    fecha_alta, fecha_baja, fecha_nacimiento, contrasena, dni, genero
)
OVERRIDING SYSTEM VALUE
VALUES

(1, 'admin1@ovi.es', 'Ana', 'Martinez Lopez', 'Calle Mayor 10', 'España', '600111111', '2026-01-01', NULL, '1990-05-10', 'hash_admin_ana_001', '11111111A', 'Mujer'),
(2, 'admin2@ovi.es', 'Pablo', 'Ortega Vidal', 'Avenida Castellon 25', 'España', '600111112', '2026-01-02', NULL, '1988-08-21', 'hash_admin_pablo_002', '22222222B', 'Hombre'),

(3, 'mario.user@ovi.es', 'Mario', 'De la Horra Falomir', 'Calle San Roque 14', 'España', '600222222', '2026-01-05', NULL, '2002-03-14', 'hash_user_mario_003', '33333333C', 'Hombre'),
(4, 'laura.user@ovi.es', 'Laura', 'Navarro Sanchez', 'Plaza del Real 8', 'Chile', '600333333', '2026-01-08', NULL, '2001-07-22', 'hash_user_laura_004', '44444444D', 'Mujer'),
(5, 'joel.user@ovi.es', 'Joel', 'Gil', 'Calle Colon 32', 'España', '600333334', '2026-01-10', NULL, '2000-11-02', 'hash_user_joel_005', '55555555E', 'Hombre'),


(6, 'carlos.patpati@ovi.es', 'Carlos', 'Ruiz Perez', 'Calle Fadrell 7', 'España', '600444444', '2026-01-03', NULL, '1995-09-18', 'hash_pap_carlos_006', '66666666F', 'Hombre'),
(7, 'lucia.patpati@ovi.es', 'Lucia', 'Fernandez Diaz', 'Avenida del Mar 18', 'Francia', '600555555', '2026-01-06', NULL, '1997-12-01', 'hash_pap_lucia_007', '77777777G', 'Mujer'),
(8, 'sofia.patpati@ovi.es', 'Sofia', 'Moreno Gil', 'Calle Herrero 11', 'Italia', '600666666', '2026-01-09', NULL, '1999-04-27', 'hash_pap_sofia_008', '88888888H', 'Mujer'),
(9, 'alex.patpati@ovi.es', 'Alex', 'Torres', 'Ronda Mijares 21', 'España', '600666667', '2026-01-11', NULL, '1998-02-13', 'hash_pap_alex_009', '99999999I', 'Otro'),


(10, 'elena.admin@ovi.es', 'Elena', 'Caso Revision', 'Calle Navarra 5', 'España', '600111113', '2026-01-12', NULL, '1987-03-09', 'hash_admin_elena_010', '10101010J', 'Mujer'),

(11, 'nora.casolimite@ovi.es', 'Nora', 'Caso Limite', 'Calle Doctor Clara 6', 'España', '600777771', '2026-02-01', NULL, '1996-06-06', 'hash_ovi_nora_011', '11111112K', 'Mujer'),
(12, 'bruno.casocero@ovi.es', 'Bruno', 'Caso Cero', 'Calle Enmedio 14', 'España', '600777772', '2026-02-02', NULL, '1994-04-14', 'hash_ovi_bruno_012', '12121212L', 'Hombre'),
(13, 'ines.fechabaja@ovi.es', 'Ines', 'Caso FechaBaja', 'Calle Asensi 9', 'España', '600777773', '2026-02-03', '2026-03-20', '1993-01-19', 'hash_ovi_ines_013', '13131313M', 'Mujer'),
(14, 'diego.multidiversidad@ovi.es', 'Diego', 'Multi Diversidad', 'Gran Via 20', 'España', '600777774', '2026-02-05', NULL, '1992-08-30', 'hash_ovi_diego_014', '14141414N', 'Hombre'),
(15, 'leo.caducado@ovi.es', 'Leo', 'Caso Caducado', 'Avenida Rey 2', 'Portugal', '600777775', '2026-02-07', NULL, '1990-12-12', 'hash_ovi_leo_015', '15151515O', 'Otro'),

(16, 'marta.caso1dia@ovi.es', 'Marta', 'Caso UnDia', 'Plaza Maria Agustina 3', 'España', '600888881', '2026-02-10', NULL, '1991-02-01', 'hash_pap_marta_016', '16161616P', 'Mujer'),
(17, 'raul.sinvehiculo@ovi.es', 'Raul', 'Sin Vehiculo', 'Calle Trinidad 4', 'España', '600888882', '2026-02-11', NULL, '1989-11-11', 'hash_pap_raul_017', '17171717Q', 'Hombre'),
(18, 'clara.multiespecialidad@ovi.es', 'Clara', 'Multi Especialidad', 'Calle Mealla 8', 'España', '600888883', '2026-02-12', NULL, '1993-05-21', 'hash_pap_clara_018', '18181818R', 'Mujer'),
(19, 'noa.perfilextremo@ovi.es', 'Noa', 'Perfil Extremo', 'Calle Gobernador 7', 'España', '600888884', '2026-02-13', NULL, '1986-09-09', 'hash_pap_noa_019', '19191919S', 'Otro'),
(20, 'hugo.hastahoy@ovi.es', 'Hugo', 'Disponible HastaHoy', 'Ronda Magdalena 12', 'España', '600888885', '2026-02-14', NULL, '1997-07-17', 'hash_pap_hugo_020', '20202020T', 'Hombre'),
(21, 'sara.rechazo@ovi.es', 'Sara', 'Caso Rechazo', 'Calle Alloza 15', 'España', '600888886', '2026-02-15', NULL, '1995-10-25', 'hash_pap_sara_021', '21212121U', 'Mujer'),
(22, 'teo.exp0@ovi.es', 'Teo', 'Experiencia Cero', 'Avenida Valencia 23', 'España', '600888887', '2026-02-16', NULL, '1998-03-03', 'hash_pap_teo_022', '22222223V', 'Otro');


-- =========================================================
-- ADMIN_OVI
-- =========================================================
INSERT INTO admin_ovi (id)
VALUES
(1),
(2),
(10);


-- =========================================================
-- OVI_USER
-- =========================================================
INSERT INTO ovi_user (
    id, grado_diversidad_funcional, grado_dependencia, estado,
    url_proyecto_de_vida, centro_social_referencia
)
VALUES
(3, 65, 2, 'Activo', 'https://documentos.ovi/proyectos/proyecto_mario.pdf', 'Centro Social Castellon Norte'),
(4, 40, 1, 'Activo', 'https://documentos.ovi/proyectos/proyecto_laura.pdf', 'Centro Social Valparaiso'),
(5, 25, NULL, 'Pendiente', 'https://documentos.ovi/proyectos/proyecto_joel.pdf', 'Centro Social Castellon Sur'),

-- Caso límite máximo permitido
(11, 100, 3, 'Activo', 'https://documentos.ovi/proyectos/proyecto_nora_caso_limite.pdf', 'Centro Social Caso Limite'),

-- Caso límite mínimo permitido
(12, 0, NULL, 'Pendiente', 'https://documentos.ovi/proyectos/proyecto_bruno_caso_cero.pdf', 'Centro Social Caso Cero'),

-- Caso con fecha_baja en persona
(13, 45, 2, 'Rechazado', 'https://documentos.ovi/proyectos/proyecto_ines_baja.pdf', 'Centro Social Fecha Baja'),

-- Caso con varias diversidades funcionales
(14, 80, 3, 'Activo', 'https://documentos.ovi/proyectos/proyecto_diego_multi.pdf', 'Centro Social Diversidad Multiple'),

-- Caso pensado para solicitudes y requests caducadas
(15, 55, 1, 'Pendiente', 'https://documentos.ovi/proyectos/proyecto_leo_caducado.pdf', 'Centro Social Caducado');


-- =========================================================
-- DIVERSIDAD_FUNCIONAL
-- =========================================================
INSERT INTO diversidad_funcional (
    id, ovi_user, tipo, observaciones
)
OVERRIDING SYSTEM VALUE
VALUES
(1, 3, 'Motora', 'Uso habitual de apoyo para desplazamientos largos'),
(2, 4, 'Visual', 'Necesita apoyo en lectura de documentos impresos'),
(3, 5, 'Cognitiva', 'Requiere instrucciones simples y pautadas'),
(4, 5, 'Psicosocial', 'Necesita acompanamiento puntual en situaciones de cambio'),

(5, 11, 'Visual', 'CasoLimite con afectacion visual severa'),
(6, 11, 'Auditiva', 'CasoLimite con apoyo combinado'),

(7, 12, 'Otra', 'CasoCero registrado para probar el minimo permitido'),

(8, 13, 'Organica', 'Usuario con baja temporal y seguimiento medico'),

(9, 14, 'Motora', 'CasoMulti con movilidad reducida'),
(10, 14, 'Cognitiva', 'CasoMulti con apoyo cognitivo leve'),

(11, 15, 'Auditiva', 'CasoCaducado con necesidad de apoyo auditivo');


-- =========================================================
-- PAP_PATI
-- =========================================================
INSERT INTO pap_pati (
    id, disponibilidad, fecha_inicio_disponible, fecha_fin_disponible,
    experiencia, vehiculo_propio, carnet_conducir, url_cv,
    descripcion_perfil, centro_social_referencia, estado
)
VALUES
(6, 'Total', '2026-01-03', NULL, 5, TRUE, TRUE, 'https://documentos.ovi/cv/cv_carlos.pdf',
 'Cuidador con experiencia en acompanamiento diario y gestiones', 'Centro Social Castellon Norte', 'Activo'),

(7, 'Parcial', '2026-01-06', '2026-06-30', 3, FALSE, TRUE, 'https://documentos.ovi/cv/cv_lucia.pdf',
 'Perfil orientado a rutinas de tarde y fines de semana', 'Centro Social Lyon', 'Activo'),

(8, 'No disponible', '2026-01-09', '2026-04-15', 2, FALSE, FALSE, 'https://documentos.ovi/cv/cv_sofia.pdf',
 'Actualmente sin disponibilidad por formacion complementaria', 'Centro Social Milan', 'Pendiente'),

(9, 'Total', '2026-01-11', NULL, 4, TRUE, TRUE, 'https://documentos.ovi/cv/cv_alex.pdf',
 'Perfil flexible con experiencia en convivencia y autonomia personal', 'Centro Social Castellon Centro', 'Activo'),


(16, 'Parcial', '2026-03-20', '2026-03-20', 1, FALSE, TRUE, 'https://documentos.ovi/cv/cv_marta_un_dia.pdf',
 'CasoUnDia para probar igualdad entre fecha inicio y fecha fin', 'Centro Social Caso UnDia', 'Pendiente'),


(17, 'Total', '2026-02-11', NULL, 6, FALSE, FALSE, 'https://documentos.ovi/cv/cv_raul_sin_vehiculo.pdf',
 'Acompanamiento urbano, tramites y apoyo en tareas del hogar', 'Centro Social Movilidad Urbana', 'Activo'),

-- Caso especial: muchas especialidades
(18, 'Parcial', '2026-02-12', '2026-08-31', 8, TRUE, TRUE, 'https://documentos.ovi/cv/cv_clara_multi.pdf',
 'Perfil con amplia experiencia en diversidad funcional y comunicacion adaptada', 'Centro Social Especialidades', 'Activo'),

-- Caso especial: perfil extremo
(19, 'Total', '2026-02-13', NULL, 15, TRUE, TRUE, 'https://documentos.ovi/cv/cv_noa_extremo.pdf',
 'Caso especial con experiencia muy alta para contrastar puntuaciones y matching', 'Centro Social Senior', 'Activo'),

-- Caso especial: disponible exactamente hasta hoy
(20, 'Parcial', '2026-02-14', '2026-03-31', 2, TRUE, TRUE, 'https://documentos.ovi/cv/cv_hugo_hasta_hoy.pdf',
 'DisponibleHastaHoy para probar fecha limite actual', 'Centro Social Fecha Actual', 'Pendiente'),


-- Caso especial: experiencia cero
(21, 'No disponible', '2026-02-16', NULL, 0, FALSE, FALSE, 'https://documentos.ovi/cv/cv_teo_exp0.pdf',
 'Caso de experiencia minima para probar filtros y ordenaciones', 'Centro Social Primer Empleo', 'Pendiente');


-- =========================================================
-- ESPECIALIDAD
-- =========================================================
INSERT INTO especialidad (
    id, pap_pati, especialidad
)
OVERRIDING SYSTEM VALUE
VALUES
(1, 6, 'Motora'),
(2, 6, 'Visual'),
(3, 7, 'Cognitiva'),
(4, 8, 'Auditiva'),
(5, 9, 'Psicosocial'),
(6, 9, 'Otra'),

(7, 16, 'Motora'),
(8, 17, 'Visual'),

(9, 18, 'Motora'),
(10, 18, 'Cognitiva'),
(11, 18, 'Auditiva'),

(12, 19, 'Organica'),
(13, 19, 'Psicosocial'),

(14, 20, 'Visual'),
(15, 21, 'Otra');



-- =========================================================
-- PA_REQUEST
-- =========================================================
INSERT INTO pa_request (
    id, status, fecha_creacion, fecha_resolucion, ovi_user
)
OVERRIDING SYSTEM VALUE
VALUES
(1, 'En activo' , '2026-02-01', NULL, 3),
(2, 'Finalizada', '2026-02-03', '2026-02-05', 3),
(3, 'Finalizada', '2026-02-04', '2026-02-06', 4),
(4, 'Finalizada', '2026-02-10', '2026-02-12', 4),
(5, 'En espera', '2026-02-14', NULL, 5),
(6, 'En activo', '2026-02-15', '2026-02-16', 5),

(7, 'En espera', '2026-03-01', NULL, 11),
(8, 'En activo', '2026-03-02', NULL, 12),
(9, 'Caducada', '2026-03-03', '2026-03-10', 13),
(10, 'Finalizada', '2026-03-05', '2026-03-15', 14),
(11, 'En espera', '2026-03-08', NULL, 15),

(12, 'Finalizada', '2026-03-09', '2026-03-09', 11),

(13, 'En activo', '2026-03-11', NULL, 14),
(14, 'Caducada', '2026-03-12', '2026-03-31', 12);


-- =========================================================
-- CONTRATO
-- Caso especial incluido: contrato de un solo dia
-- =========================================================
INSERT INTO contrato (
    id, fecha_inicio, fecha_fin, pa_request, url_documento
)
OVERRIDING SYSTEM VALUE
VALUES
(1, '2026-02-06', '2026-03-06', 2, 'https://documentos.ovi/contratos/contrato_001.pdf'),
(2, '2026-02-13', '2026-03-13', 4, 'https://documentos.ovi/contratos/contrato_002.pdf'),
(3, '2026-02-17', '2026-03-17', 6, 'https://documentos.ovi/contratos/contrato_003.pdf'),

(4, '2026-03-16', '2026-04-16', 10, 'https://documentos.ovi/contratos/contrato_004_multi.pdf'),

-- Caso especial: fecha_inicio = fecha_fin
(5, '2026-03-09', '2026-03-09', 12, 'https://documentos.ovi/contratos/contrato_005_un_dia.pdf');


-- =========================================================
-- CONVERSACION
-- =========================================================
INSERT INTO conversacion (
    id, pa_request, ovi_user, pap_pati
)
OVERRIDING SYSTEM VALUE
VALUES
(1, 1, 3, 6),
(2, 2, 3, 9),
(3, 3, 4, 7),
(4, 4, 4, 6),
(5, 5, 5, 8),
(6, 6, 5, 9),

(7, 7, 11, 18),
(8, 8, 12, 16),
(9, 9, 13, 21),
(10, 10, 14, 18),
(11, 11, 15, 20),
(12, 12, 11, 17),
(13, 13, 14, 19),
(14, 14, 12, 21);


-- =========================================================
-- MENSAJE
-- =========================================================
INSERT INTO mensaje (
    id, conversacion, emisor, contenido, fecha_hora
)
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
(13, 6, 9, 'Perfecto, quedo atento a lo que necesites.', '2026-02-16 09:03:00'),

(14, 7, 11, 'Hola Clara, he visto tu perfil y me interesa hablar contigo.', '2026-03-01 09:10:00'),
(15, 7, 18, 'Hola Nora, tengo disponibilidad parcial y experiencia en varios perfiles.', '2026-03-01 09:25:00'),
(16, 7, 11, 'Perfecto, me gustaria valorar horarios y tareas concretas.', '2026-03-01 09:40:00'),

(17, 8, 12, 'Hola Marta, tu disponibilidad de un dia me parece curiosa.', '2026-03-02 12:00:00'),
(18, 8, 16, 'Si, es un caso puntual pero la base de datos lo permite correctamente.', '2026-03-02 12:10:00'),
(19, 8, 12, 'Me viene bien para probar flujos rapidos de asignacion.', '2026-03-02 12:18:00'),

(20, 9, 13, 'Hola Sara, queria saber si sigues interesada en colaborar.', '2026-03-04 10:30:00'),
(21, 9, 21, 'Ahora mismo no, de hecho mi rol esta en estado rechazado.', '2026-03-04 10:42:00'),

(22, 10, 14, 'Hola Clara, volvemos a coincidir en otra solicitud.', '2026-03-05 16:00:00'),
(23, 10, 18, 'Sin problema, puedo asumir este caso tambien si encaja el horario.', '2026-03-05 16:07:00'),
(24, 10, 14, 'Genial, revisemos primero el contrato y las condiciones.', '2026-03-05 16:15:00'),

(25, 11, 15, 'Hola Hugo, veo que estas disponible hasta hoy.', '2026-03-31 08:00:00'),
(26, 11, 20, 'Correcto, justo hoy termina mi disponibilidad registrada.', '2026-03-31 08:12:00'),

(27, 12, 11, 'Hola Raul, veo que no tienes vehiculo ni carnet, pero me encaja un entorno urbano.', '2026-03-09 11:00:00'),
(28, 12, 17, 'Perfecto, me manejo bien en ciudad y acompanamientos cercanos.', '2026-03-09 11:08:00'),
(29, 12, 11, 'Entonces avancemos con los detalles del contrato corto.', '2026-03-09 11:15:00'),

(30, 13, 14, 'Hola Noa, tu perfil tiene mucha experiencia.', '2026-03-11 18:00:00'),
(31, 13, 19, 'Si, tengo experiencia alta y disponibilidad total.', '2026-03-11 18:06:00'),
(32, 13, 14, 'Perfecto, eso nos sirve para comparar varios candidatos.', '2026-03-11 18:12:00'),

(33, 14, 12, 'Hola Teo, veo que tu experiencia es cero pero queria valorar igualmente tu perfil.', '2026-03-12 14:00:00'),
(34, 14, 21, 'Gracias, ahora mismo no estoy disponible, pero sirve como caso de prueba.', '2026-03-12 14:10:00');


-- =========================================================
-- CANDIDATURAS
-- Incluyo puntuaciones 0 y 100 como casos borde
-- =========================================================
INSERT INTO candidaturas (
    id, pa_request, pap_pati, puntuacion
)
OVERRIDING SYSTEM VALUE
VALUES
(1, 1, 8, 85),
(2, 1, 7, 72),
(3, 2, 7, 90),
(4, 3, 6, 95),

(5, 5, 6, 88),
(6, 5, 9, 75),
(7, 6, 9, 93),


(8, 7, 18, 100),
(9, 7, 16, 65),

(11, 8, 17, 34),

(12, 9, 21, 40),
(13, 10, 18, 97),
(14, 10, 19, 89),
(15, 11, 20, 55),
(16, 11, 8, 30),
(17, 12, 17, 91),
(18, 13, 19, 99),
(19, 14, 8, 60),
(20, 14, 16, 59);


-- =========================================================
-- SOLICITUD
-- =========================================================
INSERT INTO solicitud (
    id, persona_solicitante, categoria, detalle, estado,
    mensaje_solicitante, motivo_resolucion, tecnico_revisor,
    fecha_creacion, fecha_resolucion
)
OVERRIDING SYSTEM VALUE
VALUES
(1, 11, 'Rol', 'Ovi_user', 'Pendiente',
 'Quiero completar la activacion total de mi rol de ovi_user.', NULL, NULL,
 '2026-02-27 09:00:00', NULL),

(2, 16, 'Rol', 'Pap_pati', 'Aprobada',
 'Solicito la activacion completa de mi perfil pap_pati.', NULL, 1,
 '2026-02-28 10:00:00', '2026-03-01 12:00:00'),

(3, 5, 'Proceso', 'Asistencia_tecnica', 'Rechazada',
 'No puedo acceder bien a cierta documentacion del sistema.',
 'Se reviso el caso y no se detecto incidencia tecnica reproducible.', 2,
 '2026-03-03 08:30:00', '2026-03-04 09:15:00'),

(4, 3, 'Proceso', 'Pa_request', 'Aprobada',
 'Solicito revision de una incidencia en una solicitud activa.', NULL, 1,
 '2026-03-05 11:00:00', '2026-03-06 13:30:00'),

(5, 12, 'Proceso', 'Otro', 'Pendiente',
 'Quiero consultar una situacion no contemplada en los formularios actuales.', NULL, NULL,
 '2026-03-07 14:20:00', NULL),

(6, 21, 'Rol', 'Pap_pati', 'Rechazada',
 'Me gustaria revisar el motivo por el que mi rol aparece rechazado.',
 'La documentacion aportada estaba incompleta.', 10,
 '2026-03-08 09:45:00', '2026-03-09 10:10:00'),

(7, 14, 'Rol', 'Ovi_user', 'Aprobada',
 'Solicito validacion definitiva de mi rol para operar en la plataforma.', NULL, 10,
 '2026-03-10 16:00:00', '2026-03-11 10:00:00'),

(8, 4, 'Proceso', 'Asistencia_tecnica', 'Pendiente',
 'Hay campos que no entiendo bien en el flujo de solicitudes.', NULL, NULL,
 '2026-03-15 12:00:00', NULL),

(9, 15, 'Proceso', 'Otro', 'Aprobada',
 'Quiero registrar una observacion sobre expiracion automatica de solicitudes.', NULL, 2,
 '2026-03-18 17:00:00', '2026-03-20 09:00:00'),

(10, 11, 'Proceso', 'Pa_request', 'Rechazada',
 'Solicito reabrir una request ya cerrada para revisar el matching.',
 'No procede la reapertura segun el flujo actual.', 1,
 '2026-03-21 08:00:00', '2026-03-22 08:45:00'),

(11, 22, 'Rol', 'Pap_pati', 'Aprobada',
 'Solicito alta del rol aunque mi experiencia sea cero para empezar desde nivel inicial.', NULL, 2,
 '2026-03-23 10:00:00', '2026-03-24 10:30:00'),

(12, 13, 'Proceso', 'Asistencia_tecnica', 'Pendiente',
 'Quiero dejar constancia de una incidencia aunque mi persona ya tenga fecha de baja.', NULL, NULL,
 '2026-03-30 16:10:00', NULL);
-- Resetear el id de las tablas para que continue correctamente después de los inserts
SELECT setval(pg_get_serial_sequence('persona', 'id'), COALESCE((SELECT MAX(id) FROM persona), 1), true);
SELECT setval(pg_get_serial_sequence('diversidad_funcional', 'id'), COALESCE((SELECT MAX(id) FROM diversidad_funcional), 1), true);
SELECT setval(pg_get_serial_sequence('especialidad', 'id'), COALESCE((SELECT MAX(id) FROM especialidad), 1), true);
SELECT setval(pg_get_serial_sequence('pa_request', 'id'), COALESCE((SELECT MAX(id) FROM pa_request), 1), true);
SELECT setval(pg_get_serial_sequence('contrato', 'id'), COALESCE((SELECT MAX(id) FROM contrato), 1), true);
SELECT setval(pg_get_serial_sequence('conversacion', 'id'), COALESCE((SELECT MAX(id) FROM conversacion), 1), true);
SELECT setval(pg_get_serial_sequence('mensaje', 'id'), COALESCE((SELECT MAX(id) FROM mensaje), 1), true);
SELECT setval(pg_get_serial_sequence('candidaturas', 'id'), COALESCE((SELECT MAX(id) FROM candidaturas), 1), true);
SELECT setval(pg_get_serial_sequence('solicitud', 'id'), COALESCE((SELECT MAX(id) FROM solicitud), 1), true);