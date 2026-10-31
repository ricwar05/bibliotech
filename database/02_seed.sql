-- Datos de prueba. Todos los datos son ficticios.
INSERT INTO books (isbn, title, author, publication_year, total_copies, available_copies) VALUES
  ('9789962000011', 'Cien años de soledad',        'Gabriel García Márquez', 1967, 3, 3),
  ('9789962000028', 'El reino de este mundo',      'Alejo Carpentier',       1949, 2, 2),
  ('9789962000035', 'Ficciones',                   'Jorge Luis Borges',      1944, 2, 2),
  ('9789962000042', 'Pedro Páramo',                'Juan Rulfo',             1955, 1, 1),
  ('9789962000059', 'Introducción a la programación', 'Autor de Prueba',     2020, 4, 4);

INSERT INTO members (document_id, full_name, email, phone) VALUES
  ('8-111-1111', 'Ana Pérez',     'ana.perez@ejemplo.com',  '6000-0001'),
  ('8-222-2222', 'Luis Gómez',    'luis.gomez@ejemplo.com', '6000-0002'),
  ('PE-333-333', 'María Rodríguez', NULL,                    '6000-0003');

INSERT INTO app_users (username, password_hash, full_name, role, active) VALUES 
('admin', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW', 'Administrador Principal', 'ADMIN', TRUE),
('biblio', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW', 'Bibliotecario Turno', 'LIBRARIAN', TRUE);