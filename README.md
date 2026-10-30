# BiblioTech Panamá

Sistema de escritorio para gestionar el catálogo, los socios y los préstamos de una red
de bibliotecas comunitarias. Proyecto del curso EID 1012 Programación 2.

## Tecnologías
Java 21 · JavaFX 21 · Maven · Supabase (PostgreSQL) · JDBC · jBCrypt · JUnit 5

## Requisitos
- JDK 21
- Maven 3.9 o superior (comando `mvn` disponible en la terminal)
- Un proyecto en Supabase

## Instalación
1. Clonar el repositorio:
   `git clone https://github.com/ricwar05/bibliotech.git`
2. En el SQL Editor de Supabase, ejecutar en orden:
   `database/01_schema.sql` y luego `database/02_seed.sql`
3. Copiar `application.properties.example` como `application.properties`
   (en la raíz del proyecto, junto a `pom.xml`).
4. Completar `db.url`, `db.user` y `db.password` con los datos de
   Supabase → Connect → Session pooler.
   Este archivo NO se sube a GitHub (está en `.gitignore`).

## Ejecución
mvn clean javafx:run

Por ahora la aplicación abre directamente la pantalla de libros. Cuando se integre el
login, abrirá primero la pantalla de inicio de sesión.

## Estado de la Fase 1
| Módulo | Estado |
|---|---|
| Conexión a Supabase (Singleton) | Hecho |
| Excepciones propias y logging | Hecho |
| CRUD de libros | Hecho |
| CRUD de socios | Pendiente |
| Login con bcrypt y menú por rol | Pendiente |
| Patrones Strategy y Factory Method | Pendiente |

## Estructura del código (paquete `pa.bibliotech`)
| Paquete | Responsabilidad |
|---|---|
| `config` | Lectura de configuración y conexión única (Singleton) |
| `domain` | Entidades del negocio (hoy: `Book`) |
| `repository` | Interfaces de acceso a datos |
| `repository.jdbc` | Implementación con JDBC y PreparedStatement |
| `service` | Reglas de negocio y validaciones |
| `ui` | Controladores de las pantallas JavaFX |
| `exception` | Excepciones propias |

Carpetas reservadas para lo pendiente: `strategy` (multas), `report` (exportadores) y `util`.

## Documentación
- `docs/mapa-solid.md`: evidencia de SOLID y patrones
- `docs/capturas/`: capturas de Supabase y de la aplicación
- Diagrama de clases y diccionario de datos: en `docs/` (imagen y PDF, a cargo de Nelson)

## Integrantes
- Ricardo Guerra: líder, repositorio, conexión y catálogo de libros
- Nelson Guerra: base de datos, socios, diagrama y diccionario
- Pedro Martínez: autenticación, menú por rol, patrones Strategy y Factory