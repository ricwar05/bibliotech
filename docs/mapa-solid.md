# Mapa de evidencia SOLID y patrones

## Principios SOLID
| Principio | Dónde mostrarlo | Explicación |
|---|---|---|
| S · Responsabilidad única | `ui/BookController`, `service/BookService`, `repository/jdbc/JdbcBookRepository` | Cada clase tiene una sola razón para cambiar: la pantalla, las reglas o el SQL. |
| O · Abierto/cerrado | `strategy/FineStrategy`, `PerDayFineStrategy`, `CappedFineStrategy` | Para una regla de multa nueva se crea otra clase; el código que la usa no cambia. |
| L · Sustitución de Liskov | `repository/BookRepository` y `JdbcBookRepository` | Cualquier implementación respeta el contrato: `findById` devuelve `Optional.empty()` y nunca `null`. |
| I · Segregación de interfaces | Paquete `repository` | Un repositorio por entidad; `UserRepository` solo tiene lo que el login necesita. |
| D · Inversión de dependencias | Constructores de `BookService`, `MemberService`, `AuthService` y `App.start()` | Los servicios reciben interfaces; solo `App` conoce las clases JDBC. |

## Patrones de diseño
| Patrón | Clases | Qué resuelve |
|---|---|---|
| Singleton | `config/DatabaseConnection` | Una sola conexión a la base para toda la aplicación. |
| Repository / DAO | `repository/*Repository` + `repository/jdbc/Jdbc*Repository` | El SQL queda en un solo lugar, detrás de una interfaz. |
| Strategy | `strategy/FineStrategy` y sus implementaciones | Reglas de multa intercambiables. |
| Factory Method | `report/ReportExporterFactory`, `CsvReportExporter`, `PdfReportExporter` | Un solo lugar decide qué exportador crear según el formato. |