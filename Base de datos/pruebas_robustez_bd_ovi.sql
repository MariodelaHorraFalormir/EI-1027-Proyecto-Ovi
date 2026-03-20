-- =========================================================
-- FICHERO DE PRUEBAS DE ROBUSTEZ E INTEGRIDAD
-- Base de datos OVI
-- =========================================================
--
-- ¿Qué hemos hecho en este fichero?
-- ---------------------------------
-- Este script sirve para comprobar si la base de datos es
-- resistente frente a inserciones inválidas o "maliciosas".
--
-- La idea es insertar datos que rompan las reglas del modelo:
--
--   1) valores NULL donde no deberían existir
--   2) duplicados en atributos UNIQUE
--   3) claves ajenas que no existen
--   4) fechas incoherentes
--   5) valores fuera del dominio permitido por ENUM
--   6) textos vacíos
--   7) combinaciones duplicadas
--
-- Además, al final incluimos dos pruebas muy útiles:
--
--   A) casos que la base de datos debería rechazar y efectivamente rechaza
--   B) casos que semánticamente deberían fallar, pero que la base actual
--      todavía permite porque no están cubiertos por una restricción
--      CHECK, una FOREIGN KEY compuesta o un trigger adicional
--
-- Importante:
-- ----------
-- En PostgreSQL, cuando una instrucción falla dentro de una transacción,
-- la transacción completa queda abortada. Por eso aquí usamos bloques
-- DO ... EXCEPTION ... END para:
--
--   - ejecutar la prueba
--   - capturar el error esperado
--   - continuar con la siguiente prueba
--
-- De esta forma el fichero puede ejecutarse entero y documenta
-- claramente qué restricciones están funcionando.
--
-- Requisitos previos:
-- -------------------
-- Este script asume que:
--   - el esquema ya ha sido creado
--   - los inserts válidos de datos base ya se han ejecutado
--
-- =========================================================
-- INICIO DE LAS PRUEBAS
-- =========================================================

-- =========================================================
-- PRUEBA 1
-- Debe fallar: nombre = NULL en persona (NOT NULL)
-- Restricción implicada: NOT NULL
-- =========================================================
DO $$
BEGIN
    INSERT INTO persona (
        mail, nombre, apellidos, direccion, pais, telefono,
        fecha_alta, fecha_baja, fecha_nacimiento, genero
    )
    VALUES (
        'malicioso_null_nombre@ovi.es',
        NULL,
        'Prueba Integridad',
        'Calle Falsa 1',
        'España',
        '600900001',
        CURRENT_DATE,
        NULL,
        '2000-01-01',
        'Hombre'
    );

    RAISE WARNING 'PRUEBA 1 FALLÓ: la base de datos permitió un NULL en nombre.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 1 OK: se rechazó correctamente un NULL en nombre. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 2
-- Debe fallar: mail duplicado en persona
-- Restricción implicada: UNIQUE (mail)
-- =========================================================
DO $$
BEGIN
    INSERT INTO persona (
        mail, nombre, apellidos, direccion, pais, telefono,
        fecha_alta, fecha_baja, fecha_nacimiento, genero
    )
    VALUES (
        'admin1@ovi.es',
        'Mail',
        'Duplicado',
        'Calle Copia 2',
        'España',
        '600900002',
        CURRENT_DATE,
        NULL,
        '1999-05-05',
        'Mujer'
    );

    RAISE WARNING 'PRUEBA 2 FALLÓ: la base de datos permitió un mail duplicado.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 2 OK: se rechazó correctamente un mail duplicado. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 3
-- Debe fallar: fecha_nacimiento en el futuro
-- Restricción implicada: CHECK (fecha_nacimiento <= CURRENT_DATE)
-- =========================================================
DO $$
BEGIN
    INSERT INTO persona (
        mail, nombre, apellidos, direccion, pais, telefono,
        fecha_alta, fecha_baja, fecha_nacimiento, genero
    )
    VALUES (
        'malicioso_fecha_futura@ovi.es',
        'Fecha',
        'Futura',
        'Calle Tiempo 3',
        'España',
        '600900003',
        CURRENT_DATE,
        NULL,
        '2100-01-01',
        'Otro'
    );

    RAISE WARNING 'PRUEBA 3 FALLÓ: la base de datos permitió una fecha_nacimiento futura.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 3 OK: se rechazó correctamente una fecha_nacimiento futura. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 4
-- Debe fallar: fecha_baja anterior a fecha_alta
-- Restricción implicada: CHECK (fecha_baja IS NULL OR fecha_baja >= fecha_alta)
-- =========================================================
DO $$
BEGIN
    INSERT INTO persona (
        mail, nombre, apellidos, direccion, pais, telefono,
        fecha_alta, fecha_baja, fecha_nacimiento, genero
    )
    VALUES (
        'malicioso_fechas_persona@ovi.es',
        'Fechas',
        'Incoherentes',
        'Calle Error 4',
        'España',
        '600900004',
        '2026-03-10',
        '2026-03-01',
        '2000-05-05',
        'Hombre'
    );

    RAISE WARNING 'PRUEBA 4 FALLÓ: la base de datos permitió fecha_baja < fecha_alta.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 4 OK: se rechazó correctamente fecha_baja < fecha_alta. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 5
-- Debe fallar: valor no válido en ENUM genero
-- Restricción implicada: tipo ENUM genero_enum
-- =========================================================
DO $$
BEGIN
    INSERT INTO persona (
        mail, nombre, apellidos, direccion, pais, telefono,
        fecha_alta, fecha_baja, fecha_nacimiento, genero
    )
    VALUES (
        'malicioso_enum@ovi.es',
        'Genero',
        'Invalido',
        'Calle Enum 5',
        'España',
        '600900005',
        CURRENT_DATE,
        NULL,
        '1995-08-08',
        'Masculino'
    );

    RAISE WARNING 'PRUEBA 5 FALLÓ: la base de datos permitió un valor no válido en genero.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 5 OK: se rechazó correctamente un valor no válido en genero. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 6
-- Debe fallar: insertar admin_ovi con id inexistente
-- Restricción implicada: FOREIGN KEY admin_ovi(id) -> persona(id)
-- =========================================================
DO $$
BEGIN
    INSERT INTO admin_ovi (id)
    VALUES (999);

    RAISE WARNING 'PRUEBA 6 FALLÓ: la base de datos permitió un admin_ovi sin persona asociada.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 6 OK: se rechazó correctamente un admin_ovi con id inexistente. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 7
-- Debe fallar: pa_request con ovi_user inexistente
-- Restricción implicada: FOREIGN KEY pa_request(ovi_user) -> ovi_user(id)
-- =========================================================
DO $$
BEGIN
    INSERT INTO pa_request (status, fecha_creacion, fecha_resolucion, ovi_user)
    VALUES ('En espera', CURRENT_DATE, NULL, 999);

    RAISE WARNING 'PRUEBA 7 FALLÓ: la base de datos permitió una pa_request con ovi_user inexistente.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 7 OK: se rechazó correctamente una pa_request con ovi_user inexistente. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 8
-- Debe fallar: fecha_resolucion anterior a fecha_creacion en pa_request
-- Restricción implicada: CHECK de fechas en pa_request
-- =========================================================
DO $$
BEGIN
    INSERT INTO pa_request (status, fecha_creacion, fecha_resolucion, ovi_user)
    VALUES ('Finalizada', '2026-03-10', '2026-03-01', 3);

    RAISE WARNING 'PRUEBA 8 FALLÓ: la base de datos permitió fecha_resolucion < fecha_creacion.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 8 OK: se rechazó correctamente fecha_resolucion < fecha_creacion. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 9
-- Debe fallar: fecha_fin anterior a fecha_inicio en contrato
-- Restricción implicada: CHECK (fecha_fin >= fecha_inicio)
-- =========================================================
DO $$
BEGIN
    INSERT INTO contrato (fecha_inicio, fecha_fin, pa_request, url_documento)
    VALUES (
        '2026-04-10',
        '2026-04-01',
        1,
        'https://documentos.ovi/contratos/error_fechas_contrato.pdf'
    );

    RAISE WARNING 'PRUEBA 9 FALLÓ: la base de datos permitió fecha_fin < fecha_inicio en contrato.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 9 OK: se rechazó correctamente fecha_fin < fecha_inicio en contrato. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 10
-- Debe fallar: repetir pa_request en contrato
-- Restricción implicada: UNIQUE (pa_request)
-- =========================================================
DO $$
BEGIN
    INSERT INTO contrato (fecha_inicio, fecha_fin, pa_request, url_documento)
    VALUES (
        '2026-04-01',
        '2026-05-01',
        2,
        'https://documentos.ovi/contratos/duplicado_pa_request.pdf'
    );

    RAISE WARNING 'PRUEBA 10 FALLÓ: la base de datos permitió dos contratos para la misma pa_request.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 10 OK: se rechazó correctamente duplicar pa_request en contrato. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 11
-- Debe fallar: repetir url_documento en contrato
-- Restricción implicada: UNIQUE (url_documento)
-- =========================================================
DO $$
BEGIN
    INSERT INTO contrato (fecha_inicio, fecha_fin, pa_request, url_documento)
    VALUES (
        '2026-04-01',
        '2026-05-01',
        1,
        'https://documentos.ovi/contratos/contrato_001.pdf'
    );

    RAISE WARNING 'PRUEBA 11 FALLÓ: la base de datos permitió repetir url_documento.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 11 OK: se rechazó correctamente repetir url_documento. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 12
-- Debe fallar: conversación duplicada
-- Restricción implicada: UNIQUE (pa_request, ovi_user, pat_pati)
-- =========================================================
DO $$
BEGIN
    INSERT INTO conversacion (pa_request, ovi_user, pat_pati)
    VALUES (1, 3, 6);

    RAISE WARNING 'PRUEBA 12 FALLÓ: la base de datos permitió una conversación duplicada.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 12 OK: se rechazó correctamente una conversación duplicada. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 13
-- Debe fallar: contenido vacío en mensaje
-- Restricción implicada: CHECK (length(trim(contenido)) > 0)
-- =========================================================
DO $$
BEGIN
    INSERT INTO mensaje (conversacion, emisor, contenido, fecha_hora)
    VALUES (1, 3, '    ', CURRENT_TIMESTAMP);

    RAISE WARNING 'PRUEBA 13 FALLÓ: la base de datos permitió un mensaje vacío.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 13 OK: se rechazó correctamente un mensaje vacío. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 14
-- Debe fallar: conversación inexistente en mensaje
-- Restricción implicada: FOREIGN KEY mensaje(conversacion) -> conversacion(id)
-- =========================================================
DO $$
BEGIN
    INSERT INTO mensaje (conversacion, emisor, contenido, fecha_hora)
    VALUES (999, 3, 'Mensaje imposible', CURRENT_TIMESTAMP);

    RAISE WARNING 'PRUEBA 14 FALLÓ: la base de datos permitió un mensaje en una conversación inexistente.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 14 OK: se rechazó correctamente una conversación inexistente. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 15
-- Debe fallar: candidatura duplicada
-- Restricción implicada: UNIQUE (pat_pati, pa_request)
-- =========================================================
DO $$
BEGIN
    INSERT INTO candidaturas (pa_request, pat_pati, puntuacion)
    VALUES (1, 6, 50);

    RAISE WARNING 'PRUEBA 15 FALLÓ: la base de datos permitió una candidatura duplicada.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 15 OK: se rechazó correctamente una candidatura duplicada. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBAS DE DEBILIDADES DEL MODELO ACTUAL
-- Estas pruebas no buscan romper la base por sintaxis, sino mostrar
-- incoherencias semánticas que la base actual todavía permite.
-- Si se insertan correctamente, NO es bueno: significa que falta
-- reforzar el modelo con más restricciones o triggers.
-- =========================================================

-- =========================================================
-- PRUEBA 16
-- Semánticamente debería fallar:
-- un admin_ovi (id = 1) no debería poder mandar mensajes
-- en una conversación ajena si no participa en ella.
--
-- Resultado actual esperado:
-- la base probablemente LO PERMITIRÁ, porque emisor solo referencia
-- a persona(id), no a los participantes de la conversación.
-- =========================================================
DO $$
BEGIN
    INSERT INTO mensaje (conversacion, emisor, contenido, fecha_hora)
    VALUES (
        1,
        1,
        'Prueba de debilidad: un admin ajeno envia este mensaje.',
        CURRENT_TIMESTAMP
    );

    RAISE WARNING 'PRUEBA 16 DETECTA DEBILIDAD: la base permitió que una persona ajena a la conversación enviara un mensaje.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 16 MEJOR DE LO ESPERADO: la base rechazó el mensaje de una persona ajena. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 17
-- Semánticamente debería fallar:
-- el ovi_user de la conversación debería coincidir con
-- el ovi_user real de la pa_request.
--
-- Resultado actual esperado:
-- la base probablemente LO PERMITIRÁ, porque la FK comprueba
-- que pa_request exista y que ovi_user exista, pero no que
-- correspondan entre sí.
-- =========================================================
DO $$
BEGIN
    INSERT INTO conversacion (pa_request, ovi_user, pat_pati)
    VALUES (1, 5, 6);

    RAISE WARNING 'PRUEBA 17 DETECTA DEBILIDAD: la base permitió una conversación con un ovi_user que no corresponde a la pa_request.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 17 MEJOR DE LO ESPERADO: la base rechazó la incoherencia entre pa_request y ovi_user. Error: %', SQLERRM;
END $$;


-- =========================================================
-- PRUEBA 18
-- Semánticamente debería fallar:
-- un pat_pati ajeno a la solicitud no debería poder abrir
-- conversación si no está relacionado con esa pa_request.
--
-- Resultado actual esperado:
-- la base probablemente LO PERMITIRÁ si no existe otra conversación
-- igual, porque no se comprueba que exista en candidaturas.
-- =========================================================
DO $$
BEGIN
    INSERT INTO conversacion (pa_request, ovi_user, pat_pati)
    VALUES (2, 3, 8);

    RAISE WARNING 'PRUEBA 18 DETECTA DEBILIDAD: la base permitió una conversación con un pat_pati no vinculado a la solicitud.';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'PRUEBA 18 MEJOR DE LO ESPERADO: la base rechazó un pat_pati ajeno a la solicitud. Error: %', SQLERRM;
END $$;


-- =========================================================
-- RESUMEN FINAL
-- =========================================================
--
-- Si las PRUEBAS 1 a 15 muestran NOTICE, significa que las
-- restricciones básicas del esquema funcionan correctamente.
--
-- Si las PRUEBAS 16 a 18 muestran WARNING, significa que se han
-- detectado debilidades semánticas reales del modelo actual.
--
-- Posibles mejoras futuras:
--   - impedir que mensaje.emisor sea una persona ajena a la conversación
--   - asegurar que conversacion.ovi_user coincida con el de pa_request
--   - asegurar que conversacion.pat_pati esté relacionado con la solicitud
--     mediante candidaturas o una regla equivalente
--   - añadir triggers o restricciones compuestas adicionales
--
-- Fin del fichero.
