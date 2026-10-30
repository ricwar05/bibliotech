# Mapa de evidencia SOLID y patrones

Estado al cierre de la Fase 1. Lo marcado como **pendiente** todavía no está en el código.

## Principios SOLID
| Principio | Dónde mostrarlo | Explicación |
|---|---|---|
| S · Responsabilidad única | `ui/BookController`, `service/BookService`, `repository/jdbc/JdbcBookRepository` | Cada clase tiene una sola razón para cambiar: la pantalla, las reglas o el SQL. |
| O · Abierto/cerrado | `repository/BookRepository` | Para usar otra base de datos se crea otra implementación de la interfaz; `BookService` no cambia. (Pendiente: `FineStrategy` para multas.) |
| L · Sustitución de Liskov | `repository/BookRepository` y `JdbcBookRepository` | La implementación respeta el contrato: `findById` devuelve `Optional.empty()` y nunca `null`. |
| I · Segregación de interfaces | Paquete `repository` | Un repositorio por entidad; `BookRepository` solo tiene operaciones de libros. |
| D · Inversión de dependencias | Constructor de `BookService` y `App.start()` | `BookService` recibe la interfaz `BookRepository`; solo `App` conoce `JdbcBookRepository`. |

## Patrones de diseño
| Patrón | Clases | Estado |
|---|---|---|
| Singleton | `config/DatabaseConnection` | Hecho |
| Repository / DAO | `repository/BookRepository` + `repository/jdbc/JdbcBookRepository` | Hecho |
| Strategy | `strategy/FineStrategy` y sus implementaciones | Pendiente |
| Factory Method | `report/ReportExporterFactory` | Pendiente |
