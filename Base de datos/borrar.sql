    -- =========================================
    -- ELIMINAR TABLAS SI EXISTEN
    -- =========================================
    DROP TABLE IF EXISTS persona CASCADE;
    DROP TABLE IF EXISTS mensaje CASCADE;
    DROP TABLE IF EXISTS conversacion CASCADE;
    DROP TABLE IF EXISTS candidaturas CASCADE;
    DROP TABLE IF EXISTS contrato CASCADE;
    DROP TABLE IF EXISTS solicitud CASCADE;
    DROP TABLE IF EXISTS diversidad_funcional CASCADE;
    DROP TABLE IF EXISTS especialidad CASCADE;
    DROP TABLE IF EXISTS contrato CASCADE;
    DROP TABLE IF EXISTS pa_request CASCADE;
    DROP TABLE IF EXISTS admin_ovi CASCADE;
    DROP TABLE IF EXISTS ovi_user CASCADE;
    DROP TABLE IF EXISTS pat_pati CASCADE;
    
    DROP TYPE IF EXISTS disponibilidad_enum CASCADE;
    DROP TYPE IF EXISTS status_pa_request_enum CASCADE;
    DROP TYPE IF EXISTS genero_enum CASCADE;
    DROP TYPE IF EXISTS diversidad_funcional_enum CASCADE;
    DROP TYPE IF EXISTS categoria_solicitud_enum CASCADE;
    DROP TYPE IF EXISTS detalle_solicitud_enum CASCADE;
    DROP TYPE IF EXISTS estado_rol_enum CASCADE;
    DROP TYPE IF EXISTS estado_solicitud_enum CASCADE;