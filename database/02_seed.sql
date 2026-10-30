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

-- Pedro agrega aquí los usuarios del sistema (admin y biblio) con sus hashes bcrypt.
