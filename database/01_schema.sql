-- BiblioTech Panamá · Esquema de la Fase 1
-- Ejecutar en el SQL Editor de Supabase. Orden: este archivo y luego 02_seed.sql

-- Usuarios del sistema (no se llama "users" para no confundir con auth.users de Supabase)
CREATE TABLE app_users (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(60)  NOT NULL,                -- hash bcrypt, nunca la clave
    full_name     VARCHAR(120) NOT NULL,
    role          VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'LIBRARIAN')),
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE books (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn             VARCHAR(13)  NOT NULL UNIQUE,
    title            VARCHAR(200) NOT NULL,
    author           VARCHAR(150) NOT NULL,
    publication_year SMALLINT CHECK (publication_year BETWEEN 1450 AND 2100),
    total_copies     INT NOT NULL CHECK (total_copies >= 0),
    available_copies INT NOT NULL,
    CHECK (available_copies BETWEEN 0 AND total_copies)
);

CREATE TABLE members (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    document_id VARCHAR(20)  NOT NULL UNIQUE,            -- cédula o pasaporte
    full_name   VARCHAR(120) NOT NULL,
    email       VARCHAR(120) UNIQUE,
    phone       VARCHAR(20),
    active      BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE loans (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    book_id     BIGINT NOT NULL REFERENCES books(id),
    member_id   BIGINT NOT NULL REFERENCES members(id),
    loan_date   DATE   NOT NULL DEFAULT CURRENT_DATE,
    due_date    DATE   NOT NULL,
    return_date DATE,
    fine_amount NUMERIC(10,2) NOT NULL DEFAULT 0,
    CHECK (due_date >= loan_date)
);

-- Seguridad: activa RLS para bloquear el acceso por la API pública de Supabase.
-- La app de escritorio se conecta como dueño de las tablas (postgres) y no se ve afectada.
ALTER TABLE app_users ENABLE ROW LEVEL SECURITY;
ALTER TABLE books     ENABLE ROW LEVEL SECURITY;
ALTER TABLE members   ENABLE ROW LEVEL SECURITY;
ALTER TABLE loans     ENABLE ROW LEVEL SECURITY;
