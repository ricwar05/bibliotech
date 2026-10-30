# Diccionario de datos · Fase 1

Describe cada tabla de la base de datos (Supabase / PostgreSQL), coherente con `database/01_schema.sql` y con las clases Java.

## Tabla app_users (usuarios del sistema)
| Columna | Tipo | Nulo | Descripción | Regla | Ejemplo |
|---|---|---|---|---|---|
| id | BIGINT identity | No | Identificador interno | PK, la genera la base | 1 |
| username | VARCHAR(50) | No | Nombre de usuario para entrar | Único | admin |
| password_hash | VARCHAR(60) | No | Hash bcrypt de la contraseña | Nunca la clave en texto plano | $2a$12$… |
| full_name | VARCHAR(120) | No | Nombre completo | No vacío | Administrador |
| role | VARCHAR(20) | No | Rol en el sistema | ADMIN o LIBRARIAN (enum Role) | ADMIN |
| active | BOOLEAN | No | Si puede entrar al sistema | TRUE por defecto | true |
| created_at | TIMESTAMPTZ | No | Fecha de creación | now() por defecto | 2026-10-27 10:00 |

## Tabla books (libros)
| Columna | Tipo | Nulo | Descripción | Regla | Ejemplo |
|---|---|---|---|---|---|
| id | BIGINT identity | No | Identificador interno | PK, la genera la base | 15 |
| isbn | VARCHAR(13) | No | ISBN del libro | Único; 10 o 13 dígitos | 9789962000011 |
| title | VARCHAR(200) | No | Título | No vacío | Cien años de soledad |
| author | VARCHAR(150) | No | Autor principal | No vacío | Gabriel García Márquez |
| publication_year | SMALLINT | Sí | Año de publicación | Entre 1450 y el año actual | 1967 |
| total_copies | INT | No | Ejemplares que tiene la biblioteca | Mayor o igual que 0 | 3 |
| available_copies | INT | No | Ejemplares disponibles | Entre 0 y total_copies | 2 |

## Tabla members (socios)
| Columna | Tipo | Nulo | Descripción | Regla | Ejemplo |
|---|---|---|---|---|---|
| id | BIGINT identity | No | Identificador interno | PK, la genera la base | 3 |
| document_id | VARCHAR(20) | No | Cédula o pasaporte | Único; 5 a 20 caracteres | 8-111-1111 |
| full_name | VARCHAR(120) | No | Nombre completo | No vacío | Ana Pérez |
| email | VARCHAR(120) | Sí | Correo electrónico | Único si existe; formato válido | ana@ejemplo.com |
| phone | VARCHAR(20) | Sí | Teléfono | Números, espacios, + y guiones | 6000-0001 |
| active | BOOLEAN | No | Si puede pedir préstamos | TRUE por defecto | true |

## Tabla loans (préstamos)
| Columna | Tipo | Nulo | Descripción | Regla | Ejemplo |
|---|---|---|---|---|---|
| id | BIGINT identity | No | Identificador interno | PK, la genera la base | 7 |
| book_id | BIGINT | No | Libro prestado | FK a books.id | 15 |
| member_id | BIGINT | No | Socio que lo pidió | FK a members.id | 3 |
| loan_date | DATE | No | Fecha del préstamo | Hoy por defecto | 2026-10-27 |
| due_date | DATE | No | Fecha de vencimiento | Mayor o igual que loan_date | 2026-11-10 |
| return_date | DATE | Sí | Fecha de devolución | NULL mientras esté prestado | 2026-11-12 |
| fine_amount | NUMERIC(10,2) | No | Multa cobrada | 0 por defecto; en Java es BigDecimal | 1.00 |
